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
package org.nmcpye.am.webapi.controller.event;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstanceQueryParams;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper.toOrderParams;

@Service("org.nmcpye.am.webapi.controller.event.EnrollmentCriteriaMapper")
@RequiredArgsConstructor
public class EnrollmentCriteriaMapper {

    private final CurrentUserService currentUserService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final ProgramServiceExt programService;

    private final ProjectServiceExt projectServiceExt;

    private final ActivityServiceExt activityServiceExt;

    private final TrackedEntityTypeServiceExt trackedEntityTypeService;

    private final TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    /**
     * Returns a ProgramInstanceQueryParams based on the given input.
     *
     * @param ou                    the set of organisation unit identifiers.
     * @param ouMode                the OrganisationUnitSelectionMode.
     * @param lastUpdated           the last updated for PI.
     * @param lastUpdatedDuration   the last updated duration filter.
     * @param program               the Program identifier.
     * @param programStatus         the ProgramStatus in the given program.
     * @param programStartDate      the start date for enrollment in the given
     *                              Program.
     * @param programEndDate        the end date for enrollment in the given Program.
     * @param trackedEntityType     the TrackedEntityType uid.
     * @param trackedEntityInstance the TrackedEntityInstance uid.
     * @param followUp              indicates follow up status in the given Program.
     * @param page                  the page number.
     * @param pageSize              the page size.
     * @param totalPages            indicates whether to include the total number of pages.
     * @param skipPaging            whether to skip paging.
     * @param includeDeleted        whether to include soft deleted ones
     * @return a ProgramInstanceQueryParams.
     */
    @Transactional(readOnly = true)
    public ProgramInstanceQueryParams getFromUrl(
        String project,
        Set<String> activity,
        Set<String> ou,
        OrganisationUnitSelectionMode ouMode,
        Date lastUpdated,
        String lastUpdatedDuration,
        String program,
        EventStatus programStatus,
        Date programStartDate,
        Date programEndDate,
        String trackedEntityType,
        String trackedEntityInstance,
        Boolean followUp,
        Integer page,
        Integer pageSize,
        boolean totalPages,
        boolean skipPaging,
        boolean includeDeleted,
        boolean includeInactiveActivities,
        List<OrderCriteria> orderCriteria
    ) {
        ProgramInstanceQueryParams params = new ProgramInstanceQueryParams();

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

        User user = currentUserService.getCurrentUser();

        if (user != null) {
            possibleSearchOrgUnits = user.getTeiSearchOrganisationUnitsWithFallback();
            possibleSearchOrgUnits.addAll(organisationUnitServiceExt.getAllUserAccessibleOrganisationUnits(user));
        }

        if (ou != null) {

            for (String orgUnit : ou) {
                OrganisationUnit organisationUnit = organisationUnitServiceExt.getOrganisationUnit(orgUnit);

                if (organisationUnit == null) {
                    throw new IllegalQueryException("Organisation unit does not exist: " + orgUnit);
                }

                if (!organisationUnitServiceExt.isInUserHierarchy(organisationUnit.getUid(), possibleSearchOrgUnits)) {
                    throw new IllegalQueryException("Organisation unit is not part of the search scope: " + orgUnit);
                }

                params.getOrganisationUnits().add(organisationUnit);
            }
        }

        Project pro = project != null ? projectServiceExt.getByUid(project) : null;

        if (!StringUtils.isEmpty(project) && pro == null) {
            throw new IllegalQueryException("Project does not exist: " + project);
        }

        if (activity != null) {

            for (String act : activity) {
                Activity activityObject = activityServiceExt.getByUid(act);

                if (activityObject == null) {
                    throw new IllegalQueryException("Activity does not exist: " + act);
                }
                params.getActivities().add(activityObject);
            }
        }

        Program pr = program != null ? programService.getProgram(program) : null;

        if (program != null && pr == null) {
            throw new IllegalQueryException("Program does not exist: " + program);
        }

        TrackedEntityType te = trackedEntityType != null ? trackedEntityTypeService.getTrackedEntityType(trackedEntityType) : null;

        if (trackedEntityType != null && te == null) {
            throw new IllegalQueryException("Tracked entity does not exist: " + trackedEntityType);
        }

        TrackedEntityInstance tei = trackedEntityInstance != null
            ? trackedEntityInstanceService.getTrackedEntityInstance(trackedEntityInstance)
            : null;

        if (trackedEntityInstance != null && tei == null) {
            throw new IllegalQueryException("Tracked entity instance does not exist: " + trackedEntityInstance);
        }

        params.setProject(pro);
        params.setIncludeInactiveActivities(includeInactiveActivities);
        params.setProgram(pr);
        params.setProgramStatus(programStatus);
        params.setFollowUp(followUp);
        params.setLastUpdated(DateUtils.instantFromDate(lastUpdated));
        params.setLastUpdatedDuration(lastUpdatedDuration);
        params.setProgramStartDate(programStartDate);
        params.setProgramEndDate(programEndDate);
        params.setTrackedEntityType(te);
        params.setTrackedEntityInstanceUid(Optional.ofNullable(tei).map(BaseIdentifiableObject::getUid).orElse(null));
        params.setOrganisationUnitMode(ouMode);
        params.setPage(page);
        params.setPageSize(pageSize);
        params.setTotalPages(totalPages);
        params.setSkipPaging(skipPaging);
        params.setIncludeDeleted(includeDeleted);
        params.setUser(user);
        params.setOrder(toOrderParams(orderCriteria));

        return params;
    }
}
