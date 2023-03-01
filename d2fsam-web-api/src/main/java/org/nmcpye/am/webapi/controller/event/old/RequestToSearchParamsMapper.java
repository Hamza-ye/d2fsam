package org.nmcpye.am.webapi.controller.event.old;///*
// * Copyright (c) 2004-2021, University of Oslo
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright notice, this
// * list of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright notice,
// * this list of conditions and the following disclaimer in the documentation
// * and/or other materials provided with the distribution.
// * Neither the name of the HISP project nor the names of its contributors may
// * be used to endorse or promote products derived from this software without
// * specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package org.nmcpye.am.extended.web.controller.event.old;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.mapstruct.factory.Mappers;
//import org.nmcpye.am.domain.*;
//import org.nmcpye.am.domain.enumeration.EventStatus;
//import org.nmcpye.am.common.*;
//import org.nmcpye.am.dataelement.DataElementServiceExt;
//import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
//import org.nmcpye.am.program.ProgramServiceExt;
//import org.nmcpye.am.program.ProgramStageServiceExt;
//import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
//import org.nmcpye.am.user.UserServiceExt;
//import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
//import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
//import org.nmcpye.am.dxf2.events.event.Event;
//import org.nmcpye.am.dxf2.events.event.EventSearchParams;
//import org.nmcpye.am.acl.AclService;
//import org.nmcpye.am.core.query.QueryUtils;
//import org.nmcpye.am.schema.Schema;
//import org.nmcpye.am.schema.SchemaService;
//import org.nmcpye.am.common.collection.CollectionUtils;
//import org.nmcpye.am.extended.web.controller.event.webrequest.EventCriteria;
//import org.nmcpye.am.extended.web.controller.event.webrequest.tracker.TrackerEventCriteria;
//import org.nmcpye.am.extended.web.controller.event.old.TrackerEventCriteriaMapper;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author Luciano Fiandesio
// */
//@Component
//@RequiredArgsConstructor
//public class RequestToSearchParamsMapper {
//    private final UserServiceExt currentUserService;
//
//    private final ProgramServiceExt programService;
//
//    private final OrganisationUnitServiceExt organisationUnitServiceExt;
//
//    private final ProgramStageServiceExt programStageService;
//
//    private final AclService aclService;
//
//    private final TrackedEntityInstanceServiceExt entityInstanceService;
//
//    private final DataElementServiceExt dataElementService;
//
////    private final InputUtils inputUtils;
//
//    private final SchemaService schemaService;
//
//    private final static TrackerEventCriteriaMapper TRACKER_EVENT_CRITERIA_MAPPER = Mappers
//        .getMapper(TrackerEventCriteriaMapper.class);
//
//    private Schema schema;
//
//    @PostConstruct
//    void setSchema() {
//        if (schema == null) {
//            schema = schemaService.getDynamicSchema(Event.class);
//        }
//    }
//
//    public EventSearchParams map(String program, String programStage, EventStatus programStatus, Boolean followUp,
//                                 String orgUnit, OrganisationUnitSelectionMode orgUnitSelectionMode, String trackedEntityInstance,
//                                 Date startDate, Date endDate, Date dueDateStart, Date dueDateEnd, Date lastUpdatedStartDate,
//                                 Date lastUpdatedEndDate, String lastUpdatedDuration, EventStatus status,
//                                 IdSchemes idSchemes, Integer page, Integer pageSize,
//                                 boolean totalPages, boolean skipPaging, List<OrderParam> orders, List<OrderParam> gridOrders,
//                                 boolean includeAttributes,
//                                 Set<String> events, Boolean skipEventId, AssignedUserSelectionMode assignedUserSelectionMode,
//                                 Set<String> assignedUsers, Set<String> filters, Set<String> dataElements, boolean includeAllDataElements,
//                                 boolean includeDeleted) {
//        return map(program, programStage, programStatus, followUp, orgUnit, orgUnitSelectionMode,
//            trackedEntityInstance, startDate, endDate, dueDateStart, dueDateEnd, lastUpdatedStartDate,
//            lastUpdatedEndDate, lastUpdatedDuration, status, idSchemes, page, pageSize,
//            totalPages, skipPaging, orders, gridOrders, includeAttributes, events, null, skipEventId,
//            assignedUserSelectionMode, assignedUsers, filters, dataElements, includeAllDataElements, includeDeleted);
//    }
//
//    public EventSearchParams map(String program, String programStage, EventStatus programStatus, Boolean followUp,
//                                 String orgUnit, OrganisationUnitSelectionMode orgUnitSelectionMode, String trackedEntityInstance,
//                                 Date startDate, Date endDate, Date dueDateStart, Date dueDateEnd, Date lastUpdatedStartDate,
//                                 Date lastUpdatedEndDate, String lastUpdatedDuration, EventStatus status,
//                                 IdSchemes idSchemes, Integer page, Integer pageSize,
//                                 boolean totalPages, boolean skipPaging, List<OrderParam> orders, List<OrderParam> gridOrders,
//                                 boolean includeAttributes,
//                                 Set<String> events, Set<String> programInstances, Boolean skipEventId,
//                                 AssignedUserSelectionMode assignedUserSelectionMode,
//                                 Set<String> assignedUsers, Set<String> filters, Set<String> dataElements, boolean includeAllDataElements,
//                                 boolean includeDeleted) {
//        User user = currentUserService.getCurrentUser();
//        UserData userCredentials = user.getUserData();
//
//        EventSearchParams params = new EventSearchParams();
//
//        Program pr = programService.getProgram(program);
//
//        if (!StringUtils.isEmpty(program) && pr == null) {
//            throw new IllegalQueryException("Program is specified but does not exist: " + program);
//        }
//
//        ProgramStage ps = programStageService.getProgramStage(programStage);
//
//        if (!StringUtils.isEmpty(programStage) && ps == null) {
//            throw new IllegalQueryException("Program stage is specified but does not exist: " + programStage);
//        }
//
//        OrganisationUnit ou = organisationUnitServiceExt.getOrganisationUnit(orgUnit);
//
//        if (!StringUtils.isEmpty(orgUnit) && ou == null) {
//            throw new IllegalQueryException("Org unit is specified but does not exist: " + orgUnit);
//        }
//
//        if (pr != null && !userCredentials.isSuper() && !aclService.canDataRead(user, pr)) {
//            throw new IllegalQueryException("User has no access to program: " + pr.getUid());
//        }
//
//        if (ps != null && !userCredentials.isSuper() && !aclService.canDataRead(user, ps)) {
//            throw new IllegalQueryException("User has no access to program stage: " + ps.getUid());
//        }
//
//        TrackedEntityInstance tei = entityInstanceService.getTrackedEntityInstance(trackedEntityInstance);
//
//        if (!StringUtils.isEmpty(trackedEntityInstance) && tei == null) {
//            throw new IllegalQueryException(
//                "Tracked entity instance is specified but does not exist: " + trackedEntityInstance);
//        }
//
////        if ( attributeOptionCombo != null && !userCredentials.isSuper()
////            && !aclService.canDataRead( user, attributeOptionCombo ) )
////        {
////            throw new IllegalQueryException(
////                "User has no access to attribute category option combo: " + attributeOptionCombo.getUid() );
////        }
//
//        if (!CollectionUtils.isEmpty(events) && !CollectionUtils.isEmpty(filters)) {
//            throw new IllegalQueryException("Event UIDs and filters can not be specified at the same time");
//        }
//
//        if (events == null) {
//            events = new HashSet<>();
//        }
//
//        if (filters != null) {
//            if (!StringUtils.isEmpty(programStage) && ps == null) {
//                throw new IllegalQueryException("ProgramStage needs to be specified for event filtering to work");
//            }
//
//            for (String filter : filters) {
//                QueryItem item = getQueryItem(filter);
//                params.getFilters().add(item);
//            }
//        }
//
//        if (dataElements != null) {
//            for (String de : dataElements) {
//                QueryItem dataElement = getQueryItem(de);
//
//                params.getDataElements().add(dataElement);
//            }
//        }
//
//        if (orgUnitSelectionMode == null) {
//            orgUnitSelectionMode = OrganisationUnitSelectionMode.ACCESSIBLE;
//        }
//
//        if (assignedUserSelectionMode != null && assignedUsers != null && !assignedUsers.isEmpty()
//            && !assignedUserSelectionMode.equals(AssignedUserSelectionMode.PROVIDED)) {
//            throw new IllegalQueryException(
//                "Assigned User uid(s) cannot be specified if selectionMode is not PROVIDED");
//        }
//
//        if (assignedUsers != null) {
//            assignedUsers = assignedUsers.stream()
//                .filter(CodeGenerator::isValidUid)
//                .collect(Collectors.toSet());
//        }
//
//        if (programInstances != null) {
//            programInstances = programInstances.stream()
//                .filter(CodeGenerator::isValidUid)
//                .collect(Collectors.toSet());
//        }
//
//        return params.setProgram(pr).setProgramStage(ps).setOrgUnit(ou).setTrackedEntityInstance(tei)
//            .setProgramStatus(programStatus).setFollowUp(followUp).setOrgUnitSelectionMode(orgUnitSelectionMode)
//            .setAssignedUserSelectionMode(assignedUserSelectionMode).setAssignedUsers(assignedUsers)
//            .setStartDate(startDate).setEndDate(endDate).setDueDateStart(dueDateStart).setDueDateEnd(dueDateEnd)
//            .setLastUpdatedStartDate(lastUpdatedStartDate).setLastUpdatedEndDate(lastUpdatedEndDate)
//            .setLastUpdatedDuration(lastUpdatedDuration).setEventStatus(status)
//            /*.setCategoryOptionCombo(attributeOptionCombo)*/.setIdSchemes(idSchemes).setPage(page)
//            .setPageSize(pageSize).setTotalPages(totalPages).setSkipPaging(skipPaging)
//            .setSkipEventId(skipEventId).setIncludeAttributes(includeAttributes)
//            .setIncludeAllDataElements(includeAllDataElements).setOrders(orders).setGridOrders(gridOrders)
//            .setEvents(events).setProgramInstances(programInstances).setIncludeDeleted(includeDeleted);
//    }
//
//    private QueryItem getQueryItem(String item) {
//        String[] split = item.split(DimensionalObject.DIMENSION_NAME_SEP);
//
//        if (split == null || split.length % 2 != 1) {
//            throw new IllegalQueryException("Query item or filter is invalid: " + item);
//        }
//
//        QueryItem queryItem = getItem(split[0]);
//
//        if (split.length > 1) {
//            for (int i = 1; i < split.length; i += 2) {
//                QueryOperator operator = QueryOperator.fromString(split[i]);
//                queryItem.getFilters().add(new QueryFilter(operator, split[i + 1]));
//            }
//        }
//
//        return queryItem;
//    }
//
//    private QueryItem getItem(String item) {
//        DataElement de = dataElementService.getByUid(item);
//
//        if (de == null) {
//            throw new IllegalQueryException("Dataelement does not exist: " + item);
//        }
//
//        return new QueryItem(de, null, de.getValueType(), de.getAggregationType(), de.getOptionSet());
//    }
//
//    public EventSearchParams map(EventCriteria eventCriteria) {
//
////        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo(
////            eventCriteria.getAttributeCc(),
////            eventCriteria.getAttributeCos(),
////            true);
//
//        Set<String> eventIds = eventCriteria.getEvents();
//        Set<String> assignedUserIds = eventCriteria.getAssignedUsers();
//        Map<String, OrderParam.SortDirection> dataElementOrders = getDataElementsFromOrder(eventCriteria.getOrder());
//
//        return map(eventCriteria.getProgram(),
//            eventCriteria.getProgramStage(),
//            eventCriteria.getProgramStatus(),
//            eventCriteria.getFollowUp(),
//            eventCriteria.getOrgUnit(),
//            eventCriteria.getOuMode(),
//            eventCriteria.getTrackedEntityInstance(),
//            eventCriteria.getStartDate(),
//            eventCriteria.getEndDate(),
//            eventCriteria.getDueDateStart(),
//            eventCriteria.getDueDateEnd(),
//            eventCriteria.getLastUpdatedStartDate() != null ? eventCriteria.getLastUpdatedStartDate()
//                : eventCriteria.getLastUpdated(),
//            eventCriteria.getLastUpdatedEndDate(),
//            eventCriteria.getLastUpdatedDuration(),
//            eventCriteria.getStatus(),
////            attributeOptionCombo,
//            eventCriteria.getIdSchemes(),
//            eventCriteria.getPage(),
//            eventCriteria.getPageSize(),
//            eventCriteria.isTotalPages(),
//            eventCriteria.isSkipPaging(),
//            getOrderParams(eventCriteria.getOrder()),
//            getGridOrderParams(eventCriteria.getOrder(), dataElementOrders),
//            false,
//            eventIds,
//            eventCriteria.getProgramInstances(),
//            eventCriteria.getSkipEventId(),
//            eventCriteria.getAssignedUserMode(),
//            assignedUserIds,
//            eventCriteria.getFilter(),
//            dataElementOrders.keySet(),
//            false,
//            eventCriteria.isIncludeDeleted());
//    }
//
//    private List<OrderParam> getOrderParams(List<OrderCriteria> order) {
//        if (order != null && !order.isEmpty()) {
//            return QueryUtils.filteredBySchema(order, schema);
//        }
//        return Collections.emptyList();
//    }
//
//    private List<OrderParam> getGridOrderParams(List<OrderCriteria> order,
//                                                Map<String, OrderParam.SortDirection> dataElementOrders) {
//        return Optional.ofNullable(order)
//            .orElse(Collections.emptyList())
//            .stream()
//            .filter(Objects::nonNull)
//            .filter(orderCriteria -> dataElementOrders.containsKey(orderCriteria.getField()))
//            .map(orderCriteria -> OrderParam.builder()
//                .field(orderCriteria.getField())
//                .direction(dataElementOrders.get(orderCriteria.getField()))
//                .build())
//            .collect(Collectors.toList());
//    }
//
//    private Map<String, OrderParam.SortDirection> getDataElementsFromOrder(List<OrderCriteria> allOrders) {
//        Map<String, OrderParam.SortDirection> dataElements = new HashMap<>();
//
//        if (allOrders != null) {
//            for (OrderCriteria orderCriteria : allOrders) {
//                DataElement de = dataElementService.getByUid(orderCriteria.getField());
//                if (de != null) {
//                    dataElements.put(orderCriteria.getField(), orderCriteria.getDirection());
//                }
//            }
//        }
//        return dataElements;
//    }
//
//    public EventSearchParams map(TrackerEventCriteria eventCriteria) {
//        return map(TRACKER_EVENT_CRITERIA_MAPPER.toEventCriteria(eventCriteria));
//    }
//}
