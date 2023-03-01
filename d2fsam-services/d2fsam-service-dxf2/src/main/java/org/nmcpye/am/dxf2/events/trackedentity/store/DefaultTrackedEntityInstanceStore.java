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
package org.nmcpye.am.dxf2.events.trackedentity.store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.dxf2.events.aggregates.AggregateContext;
import org.nmcpye.am.dxf2.events.trackedentity.Attribute;
import org.nmcpye.am.dxf2.events.trackedentity.ProgramOwner;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.OwnedTeiMapper;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.ProgramOwnerRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.TrackedEntityAttributeRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.TrackedEntityInstanceRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.query.TeiAttributeQuery;
import org.nmcpye.am.dxf2.events.trackedentity.store.query.TrackedEntityInstanceQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.nmcpye.am.organisationunit.OrganisationUnitRepositoryExt.USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME;
import static org.nmcpye.am.organisationunit.OrganisationUnitRepositoryExt.USER_ORG_UNIT_TEI_SEARCH_RELATION_NAME;
import static org.nmcpye.am.team.TeamRepositoryExt.USER_TEAM_MEMBERSHIP_RELATION_NAME;

/**
 * @author Luciano Fiandesio
 * @author Ameen Mohamed
 */
@Repository
public class DefaultTrackedEntityInstanceStore
    extends AbstractStore
    implements TrackedEntityInstanceStore {

    private static final String GET_TEIS_SQL = TrackedEntityInstanceQuery.getQuery();

    private static final String GET_TEI_ATTRIBUTES = TeiAttributeQuery.getQuery();

    private static final String GET_PROGRAM_OWNERS =
        "select tei.uid as key, p.uid as prguid, o.uid as ouuid " +
            "from tracked_entity_program_owner teop " +
            "join program p on teop.programid = p.programid " +
            "join organisation_unit o on teop.organisationunitid = o.organisationunitid " +
            "join tracked_entity_instance tei on teop.trackedentityinstanceid = TEI.trackedentityinstanceid " +
            "where teop.trackedentityinstanceid in (:ids)";

    private static final String GET_OWNERSHIP_DATA_FOR_TEIS_FOR_ALL_PROGRAM =
        "SELECT tei.uid as tei_uid, tpo.trackedentityinstanceid, tpo.programid, tpo.organisationunitid, p.accesslevel,p.uid as pgm_uid " +
            "FROM tracked_entity_program_owner TPO " +
            "LEFT JOIN program P on p.programid = tpo.programid " +
            "LEFT JOIN organisation_unit OU on ou.organisationunitid = tpo.organisationunitid " +
            "LEFT JOIN tracked_entity_instance TEI on TEI.trackedentityinstanceid =  tpo.trackedentityinstanceid " +
            "WHERE TPO.trackedentityinstanceid in (:ids) " +
            "AND p.programid in (SELECT programid FROM program) " +
            "GROUP BY tei.uid,tpo.trackedentityinstanceid, tpo.programid, tpo.organisationunitid, ou.path, p.accesslevel,p.uid " +
            "HAVING (P.accesslevel in ('OPEN', 'AUDITED') " +
            "AND (EXISTS(SELECT SS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_TEI_SEARCH_RELATION_NAME +
            " SS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = SS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')) " +
            "OR EXISTS(SELECT CS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
            " CS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = CS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')))) " +
            "OR (P.accesslevel in ('CLOSED', 'PROTECTED') " +
            "AND EXISTS(SELECT CS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
            " CS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = CS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%'))) " +
            "OR (P.accesslevel in ('OPEN', 'AUDITED') AND EXISTS (SELECT mem.teamid " +
            "FROM " +
            USER_TEAM_MEMBERSHIP_RELATION_NAME +
            " mem " +
            "LEFT JOIN app_user au on au.userid = mem.member_id " +
            "LEFT JOIN team tm ON tm.teamid = mem.teamid " +
            "LEFT JOIN assignment assi ON assi.assignedteamid = tm.teamid " +

            "LEFT JOIN activity act ON act.activityid = tm.activityid " +
            "LEFT JOIN project pro ON pro.projectid = act.projectid " +

            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = assi.organisationunitid " +
            "WHERE member_id = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%') " +
//        "AND pro.projectid IN (:projectIds) " +
            "AND tm.inactive = false));";

//    private static final String GET_OWNERSHIP_DATA_FOR_TEIS_FOR_ALL_PROGRAM =
//        "SELECT tei.uid as tei_uid, tpo.trackedentityinstanceid, " +
//            "tpo.programid, tpo.organisationunitid, p.accesslevel, p.uid as pgm_uid " +
//            "FROM tracked_entity_program_owner TPO " +
//            "LEFT JOIN program P on p.programid = TPO.programid " +
//            "LEFT JOIN organisation_unit OU on ou.organisationunitid = TPO.organisationunitid " +
//            "LEFT JOIN tracked_entity_instance TEI on TEI.trackedentityinstanceid = tpo.trackedentityinstanceid " +
//            "WHERE  tpo.trackedentityinstanceid in (:ids) " +
//            "AND p.programid in (SELECT programid FROM program) " +
//            "GROUP BY tei.uid,tpo.trackedentityinstanceid, tpo.programid, " +
//            "tpo.organisationunitid, ou.path, p.accesslevel, p.uid " +
//            "HAVING (P.accesslevel in ('OPEN', 'AUDITED') " +
//            "AND (EXISTS(" +
//            "SELECT SS.organisationunitid " +
//            "FROM " +
//            USER_ORG_UNIT_TEI_SEARCH_RELATION_NAME +
//            " SS " +
//            "LEFT JOIN organisation_unit OU2 ON OU2.id = SS.organisationunitid " +
//            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')) " +
//            "OR EXISTS(SELECT CS.organisationunitid " +
//            "FROM " +
//            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
//            " CS " +
//            "LEFT JOIN organisation_unit OU2 ON OU2.id = CS.organisationunitid " +
//            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')))) " +
//            "OR (P.accesslevel in ('CLOSED', 'PROTECTED') " +
//            "AND EXISTS(SELECT CS.organisationunitid " +
//            "FROM " +
//            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
//            " CS " +
//            "LEFT JOIN organisation_unit OU2 ON OU2.id = CS.organisationunitid " +
//            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%'))) " +
//            "OR (P.accesslevel in ('OPEN', 'AUDITED') AND EXISTS (SELECT mem.teamid " +
//            "FROM " +
//            USER_TEAM_MEMBERSHIP_RELATION_NAME +
//            " mem " +
//            "Left join app_user au on au.id = mem.member_id " +
//            "LEFT JOIN team tm ON tm.id = mem.teamid " +
//            "LEFT JOIN assignment assi ON assi.assignedteamid = tm.id " +
//
//            "LEFT JOIN activity act ON act.id = tm.activityid " +
//            "LEFT JOIN project pro ON pro.projectid = act.projectid " +
//
//            "LEFT JOIN organisation_unit OU2 ON OU2.id = assi.organisationunitid " +
//            "WHERE member_id = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%') " +
////        "AND pro.projectid IN (:projectIds) " +
//            "AND tm.inactive = false));";

    private static final String GET_OWNERSHIP_DATA_FOR_TEIS_FOR_SPECIFIC_PROGRAM =
        "SELECT tei.uid as tei_uid, tpo.trackedentityinstanceid, tpo.programid, tpo.organisationunitid, p.accesslevel,p.uid as pgm_uid " +
            "FROM tracked_entity_program_owner TPO " +
            "LEFT JOIN program P on p.programid = tpo.programid " +
            "LEFT JOIN organisation_unit OU on ou.organisationunitid = tpo.organisationunitid " +
            "LEFT JOIN tracked_entity_instance TEI on TEI.trackedentityinstanceid =  tpo.trackedentityinstanceid " +
            "WHERE TPO.trackedentityinstanceid in (:ids) " +
            "AND p.uid = :programUid " +
            "GROUP BY tei.uid,tpo.trackedentityinstanceid, tpo.programid, tpo.organisationunitid, ou.path, p.accesslevel,p.uid " +
            "HAVING (P.accesslevel in ('OPEN', 'AUDITED') " +
            "AND (EXISTS(SELECT SS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_TEI_SEARCH_RELATION_NAME +
            " SS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = SS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')) " +
            "OR EXISTS(SELECT CS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
            " CS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = CS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%')))) " +
            "OR (P.accesslevel in ('CLOSED', 'PROTECTED') " +
            "AND EXISTS(SELECT CS.organisationunitid " +
            "FROM " +
            USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME +
            " CS " +
            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = CS.organisationunitid " +
            "WHERE userid = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%'))) " +
            "OR (P.accesslevel in ('OPEN', 'AUDITED') " +
            "AND EXISTS (SELECT mem.teamid " +
            "FROM " +
            USER_TEAM_MEMBERSHIP_RELATION_NAME +
            " mem " +
            "LEFT join app_user au on au.userid = mem.member_id " +
            "LEFT JOIN team tm ON tm.teamid = mem.teamid " +
            "LEFT JOIN assignment assi ON assi.assignedteamid = tm.teamid " +

            "LEFT JOIN activity act ON act.activityid = tm.activityid " +
            "LEFT JOIN project pro ON pro.projectid = act.projectid " +

            "LEFT JOIN organisation_unit OU2 ON OU2.organisationunitid = assi.organisationunitid " +
            "WHERE member_id = :userInfoId AND OU.path LIKE CONCAT(OU2.path, '%') " +
//        "AND pro.projectid IN (:projectIds) " +
            "AND tm.inactive = false));";


    private static final String FILTER_OUT_DELETED_TEIS = "tei.deleted=false";

    public DefaultTrackedEntityInstanceStore(@Qualifier("readOnlyJdbcTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    String getRelationshipEntityColumn() {
        return "trackedentityinstanceid";
    }

    @Override
    public Map<String, TrackedEntityInstance> getTrackedEntityInstances(List<Long> ids, AggregateContext ctx) {
        List<List<Long>> idPartitions = Lists.partition(ids, PARITITION_SIZE);

        Map<String, TrackedEntityInstance> trackedEntityMap = new LinkedHashMap<>();

        idPartitions.forEach(partition -> trackedEntityMap.putAll(getTrackedEntityInstancesPartitioned(partition, ctx)));
        return trackedEntityMap;
    }

    private Map<String, TrackedEntityInstance> getTrackedEntityInstancesPartitioned(List<Long> ids, AggregateContext ctx) {
        TrackedEntityInstanceRowCallbackHandler handler = new TrackedEntityInstanceRowCallbackHandler();

        if (!ctx.isSuperUser() && ctx.getTrackedEntityTypes().isEmpty()) {
            // If not super user and no tets are accessible. then simply return
            // empty list.
            return new HashMap<>();
        }

        String sql = getQuery(GET_TEIS_SQL, ctx, "tei.trackedentitytypeid in (:teiTypeIds)", FILTER_OUT_DELETED_TEIS);
        jdbcTemplate.query(
            applySortOrder(sql, StringUtils.join(ids, ","), "trackedentityinstanceid"),
            createIdsParam(ids).addValue("teiTypeIds", ctx.getTrackedEntityTypes()),
            handler
        );

        return handler.getItems();
    }

    @Override
    public Multimap<String, Attribute> getAttributes(List<Long> ids) {
        return fetch(GET_TEI_ATTRIBUTES, new TrackedEntityAttributeRowCallbackHandler(), ids);
    }

    public Multimap<String, ProgramOwner> getProgramOwners(List<Long> ids) {
        return fetch(GET_PROGRAM_OWNERS, new ProgramOwnerRowCallbackHandler(), ids);
    }

    @Override
    public Multimap<String, String> getOwnedTeis(List<Long> ids, AggregateContext ctx) {
        List<List<Long>> teiIds = Lists.partition(ids, PARITITION_SIZE);

        Multimap<String, String> ownedTeisMultiMap = ArrayListMultimap.create();

        teiIds.forEach(partition -> {
            ownedTeisMultiMap.putAll(getOwnedTeisPartitioned(partition, ctx));
        });

        return ownedTeisMultiMap;
    }

    private Multimap<String, String> getOwnedTeisPartitioned(List<Long> ids, AggregateContext ctx) {
        OwnedTeiMapper handler = new OwnedTeiMapper();

        MapSqlParameterSource paramSource = createIdsParam(ids)
            .addValue("userInfoId", ctx.getUserId());

        boolean checkForOwnership = ctx.getQueryParams().isIncludeAllAttributes()
            || ctx.getParams().isIncludeEnrollments() || ctx.getParams().isIncludeEvents();

        String sql;

        if (ctx.getQueryParams().hasProgram()) {
            sql = GET_OWNERSHIP_DATA_FOR_TEIS_FOR_SPECIFIC_PROGRAM;
            paramSource.addValue("programUid", ctx.getQueryParams().getProgram().getUid());
        } else if (checkForOwnership) {
            sql = GET_OWNERSHIP_DATA_FOR_TEIS_FOR_ALL_PROGRAM;
        } else {
            return ArrayListMultimap.create();
        }

        jdbcTemplate.query(sql, paramSource, handler);

        var items = handler.getItems();
        ;

        return handler.getItems();
    }
}
