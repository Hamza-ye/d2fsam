package org.nmcpye.am.team.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.team.TeamGroupRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Repository("org.nmcpye.am.team.TeamGroupRepositoryExt")
public class TeamGroupRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<TeamGroup>
    implements TeamGroupRepositoryExt {

    public TeamGroupRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher,
                                            CurrentUserService currentUserService,
                                            AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, TeamGroup.class, currentUserService, aclService, true);
    }

    @Override
    public void save(@Nonnull TeamGroup object, boolean clearSharing) {
        super.save(object, clearSharing);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }

    @Override
    public void update(@Nonnull TeamGroup object, User user) {
        super.update(object, user);
        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
    }

    @Override
    public List<TeamGroup> getByMembers(Collection<Team> teams) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> root.get("members").in(teams)));
    }
}
