package org.nmcpye.am.fileresource.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.fileresource.ExternalFileResource;
import org.nmcpye.am.fileresource.ExternalFileResourceRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.fileresource.ExternalFileResourceRepositoryExt")
public class ExternalFileResourceRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<ExternalFileResource>
    implements ExternalFileResourceRepositoryExt {

    public ExternalFileResourceRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                       ApplicationEventPublisher publisher,
                                                       CurrentUserService currentUserService,
                                                       AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ExternalFileResource.class,
            currentUserService, aclService, false);
    }

    @Override
    public ExternalFileResource getExternalFileResourceByAccessToken(String accessToken) {
        return getQuery("from ExternalFileResource where accessToken = :accessToken")
            .setParameter("accessToken", accessToken)
            .uniqueResult();
    }
}
