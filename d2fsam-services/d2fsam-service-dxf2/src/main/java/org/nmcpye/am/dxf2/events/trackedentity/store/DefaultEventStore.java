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
import org.nmcpye.am.dxf2.events.aggregates.AggregateContext;
import org.nmcpye.am.dxf2.events.event.DataValue;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.Note;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.EventDataValueRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.EventRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.mapper.NoteRowCallbackHandler;
import org.nmcpye.am.dxf2.events.trackedentity.store.query.EventQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luciano Fiandesio
 */
@Repository
public class DefaultEventStore extends AbstractStore implements EventStore {

    private static final String GET_EVENTS_SQL = EventQuery.getQuery();

    private static final String GET_DATAVALUES_SQL =
        "SELECT psi.uid AS key, " + "psi.eventdatavalues " + "FROM program_stage_instance psi " + "WHERE psi.programstageinstanceid IN (:ids)";

    private static final String GET_NOTES_SQL =
        "SELECT pi.uid as key, tec.uid, tec.commenttext, " +
            "tec.creator, tec.created " +
            "FROM comment tec " +
            "JOIN program_stage_instance__comments psic " +
            "ON tec.trackedentitycommentid = psic.trackedentitycommentid " +
            "JOIN program_instance pi ON psic.programstageinstanceid = pi.programinstanceid " +
            "WHERE psic.programstageinstanceid IN (:ids)";

    private static final String ACL_FILTER_SQL =
        "CASE WHEN p.type = 'WITH_REGISTRATION' THEN " +
            "p.trackedentitytypeid in (:trackedEntityTypeIds) else true END " +
            "AND psi.programstageid in (:programStageIds) AND pi.programid IN (:programIds)";

    private static final String ACL_FILTER_SQL_NO_PROGRAM_STAGE =
        "CASE WHEN p.type = 'WITH_REGISTRATION' THEN " +
            "p.trackedentitytypeid in (:trackedEntityTypeIds) else true END " +
            "AND pi.programid IN (:programIds)";

    private static final String FILTER_OUT_DELETED_EVENTS = "psi.deleted=false";

    public DefaultEventStore(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    String getRelationshipEntityColumn() {
        return "programstageinstanceid";
    }

    @Override
    public Multimap<String, Event> getEventsByEnrollmentIds(List<Long> enrollmentsId, AggregateContext ctx) {
        List<List<Long>> enrollmentIdsPartitions = Lists.partition(enrollmentsId, PARITITION_SIZE);

        Multimap<String, Event> eventMultimap = ArrayListMultimap.create();

        enrollmentIdsPartitions.forEach(partition -> eventMultimap.putAll(getEventsByEnrollmentIdsPartitioned(partition, ctx)));

        return eventMultimap;
    }

    //    private String getAttributeOptionComboClause(AggregateContext ctx) {
    //        return (
    //            " and psi.attributeoptioncomboid not in (" +
    //            "select distinct(cocco.categoryoptioncomboid) " +
    //            "from categoryoptioncombos_categoryoptions as cocco " +
    //            // Get inaccessible category options
    //            "where cocco.categoryoptionid not in ( " +
    //            "select co.categoryoptionid " +
    //            "from dataelementcategoryoption co  " +
    //            " where " +
    //            JpaQueryUtils.generateSQlQueryForSharingCheck("co.sharing", ctx.getUserUid(), ctx.getUserGroups(), AclService.LIKE_READ_DATA) +
    //            ") )"
    //        );
    //    }

    private Multimap<String, Event> getEventsByEnrollmentIdsPartitioned(List<Long> enrollmentsId, AggregateContext ctx) {
        EventRowCallbackHandler handler = new EventRowCallbackHandler();

        List<Long> programStages = ctx.getProgramStages();

        //        String aocSql = ctx.isSuperUser() ? "" : getAttributeOptionComboClause(ctx);

        if (programStages.isEmpty()) {
            jdbcTemplate.query(
                getQuery(GET_EVENTS_SQL, ctx, ACL_FILTER_SQL_NO_PROGRAM_STAGE/* + aocSql */, FILTER_OUT_DELETED_EVENTS),
                createIdsParam(enrollmentsId)
                    .addValue("trackedEntityTypeIds", ctx.getTrackedEntityTypes())
                    .addValue("programIds", ctx.getPrograms()),
                handler
            );
        } else {
            jdbcTemplate.query(
                getQuery(GET_EVENTS_SQL, ctx, ACL_FILTER_SQL/* + aocSql */, FILTER_OUT_DELETED_EVENTS),
                createIdsParam(enrollmentsId)
                    .addValue("trackedEntityTypeIds", ctx.getTrackedEntityTypes())
                    .addValue("programStageIds", programStages)
                    .addValue("programIds", ctx.getPrograms()),
                handler
            );
        }

        return handler.getItems();
    }

    @Override
    public Map<String, List<DataValue>> getDataValues(List<Long> programStageInstanceId) {
        List<List<Long>> psiIdsPartitions = Lists.partition(programStageInstanceId, PARITITION_SIZE);

        Map<String, List<DataValue>> dataValueListMultimap = new HashMap<>();

        psiIdsPartitions.forEach(partition -> dataValueListMultimap.putAll(getDataValuesPartitioned(partition)));

        return dataValueListMultimap;
    }

    private Map<String, List<DataValue>> getDataValuesPartitioned(List<Long> programStageInstanceId) {
        EventDataValueRowCallbackHandler handler = new EventDataValueRowCallbackHandler();

        jdbcTemplate.query(GET_DATAVALUES_SQL, createIdsParam(programStageInstanceId), handler);

        return handler.getItems();
    }

    @Override
    public Multimap<String, Note> getNotes(List<Long> eventIds) {
        return fetch(GET_NOTES_SQL, new NoteRowCallbackHandler(), eventIds);
    }
}
