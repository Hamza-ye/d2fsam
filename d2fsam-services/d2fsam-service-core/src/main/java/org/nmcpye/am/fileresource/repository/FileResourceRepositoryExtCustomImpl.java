package org.nmcpye.am.fileresource.repository;

import com.google.common.collect.ImmutableSet;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository("org.nmcpye.am.fileresource.FileResourceRepositoryExt")
public class FileResourceRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<FileResource>
    implements FileResourceRepositoryExt {

    private static final Set<String> IMAGE_CONTENT_TYPES = new ImmutableSet.Builder<String>()
        .add("image/jpg")
        .add("image/png")
        .add("image/jpeg")
        .build();

    public FileResourceRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                               ApplicationEventPublisher publisher,
                                               CurrentUserService currentUserService,
                                               AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, FileResource.class, currentUserService, aclService, false);
    }

    @Override
    public List<FileResource> getExpiredFileResources(DateTime expires) {
        List<FileResource> results = getSession()
            .createNativeQuery(
                "select fr.* " +
                    "from file_resource fr " +
                    "inner join (select dva.value " +
                    "from data_value_audit dva " +
                    "where dva.created < :date " +
                    "and dva.audittype in ('DELETE', 'UPDATE') " +
                    "and dva.dataelementid in " +
                    "(select dataelementid from data_element where valuetype = 'FILE_RESOURCE')) dva " +
                    "on dva.value = fr.uid " +
                    "where fr.assigned = true; ",
                FileResource.class
            )
            .setParameter("date", expires.toDate().toInstant())
            .getResultList();

        return results;
    }

    @Override
    public List<FileResource> getAllUnProcessedImages() {
        return getQuery(
            "FROM FileResource fr WHERE fr.domain IN ( :domains ) AND fr.contentType IN ( :contentTypes ) AND hasMultipleStorageFiles = :hasMultipleStorageFiles"
        )
            .setParameter("domains", FileResourceDomain.getDomainForMultipleImages())
            .setParameter("contentTypes", IMAGE_CONTENT_TYPES)
            .setParameter("hasMultipleStorageFiles", false)
            .setMaxResults(50)
            .getResultList();
    }
}
