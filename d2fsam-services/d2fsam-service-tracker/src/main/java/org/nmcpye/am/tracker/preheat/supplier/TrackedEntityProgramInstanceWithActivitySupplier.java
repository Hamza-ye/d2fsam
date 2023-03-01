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
package org.nmcpye.am.tracker.preheat.supplier;

import com.google.common.collect.Lists;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStatus;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.domain.Enrollment;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.util.Constant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adds to the preheat a Map of Tracked Entities to related Program Instances
 *
 * @author Luca Cambi
 */
@Component
public class TrackedEntityProgramInstanceWithActivitySupplier extends JdbcAbstractPreheatSupplier {

    // NMCP Extend
    private static final String ACT_UID_COLUMN = "act.uid";

    private static final String ACT_UID_COLUMN_ALIAS = "actuid";
    ////

    private static final String PR_UID_COLUMN = "pr.uid";

    private static final String PR_UID_COLUMN_ALIAS = "pruid";

    private static final String PI_UID_COLUMN = "pi.uid";

    private static final String PI_UID_COLUMN_ALIAS = "piuid";

    private static final String PI_STATUS_COLUMN = "pi.status";

    private static final String PI_STATUS_COLUMN_ALIAS = "status";

    private static final String TEI_UID_COLUMN = "tei.uid";

    private static final String TEI_UID_COLUMN_ALIAS = "teiuid";

    private static final String SQL = "select  " + PR_UID_COLUMN + " as " + PR_UID_COLUMN_ALIAS + ", "
        + PI_UID_COLUMN + " as " + PI_UID_COLUMN_ALIAS + ", "
        + ACT_UID_COLUMN + " as " + ACT_UID_COLUMN_ALIAS + ", "
        + PI_STATUS_COLUMN + " as " + PI_STATUS_COLUMN_ALIAS + ", "
        + TEI_UID_COLUMN + " as " + TEI_UID_COLUMN_ALIAS +
        " from program_instance pi " +
        " join tracked_entity_instance tei on pi.trackedentityinstanceid = tei.trackedentityinstanceid " +
        " join program pr on pr.programid = pi.programid " +
        " join activity act on act.activityid = pi.activityid " +
        " where pi.deleted = false " +
        " and tei.uid in (:teuids)" +
        " and pr.uid in (:pruids)";

    protected TrackedEntityProgramInstanceWithActivitySupplier(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void preheatAdd(TrackerImportParams params, TrackerPreheat preheat) {
        List<String> trackedEntityList = params.getEnrollments().stream().map(Enrollment::getTrackedEntity)
            .collect(Collectors.toList());

        List<String> programList = preheat.getAll(Program.class).stream().map(BaseIdentifiableObject::getUid)
            .collect(Collectors.toList());

        List<List<String>> teiList = Lists.partition(new ArrayList<>(trackedEntityList),
            Constant.SPLIT_LIST_PARTITION_SIZE);

        if (programList.isEmpty() || teiList.isEmpty())
            return;

        Map<String, List<ProgramInstance>> trackedEntityToProgramInstanceMap = new HashMap<>();

        if (params.getEnrollments().isEmpty())
            return;

        for (List<String> trackedEntityListSubList : teiList) {
            queryTeiAndAddToMap(trackedEntityToProgramInstanceMap, trackedEntityListSubList, programList);
        }

        preheat.setTrackedEntityToProgramInstanceMap(trackedEntityToProgramInstanceMap);
    }

    private void queryTeiAndAddToMap(Map<String, List<ProgramInstance>> trackedEntityToProgramInstanceMap,
                                     List<String> trackedEntityListSubList, List<String> programList) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("teuids", trackedEntityListSubList);
        parameters.addValue("pruids", programList);

        jdbcTemplate.query(SQL, parameters, resultSet -> {
            String tei = resultSet.getString(TEI_UID_COLUMN_ALIAS);

            ProgramInstance newPi = new ProgramInstance();
            newPi.setUid(resultSet.getString(PI_UID_COLUMN_ALIAS));
            newPi.setStatus(ProgramStatus.valueOf(resultSet.getString(PI_STATUS_COLUMN_ALIAS)));

            Program program = new Program();
            program.setUid(resultSet.getString(PR_UID_COLUMN_ALIAS));
            newPi.setProgram(program);

            Activity activity = new Activity();
            activity.setUid(resultSet.getString(ACT_UID_COLUMN_ALIAS));
            newPi.setActivity(activity);

            List<ProgramInstance> piList = trackedEntityToProgramInstanceMap.getOrDefault(tei,
                new ArrayList<>());

            piList.add(newPi);

            trackedEntityToProgramInstanceMap.put(tei, piList);
        });
    }
}
