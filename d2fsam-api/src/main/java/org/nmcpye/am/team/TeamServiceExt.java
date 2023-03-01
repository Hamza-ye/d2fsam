package org.nmcpye.am.team;

import org.nmcpye.am.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Service Interface for managing {@link Team}.
 */
public interface TeamServiceExt {
    String USERS_TEAMS_BY_UID_CACHE = "teamsByUserUid";

    String USERS_MANAGED_TEAMS_BY_UID_CACHE = "managedTeamsByUserUid";

    /**
     * Indicates whether the current user can add or remove members for the user
     * group with the given UID. To to so the current user must have write
     * access to the group or have read access as well as the
     * F_USER_GROUPS_READ_ONLY_ADD_MEMBERS authority.
     *
     * @param uid the user group UID.
     * @return true if the current user can add or remove members of the user
     * group.
     */
    boolean canAddOrRemoveMember(String uid);

    boolean canAddOrRemoveMember(String uid, User currentUser);

    Long addTeam(Team team);

    void updateTeam(Team team);

    Long addTeamGroup(TeamGroup teamGroup);

    Team getTeam(String uid);

    List<Team> getTeams(Collection<String> uids);

    List<Team> getTeamsBetweenByName(String name, int first, int max);

    String getDisplayName(String uid);
}
