package org.nmcpye.am.team;

import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("org.nmcpye.am.team.TeamGroupServiceExt")
public class TeamGroupServiceExtImpl implements TeamGroupServiceExt {

    private final TeamGroupRepositoryExt teamGroupRepositoryExt;

    private final UserServiceExt userServiceExt;

    private final Cache<String> teamGroupNameCache;

    public TeamGroupServiceExtImpl(TeamGroupRepositoryExt teamGroupRepositoryExt,
                                   UserServiceExt userServiceExt, CacheProvider cacheProvider) {
        this.teamGroupRepositoryExt = teamGroupRepositoryExt;
        this.userServiceExt = userServiceExt;
        teamGroupNameCache = cacheProvider.createTeamGroupDisplayNameCache();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamGroup getByUid(String uid) {
        return teamGroupRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamGroup> getUserTeamGroup(String uid) {
        User user = userServiceExt.getUser(uid);
        Set<Team> userTeams = new HashSet<>();
        if (user != null) {
            userTeams = user.getTeams();
        }
        return teamGroupRepositoryExt.getByMembers(userTeams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamGroup> getTeamGroupsBetweenByName(String name, int first, int max) {
        return teamGroupRepositoryExt.getAllLikeName(name, first, max, false);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDisplayName(String uid) {
        return teamGroupNameCache.get(uid, n -> teamGroupRepositoryExt.getByUidNoAcl(uid).getDisplayName());
    }
}
