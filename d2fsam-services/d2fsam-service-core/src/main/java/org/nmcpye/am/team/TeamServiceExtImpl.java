package org.nmcpye.am.team;

import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Service Implementation for managing {@link Team}.
 */
@Service("org.nmcpye.am.team.TeamServiceExt")
public class TeamServiceExtImpl implements TeamServiceExt {

    private final TeamRepositoryExt teamRepositoryExt;

    private final TeamGroupRepositoryExt teamGroupRepositoryExt;

    private final CurrentUserService currentUserService;

    private final AclService aclService;


    private final Cache<String> teamNameCache;

    public TeamServiceExtImpl(
        TeamRepositoryExt teamRepositoryExt,
        TeamGroupRepositoryExt teamGroupRepositoryExt,
        CurrentUserService currentUserService, AclService aclService,
        CacheProvider cacheProvider) {
        this.teamRepositoryExt = teamRepositoryExt;
        this.teamGroupRepositoryExt = teamGroupRepositoryExt;
        this.currentUserService = currentUserService;
        this.aclService = aclService;
        teamNameCache = cacheProvider.createTeamDisplayNameCache();
    }

    @Override
    @Transactional
    public Long addTeam(Team team) {
        teamRepositoryExt.saveObject(team);
        return team.getId();
    }

    @Override
    @Transactional
    public void updateTeam(Team team) {
        teamRepositoryExt.update(team);
    }

    @Override
    public Long addTeamGroup(TeamGroup teamGroup) {
        teamGroupRepositoryExt.saveObject(teamGroup);
        return teamGroup.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeam(String uid) {
        return teamRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getTeams(Collection<String> uids) {
        return teamRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getTeamsBetweenByName(String name, int first, int max) {
        return teamRepositoryExt.getAllLikeName(name, first, max, false);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDisplayName(String uid) {
        return teamNameCache.get(uid, n -> teamRepositoryExt.getByUidNoAcl(uid).getDisplayName());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrRemoveMember(String uid) {
        return canAddOrRemoveMember(uid, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrRemoveMember(String uid, User currentUser) {
        Team team = getTeam(uid);

        if (team == null || currentUser == null) {
            return false;
        }

        boolean canUpdate = aclService.canUpdate(currentUser, team);
        boolean canAddMember = currentUser.isAuthorized(Team.AUTH_ADD_MEMBERS_TO_READ_ONLY_TEAMS);

        return canUpdate || canAddMember;
    }

    //    @Override
    //    public void clearTeamsCaches(User user) {
    //        Objects.requireNonNull(cacheManager.getCache(USERS_TEAMS_BY_UID_CACHE)).evict(user.getUid());
    //        if (user.getUid() != null) {
    //            Objects.requireNonNull(cacheManager.getCache(USERS_MANAGED_TEAMS_BY_UID_CACHE)).evict(user.getUid());
    //        }
    //    }
}
