package org.nmcpye.am.team.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
@Repository("org.nmcpye.am.team.TeamRepositoryExt")
public class TeamRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Team> implements TeamRepositoryExt {

    public TeamRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                       ApplicationEventPublisher publisher,
                                       CurrentUserService currentUserService,
                                       AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Team.class, currentUserService, aclService, true);
    }

    @Override
    public void save(@Nonnull Team object, boolean clearSharing) {
        super.save(object, clearSharing);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }

    @Override
    public void update(@Nonnull Team object, User user) {
        super.update(object, user);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }
}
