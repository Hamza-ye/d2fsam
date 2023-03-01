package org.nmcpye.am.activity.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityRepositoryExt;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
@Repository("org.nmcpye.am.activity.ActivityRepositoryExt")
public class ActivityRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Activity>
    implements ActivityRepositoryExt {

    public ActivityRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                           ApplicationEventPublisher publisher,
                                           CurrentUserService currentUserService,
                                           AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Activity.class, currentUserService, aclService, true);
    }
}
