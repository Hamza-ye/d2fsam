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
package org.nmcpye.am.dxf2.events.importer.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * NMCP Extended
 *
 * @author Luciano Fiandesio
 */
@Component("workContextProgramInstancesSupplier")
public class ProgramInstanceSupplier extends AbstractSupplier<Map<String, ProgramInstance>> {
    private final ProgramSupplier programSupplier;

    private final ActivitySupplier activitySupplier;

    public ProgramInstanceSupplier(NamedParameterJdbcTemplate jdbcTemplate, ProgramSupplier programSupplier,
                                   ActivitySupplier activitySupplier) {
        super(jdbcTemplate);
        this.programSupplier = programSupplier;
        this.activitySupplier = activitySupplier;
    }

    public Map<String, ProgramInstance> get(ImportOptions importOptions,
                                            Map<String, Pair<TrackedEntityInstance, Boolean>> teiMap, List<Event> events) {
        if (events == null) {
            return new HashMap<>();
        }

        // Collect all the program instance UIDs to pass as SQL query argument
        Set<String> programInstanceUids = events.stream()
            .map(Event::getEnrollment)
            .filter(StringUtils::isNotEmpty).collect(Collectors.toSet());

        Map<String, ProgramInstance> programInstances = new HashMap<>();

        if (!programInstanceUids.isEmpty()) {
            // Create a bi-directional map enrollment uid -> event uid
            Multimap<String, String> programInstanceToEvent = HashMultimap.create();
            for (Event event : events) {
                programInstanceToEvent.put(event.getEnrollment(), event.getUid());
            }

            // Collect all the Program Stage Instances specified in the Events
            // (enrollment
            // property)
            programInstances = getProgramInstancesByUid(importOptions, events, programInstanceToEvent,
                programInstanceUids);
        }

        mapExistingEventsToProgramInstances(importOptions, events, programInstances);

        mapEventsToProgramInstanceByTei(importOptions, events, programInstances, teiMap);

        return programInstances;
    }

    /**
     * Loop through the events and check if there is any event left without a
     * Program Instance: for each Event without a PI, try to fetch the Program
     * Instance by Program and Tracked Entity Instance
     */
    private void mapEventsToProgramInstanceByTei(ImportOptions importOptions, List<Event> events,
                                                 Map<String, ProgramInstance> programInstances, Map<String, Pair<TrackedEntityInstance, Boolean>> teiMap) {
        for (Event event : events) {
            if (!programInstances.containsKey(event.getUid())) {
                Pair<TrackedEntityInstance, Boolean> teiPair = teiMap.get(event.getUid());
                Program program = getProgramByUid(event.getProgram(),
                    programSupplier.get(importOptions, events).values());
                if (teiPair != null && program != null) {
                    TrackedEntityInstance tei = teiPair.getKey();

                    ProgramInstance programInstance = getByTeiAndProgram(importOptions, tei.getId(), program.getId(),
                        event);
                    if (programInstance != null) {
                        programInstances.put(event.getUid(), programInstance);
                    }
                }
            }
        }
    }

    /**
     * This method is only used if the Event already exist in the db (update) If
     * the Event does not have the "enrollment" property set OR enrollment is
     * pointing to an invalid UID, use the Program Instance already connected to
     * the Event.
     */
    private void mapExistingEventsToProgramInstances(ImportOptions importOptions, List<Event> events,
                                                     Map<String, ProgramInstance> programInstances) {
        // Collect all the Program Instances by event uid
        final Map<String, ProgramInstance> programInstancesByEvent = getProgramInstanceByEvent(importOptions, events);

        if (!programInstancesByEvent.isEmpty()) {
            for (Event event : events) {
                if (!programInstances.containsKey(event.getUid())) {
                    if (programInstancesByEvent.containsKey(event.getUid())) {
                        programInstances.put(event.getUid(), programInstancesByEvent.get(event.getUid()));
                    }
                }
            }
        }
    }

