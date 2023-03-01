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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.EventUtils;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * @author Luciano Fiandesio
 */
@Component("workContextProgramStageInstancesSupplier")
@Slf4j
public class ProgramStageInstanceSupplier extends AbstractSupplier<Map<String, ProgramStageInstance>> {
    private final ObjectMapper jsonMapper;

    private final ProgramSupplier programSupplier;

    public ProgramStageInstanceSupplier(NamedParameterJdbcTemplate jdbcTemplate,
                                        @Qualifier("dataValueJsonMapper") ObjectMapper jsonMapper, ProgramSupplier programSupplier) {
        super(jdbcTemplate);
        this.jsonMapper = jsonMapper;
        this.programSupplier = programSupplier;
    }

    @Override
    public Map<String, ProgramStageInstance> get(ImportOptions importOptions, List<Event> events) {
        if (events == null) {
            return new HashMap<>();
        }

        Set<String> psiUid = events.stream().map(Event::getUid).collect(Collectors.toSet());

        if (isEmpty(psiUid)) {
            return new HashMap<>();
        }

        final String sql = "select psi.programinstanceid, psi.programstageid, psi.programstageinstanceid, " +
            "psi.uid, psi.status, psi.deleted, psi.eventdatavalues, psi.duedate, psi.executiondate, " +
            "psi.completeddate, psi.geometry, " +
            "act.activityid as act_id, act.uid as act_uid, act.code as act_code, act.name as act_name, " +
            "ou.organisationunitid as ou_id, ou.uid as ou_uid, ou.code as ou_code, ou.name as ou_name " +
            "from program_stage_instance psi join organisation_unit ou on psi.organisationunitid = ou.organisationunitid "
            +
            "join activity act on psi.activityid = act.activityid " +
            "where psi.uid in (:ids)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", psiUid);

        return jdbcTemplate.query(sql, parameters, rs -> {
            Map<String, ProgramStageInstance> results = new HashMap<>();

            while (rs.next()) {
                ProgramStageInstance psi = new ProgramStageInstance();

                psi.setId(rs.getLong("programstageinstanceid"));
                psi.setUid(rs.getString("uid"));
                psi.setStatus(EventStatus.valueOf(rs.getString("status")));
                psi.setDeleted(rs.getBoolean("deleted"));
                psi.setProgramStage(getProgramStage(importOptions, rs.getLong("programstageid")));
                psi.setOrganisationUnit(getOu(rs));
                psi.setDueDate(rs.getObject("duedate", LocalDateTime.class));
                psi.setExecutionDate(rs.getObject("executiondate", LocalDateTime.class));
                psi.setCompletedDate(rs.getObject("completeddate", LocalDateTime.class));
//                psi.setCatOptionCombo(getCatOptionCombo(rs));
                psi.setActivity(getActivity(rs));

                try {
                    psi.setEventDataValues(EventUtils.jsonToEventDataValues(jsonMapper, rs.getObject(
                        "eventdatavalues")));
                } catch (JsonProcessingException e) {
                    log.error(
                        "Invalid Data Element Values payload, skipping Program Stage Instance with id: " + psi.getId(),
                        e);
                }
                results.put(psi.getUid(), psi);
            }
            return results;
        });
    }

//    private CategoryOptionCombo getCatOptionCombo(ResultSet rs)
//        throws SQLException {
//        CategoryOptionCombo coc = new CategoryOptionCombo();
//
//        coc.setUid(rs.getString("coc_uid"));
//
//        return coc;
//    }

    private Activity getActivity(ResultSet rs)
        throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getLong("act_id"));
        activity.setUid(rs.getString("act_uid"));
        activity.setCode(rs.getString("act_code"));
        activity.setName(rs.getString("act_name"));

        return activity;
    }

    private OrganisationUnit getOu(ResultSet rs)
        throws SQLException {
        OrganisationUnit ou = new OrganisationUnit();
        ou.setId(rs.getLong("ou_id"));
        ou.setUid(rs.getString("ou_uid"));
        ou.setCode(rs.getString("ou_code"));
        ou.setName(rs.getString("ou_name"));

        return ou;
    }

    private ProgramStage getProgramStage(ImportOptions importOptions, Long programStageId) {
        Collection<Program> programs = this.programSupplier.get(importOptions, new ArrayList<>()).values();
        for (Program program : programs) {
            Set<ProgramStage> programStages = program.getProgramStages();
            for (ProgramStage programStage : programStages) {
                if (programStageId.equals(programStage.getId())) {
                    programStage.setProgram(program);
                    return programStage;
                }
            }
        }
        return null;
    }
}
