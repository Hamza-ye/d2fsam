/*
 * Copyright (c) 2004-2021, University of Oslo
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
package org.nmcpye.am.dxf2.events.event;

import org.apache.commons.lang3.tuple.Pair;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.dxf2.events.report.EventRow;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerIds;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerServiceExt;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceContextBuilder {

    private final TrackedEntityInstanceServiceExt entityInstanceService;

    private final ProgramServiceExt programService;

    private final TrackedEntityProgramOwnerServiceExt trackedEntityProgramOwnerService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    public EventServiceContextBuilder(
        TrackedEntityInstanceServiceExt entityInstanceService,
        ProgramServiceExt programService,
        TrackedEntityProgramOwnerServiceExt trackedEntityProgramOwnerService,
        OrganisationUnitServiceExt organisationUnitServiceExt
    ) {
        this.entityInstanceService = entityInstanceService;
        this.programService = programService;
        this.trackedEntityProgramOwnerService = trackedEntityProgramOwnerService;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
    }

    public EventContext build(List<EventRow> eventRowList, User user) {
        Map<String, List<EventRow>> eventsByProgramUid = eventRowList.stream().collect(Collectors.groupingBy(EventRow::getProgram));

        List<String> trackedEntityInstanceUids = eventRowList
            .stream()
            .map(EventRow::getTrackedEntityInstance)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        Map<String, EventContext.TrackedEntityOuInfo> trackedEntityInstanceByUid = entityInstanceService
            .getTrackedEntityOuInfoByUid(trackedEntityInstanceUids, user)
            .stream()
            .collect(Collectors.toMap(EventContext.TrackedEntityOuInfo::getTrackedEntityUid, trackedEntityOuInfo -> trackedEntityOuInfo));

        Map<String, Program> programsByUid = programService
            .getPrograms(eventRowList.stream().map(EventRow::getProgram).collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.toMap(BaseIdentifiableObject::getUid, program -> program));

        Map<Pair<String, String>, String> orgUnitByTeiUidAndProgramUidPairs = eventsByProgramUid
            .keySet()
            .stream()
            .flatMap(programUid ->
                trackedEntityProgramOwnerService
                    .getTrackedEntityProgramOwnersUidsUsingId(
                        eventsByProgramUid
                            .get(programUid)
                            .stream()
                            .map(EventRow::getTrackedEntityInstance)
                            .filter(Objects::nonNull)
                            .map(trackedEntityInstanceByUid::get)
                            .map(EventContext.TrackedEntityOuInfo::getTrackerEntityId)
                            .distinct()
                            .collect(Collectors.toList()),
                        programsByUid.get(programUid)
                    )
                    .stream()
            )
            .collect(
                Collectors.toMap(
                    trackedEntityProgramOwnerIds ->
                        Pair.of(trackedEntityProgramOwnerIds.getTrackedEntityInstanceId(), trackedEntityProgramOwnerIds.getProgramId()),
                    TrackedEntityProgramOwnerIds::getOrgUnitUid
                )
            );

        Map<String, OrganisationUnit> orgUnitsByUid = organisationUnitServiceExt
            .getOrganisationUnitsByUid(orgUnitByTeiUidAndProgramUidPairs.values())
            .stream()
            .collect(Collectors.toMap(BaseIdentifiableObject::getUid, ou -> ou));

        return new EventContext(trackedEntityInstanceByUid, programsByUid, orgUnitByTeiUidAndProgramUidPairs, orgUnitsByUid);
    }
}
