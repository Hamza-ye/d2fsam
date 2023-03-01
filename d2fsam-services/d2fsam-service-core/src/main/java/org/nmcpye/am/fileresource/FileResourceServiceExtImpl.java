package org.nmcpye.am.fileresource;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.fileresource.events.BinaryFileSavedEvent;
import org.nmcpye.am.fileresource.events.FileDeletedEvent;
import org.nmcpye.am.fileresource.events.FileSavedEvent;
import org.nmcpye.am.fileresource.events.ImageFileSavedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link FileResource}.
 */
@Service
@Slf4j
public class FileResourceServiceExtImpl implements FileResourceServiceExt {

    private static final Duration IS_ORPHAN_TIME_DELTA = Hours.TWO.toStandardDuration();

    public static final Predicate<FileResource> IS_ORPHAN_PREDICATE = (fr -> !fr.isAssigned());

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private final FileResourceRepositoryExt fileResourceStore;

    private final SessionFactory sessionFactory;

    private final FileResourceContentStore fileResourceContentStore;

    private final ImageProcessingService imageProcessingService;

    private final ApplicationEventPublisher fileEventPublisher;

    public FileResourceServiceExtImpl(FileResourceRepositoryExt fileResourceStore, SessionFactory sessionFactory,
                                      FileResourceContentStore fileResourceContentStore,
                                      ImageProcessingService imageProcessingService,
                                      ApplicationEventPublisher fileEventPublisher) {
        this.sessionFactory = sessionFactory;
        this.fileResourceStore = fileResourceStore;
        this.fileResourceContentStore = fileResourceContentStore;
        this.imageProcessingService = imageProcessingService;
        this.fileEventPublisher = fileEventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public FileResource getFileResource(String uid) {
        return checkStorageStatus(fileResourceStore.getByUid(uid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResource> getFileResources(List<String> uids) {
        return fileResourceStore.getByUid(uids).stream().map(this::checkStorageStatus).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResource> getOrphanedFileResources() {
        return fileResourceStore.getAllLeCreated(new DateTime().minus(IS_ORPHAN_TIME_DELTA).toDate().toInstant()).stream()
            .filter(IS_ORPHAN_PREDICATE)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveFileResource(FileResource fileResource, File file) {
        validateFileResource(fileResource);

        fileResource.setStorageStatus(FileResourceStorageStatus.PENDING);
        fileResourceStore.saveObject(fileResource);
        sessionFactory.getCurrentSession().flush();

        if (FileResource.IMAGE_CONTENT_TYPES.contains(fileResource.getContentType()) &&
            FileResourceDomain.getDomainForMultipleImages().contains(fileResource.getDomain())) {
            Map<ImageFileDimension, File> imageFiles = imageProcessingService.createImages(fileResource, file);

            fileEventPublisher.publishEvent(new ImageFileSavedEvent(fileResource.getUid(), imageFiles));
            return;
        }

        fileEventPublisher.publishEvent(new FileSavedEvent(fileResource.getUid(), file));
    }

    @Override
    @Transactional
    public String saveFileResource(FileResource fileResource, byte[] bytes) {
        fileResource.setStorageStatus(FileResourceStorageStatus.PENDING);
        fileResourceStore.saveObjectAndFlush(fileResource);

        final String uid = fileResource.getUid();

        fileEventPublisher.publishEvent(new BinaryFileSavedEvent(fileResource.getUid(), bytes));

        return uid;
    }

    @Override
    @Transactional
    public void deleteFileResource(String uid) {
        if (uid == null) {
            return;
        }

        FileResource fileResource = fileResourceStore.getByUid(uid);

        deleteFileResource(fileResource);
    }

    @Override
    @Transactional
    public void deleteFileResource(FileResource fileResource) {
        if (fileResource == null) {
            return;
        }

        FileResource existingResource = fileResourceStore.get(fileResource.getId());

        if (existingResource == null) {
            return;
        }

        FileDeletedEvent deleteFileEvent = new FileDeletedEvent(
            existingResource.getStorageKey(),
            existingResource.getContentType(),
            existingResource.getDomain()
        );

        fileResourceStore.deleteObject(existingResource);

        fileEventPublisher.publishEvent(deleteFileEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream getFileResourceContent(FileResource fileResource) {
        return fileResourceContentStore.getFileResourceContent(fileResource.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public long getFileResourceContentLength(FileResource fileResource) {
        return fileResourceContentStore.getFileResourceContentLength(fileResource.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public void copyFileResourceContent(FileResource fileResource, OutputStream outputStream) throws IOException, NoSuchElementException {
        fileResourceContentStore.copyContent(fileResource.getStorageKey(), outputStream);
    }

    @Override
    @Transactional
    public boolean fileResourceExists(String uid) {
        return fileResourceStore.getByUid(uid) != null;
    }

    @Override
    @Transactional
    public void updateFileResource(FileResource fileResource) {
        fileResourceStore.update(fileResource);
    }

    @Override
    @Transactional(readOnly = true)
    public URI getSignedGetFileResourceContentUri(String uid) {
        FileResource fileResource = getFileResource(uid);

        if (fileResource == null) {
            return null;
        }

        return fileResourceContentStore.getSignedGetContentUri(fileResource.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public URI getSignedGetFileResourceContentUri(FileResource fileResource) {
        if (fileResource == null) {
            return null;
        }

        return fileResourceContentStore.getSignedGetContentUri(fileResource.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResource> getExpiredFileResources(FileResourceRetentionStrategy retentionStrategy) {
        DateTime expires = DateTime.now().minus(retentionStrategy.getRetentionTime());
        return fileResourceStore.getExpiredFileResources(expires);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResource> getAllUnProcessedImagesFiles() {
        return fileResourceStore.getAllUnProcessedImages();
    }

    /**
     * Validates the given {@link FileResource}. Throws an exception if not.
     *
     * @param fileResource the file resource.
     * @throws IllegalQueryException if the given file resource is invalid.
     */
    private void validateFileResource(FileResource fileResource) throws IllegalQueryException {
        if (fileResource.getName() == null) {
            throw new IllegalQueryException(ErrorCode.E6100);
        }

        if (!FileResourceBlocklist.isValid(fileResource)) {
            throw new IllegalQueryException(ErrorCode.E6101);
        }
    }

    private FileResource checkStorageStatus(FileResource fileResource) {
        if (fileResource != null) {
            boolean exists = fileResourceContentStore.fileResourceContentExists(fileResource.getStorageKey());

            if (exists) {
                fileResource.setStorageStatus(FileResourceStorageStatus.STORED);
            } else {
                fileResource.setStorageStatus(FileResourceStorageStatus.PENDING);
            }
        }

        return fileResource;
    }
}
