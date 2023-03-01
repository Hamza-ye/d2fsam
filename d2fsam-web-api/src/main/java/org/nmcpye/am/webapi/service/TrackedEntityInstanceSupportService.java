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
package org.nmcpye.am.webapi.service;

import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.nmcpye.am.common.AccessLevel;
import org.nmcpye.am.dxf2.events.TrackedEntityInstanceParams;
import org.nmcpye.am.dxf2.events.trackedentity.ProgramOwner;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.unauthorized;

/**
 * This service should not be used in the new tracker.
 */
@Service
@RequiredArgsConstructor
public class TrackedEntityInstanceSupportService {

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    private final CurrentUserService currentUserService;

    private final ProgramServiceExt programService;

    private final TrackerAccessManager trackerAccessManager;

    private final TrackedEntityInstanceServiceExt instanceService;

    private final TrackedEntityTypeServiceExt trackedEntityTypeService;

    @SneakyThrows
    public TrackedEntityInstance getTrackedEntityInstance(String id, String pr, List<String> fields) {
        User user = currentUserService.getCurrentUser();

        TrackedEntityInstanceParams trackedEntityInstanceParams = getTrackedEntityInstanceParams(fields);

        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance(id,
            trackedEntityInstanceParams);

        if (trackedEntityInstance == null) {
            throw new NotFoundException("TrackedEntityInstance", id);
        }

        if (pr != null) {
            Program program = programService.getProgram(pr);

            if (program == null) {
                throw new NotFoundException("Program", pr);
            }

            List<String> errors = trackerAccessManager.canRead(user,
                instanceService.getTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstance()), program,
                false);

            if (!errors.isEmpty()) {
                if (program.getAccessLevel() == AccessLevel.CLOSED) {
                    throw new WebMessageException(
                        unauthorized(TrackerOwnershipManager.PROGRAM_ACCESS_CLOSED));
                }
                throw new WebMessageException(
                    unauthorized(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED));
            }

            if (trackedEntityInstanceParams.isIncludeProgramOwners()) {
                List<ProgramOwner> filteredProgramOwners = trackedEntityInstance.getProgramOwners().stream()
                    .filter(tei -> tei.getProgram().equals(pr)).collect(Collectors.toList());
                trackedEntityInstance.setProgramOwners(filteredProgramOwners);
            }
        } else {
            // return only tracked entity type attributes

            TrackedEntityType trackedEntityType = trackedEntityTypeService
                .getTrackedEntityType(trackedEntityInstance.getTrackedEntityType());

            if (trackedEntityType != null) {
                List<String> tetAttributes = trackedEntityType.getTrackedEntityAttributes().stream()
                    .map(TrackedEntityAttribute::getUid).collect(Collectors.toList());

                trackedEntityInstance.setAttributes(trackedEntityInstance.getAttributes().stream()
                    .filter(att -> tetAttributes.contains(att.getAttribute())).collect(Collectors.toList()));
            }
        }

        return trackedEntityInstance;
    }

    public TrackedEntityInstanceParams getTrackedEntityInstanceParams(List<String> fields) {
        String joined = Joiner.on("").join(fields);

        if (joined.contains("*")) {
            return TrackedEntityInstanceParams.TRUE;
        }

        TrackedEntityInstanceParams params = TrackedEntityInstanceParams.FALSE;

        if (joined.contains("relationships")) {
            params = params.withIncludeRelationships(true);
        }

        if (joined.contains("enrollments")) {
            params = params.withIncludeEnrollments(true);
        }

        if (joined.contains("events")) {
            params = params.withIncludeEvents(true);
        }

        if (joined.contains("programOwners")) {
            params = params.withIncludeProgramOwners(true);
        }

        return params;
    }

}