    private Program getProgramById(long id, Collection<Program> programs) {
        return programs.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private Program getProgramByUid(String uid, Collection<Program> programs) {
        return programs.stream().filter(p -> p.getUid().equals(uid)).findFirst().orElse(null);
    }

    // NMCP
    private Activity getActivityById(long id, Collection<Activity> activities) {
        return activities.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private Activity getActivityByUid(String uid, Collection<Activity> activities) {
        return activities.stream().filter(p -> p.getUid().equals(uid)).findFirst().orElse(null);
    }

    /////
    private ProgramInstance getByTeiAndProgram(ImportOptions importOptions, Long teiId, Long programId, Event event) {
        final String sql = "select pi.programinstanceid, pi.programid, pi.uid , act.activityid as act_id, t.trackedentityinstanceid as tei_id, t.uid as tei_uid, "
            +
            "ou.uid as tei_ou_uid, ou.path as tei_ou_path from program_instance pi " +
            "join tracked_entity_instance t on t.trackedentityinstanceid = pi.trackedentityinstanceid " +
            "join organisation_unit ou on t.organisationunitid = ou.organisationunitid " +
            "join activity act on pi.activityid = act.activityid " +
            "where pi.programid = :programid " +
            "and pi.status = 'ACTIVE' " +
            "and pi.trackedentityinstanceid = :teiid";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("programid", programId);
        parameters.addValue("teiid", teiId);

        List<ProgramInstance> query = jdbcTemplate.query(sql, parameters, (ResultSet rs) -> {

            List<ProgramInstance> results = new ArrayList<>();

            while (rs.next()) {
                results.add(mapFromResultset(rs, importOptions, Collections.singletonList(event)));
            }
            return results;
        });

        if (query != null && query.size() == 1) {
            return query.get(0);
        } else {
            return null;
        }
    }

    private Map<String, ProgramInstance> getProgramInstanceByEvent(ImportOptions importOptions, List<Event> events) {
        final Set<String> eventUids = events.stream().map(Event::getUid).collect(Collectors.toSet());
        if (isEmpty(eventUids)) {
            return new HashMap<>();
        }

        final String sql = "select psi.uid as psi_uid, pi.programinstanceid, pi.programid, pi.uid , act.activityid as act_id, t.trackedentityinstanceid as tei_id, t.uid as tei_uid, "
            + "ou.uid as tei_ou_uid, ou.path as tei_ou_path "
            + "from program_instance pi "
            + "left outer join tracked_entity_instance t on pi.trackedentityinstanceid = t.trackedentityinstanceid "
            + "left join organisation_unit ou on t.organisationunitid = ou.organisationunitid "
            + "left join activity act on pi.activityid = act.activityid "
            + "join program_stage_instance psi on pi.programinstanceid = psi.programinstanceid "
            + "where psi.uid in (:ids)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", eventUids);

        return jdbcTemplate.query(sql, parameters, (ResultSet rs) -> {
            Map<String, ProgramInstance> results = new HashMap<>();

            while (rs.next()) {
                results.put(rs.getString("psi_uid"), mapFromResultset(rs, importOptions, events));
            }
            return results;
        });
    }

    private Map<String, ProgramInstance> getProgramInstancesByUid(ImportOptions importOptions, List<Event> events,
                                                                  Multimap<String, String> programInstanceToEvent, Set<String> uids) {

        final String sql = "select pi.programinstanceid, pi.programid, pi.uid, act.activityid as act_id, t.trackedentityinstanceid as tei_id, t.uid as tei_uid, "
            + "ou.uid as tei_ou_uid, ou.path as tei_ou_path "
            + "from program_instance pi join tracked_entity_instance t on pi.trackedentityinstanceid = t.trackedentityinstanceid "
            + "join activity act on pi.activityid = act.activityid "
            + "join organisation_unit ou on t.organisationunitid = ou.organisationunitid where pi.uid in (:ids)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", uids);

        return jdbcTemplate.query(sql, parameters, (ResultSet rs) -> {
            Map<String, ProgramInstance> results = new HashMap<>();

            while (rs.next()) {
                ProgramInstance pi = mapFromResultset(rs, importOptions, events);

                for (String event : programInstanceToEvent.get(pi.getUid())) {
                    results.put(event, pi);
                }
            }
            return results;
        });

    }

    private ProgramInstance mapFromResultset(ResultSet rs, ImportOptions importOptions, List<Event> events)
        throws SQLException {
        ProgramInstance pi = new ProgramInstance();
        pi.setId(rs.getLong("programinstanceid"));
        pi.setUid(rs.getString("uid"));
        pi.setProgram(
            getProgramById(rs.getLong("programid"), programSupplier.get(importOptions, events).values()));

        pi.setActivity(
            getActivityById(rs.getLong("act_id"), activitySupplier.get(importOptions, events).values()));

        String teiUid = rs.getString("tei_uid");

        if (teiUid != null) {
            TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
            trackedEntityInstance.setId(rs.getLong("tei_id"));
            String teiOuUid = rs.getString("tei_ou_uid");
            if (teiOuUid != null) {
                OrganisationUnit organisationUnit = new OrganisationUnit();
                organisationUnit.setUid(teiOuUid);
                organisationUnit
                    .setParent(SupplierUtils.getParentHierarchy(organisationUnit, rs.getString("tei_ou_path")));
                trackedEntityInstance.setOrganisationUnit(organisationUnit);
            }
            trackedEntityInstance.setUid(teiUid);

            pi.setEntityInstance(trackedEntityInstance);
        }

        return pi;
    }

    @Override
    public Map<String, ProgramInstance> get(ImportOptions importOptions, List<Event> events) {
        throw new NotImplementedException("Use other get method");
    }
}
