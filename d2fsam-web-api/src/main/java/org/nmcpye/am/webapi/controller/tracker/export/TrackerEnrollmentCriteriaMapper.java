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
package org.nmcpye.am.webapi.controller.tracker.export;

import lombok.RequiredArgsConstructor;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.IllegalQueryException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper.toOrderParams;
import static org.nmcpye.am.webapi.controller.tracker.export.RequestParamUtils.applyIfNonEmpty;
import static org.nmcpye.am.webapi.controller.tracker.export.RequestParamUtils.parseUids;

/**
 * Maps query parameters from {@link TrackerEnrollmentsExportController} stored
 * in {@link TrackerEnrollmentCriteria} to {@link ProgramInstanceQueryParams}
 * which is used to fetch enrollments from the DB.
 */
@Service("org.nmcpye.am.webapi.controller.tracker.export.TrackerEnrollmentCriteriaMapper")
@RequiredArgsConstructor
public class TrackerEnrollmentCriteriaMapper {

    @Nonnull
    private final CurrentUserService currentUserService;

    @Nonnull
    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    @Nonnull
    private final ProgramServiceExt programService;

    @Nonnull
    private final TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Nonnull
    private final TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    @Nonnull
    private final ActivityServiceExt activityServiceExt;

    @Nonnull
    private final ProjectServiceExt projectServiceExt;

    @Transactional(readOnly = true)
    public ProgramInstanceQueryParams map(TrackerEnrollmentCriteria criteria) {
        Program program = applyIfNonEmpty(programService::getProgram, criteria.getProgram());
        validateProgram(criteria.getProgram(), program);

        TrackedEntityType trackedEntityType = applyIfNonEmpty(trackedEntityTypeService::getTrackedEntityType,
            criteria.getTrackedEntityType());
        validateTrackedEntityType(criteria.getTrackedEntityType(), trackedEntityType);

        TrackedEntityInstance trackedEntity = applyIfNonEmpty(trackedEntityInstanceService::getTrackedEntityInstance,
            criteria.getTrackedEntity());
        validateTrackedEntityInstance(criteria.getTrackedEntity(), trackedEntity);

        Project project = applyIfNonEmpty(projectServiceExt::getByUid,
            criteria.getProject());

        Set<String> activitiesIds = parseUids(criteria.getActivity());
        Set<Activity> activities = validateActivities(activitiesIds);

        User user = currentUserService.getCurrentUser();
        Set<String> orgUnitIds = parseUids(criteria.getOrgUnit());
        Set<OrganisationUnit> orgUnits = validateOrgUnits(orgUnitIds, user);

        ProgramInstanceQueryParams params = new ProgramInstanceQueryParams();
        params.setProject(project);
        params.setActivities(activities);
        params.setProgram(program);
        params.setProgramStatus(criteria.getProgramStatus());
        params.setFollowUp(criteria.getFollowUp());
        params.setLastUpdated(DateUtils.instantFromDate(criteria.getUpdatedAfter()));
        params.setLastUpdatedDuration(criteria.getUpdatedWithin());
        params.setProgramStartDate(criteria.getEnrolledAfter());
        params.setProgramEndDate(criteria.getEnrolledBefore());
        params.setTrackedEntityType(trackedEntityType);
        params.setTrackedEntityInstanceUid(
            Optional.ofNullable(trackedEntity).map(BaseIdentifiableObject::getUid).orElse(null));
        params.addOrganisationUnits(orgUnits);
        params.setOrganisationUnitMode(criteria.getOuMode());
        params.setPage(criteria.getPage());
        params.setPageSize(criteria.getPageSize());
        params.setTotalPages(criteria.isTotalPages());
        params.setSkipPaging(criteria.isSkipPaging());
        params.setIncludeDeleted(criteria.isIncludeDeleted());
        params.setIncludeInactiveActivities(criteria.isIncludeInactiveActivities());
        params.setUser(user);
        params.setOrder(toOrderParams(criteria.getOrder()));

        return params;
    }

    private static void validateProgram(String id, Program program) {
        if (isNotEmpty(id) && program == null) {
            throw new IllegalQueryException("Program is specified but does not exist: " + id);
        }
    }

    private void validateTrackedEntityType(String id, TrackedEntityType trackedEntityType) {
        if (isNotEmpty(id) && trackedEntityType == null) {
            throw new IllegalQueryException("Tracked entity type is specified but does not exist: " + id);
        }
    }

    private void validateTrackedEntityInstance(String id, TrackedEntityInstance trackedEntityInstance) {
        if (isNotEmpty(id) && trackedEntityInstance == null) {
            throw new IllegalQueryException("Tracked entity instance is specified but does not exist: " + id);
        }
    }

    private Set<OrganisationUnit> validateOrgUnits(Set<String> orgUnitIds, User user) {

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

        if (user != null) {
            possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                .getAllUserAccessibleOrganisationUnits(user));
            possibleSearchOrgUnits.addAll(user.getTeiSearchOrganisationUnitsWithFallback());
        }
        Set<OrganisationUnit> orgUnits = new HashSet<>();
        if (orgUnitIds != null) {
            for (String orgUnitId : orgUnitIds) {
                OrganisationUnit organisationUnit = organisationUnitServiceExt.getOrganisationUnit(orgUnitId);

                if (organisationUnit == null) {
                    throw new IllegalQueryException("Organisation unit does not exist: " + orgUnitId);
                }

                if (!organisationUnitServiceExt.isInUserHierarchy(organisationUnit.getUid(), possibleSearchOrgUnits)) {
                    throw new IllegalQueryException(
                        "Organisation unit is not part of the search scope: " + orgUnitId);
                }
                orgUnits.add(organisationUnit);
            }
        }

        return orgUnits;
    }

    private Set<Activity> validateActivities(Set<String> activityIds) {

        Set<Activity> activities = new HashSet<>();
        if (activityIds != null) {
            for (String activityId : activityIds) {
                Activity activity = activityServiceExt.getByUid(activityId);

                if (activity == null) {
                    throw new IllegalQueryException("Activity does not exist: " + activityId);
                }

                activities.add(activity);
            }
        }

        return activities;
    }
}
