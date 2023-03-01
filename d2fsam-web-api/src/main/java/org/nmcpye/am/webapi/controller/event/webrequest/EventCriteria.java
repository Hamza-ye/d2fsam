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
package org.nmcpye.am.webapi.controller.event.webrequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.nmcpye.am.common.*;
import org.nmcpye.am.commons.collection.CollectionUtils;
import org.nmcpye.am.commons.util.TextUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to hold EventController request parameters into a handy place
 *
 * @author Giuseppe Nespolino <g.nespolino@gmail.com>
 */
@Data
@NoArgsConstructor
public class EventCriteria extends PagingAndSortingCriteriaAdapter {

    private String project;

    private String activity;

    private String program;

    private String programStage;

    private EventStatus programStatus;

    private Boolean followUp;

    private String trackedEntityInstance;

    private String orgUnit;

    private OrganisationUnitSelectionMode ouMode;

    private AssignedUserSelectionMode assignedUserMode;

    private String assignedUser;

    private Date startDate;

    private Date endDate;

    private Date dueDateStart;

    private Date dueDateEnd;

    private Date lastUpdated;

    private Date lastUpdatedStartDate;

    private Date lastUpdatedEndDate;

    private String lastUpdatedDuration;

    private EventStatus status;

    private String attributeCc;

    private String attributeCos;

    private boolean skipMeta;

    private String attachment;

    private boolean includeDeleted;

    private boolean includeInactiveActivities;

    private String event;

    private Boolean skipEventId;

    private Set<String> filter;

    private Set<String> programInstances;

    private IdSchemes idSchemes = new IdSchemes();

    public Set<String> getAssignedUsers() {
        Set<String> assignedUsers = new HashSet<>();

        if (assignedUser != null && !assignedUser.isEmpty()) {
            assignedUsers =
                TextUtils
                    .splitToArray(assignedUser, TextUtils.SEMICOLON)
                    .stream()
                    .filter(CodeGenerator::isValidUid)
                    .collect(Collectors.toSet());
        }

        return assignedUsers;
    }

    public Set<String> getEvents() {
        return CollectionUtils
            .emptyIfNull(TextUtils.splitToArray(event, TextUtils.SEMICOLON))
            .stream()
            .filter(CodeGenerator::isValidUid)
            .collect(Collectors.toSet());
    }
}
