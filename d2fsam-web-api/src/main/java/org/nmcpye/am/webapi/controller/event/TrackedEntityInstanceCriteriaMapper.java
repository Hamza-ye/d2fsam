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

import org.apache.commons.lang3.ObjectUtils;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.*;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectServiceExt;
import org.nmcpye.am.security.SecurityUtils;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.nmcpye.am.webapi.controller.event.webrequest.TrackedEntityInstanceCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.nmcpye.am.security.AuthoritiesConstants.ADMIN;
import static org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams.OrderColumn.isStaticColumn;
import static org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper.toOrderParams;
import static org.nmcpye.am.webapi.controller.tracker.export.RequestParamUtils.parseUids;

/**
 *
 *
 * @author Luciano Fiandesio
 */
@Component
public class TrackedEntityInstanceCriteriaMapper {

    private final Logger log = LoggerFactory.getLogger(TrackedEntityInstanceCriteriaMapper.class);

    private final CurrentUserService currentUserService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final ProgramServiceExt programServiceExt;

    private final ActivityServiceExt activityServiceExt;

    private final ProjectServiceExt projectServiceExt;

    private final TrackedEntityTypeServiceExt trackedEntityTypeServiceExt;

    private final DataElementServiceExt attributeService;

    public TrackedEntityInstanceCriteriaMapper(
        CurrentUserService currentUserService,
        OrganisationUnitServiceExt organisationUnitServiceExt,
        ProgramServiceExt programServiceExt,
        ActivityServiceExt activityServiceExt,
        ProjectServiceExt projectServiceExt, DataElementServiceExt attributeService,
        TrackedEntityTypeServiceExt trackedEntityTypeServiceExt
    ) {
        this.projectServiceExt = projectServiceExt;
        checkNotNull(currentUserService);
        checkNotNull(organisationUnitServiceExt);
        checkNotNull(programServiceExt);
        checkNotNull(attributeService);
        checkNotNull(trackedEntityTypeServiceExt);

        this.activityServiceExt = activityServiceExt;
        this.currentUserService = currentUserService;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
        this.programServiceExt = programServiceExt;
        this.attributeService = attributeService;
        this.trackedEntityTypeServiceExt = trackedEntityTypeServiceExt;
    }

