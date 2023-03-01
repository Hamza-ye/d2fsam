package org.nmcpye.am.trackedentity.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.trackedentity.TrackedEntityTypeRepositoryExt")
public class TrackedEntityTypeRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<TrackedEntityType>
    implements TrackedEntityTypeRepositoryExt {

    public TrackedEntityTypeRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                    ApplicationEventPublisher publisher,
                                                    CurrentUserService currentUserService,
                                                    AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityType.class,
            currentUserService, aclService, true);
    }
}
