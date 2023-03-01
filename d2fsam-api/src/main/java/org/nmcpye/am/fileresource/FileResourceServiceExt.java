package org.nmcpye.am.fileresource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service Interface for managing {@link FileResource}.
 */
public interface FileResourceServiceExt {
    FileResource getFileResource(String uid);

    List<FileResource> getFileResources(List<String> uids);

    List<FileResource> getOrphanedFileResources();

    void saveFileResource(FileResource fileResource, File file);

    String saveFileResource(FileResource fileResource, byte[] bytes);

    void deleteFileResource(String uid);

    void deleteFileResource(FileResource fileResource);

    InputStream getFileResourceContent(FileResource fileResource);

    /**
     * Copy fileResource content to outputStream and Return File content length
     *
     * @param fileResource
     * @param outputStream
     * @return
     * @throws IOException
     * @throws NoSuchElementException
     */
    void copyFileResourceContent(FileResource fileResource, OutputStream outputStream) throws IOException, NoSuchElementException;

    boolean fileResourceExists(String uid);

    void updateFileResource(FileResource fileResource);

    URI getSignedGetFileResourceContentUri(String uid);

    URI getSignedGetFileResourceContentUri(FileResource fileResource);

    List<FileResource> getExpiredFileResources(FileResourceRetentionStrategy retentionStrategy);

    List<FileResource> getAllUnProcessedImagesFiles();

    long getFileResourceContentLength(FileResource fileResource);
}
