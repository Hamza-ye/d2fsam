/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.dxf2.events.trackedentity;

import org.nmcpye.am.commons.collection.CollectionUtils;
import org.nmcpye.am.hibernate.jsonb.type.JsonbFunctions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Luciano Fiandesio
 */
@Repository
public class DefaultAclStore implements AclStore {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    ///////////////// NMCP Extend
    private static final String TEAM_SQL_PARAM_NAME = "teamIds";

    private static final String TEAM_GROUP_SQL_PARAM_NAME = "teamGroupUIDs";

//    private static final String TEAM_ACCESS_CONDITION = "sharing->'teams'->:" + TEAM_SQL_PARAM_NAME + "->>'access' LIKE '__r%'";

    private static final String USERTEAMACCESS_CONDITION =
        JsonbFunctions.HAS_TEAMS_IDS +
            "( sharing, :" +
            TEAM_SQL_PARAM_NAME +
            ") = true " +
            "and " +
            JsonbFunctions.CHECK_TEAMS_ACCESS +
            "(sharing, '__r%', :" +
            TEAM_SQL_PARAM_NAME +
            ") = true";

    private static final String TEAM_GROUP_ACCESS_CONDITION =
        JsonbFunctions.HAS_TEAM_GROUP_IDS +
            "( sharing, :" +
            TEAM_GROUP_SQL_PARAM_NAME +
            ") = true " +
            "and " +
            JsonbFunctions.CHECK_TEAM_GROUPS_ACCESS +
            "(sharing, '__r%', :" +
            TEAM_GROUP_SQL_PARAM_NAME +
            ") = true";
    ///////////////////////////////////////////////

    private static final String USER_SQL_PARAM_NAME = "userId";

    private static final String USER_GROUP_SQL_PARAM_NAME = "userGroupUIDs";

    private static final String PUBLIC_ACCESS_CONDITION = "sharing->>'public' LIKE '__r%' OR sharing->>'public' IS NULL";

    private static final String USERACCESS_CONDITION = "sharing->'users'->:" + USER_SQL_PARAM_NAME + "->>'access' LIKE '__r%'";

    private static final String USERGROUPACCESS_CONDITION =
        JsonbFunctions.HAS_USER_GROUP_IDS +
            "( sharing, :" +
            USER_GROUP_SQL_PARAM_NAME +
            ") = true " +
            "and " +
            JsonbFunctions.CHECK_USER_GROUPS_ACCESS +
            "(sharing, '__r%', :" +
            USER_GROUP_SQL_PARAM_NAME +
            ") = true";

    private static final String GET_TEI_TYPE_ACL =
        "SELECT trackedentitytypeid FROM tracked_entity_type " +
            " WHERE " +
            PUBLIC_ACCESS_CONDITION +
            " OR " +
            USERACCESS_CONDITION /*+
        " OR " +
        TEAM_ACCESS_CONDITION*/;

    static final String GET_PROGRAM_ACL =
        "SELECT p.programid FROM program p " +
            " WHERE " +
            PUBLIC_ACCESS_CONDITION +
            " OR " +
            USERACCESS_CONDITION /*+
        " OR " +
        TEAM_ACCESS_CONDITION*/;

    static final String GET_PROGRAMSTAGE_ACL =
        "SELECT ps.programstageid FROM program_stage ps " +
            " WHERE " +
            PUBLIC_ACCESS_CONDITION +
            " OR " +
            USERACCESS_CONDITION /*+
        " OR " +
        TEAM_ACCESS_CONDITION*/;

    private static final String GET_RELATIONSHIPTYPE_ACL =
        "SELECT rs.relationshiptypeid " +
            "FROM relationship_type rs" +
            " WHERE " +
            PUBLIC_ACCESS_CONDITION +
            " OR " +
            USERACCESS_CONDITION /*+
        " OR " +
        TEAM_ACCESS_CONDITION*/;

