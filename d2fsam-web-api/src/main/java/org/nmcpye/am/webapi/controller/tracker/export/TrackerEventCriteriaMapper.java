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
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.AssignedUserSelectionMode;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.QueryItem;
import org.nmcpye.am.commons.collection.CollectionUtils;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.EventSearchParams;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageServiceExt;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectServiceExt;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam.SortDirection;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper;
import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nmcpye.am.webapi.controller.tracker.export.RequestParamUtils.*;

/**
 * Maps query parameters from {@link TrackerEventsExportController} stored in
 * {@link TrackerEventCriteria} to {@link EventSearchParams} which is used to
 * fetch events from the DB.
 */
@Component("org.nmcpye.am.webapi.controller.tracker.export.TrackerEventCriteriaMapper")
@RequiredArgsConstructor
class TrackerEventCriteriaMapper {

    /**
     * Properties other than the {@link Property#isSimple()} ones on
     * {@link org.nmcpye.am.dxf2.events.event.Event} which are valid order query
     * parameter property names. These need to be supported by the underlying
     * Event store like {@link org.nmcpye.am.dxf2.events.event.JdbcEventStore}
     * see QUERY_PARAM_COL_MAP.
     */
    private static final Set<String> NON_EVENT_SORTABLE_PROPERTIES = Set.of("enrolledAt", "occurredAt");

    private final CurrentUserService currentUserService;

    private final ProgramServiceExt programService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final ProgramStageServiceExt programStageService;

    private final AclService aclService;

    private final TrackedEntityInstanceServiceExt entityInstanceService;

    private final TrackedEntityAttributeServiceExt attributeService;

    private final DataElementServiceExt dataElementService;

    private final DataElementServiceExt trackedEntityAttributeService;

//    private final InputUtils inputUtils;

    private final SchemaService schemaService;

    private final ActivityServiceExt activityServiceExt;

    private final ProjectServiceExt projectServiceExt;

    private Schema schema;

    @PostConstruct
    void setSchema() {
        if (schema == null) {
            schema = schemaService.getDynamicSchema(Event.class);
        }
    }

    public EventSearchParams map(TrackerEventCriteria criteria) {
        Program program = applyIfNonEmpty(programService::getProgram, criteria.getProgram());
        validateProgram(criteria.getProgram(), program);

        ProgramStage programStage = applyIfNonEmpty(programStageService::getProgramStage,
            criteria.getProgramStage());
        validateProgramStage(criteria.getProgramStage(), programStage);

        OrganisationUnit orgUnit = applyIfNonEmpty(organisationUnitServiceExt::getOrganisationUnit,
            criteria.getOrgUnit());
        validateOrgUnit(criteria.getOrgUnit(), orgUnit);

        Activity activity = applyIfNonEmpty(activityServiceExt::getByUid,
            criteria.getActivity());

        Project project = applyIfNonEmpty(projectServiceExt::getByUid,
            criteria.getProject());

        User user = currentUserService.getCurrentUser();
        validateUser(user, program, programStage);

        TrackedEntityInstance trackedEntityInstance = applyIfNonEmpty(entityInstanceService::getTrackedEntityInstance,
            criteria.getTrackedEntity());
        validateTrackedEntity(criteria.getTrackedEntity(), trackedEntityInstance);

//        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo(
//            criteria.getAttributeCc(),
//            criteria.getAttributeCos(),
//            true );
//        validateAttributeOptionCombo( attributeOptionCombo, user );

        Set<String> eventIds = parseAndFilterUids(criteria.getEvent());
        validateFilter(criteria.getFilter(), eventIds, criteria.getProgramStage(), programStage);

        Set<String> assignedUserIds = parseAndFilterUids(criteria.getAssignedUser());
        validateAssignedUsers(criteria.getAssignedUserMode(), assignedUserIds);

        Map<String, SortDirection> dataElementOrders = getDataElementsFromOrder(criteria.getOrder());
        List<QueryItem> dataElements = dataElementOrders.keySet()
            .stream()
            .map(i -> parseQueryItem(i, this::dataElementToQueryItem))
            .collect(Collectors.toList());

        Map<String, SortDirection> attributeOrders = getAttributesFromOrder(criteria.getOrder());
        List<OrderParam> attributeOrderParams = mapToOrderParams(attributeOrders);
        List<OrderParam> dataElementOrderParams = mapToOrderParams(dataElementOrders);

        List<QueryItem> filterAttributes = parseFilterAttributes(criteria.getFilterAttributes(),
            attributeOrderParams);
        validateFilterAttributes(filterAttributes);

        List<QueryItem> filters = criteria.getFilter()
            .stream()
            .map(i -> parseQueryItem(i, this::dataElementToQueryItem))
            .collect(Collectors.toList());

        Set<String> programInstances = criteria.getEnrollments().stream()
            .filter(CodeGenerator::isValidUid)
            .collect(Collectors.toSet());

        EventSearchParams params = new EventSearchParams();

        return params.setProject(project).setActivity(activity)
            .setProgram(program).setProgramStage(programStage).setOrgUnit(orgUnit)
            .setTrackedEntityInstance(trackedEntityInstance)
            .setProgramStatus(criteria.getProgramStatus()).setFollowUp(criteria.getFollowUp())
            .setOrgUnitSelectionMode(criteria.getOuMode())
            .setAssignedUserSelectionMode(criteria.getAssignedUserMode())
            .setAssignedUsers(assignedUserIds)
            .setStartDate(criteria.getOccurredAfter()).setEndDate(criteria.getOccurredBefore())
            .setDueDateStart(criteria.getScheduledAfter()).setDueDateEnd(criteria.getScheduledBefore())
            .setLastUpdatedStartDate(criteria.getUpdatedAfter())
            .setLastUpdatedEndDate(criteria.getUpdatedBefore())
            .setLastUpdatedDuration(criteria.getUpdatedWithin())
            .setEnrollmentEnrolledBefore(criteria.getEnrollmentEnrolledBefore())
            .setEnrollmentEnrolledAfter(criteria.getEnrollmentEnrolledAfter())
            .setEnrollmentOccurredBefore(criteria.getEnrollmentOccurredBefore())
            .setEnrollmentOccurredAfter(criteria.getEnrollmentOccurredAfter())
            .setEventStatus(criteria.getStatus())
//            .setCategoryOptionCombo( attributeOptionCombo ).setIdSchemes( criteria.getIdSchemes() )
            .setPage(criteria.getPage())
            .setPageSize(criteria.getPageSize()).setTotalPages(criteria.isTotalPages())
            .setSkipPaging(criteria.isSkipPaging())
            .setSkipEventId(criteria.getSkipEventId()).setIncludeAttributes(false)
            .setIncludeAllDataElements(false).addDataElements(dataElements)
            .addFilters(filters).addFilterAttributes(filterAttributes)
            .addOrders(getOrderParams(criteria.getOrder()))
            .addGridOrders(dataElementOrderParams)
            .addAttributeOrders(attributeOrderParams)
            .setEvents(eventIds).setProgramInstances(programInstances)
            .setIncludeDeleted(criteria.isIncludeDeleted())
            .setIncludeInactiveActivities(criteria.isIncludeInactiveActivities());
    }

