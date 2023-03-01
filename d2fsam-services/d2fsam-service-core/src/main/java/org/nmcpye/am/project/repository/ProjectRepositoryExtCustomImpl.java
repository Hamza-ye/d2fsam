package org.nmcpye.am.project.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.project.ProjectRepositoryExt")
public class ProjectRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Project>
    implements ProjectRepositoryExt {

    public ProjectRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                          ApplicationEventPublisher publisher,
                                          CurrentUserService currentUserService,
                                          AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Project.class, currentUserService, aclService, true);
    }
}