    //////////////////////////////////////////////////
    public DefaultAclStore(@Qualifier("readOnlyJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public List<Long> getAccessibleTrackedEntityInstanceTypes(String userUID, List<String> userGroupUIDs) {
        return executeAclQuery(userUID, userGroupUIDs,
            GET_TEI_TYPE_ACL, "trackedentitytypeid");
    }

    @Override
    public List<Long> getAccessibleTrackedEntityInstanceTypes(String userUID, List<String> userGroupUIDs,
                                                              List<String> userTeamUIDs,
                                                              List<String> userTeamGroupsUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs,
            GET_TEI_TYPE_ACL, "trackedentitytypeid");
    }

    @Override
    public List<Long> getAccessiblePrograms(String userUID, List<String> userGroupUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, GET_PROGRAM_ACL, "programid");
    }

    @Override
    public List<Long> getAccessiblePrograms(String userUID, List<String> userGroupUIDs, List<String> userTeamUIDs,
                                            List<String> userTeamGroupsUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs,
            GET_PROGRAM_ACL, "programid");
    }

    @Override
    public List<Long> getAccessibleProgramStages(String userUID, List<String> userGroupUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, GET_PROGRAMSTAGE_ACL, "programstageid");
    }

    @Override
    public List<Long> getAccessibleProgramStages(String userUID, List<String> userGroupUIDs, List<String> userTeamUIDs,
                                                 List<String> userTeamGroupsUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs,
            GET_PROGRAMSTAGE_ACL, "programstageid");
    }

    @Override
    public List<Long> getAccessibleRelationshipTypes(String userUID, List<String> userGroupUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, GET_RELATIONSHIPTYPE_ACL, "relationshiptypeid");
    }

    @Override
    public List<Long> getAccessibleRelationshipTypes(String userUID, List<String> userGroupUIDs, List<String> userTeamUIDs,
                                                     List<String> userTeamGroupsUIDs) {
        return executeAclQuery(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs,
            GET_RELATIONSHIPTYPE_ACL, "relationshiptypeid");
    }

    private List<Long> executeAclQuery(String userUID, List<String> userGroupUIDs, String sql, String primaryKey) {
        MapSqlParameterSource parameterMap = new MapSqlParameterSource();
        parameterMap.addValue(USER_SQL_PARAM_NAME, userUID);

        if (!CollectionUtils.isEmpty(userGroupUIDs)) {
            sql += " OR " + USERGROUPACCESS_CONDITION;
            parameterMap.addValue(USER_GROUP_SQL_PARAM_NAME, "{" + String.join(",", userGroupUIDs) + "}");
        }

        return jdbcTemplate.query(sql, parameterMap, (rs, i) -> rs.getLong(primaryKey));
    }

    private List<Long> executeAclQuery(
        String userUID,
        List<String> userGroupUIDs,
        List<String> userTeamUIDs,
        List<String> userTeamGroupsUIDs,
        String sql,
        String primaryKey
    ) {
        MapSqlParameterSource parameterMap = new MapSqlParameterSource();
        parameterMap.addValue(USER_SQL_PARAM_NAME, userUID);

        if (!CollectionUtils.isEmpty(userGroupUIDs)) {
            sql += " OR " + USERGROUPACCESS_CONDITION;
            parameterMap.addValue(USER_GROUP_SQL_PARAM_NAME, "{" + String.join(",", userGroupUIDs) + "}");
        }

        if (!CollectionUtils.isEmpty(userTeamUIDs)) {
            sql += " OR " + USERTEAMACCESS_CONDITION;
            parameterMap.addValue(TEAM_SQL_PARAM_NAME, "{" + String.join(",", userTeamUIDs) + "}");
        }

        if (!CollectionUtils.isEmpty(userTeamGroupsUIDs)) {
            sql += " OR " + TEAM_GROUP_ACCESS_CONDITION;
            parameterMap.addValue(TEAM_GROUP_SQL_PARAM_NAME, "{" + String.join(",", userTeamGroupsUIDs) + "}");
        }

        return jdbcTemplate.query(sql, parameterMap, (rs, i) -> rs.getLong(primaryKey));
    }
}
