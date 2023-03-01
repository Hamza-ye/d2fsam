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
import org.nmcpye.am.common.*;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectServiceExt;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams.OrderColumn.isStaticColumn;
import static org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper.toOrderParams;
import static org.nmcpye.am.webapi.controller.tracker.export.RequestParamUtils.*;

/**
 * Maps query parameters from {@link TrackerTrackedEntitiesExportController}
 * stored in {@link TrackerTrackedEntityCriteria} to
 * {@link TrackedEntityInstanceQueryParams} which is used to fetch tracked
 * entities from the DB.
 *
 * @author Luciano Fiandesio
 */
@Component("org.nmcpye.am.webapi.controller.tracker.export.TrackedEntityCriteriaMapper")
@RequiredArgsConstructor
public class TrackerTrackedEntityCriteriaMapper {
    @Nonnull
    private final CurrentUserService currentUserService;

    @Nonnull
    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    @Nonnull
    private final ProgramServiceExt programService;

    @Nonnull
    private final ActivityServiceExt activityServiceExt;

    @Nonnull
    private final ProjectServiceExt projectServiceExt;

    @Nonnull
    private final TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Nonnull
    private final TrackedEntityAttributeServiceExt attributeService;

    @Transactional(readOnly = true)
    public TrackedEntityInstanceQueryParams map(TrackerTrackedEntityCriteria criteria) {
        Program program = applyIfNonEmpty(programService::getProgram, criteria.getProgram());
        validateProgram(criteria.getProgram(), program);

        Project project = applyIfNonEmpty(projectServiceExt::getByUid, criteria.getProject());
        validateProject(criteria.getProject(), project);

        Set<String> activitiesIds = parseUids(criteria.getActivity());
        Set<Activity> activities = validateActivities(activitiesIds);

        ProgramStage programStage = validateProgramStage(criteria, program);

        TrackedEntityType trackedEntityType = applyIfNonEmpty(trackedEntityTypeService::getTrackedEntityType,
            criteria.getTrackedEntityType());
        validateTrackedEntityType(criteria.getTrackedEntityType(), trackedEntityType);

        Set<String> assignedUserIds = parseAndFilterUids(criteria.getAssignedUser());
        validateAssignedUsers(criteria.getAssignedUserMode(), assignedUserIds);

        User user = currentUserService.getCurrentUser();
        Set<String> orgUnitIds = parseUids(criteria.getOrgUnit());

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();
        if (user != null) {
            possibleSearchOrgUnits.addAll(
                organisationUnitServiceExt.getAllUserAccessibleOrganisationUnits(user));
        }

        Set<OrganisationUnit> orgUnits = validateOrgUnits(orgUnitIds, possibleSearchOrgUnits, user);

        if (criteria.getOuMode() == OrganisationUnitSelectionMode.ACCESSIBLE && user != null) {
            orgUnits.addAll(user.getOrganisationUnits());
            orgUnits.addAll(possibleSearchOrgUnits);
        }

        if (criteria.getOuMode() == OrganisationUnitSelectionMode.CAPTURE && user != null) {
            orgUnits.addAll(user.getOrganisationUnits());
            orgUnits.addAll(possibleSearchOrgUnits);
        }

        QueryFilter queryFilter = getQueryFilter(criteria.getQuery());

        Map<String, TrackedEntityAttribute> attributes = attributeService.getAllTrackedEntityAttributes()
            .stream().collect(Collectors.toMap(TrackedEntityAttribute::getUid, att -> att));

        List<QueryItem> attributeItems = parseAttributeQueryItems(criteria.getAttribute(), attributes);

        List<QueryItem> filters = parseAttributeQueryItems(criteria.getFilter(), attributes);

        List<OrderParam> orderParams = toOrderParams(criteria.getOrder());
        validateOrderParams(orderParams, attributes);

        Set<String> trackedEntities = parseUids(criteria.getTrackedEntity());

        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setQuery(queryFilter)
            .setProject(project)
            .setActivities(activities)
            .setProgram(program)
            .setProgramStage(programStage)
            .setProgramStatus(criteria.getProgramStatus())
            .setFollowUp(criteria.getFollowUp())
            .setLastUpdatedStartDate(criteria.getUpdatedAfter())
            .setLastUpdatedEndDate(criteria.getUpdatedBefore())
            .setLastUpdatedDuration(criteria.getUpdatedWithin())
            .setProgramEnrollmentStartDate(criteria.getEnrollmentEnrolledAfter())
            .setProgramEnrollmentEndDate(criteria.getEnrollmentEnrolledBefore())
            .setProgramIncidentStartDate(criteria.getEnrollmentOccurredAfter())
            .setProgramIncidentEndDate(criteria.getEnrollmentOccurredBefore())
            .setTrackedEntityType(trackedEntityType)
            .addOrganisationUnits(orgUnits)
            .setOrganisationUnitMode(criteria.getOuMode())
            .setEventStatus(criteria.getEventStatus())
            .setEventStartDate(criteria.getEventOccurredAfter())
            .setEventEndDate(criteria.getEventOccurredBefore())
            .setUserWithAssignedUsers(criteria.getAssignedUserMode(), user, assignedUserIds)
            .setTrackedEntityInstanceUids(trackedEntities)
            .setAttributes(attributeItems)
            .setFilters(filters)
            .setSkipMeta(criteria.isSkipMeta())
            .setPage(criteria.getPage())
            .setPageSize(criteria.getPageSize())
            .setTotalPages(criteria.isTotalPages())
            .setSkipPaging(criteria.isSkipPaging())
            .setIncludeDeleted(criteria.isIncludeDeleted())
            .setIncludeInactiveActivities(criteria.isIncludeInactiveActivities())
            .setIncludeAllAttributes(criteria.isIncludeAllAttributes())
            .setOrders(orderParams);
        return params;
    }

