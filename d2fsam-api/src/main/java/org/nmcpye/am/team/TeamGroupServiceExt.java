package org.nmcpye.am.team;

import java.util.List;

/**
 * Service Interface for managing {@link TeamGroup}.
 */
public interface TeamGroupServiceExt {
    TeamGroup getByUid(String uid);

    List<TeamGroup> getUserTeamGroup(String uid);

    List<TeamGroup> getTeamGroupsBetweenByName(String name, int first, int max);

    String getDisplayName(String uid);
}