    private static void validateProgram(String program, Program pr) {
        if (!StringUtils.isEmpty(program) && pr == null) {
            throw new IllegalQueryException("Program is specified but does not exist: " + program);
        }
    }

    private static void validateProgramStage(String programStage, ProgramStage ps) {
        if (!StringUtils.isEmpty(programStage) && ps == null) {
            throw new IllegalQueryException("Program stage is specified but does not exist: " + programStage);
        }
    }

    private static void validateOrgUnit(String orgUnit, OrganisationUnit ou) {
        if (!StringUtils.isEmpty(orgUnit) && ou == null) {
            throw new IllegalQueryException("Org unit is specified but does not exist: " + orgUnit);
        }
    }

    private void validateUser(User user, Program pr, ProgramStage ps) {
        if (pr != null && !user.isSuper() && !aclService.canDataRead(user, pr)) {
            throw new IllegalQueryException("User has no access to program: " + pr.getUid());
        }

        if (ps != null && !user.isSuper() && !aclService.canDataRead(user, ps)) {
            throw new IllegalQueryException("User has no access to program stage: " + ps.getUid());
        }
    }

    private void validateTrackedEntity(String trackedEntity, TrackedEntityInstance trackedEntityInstance) {
        if (!StringUtils.isEmpty(trackedEntity) && trackedEntityInstance == null) {
            throw new IllegalQueryException(
                "Tracked entity instance is specified but does not exist: " + trackedEntity);
        }
    }

//    private void validateAttributeOptionCombo( CategoryOptionCombo attributeOptionCombo, User user )
//    {
//        if ( attributeOptionCombo != null && !user.isSuper()
//            && !aclService.canDataRead( user, attributeOptionCombo ) )
//        {
//            throw new IllegalQueryException(
//                "User has no access to attribute category option combo: " + attributeOptionCombo.getUid() );
//        }
//    }