    private Set<OrganisationUnit> validateOrgUnits(Set<String> orgUnitIds,
                                                   Set<OrganisationUnit> possibleSearchOrgUnits,
                                                   User user) {

        Set<OrganisationUnit> orgUnits = new HashSet<>();

        for (String orgUnitId : orgUnitIds) {
            OrganisationUnit orgUnit = organisationUnitServiceExt.getOrganisationUnit(orgUnitId);

            if (orgUnit == null) {
                throw new IllegalQueryException("Organisation unit does not exist: " + orgUnitId);
            }

            if (user != null && !user.isSuper() &&
                !(organisationUnitServiceExt.isInUserHierarchy(orgUnit.getUid(),
                    possibleSearchOrgUnits) || organisationUnitServiceExt.isInUserHierarchy(orgUnit.getUid(),
                    user.getTeiSearchOrganisationUnitsWithFallback()))) {
                throw new IllegalQueryException("Organisation unit is not part of the search scope: " + orgUnitId);
            }
            orgUnits.add(orgUnit);
        }

        return orgUnits;
    }

    /**
     * Creates a QueryFilter from the given query string. Query is on format
     * {operator}:{filter-value}. Only the filter-value is mandatory. The EQ
     * QueryOperator is used as operator if not specified.
     */
    private QueryFilter getQueryFilter(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        if (!query.contains(DimensionalObject.DIMENSION_NAME_SEP)) {
            return new QueryFilter(QueryOperator.EQ, query);
        } else {
            String[] split = query.split(DimensionalObject.DIMENSION_NAME_SEP);

            if (split.length != 2) {
                throw new IllegalQueryException("Query has invalid format: " + query);
            }

            QueryOperator op = QueryOperator.fromString(split[0]);

            return new QueryFilter(op, split[1]);
        }
    }

    private static void validateProject(String id, Project project) {
        if (isNotEmpty(id) && project == null) {
            throw new IllegalQueryException("Project is specified but does not exist: " + id);
        }
    }

    private Set<Activity> validateActivities(Set<String> activityIds) {
        Set<Activity> activities = new HashSet<>();
        if (activityIds != null) {
            for (String activityId : activityIds) {
                Activity activity = activityServiceExt.getByUid(activityId);

                if (activity == null) {
                    throw new IllegalQueryException("Organisation unit does not exist: " + activityId);
                }

                activities.add(activity);
            }
        }

        return activities;
    }

    private static void validateProgram(String id, Program program) {
        if (isNotEmpty(id) && program == null) {
            throw new IllegalQueryException("Program is specified but does not exist: " + id);
        }
    }

    private ProgramStage validateProgramStage(TrackerTrackedEntityCriteria criteria, Program program) {

        final String programStage = criteria.getProgramStage();

        ProgramStage ps = programStage != null ? getProgramStageFromProgram(program, programStage) : null;

        if (programStage != null && ps == null) {
            throw new IllegalQueryException("Program does not contain the specified programStage: " + programStage);
        }
        return ps;
    }

    private ProgramStage getProgramStageFromProgram(Program program, String programStage) {
        if (program == null) {
            return null;
        }

        return program.getProgramStages().stream().filter(ps -> ps.getUid().equals(programStage)).findFirst()
            .orElse(null);
    }

    private void validateTrackedEntityType(String id, TrackedEntityType trackedEntityType) {
        if (isNotEmpty(id) && trackedEntityType == null) {
            throw new IllegalQueryException("Tracked entity type does not exist: " + id);
        }
    }

    private static void validateAssignedUsers(AssignedUserSelectionMode mode, Set<String> assignedUserIds) {
        if (mode == null) {
            return;
        }

        if (!assignedUserIds.isEmpty() && AssignedUserSelectionMode.PROVIDED != mode) {
            throw new IllegalQueryException(
                "Assigned User uid(s) cannot be specified if selectionMode is not PROVIDED");
        }
    }

    private void validateOrderParams(List<OrderParam> orderParams, Map<String, TrackedEntityAttribute> attributes) {
        if (orderParams != null && !orderParams.isEmpty()) {
            for (OrderParam orderParam : orderParams) {
                if (!isStaticColumn(orderParam.getField()) && !attributes.containsKey(orderParam.getField())) {
                    throw new IllegalQueryException("Invalid order property: " + orderParam.getField());
                }
            }
        }
    }
}
