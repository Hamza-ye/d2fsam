package org.nmcpye.am.user.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.UserAuthorityGroupRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.user.UserAuthorityGroupRepository")
public class UserAuthorityGroupRepositoryImpl
    extends HibernateIdentifiableObjectStore<UserAuthorityGroup>
    implements UserAuthorityGroupRepository {

    public UserAuthorityGroupRepositoryImpl(SessionFactory sessionFactory,
                                            JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher,
                                            CurrentUserService currentUserService,
                                            AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, UserAuthorityGroup.class, currentUserService, aclService,
            true);
    }
}