    private static void validateFilter(Set<String> filters, Set<String> eventIds, String programStage,
                                       ProgramStage ps) {
        if (!CollectionUtils.isEmpty(eventIds) && !CollectionUtils.isEmpty(filters)) {
            throw new IllegalQueryException("Event UIDs and filters can not be specified at the same time");
        }
        if (!CollectionUtils.isEmpty(filters) && !StringUtils.isEmpty(programStage) && ps == null) {
            throw new IllegalQueryException("ProgramStage needs to be specified for event filtering to work");
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

    private List<QueryItem> parseFilterAttributes(Set<String> filterAttributes, List<OrderParam> attributeOrderParams) {
        Map<String, TrackedEntityAttribute> attributes = attributeService.getAllTrackedEntityAttributes()
            .stream()
            .collect(Collectors.toMap(TrackedEntityAttribute::getUid, att -> att));

        List<QueryItem> filterItems = parseAttributeQueryItems(filterAttributes, attributes);
        List<QueryItem> orderItems = attributeQueryItemsFromOrder(filterItems, attributes, attributeOrderParams);

        return Stream.concat(filterItems.stream(), orderItems.stream()).collect(Collectors.toList());
    }

    private List<QueryItem> attributeQueryItemsFromOrder(List<QueryItem> filterAttributes,
                                                         Map<String, TrackedEntityAttribute> attributes, List<OrderParam> attributeOrderParams) {
        return attributeOrderParams.stream()
            .map(OrderParam::getField)
            .filter(att -> !containsAttributeFilter(filterAttributes, att))
            .map(attributes::get)
            .map(at -> new QueryItem(at, null, at.getValueType(), at.getAggregationType(), at.getOptionSet()))
            .collect(Collectors.toList());
    }

    private boolean containsAttributeFilter(List<QueryItem> attributeFilters, String attributeUid) {
        for (QueryItem item : attributeFilters) {
            if (Objects.equals(item.getItem().getUid(), attributeUid)) {
                return true;
            }
        }
        return false;
    }

    private void validateFilterAttributes(List<QueryItem> queryItems) {
        Set<String> attributes = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (QueryItem item : queryItems) {
            if (!attributes.add(item.getItemId())) {
                duplicates.add(item.getItemId());
            }
        }

        if (!duplicates.isEmpty()) {
            throw new IllegalQueryException(String.format(
                "filterAttributes contains duplicate tracked entity attribute (TEA): %s. Multiple filters for the same TEA can be specified like 'uid:gt:2:lt:10'",
                String.join(", ", duplicates)));
        }
    }

    private Map<String, SortDirection> getDataElementsFromOrder(List<OrderCriteria> allOrders) {
        if (allOrders == null) {
            return Collections.emptyMap();
        }

        Map<String, SortDirection> dataElements = new HashMap<>();
        for (OrderCriteria orderCriteria : allOrders) {
            DataElement de = dataElementService.getDataElement(orderCriteria.getField());
            if (de != null) {
                dataElements.put(orderCriteria.getField(), orderCriteria.getDirection());
            }
        }
        return dataElements;
    }

    private Map<String, SortDirection> getAttributesFromOrder(List<OrderCriteria> allOrders) {
        if (allOrders == null) {
            return Collections.emptyMap();
        }

        Map<String, SortDirection> attributes = new HashMap<>();
        for (OrderCriteria orderCriteria : allOrders) {
            DataElement attribute = trackedEntityAttributeService
                .getDataElement(orderCriteria.getField());
            if (attribute != null) {
                attributes.put(orderCriteria.getField(), orderCriteria.getDirection());
            }
        }
        return attributes;
    }

    private QueryItem dataElementToQueryItem(String item) {
        DataElement de = dataElementService.getDataElement(item);

        if (de == null) {
            throw new IllegalQueryException("Dataelement does not exist: " + item);
        }

        return new QueryItem(de, null, de.getValueType(), de.getAggregationType(), de.getOptionSet());
    }

    private List<OrderParam> getOrderParams(List<OrderCriteria> order) {
        if (order == null || order.isEmpty()) {
            return Collections.emptyList();
        }
        validateOrderParams(order);

        return OrderParamsHelper.toOrderParams(order);
    }

    private void validateOrderParams(List<OrderCriteria> order) {
        Set<String> requestProperties = order.stream()
            .map(OrderCriteria::getField)
            .filter(field -> !CodeGenerator.isValidUid(field))
            .collect(Collectors.toSet());

        Set<String> allowedProperties = schema.getProperties().stream().filter(Property::isSimple)
            .map(Property::getName).collect(Collectors.toSet());
        allowedProperties.addAll(NON_EVENT_SORTABLE_PROPERTIES);

        requestProperties.removeAll(allowedProperties);
        if (!requestProperties.isEmpty()) {
            throw new IllegalQueryException(
                String.format("Order by property `%s` is not supported. Supported are `%s`",
                    String.join(", ", requestProperties), String.join(", ", allowedProperties)));
        }
    }

    private List<OrderParam> mapToOrderParams(Map<String, SortDirection> orders) {
        return orders.entrySet().stream()
            .map(e -> new OrderParam(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
}