    @Transactional(readOnly = true)
    public TrackedEntityInstanceQueryParams map(TrackedEntityInstanceCriteria criteria) {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();

        final Date programEnrollmentStartDate = ObjectUtils.firstNonNull(
            criteria.getProgramEnrollmentStartDate(),
            criteria.getProgramStartDate()
        );

        final Date programEnrollmentEndDate = ObjectUtils.firstNonNull(
            criteria.getProgramEnrollmentEndDate(),
            criteria.getProgramEndDate()
        );

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

        User user = currentUserService.getCurrentUser();

        if (user != null) {
            possibleSearchOrgUnits = user.getTeiSearchOrganisationUnitsWithFallback();
            possibleSearchOrgUnits.addAll(
                organisationUnitServiceExt.getAllUserAccessibleOrganisationUnits(user));
        }

        Map<String, DataElement> attributes = attributeService
            .getAllDataElements()
            .stream()
            .collect(Collectors.toMap(DataElement::getUid, att -> att));

        if (criteria.getAttribute() != null) {
            for (String attr : criteria.getAttribute()) {
                QueryItem it = getQueryItem(attr, attributes);

                params.getAttributes().add(it);
            }
        }

        if (criteria.getFilter() != null) {
            for (String filt : criteria.getFilter()) {
                QueryItem it = getQueryItem(filt, attributes);

                params.getFilters().add(it);
            }
        }

        for (String orgUnit : criteria.getOrgUnits()) {
            OrganisationUnit organisationUnit = organisationUnitServiceExt.getOrganisationUnit(orgUnit);
            if (organisationUnit == null) {
                throw new IllegalQueryException("Organisation unit does not exist: " + orgUnit);
            }

            if (
                user != null &&
                    !SecurityUtils.hasCurrentUserAnyOfAuthorities(ADMIN) &&
                    !organisationUnitServiceExt.isInUserHierarchy(organisationUnit.getUid(), possibleSearchOrgUnits)
            ) {
                throw new IllegalQueryException("Organisation unit is not part of the search scope: " + orgUnit);
            }

            params.getOrganisationUnits().add(organisationUnit);
        }

        validateAssignedUser(criteria);

        if (criteria.getOuMode() == OrganisationUnitSelectionMode.CAPTURE && user != null) {
            params.getOrganisationUnits().addAll(user.getOrganisationUnits());
            params.getOrganisationUnits().addAll(possibleSearchOrgUnits);
        }

        if (criteria.getOuMode() == OrganisationUnitSelectionMode.ACCESSIBLE && user != null) {
            params.getOrganisationUnits().addAll(user.getTeiSearchOrganisationUnitsWithFallback());
            params.getOrganisationUnits().addAll(possibleSearchOrgUnits);
        }

        Program program = validateProgram(criteria);

        Set<String> activitiesIds = parseUids(criteria.getActivity());
        Set<Activity> activities = validateActivities(activitiesIds);

        Project project = validateProject(criteria);

        List<OrderParam> orderParams = toOrderParams(criteria.getOrder());

        validateOrderParams(program, orderParams, attributes);

        params
            .setQuery(getQueryFilter(criteria.getQuery()))
            .setProgram(program)
            .setActivities(activities)
            .setProject(project)
            .setProgramStage(validateProgramStage(criteria, program))
            .setProgramStatus(criteria.getProgramStatus())
            .setFollowUp(criteria.getFollowUp())
            .setLastUpdatedStartDate(criteria.getLastUpdatedStartDate())
            .setLastUpdatedEndDate(criteria.getLastUpdatedEndDate())
            .setLastUpdatedDuration(criteria.getLastUpdatedDuration())
            .setProgramEnrollmentStartDate(programEnrollmentStartDate)
            .setProgramEnrollmentEndDate(programEnrollmentEndDate)
            .setProgramIncidentStartDate(criteria.getProgramIncidentStartDate())
            .setProgramIncidentEndDate(criteria.getProgramIncidentEndDate())
            .setTrackedEntityType(validateTrackedEntityType(criteria))
            .setOrganisationUnitMode(criteria.getOuMode())
            .setEventStatus(criteria.getEventStatus())
            .setEventStartDate(criteria.getEventStartDate())
            .setEventEndDate(criteria.getEventEndDate())
            .setUserWithAssignedUsers(criteria.getAssignedUserMode(), user, criteria.getAssignedUsers())
            .setTrackedEntityInstanceUids(criteria.getTrackedEntityInstances())
            .setSkipMeta(criteria.isSkipMeta())
            .setPage(criteria.getPage())
            .setPageSize(criteria.getPageSize())
            .setTotalPages(criteria.isTotalPages())
            .setSkipPaging(criteria.isSkipPaging())
            .setIncludeDeleted(criteria.isIncludeDeleted())
            .setIncludeAllAttributes(criteria.isIncludeAllAttributes())
            .setOrders(orderParams);

        return params;
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

    /**
     * Creates a QueryItem from the given item string. Item is on format
     * {attribute-id}:{operator}:{filter-value}[:{operator}:{filter-value}].
     * Only the attribute-id is mandatory.
     */
    private QueryItem getQueryItem(String item, Map<String, DataElement> attributes) {
        String[] split = item.split(DimensionalObject.DIMENSION_NAME_SEP);

        if (split.length % 2 != 1) {
            throw new IllegalQueryException("Query item or filter is invalid: " + item);
        }

        QueryItem queryItem = getItem(split[0], attributes);

        if (split.length > 1) { // Filters specified
            for (int i = 1; i < split.length; i += 2) {
                QueryOperator operator = QueryOperator.fromString(split[i]);
                queryItem.getFilters().add(new QueryFilter(operator, split[i + 1]));
            }
        }

        return queryItem;
    }

    private QueryItem getItem(String item, Map<String, DataElement> attributes) {
        if (attributes.isEmpty()) {
            throw new IllegalQueryException("Attribute does not exist: " + item);
        }

        DataElement at = attributes.get(item);

        if (at == null) {
            throw new IllegalQueryException("Attribute does not exist: " + item);
        }

        return new QueryItem(at, null, at.getValueType(), at.getAggregationType(), null);
    }

    private Project validateProject(TrackedEntityInstanceCriteria criteria) {
        Function<String, Project> getProject = uid -> {
            if (isNotEmpty(uid)) {
                return projectServiceExt.getByUid(uid);
            }
            return null;
        };

        final Project project = getProject.apply(criteria.getProject());
        if (isNotEmpty(criteria.getProject()) && project == null) {
            throw new IllegalQueryException("Project is specified but does not exist: " + criteria.getProgram());
        }
        return project;
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

    private Program validateProgram(TrackedEntityInstanceCriteria criteria) {
        Function<String, Program> getProgram = uid -> {
            if (isNotEmpty(uid)) {
                return programServiceExt.getProgram(uid);
            }
            return null;
        };

        final Program program = getProgram.apply(criteria.getProgram());
        if (isNotEmpty(criteria.getProgram()) && program == null) {
            throw new IllegalQueryException("Program is specified but does not exist: " + criteria.getProgram());
        }
        return program;
    }

    private ProgramStage validateProgramStage(TrackedEntityInstanceCriteria criteria, Program program) {
        final String programStage = criteria.getProgramStage();

        ProgramStage ps = programStage != null ? getProgramStageFromProgram(program, programStage) : null;

        if (programStage != null && ps == null) {
            throw new IllegalQueryException("Program does not contain the specified programStage: " + programStage);
        }
        return ps;
    }

    private TrackedEntityType validateTrackedEntityType(TrackedEntityInstanceCriteria criteria) {
        Function<String, TrackedEntityType> getTeiType = uid -> {
            if (isNotEmpty(uid)) {
                return trackedEntityTypeServiceExt.getTrackedEntityType(uid);
            }
            return null;
        };

        final TrackedEntityType trackedEntityType = getTeiType.apply(criteria.getTrackedEntityType());

        if (isNotEmpty(criteria.getTrackedEntityType()) && trackedEntityType == null) {
            throw new IllegalQueryException("Tracked entity type does not exist: " + criteria.getTrackedEntityType());
        }
        return trackedEntityType;
    }

    private void validateAssignedUser(TrackedEntityInstanceCriteria criteria) {
        if (
            criteria.getAssignedUserMode() != null &&
                !criteria.getAssignedUsers().isEmpty() &&
                !criteria.getAssignedUserMode().equals(AssignedUserSelectionMode.PROVIDED)
        ) {
            throw new IllegalQueryException("Assigned User uid(s) cannot be specified if selectionMode is not PROVIDED");
        }
    }

    private ProgramStage getProgramStageFromProgram(Program program, String programStage) {
        if (program == null) {
            return null;
        }

        return program.getProgramStages().stream().filter(ps -> ps.getUid().equals(programStage)).findFirst().orElse(null);
    }

    private void validateOrderParams(Program program, List<OrderParam> orderParams, Map<String, DataElement> attributes) {
        if (orderParams != null && !orderParams.isEmpty()) {
            for (OrderParam orderParam : orderParams) {
                if (!isStaticColumn(orderParam.getField()) && !attributes.containsKey(orderParam.getField())) {
                    throw new IllegalQueryException("Invalid order property: " + orderParam.getField());
                }
            }
        }
    }
}
