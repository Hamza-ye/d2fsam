package org.nmcpye.am.user.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserGroup;
import org.nmcpye.am.user.UserGroupRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

@Repository("org.nmcpye.am.user.UserGroupRepositoryExt")
public class UserGroupRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<UserGroup>
    implements UserGroupRepositoryExt {

    public UserGroupRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher,
                                            CurrentUserService currentUserService,
                                            AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, UserGroup.class, currentUserService,
            aclService, true);
    }

    @Override
    public void save(@Nonnull UserGroup object, boolean clearSharing) {
        super.save(object, clearSharing);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }

    @Override
    public void update(@Nonnull UserGroup object, User user) {
        super.update(object, user);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }
}
