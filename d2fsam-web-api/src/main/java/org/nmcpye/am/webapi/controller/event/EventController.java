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

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.io.ParseException;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.cache.CacheStrategy;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.commons.util.StreamUtils;
import org.nmcpye.am.commons.util.TextUtils;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.event.*;
import org.nmcpye.am.dxf2.events.event.csv.CsvEventService;
import org.nmcpye.am.dxf2.events.report.EventRowService;
import org.nmcpye.am.dxf2.events.report.EventRows;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummaries;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.dxf2.webmessage.responses.FileResourceWebMessageResponse;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.fileresource.FileResourceStorageStatus;
import org.nmcpye.am.fileresource.ImageFileDimension;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.node.Preset;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStageInstanceServiceExt;
import org.nmcpye.am.query.QueryUtils;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.scheduling.JobConfiguration;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.system.grid.GridUtils;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.nmcpye.am.webapi.controller.event.webrequest.EventCriteria;
import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
import org.nmcpye.am.webapi.service.ContextService;
import org.nmcpye.am.webapi.utils.ContextUtils;
import org.nmcpye.am.webapi.utils.FileResourceUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;
import static org.nmcpye.am.scheduling.JobType.EVENT_IMPORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@OpenApi.Tags("tracker")
@Controller
@RequestMapping(value = EventController.RESOURCE_PATH)

@RequiredArgsConstructor
public class EventController {

    public static final String RESOURCE_PATH = "/events";

    private static final String META_DATA_KEY_DE = "de";

    // --------------------------------------------------------------------------
    // Dependencies
    // --------------------------------------------------------------------------

    private final CurrentUserService currentUserService;

    private final AsyncTaskExecutor taskExecutor;

    private final EventService eventService;

    private final CsvEventService<Event> csvEventService;

    private final EventRowService eventRowService;

    private final DataElementServiceExt dataElementService;

    //    private final InputUtils inputUtils;

    private final RenderService renderService;

    private final ProgramStageInstanceServiceExt programStageInstanceService;

    private final FileResourceServiceExt fileResourceService;

    //    private final FieldFilterService fieldFilterService;

    private final ContextService contextService;

    private final SchemaService schemaService;

    private final EventRequestToSearchParamsMapper requestToSearchParamsMapper;

    private final ContextUtils contextUtils;

    private Schema schema;

    protected Schema getSchema() {
        if (schema == null) {
            schema = schemaService.getDynamicSchema(Event.class);
        }

        return schema;
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Query Read
    // -------------------------------------------------------------------------

    @GetMapping(value = "/query", produces = {ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CONTENT_TYPE_JAVASCRIPT})
    public @ResponseBody
    Grid queryEventsJson(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String activity,
        @RequestParam(required = false) String program,
        @RequestParam(required = false) String programStage,
        @RequestParam(required = false) EventStatus programStatus,
        @RequestParam(required = false) Boolean followUp,
        @RequestParam(required = false) String trackedEntityInstance,
        @RequestParam(required = false) String orgUnit,
        @RequestParam(required = false) OrganisationUnitSelectionMode ouMode,
        @RequestParam(required = false) AssignedUserSelectionMode assignedUserMode,
        @RequestParam(required = false) String assignedUser,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate,
        @RequestParam(required = false) Date dueDateStart,
        @RequestParam(required = false) Date dueDateEnd,
        @RequestParam(required = false) Date lastUpdated,
        @RequestParam(required = false) Date lastUpdatedStartDate,
        @RequestParam(required = false) Date lastUpdatedEndDate,
        @RequestParam(required = false) EventStatus status,
        @RequestParam(required = false) String attributeCc,
        @RequestParam(required = false) String attributeCos,
        @RequestParam(required = false) boolean skipMeta,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) boolean totalPages,
        @RequestParam(required = false) Boolean skipPaging,
        @RequestParam(required = false) Boolean paging,
        @RequestParam(required = false) List<OrderCriteria> order,
        @RequestParam(required = false) String attachment,
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
        @RequestParam(required = false, defaultValue = "false") boolean includeInactiveActivities,
        @RequestParam(required = false) String event,
        @RequestParam(required = false) Set<String> filter,
        @RequestParam(required = false) Set<String> dataElement,
        @RequestParam(required = false, defaultValue = "false") boolean includeAllDataElements,
        @RequestParam Map<String, String> parameters,
        IdSchemes idSchemes,
        Model model,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws WebMessageException {
        List<String> fields = Lists.newArrayList(contextService.getParameterValues("fields"));

        if (fields.isEmpty()) {
            fields.add("*");
            fields.add("dataValues");
        }

        //        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( attributeCc, attributeCos,
        //            false );

        //        if ( attributeOptionCombo == null )
        //        {
        //            throw new WebMessageException(
        //                conflict( "Illegal attribute option combo identifier: " + attributeCc + " " + attributeCos ) );
        //        }

        Set<String> eventIds = TextUtils.splitToSet(event, TextUtils.SEMICOLON);

        Set<String> assignedUserIds = TextUtils.splitToSet(assignedUser, TextUtils.SEMICOLON);

        lastUpdatedStartDate = lastUpdatedStartDate != null ? lastUpdatedStartDate : lastUpdated;

        skipPaging = PagerUtils.isSkipPaging(skipPaging, paging);

        EventSearchParams params = requestToSearchParamsMapper.map(
            project,
            activity,
            program,
            programStage,
            programStatus,
            followUp,
            orgUnit,
            ouMode,
            trackedEntityInstance,
            startDate,
            endDate,
            dueDateStart,
            dueDateEnd,
            lastUpdatedStartDate,
            lastUpdatedEndDate,
            null,
            status,
            /*attributeOptionCombo,*/idSchemes,
            page,
            pageSize,
            totalPages,
            skipPaging,
            getOrderParams(null),
            getGridOrderParams(order),
            false,
            eventIds,
            false,
            assignedUserMode,
            assignedUserIds,
            filter,
            dataElement,
            includeAllDataElements,
            includeDeleted,
            includeInactiveActivities
        );

        contextUtils.configureResponse(response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.NO_CACHE);

        return eventService.getEventsGrid(params);
    }

    @GetMapping(value = "/query", produces = ContextUtils.CONTENT_TYPE_EXCEL)
    public void queryEventsXls(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String activity,
        @RequestParam(required = false) String program,
        @RequestParam(required = false) String programStage,
        @RequestParam(required = false) EventStatus programStatus,
        @RequestParam(required = false) Boolean followUp,
        @RequestParam(required = false) String trackedEntityInstance,
        @RequestParam(required = false) String orgUnit,
        @RequestParam(required = false) OrganisationUnitSelectionMode ouMode,
        @RequestParam(required = false) AssignedUserSelectionMode assignedUserMode,
        @RequestParam(required = false) String assignedUser,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate,
        @RequestParam(required = false) Date dueDateStart,
        @RequestParam(required = false) Date dueDateEnd,
        @RequestParam(required = false) Date lastUpdated,
        @RequestParam(required = false) Date lastUpdatedStartDate,
        @RequestParam(required = false) Date lastUpdatedEndDate,
        @RequestParam(required = false) EventStatus status,
        @RequestParam(required = false) String attributeCc,
        @RequestParam(required = false) String attributeCos,
        @RequestParam(required = false) boolean skipMeta,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) boolean totalPages,
        @RequestParam(required = false) Boolean skipPaging,
        @RequestParam(required = false) Boolean paging,
        @RequestParam(required = false) List<OrderCriteria> order,
        @RequestParam(required = false) String attachment,
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
        @RequestParam(required = false, defaultValue = "false") boolean includeInactiveActivities,
        @RequestParam(required = false) String event,
        @RequestParam(required = false) Set<String> filter,
        @RequestParam(required = false) Set<String> dataElement,
        @RequestParam(required = false, defaultValue = "false") boolean includeAllDataElements,
        @RequestParam Map<String, String> parameters,
        IdSchemes idSchemes,
        Model model,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws Exception {
        List<String> fields = Lists.newArrayList(contextService.getParameterValues("fields"));

        if (fields.isEmpty()) {
            fields.addAll(Preset.ALL.getFields());
        }

        //        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( attributeCc, attributeCos,
        //            false );

        //        if ( attributeOptionCombo == null )
        //        {
        //            throw new WebMessageException(
        //                conflict( "Illegal attribute option combo identifier: " + attributeCc + " " + attributeCos ) );
        //        }

        Set<String> eventIds = TextUtils.splitToSet(event, TextUtils.SEMICOLON);

        Set<String> assignedUserIds = TextUtils.splitToSet(assignedUser, TextUtils.SEMICOLON);

        lastUpdatedStartDate = lastUpdatedStartDate != null ? lastUpdatedStartDate : lastUpdated;

        skipPaging = PagerUtils.isSkipPaging(skipPaging, paging);

        EventSearchParams params = requestToSearchParamsMapper.map(
            project,
            activity,
            program,
            programStage,
            programStatus,
            followUp,
            orgUnit,
            ouMode,
            trackedEntityInstance,
            startDate,
            endDate,
            dueDateStart,
            dueDateEnd,
            lastUpdatedStartDate,
            lastUpdatedEndDate,
            null,
            status,
            /*attributeOptionCombo,*/idSchemes,
            page,
            pageSize,
            totalPages,
            skipPaging,
            getOrderParams(null),
            getGridOrderParams(order),
            false,
            eventIds,
            false,
            assignedUserMode,
            assignedUserIds,
            filter,
            dataElement,
            includeAllDataElements,
            includeDeleted,
            includeInactiveActivities
        );

        contextUtils.configureResponse(response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.NO_CACHE);
        Grid grid = eventService.getEventsGrid(params);
        GridUtils.toXls(grid, response.getOutputStream());
    }

    //    @GetMapping(value = "/query", produces = ContextUtils.CONTENT_TYPE_CSV)
    @GetMapping(value = "/query", produces = {ContextUtils.CONTENT_TYPE_CSV,
        ContextUtils.CONTENT_TYPE_TEXT_CSV})
    public void queryEventsCsv(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String activity,
        @RequestParam(required = false) String program,
        @RequestParam(required = false) String programStage,
        @RequestParam(required = false) EventStatus programStatus,
        @RequestParam(required = false) Boolean followUp,
        @RequestParam(required = false) String trackedEntityInstance,
        @RequestParam(required = false) String orgUnit,
        @RequestParam(required = false) OrganisationUnitSelectionMode ouMode,
        @RequestParam(required = false) AssignedUserSelectionMode assignedUserMode,
        @RequestParam(required = false) String assignedUser,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate,
        @RequestParam(required = false) Date dueDateStart,
        @RequestParam(required = false) Date dueDateEnd,
        @RequestParam(required = false) Date lastUpdated,
        @RequestParam(required = false) Date lastUpdatedStartDate,
        @RequestParam(required = false) Date lastUpdatedEndDate,
        @RequestParam(required = false) EventStatus status,
        @RequestParam(required = false) String attributeCc,
        @RequestParam(required = false) String attributeCos,
        @RequestParam(required = false) boolean skipMeta,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) boolean totalPages,
        @RequestParam(required = false) Boolean skipPaging,
        @RequestParam(required = false) Boolean paging,
        @RequestParam(required = false) List<OrderCriteria> order,
        @RequestParam(required = false) String attachment,
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
        @RequestParam(required = false, defaultValue = "false") boolean includeInactiveActivities,
        @RequestParam(required = false) String event,
        @RequestParam(required = false) Set<String> filter,
        @RequestParam(required = false) Set<String> dataElement,
        @RequestParam(required = false, defaultValue = "false") boolean includeAllDataElements,
        @RequestParam Map<String, String> parameters,
        IdSchemes idSchemes,
        Model model,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws Exception {
        List<String> fields = Lists.newArrayList(contextService.getParameterValues("fields"));

        if (fields.isEmpty()) {
            fields.addAll(Preset.ALL.getFields());
        }

        //        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( attributeCc, attributeCos,
        //            false );
        //
        //        if ( attributeOptionCombo == null )
        //        {
        //            throw new WebMessageException(
        //                conflict( "Illegal attribute option combo identifier: " + attributeCc + " " + attributeCos ) );
        //        }

        Set<String> eventIds = TextUtils.splitToSet(event, TextUtils.SEMICOLON);

        Set<String> assignedUserIds = TextUtils.splitToSet(assignedUser, TextUtils.SEMICOLON);

        lastUpdatedStartDate = lastUpdatedStartDate != null ? lastUpdatedStartDate : lastUpdated;

        skipPaging = PagerUtils.isSkipPaging(skipPaging, paging);

        EventSearchParams params = requestToSearchParamsMapper.map(
            project,
            activity,
            program,
            programStage,
            programStatus,
            followUp,
            orgUnit,
            ouMode,
            trackedEntityInstance,
            startDate,
            endDate,
            dueDateStart,
            dueDateEnd,
            lastUpdatedStartDate,
            lastUpdatedEndDate,
            null,
            status,
            /*attributeOptionCombo,*/idSchemes,
            page,
            pageSize,
            totalPages,
            skipPaging,
            getOrderParams(null),
            getGridOrderParams(order),
            false,
            eventIds,
            false,
            assignedUserMode,
            assignedUserIds,
            filter,
            dataElement,
            includeAllDataElements,
            includeDeleted,
            includeInactiveActivities
        );

        contextUtils.configureResponse(response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.NO_CACHE);

        Grid grid = eventService.getEventsGrid(params);
        GridUtils.toCsv(grid, response.getWriter());
    }

    // -------------------------------------------------------------------------
    // Object Read
    // -------------------------------------------------------------------------

    //    @GetMapping
    //    public @ResponseBody RootNode getEvents(
    //        EventCriteria eventCriteria, @RequestParam Map<String, String> parameters, Model model,
    //        HttpServletResponse response,
    //        HttpServletRequest request )
    //    {
    //        WebOptions options = new WebOptions( parameters );
    //        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
    //
    //        if ( fields.isEmpty() )
    //        {
    //            fields.add(
    //                "event,uid,program,programStage,programType,status,assignedUser,orgUnit,orgUnitName,eventDate,orgUnit,orgUnitName,created,lastUpdated,followup,dataValues" );
    //        }
    //
    //        EventSearchParams params = requestToSearchParamsMapper.map( eventCriteria );
    //
    //        setParamBasedOnFieldParameters( params, fields );
    //
    //        Events events = eventService.getEvents( params );
    //
    //        if ( hasHref( fields, eventCriteria.getSkipEventId() ) )
    //        {
    //            events.getEvents()
    //                .forEach( e -> e.setHref( ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + e.getEvent() ) );
    //        }
    //
    //        if ( !eventCriteria.isSkipMeta() && params.getProgram() != null )
    //        {
    //            events.setMetaData( getMetaData( params.getProgram() ) );
    //        }
    //
    //        model.addAttribute( "model", events );
    //        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );
    //
    //        RootNode rootNode = NodeUtils.createMetadata();
    //
    //        addPager( params, events, rootNode );
    //
    //        if ( !StringUtils.isEmpty( eventCriteria.getAttachment() ) )
    //        {
    //            response.addHeader( ContextUtils.HEADER_CONTENT_DISPOSITION,
    //                "attachment; filename=" + eventCriteria.getAttachment() );
    //            response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
    //        }
    //
    //        rootNode.addChild(
    //            fieldFilterService.toCollectionNode( Event.class, new FieldFilterParams( events.getEvents(), fields ) ) );
    //
    //        return rootNode;
    //    }

    //    /**
    //     * Adds a pager object to the root node respecting the given params.
    //     *
    //     * @param params
    //     * @param events
    //     * @param rootNode
    //     */
    //    private void addPager( final EventSearchParams params, final Events events, final RootNode rootNode )
    //    {
    //        if ( events.getPager() != null )
    //        {
    //            if ( params.isTotalPages() )
    //            {
    //                rootNode.addChild( NodeUtils.createPager( events.getPager() ) );
    //            }
    //            else
    //            {
    //                rootNode.addChild( NodeUtils.createSlimPager( (SlimPager) events.getPager() ) );
    //            }
    //        }
    //    }

    @GetMapping(produces = {"application/csv", "application/csv+gzip", "application/csv+zip", "text/csv"})
    public void getCsvEvents(
        EventCriteria eventCriteria,
        @RequestParam(required = false, defaultValue = "false") boolean skipHeader,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws IOException, WebMessageException {
        EventSearchParams params = requestToSearchParamsMapper.map(eventCriteria);

        Events events = eventService.getEvents(params);

        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/csv");

        if (ContextUtils.isAcceptCsvGzip(request)) {
            response.addHeader(ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary");
            outputStream = new GZIPOutputStream(outputStream);
            response.setContentType("application/csv+gzip");
        } else if (ContextUtils.isAcceptCsvZip(request)) {
            response.addHeader(ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary");
            response.setContentType("application/csv+zip");
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            zos.putNextEntry(new ZipEntry("events.csv"));
            outputStream = zos;
        }

        if (!StringUtils.isEmpty(eventCriteria.getAttachment())) {
            response.addHeader("Content-Disposition", "attachment; filename=" + eventCriteria.getAttachment());
        }

        csvEventService.writeEvents(outputStream, events.getEvents(), !skipHeader);
    }

    // -------------------------------------------------------------------------
    // Rows Read
    // -------------------------------------------------------------------------

    @GetMapping("/eventRows")
    public @ResponseBody
    EventRows getEventRows(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String activity,
        @RequestParam(required = false) String program,
        @RequestParam(required = false) String orgUnit,
        @RequestParam(required = false) OrganisationUnitSelectionMode ouMode,
        @RequestParam(required = false) EventStatus programStatus,
        @RequestParam(required = false) EventStatus eventStatus,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate,
        @RequestParam(required = false) String attributeCc,
        @RequestParam(required = false) String attributeCos,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) boolean totalPages,
        @RequestParam(required = false) Boolean skipPaging,
        @RequestParam(required = false) Boolean paging,
        @RequestParam(required = false) List<OrderCriteria> order,
        @RequestParam(required = false) Boolean skipEventId,
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
        @RequestParam(required = false, defaultValue = "false") boolean includeInactiveActivities,
        @RequestParam Map<String, String> parameters,
        IdSchemes idSchemes,
        Model model
    ) throws WebMessageException {
        //        CategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( attributeCc, attributeCos,
        //            true );

        skipPaging = PagerUtils.isSkipPaging(skipPaging, paging);

        EventSearchParams params = requestToSearchParamsMapper.map(
            project,
            activity,
            program,
            null,
            programStatus,
            null,
            orgUnit,
            ouMode,
            null,
            startDate,
            endDate,
            null,
            null,
            null,
            null,
            null,
            eventStatus,/*attributeOptionCombo,*/
            idSchemes,
            page,
            pageSize,
            totalPages,
            skipPaging,
            getOrderParams(order),
            getGridOrderParams(order),
            true,
            null,
            skipEventId,
            null,
            null,
            null,
            null,
            false,
            includeDeleted,
            includeInactiveActivities
        );

        return eventRowService.getEventRows(params);
    }

    @GetMapping("/{uid}")
    public @ResponseBody
    Event getEvent(
        @PathVariable("uid") String uid,
        @RequestParam Map<String, String> parameters,
        Model model,
        HttpServletRequest request
    ) throws Exception {
        Event event = eventService.getEvent(programStageInstanceService.getProgramStageInstance(uid), true);

        if (event == null) {
            throw new WebMessageException(notFound("Event not found for ID " + uid));
        }

        event.setHref(ContextUtils.getRootPath(request) + RESOURCE_PATH + "/" + uid);

        return event;
    }

    @GetMapping("/files")
    public void getEventDataValueFile(
        @RequestParam String eventUid,
        @RequestParam String dataElementUid,
        @RequestParam(required = false) ImageFileDimension dimension,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws Exception {
        Event event = eventService.getEvent(programStageInstanceService.getProgramStageInstance(eventUid), true);

        if (event == null) {
            throw new WebMessageException(notFound("Event not found for ID " + eventUid));
        }

        DataElement dataElement = dataElementService.getDataElement(dataElementUid);

        if (dataElement == null) {
            throw new WebMessageException(notFound("DataElement not found for ID " + dataElementUid));
        }

        if (!dataElement.isFileType()) {
            throw new WebMessageException(conflict("DataElement must be of type file"));
        }

        // ---------------------------------------------------------------------
        // Get file resource
        // ---------------------------------------------------------------------

        String uid = null;

        for (DataValue value : event.getDataValues()) {
            if (value.getDataElement() != null && value.getDataElement().equals(dataElement.getUid())) {
                uid = value.getValue();
                break;
            }
        }

        if (uid == null) {
            throw new WebMessageException(conflict("DataElement must be of type file"));
        }

        FileResource fileResource = fileResourceService.getFileResource(uid);

        if (fileResource == null || fileResource.getDomain() != FileResourceDomain.DATA_VALUE) {
            throw new WebMessageException(notFound("A data value file resource with id " + uid + " does not exist."));
        }

        if (fileResource.getStorageStatus() != FileResourceStorageStatus.STORED) {
            // -----------------------------------------------------------------
            // The FileResource exists and is tied to DataValue, however the
            // underlying file content still not stored to external file store
            // -----------------------------------------------------------------

            throw new WebMessageException(
                conflict(
                    "The content is being processed and is not available yet. Try again later.",
                    "The content requested is in transit to the file store and will be available at a later time."
                )
                    .setResponse(new FileResourceWebMessageResponse(fileResource))
            );
        }

        FileResourceUtils.setImageFileDimensions(fileResource, MoreObjects.firstNonNull(dimension, ImageFileDimension.ORIGINAL));

        response.setContentType(fileResource.getContentType());
        response.setContentLengthLong(fileResource.getContentLength());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileResource.getName());

        try {
            fileResourceService.copyFileResourceContent(fileResource, response.getOutputStream());
        } catch (IOException e) {
            throw new WebMessageException(
                error(
                    "Failed fetching the file from storage",
                    "There was an exception when trying to fetch the file from the storage backend, could be network or filesystem related"
                )
            );
        }
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    //    @PostMapping(consumes = APPLICATION_XML_VALUE)
    //    @ResponseBody
    //    public WebMessage postXmlEvent(@RequestParam(defaultValue = "CREATE_AND_UPDATE") ImportStrategy strategy,
    //                                   HttpServletRequest request, ImportOptions importOptions) {
    //        return postEvent(strategy, request, importOptions, this::safeAddEventsXml, this::safeGetEventsXml);
    //    }

    //    @SneakyThrows
    //    private List<Event> safeGetEventsXml(InputStream inputStream) {
    //        return eventService.getEventsXml(inputStream);
    //    }
    //
    //    @SneakyThrows
    //    private ImportSummaries safeAddEventsXml(InputStream inputStream, ImportOptions importOptions) {
    //        return eventService.addEventsXml(inputStream, importOptions);
    //    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage postJsonEvent(
        @RequestParam(defaultValue = "CREATE_AND_UPDATE") ImportStrategy strategy,
        HttpServletRequest request,
        ImportOptions importOptions
    ) {
        return postEvent(strategy, request, importOptions, this::safeAddEventsJson, this::safeGetEventsJson);
    }

    @SneakyThrows
    private List<Event> safeGetEventsJson(InputStream inputStream) {
        return eventService.getEventsJson(inputStream);
    }

    @SneakyThrows
    private ImportSummaries safeAddEventsJson(InputStream inputStream, ImportOptions importOptions) {
        return eventService.addEventsJson(inputStream, importOptions);
    }

    @SneakyThrows
    private WebMessage postEvent(
        ImportStrategy strategy,
        HttpServletRequest request,
        ImportOptions importOptions,
        BiFunction<InputStream, ImportOptions, ImportSummaries> eventAdder,
        Function<InputStream, List<Event>> eventConverter
    ) {
        importOptions.setImportStrategy(strategy);
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
        importOptions.setIdSchemes(getIdSchemesFromParameters(importOptions.getIdSchemes(), contextService.getParameterValuesMap()));

        if (!importOptions.isAsync()) {
            ImportSummaries importSummaries = eventAdder.apply(inputStream, importOptions);
            importSummaries.setImportOptions(importOptions);

            importSummaries
                .getImportSummaries()
                .stream()
                .filter(importSummary ->
                    !importOptions.isDryRun() &&
                        !importSummary.getStatus().equals(ImportStatus.ERROR) &&
                        !importOptions.getImportStrategy().isDelete() &&
                        (!importOptions.getImportStrategy().isSync() || importSummary.getImportCount().getDeleted() == 0)
                )
                .forEach(importSummary ->
                    importSummary.setHref(ContextUtils.getRootPath(request) + RESOURCE_PATH + "/" + importSummary.getReference())
                );

            if (importSummaries.getImportSummaries().size() == 1) {
                ImportSummary importSummary = importSummaries.getImportSummaries().get(0);
                importSummary.setImportOptions(importOptions);

                if (!importOptions.isDryRun() && !importSummary.getStatus().equals(ImportStatus.ERROR)) {
                    return importSummaries(importSummaries).setLocation(RESOURCE_PATH + "/" + importSummary.getReference());
                }
            }

            return importSummaries(importSummaries);
        }
        return startAsyncImport(importOptions, eventConverter.apply(inputStream));
    }

    @PostMapping(value = "/{uid}/note", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage postJsonEventForNote(@PathVariable("uid") String uid, HttpServletRequest request, ImportOptions importOptions)
        throws IOException {
        if (!programStageInstanceService.programStageInstanceExists(uid)) {
            return notFound("Event not found for ID " + uid);
        }

        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
        Event event = renderService.fromJson(inputStream, Event.class);
        event.setEvent(uid);

        eventService.updateEventForNote(event);
        return ok("Event updated: " + uid);
    }

    @PostMapping(consumes = {"application/csv", "text/csv"})
    @ResponseBody
    public WebMessage postCsvEvents(
        @RequestParam(required = false, defaultValue = "false") boolean skipFirst,
        HttpServletRequest request,
        ImportOptions importOptions
    ) throws IOException, ParseException {
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());

        List<Event> events = csvEventService.readEvents(inputStream, skipFirst);

        if (!importOptions.isAsync()) {
            ImportSummaries importSummaries = eventService.addEvents(events, importOptions, null);
            importSummaries.setImportOptions(importOptions);
            return importSummaries(importSummaries);
        }
        return startAsyncImport(importOptions, events);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    //    @PutMapping(value = "/{uid}", consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    //    @ResponseBody
    //    public WebMessage putXmlEvent(HttpServletRequest request,
    //                                  @PathVariable("uid") String uid, ImportOptions importOptions)
    //        throws IOException {
    //        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
    //        Event updatedEvent = renderService.fromXml(inputStream, Event.class);
    //        updatedEvent.setEvent(uid);
    //
    //        return updateEvent(updatedEvent, false, importOptions);
    //    }

    @PutMapping(value = "/{uid}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putJsonEvent(HttpServletRequest request, @PathVariable("uid") String uid, ImportOptions importOptions)
        throws IOException {
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
        Event updatedEvent = renderService.fromJson(inputStream, Event.class);
        updatedEvent.setEvent(uid);

        return updateEvent(updatedEvent, false, importOptions);
    }

    private WebMessage updateEvent(Event updatedEvent, boolean singleValue, ImportOptions importOptions) {
        ImportSummary importSummary = eventService.updateEvent(updatedEvent, singleValue, importOptions, false);
        importSummary.setImportOptions(importOptions);
        return importSummary(importSummary);
    }

    @PutMapping(value = "/{uid}/{dataElementUid}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putJsonEventSingleValue(
        HttpServletRequest request,
        @PathVariable("uid") String uid,
        @PathVariable("dataElementUid") String dataElementUid
    ) throws IOException {
        DataElement dataElement = dataElementService.getDataElement(dataElementUid);

        if (dataElement == null) {
            return notFound("DataElement not found for ID " + dataElementUid);
        }

        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
        Event updatedEvent = renderService.fromJson(inputStream, Event.class);
        updatedEvent.setEvent(uid);

        return updateEvent(updatedEvent, true, null);
    }

    @PutMapping(value = "/{uid}/eventDate", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putJsonEventForEventDate(HttpServletRequest request, @PathVariable("uid") String uid, ImportOptions importOptions)
        throws IOException {
        if (!programStageInstanceService.programStageInstanceExists(uid)) {
            return notFound("Event not found for ID " + uid);
        }

        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());
        Event updatedEvent = renderService.fromJson(inputStream, Event.class);
        updatedEvent.setEvent(uid);

        eventService.updateEventForEventDate(updatedEvent);
        return ok("Event updated " + uid);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @DeleteMapping("/{uid}")
    @ResponseBody
    public WebMessage deleteEvent(@PathVariable("uid") String uid) {
        return importSummary(eventService.deleteEvent(uid));
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Starts an asynchronous import task.
     *
     * @param importOptions the ImportOptions.
     * @param events        the events to import.
     */
    private WebMessage startAsyncImport(ImportOptions importOptions, List<Event> events) {
        JobConfiguration jobId = new JobConfiguration(
            "inMemoryEventImport",
            EVENT_IMPORT,
            currentUserService.getCurrentUser().getUid(),
            true
        );
        taskExecutor.executeTask(new ImportEventsTask(events, eventService, importOptions, jobId));

        return jobConfigurationReport(jobId).setLocation("/system/tasks/" + EVENT_IMPORT);
    }

    private boolean fieldsContains(String match, List<String> fields) {
        for (String field : fields) {
            // For now assume href/access if * or preset is requested
            if (field.contains(match) || field.equals("*") || field.startsWith(":")) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasHref(List<String> fields, Boolean skipEventId) {
        return (skipEventId == null || !skipEventId) && fieldsContains("href", fields);
    }

    private List<OrderParam> getOrderParams(List<OrderCriteria> order) {
        if (order != null && !order.isEmpty()) {
            return QueryUtils.filteredBySchema(order, schema);
        }
        return Collections.emptyList();
    }

    private List<OrderParam> getGridOrderParams(List<OrderCriteria> order) {
        if (order != null && !order.isEmpty()) {
            return order.stream().map(OrderCriteria::toOrderParam).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Map<Object, Object> getMetaData(Program program) {
        Map<Object, Object> metaData = new HashMap<>();

        if (program != null) {
            Map<String, String> dataElements = new HashMap<>();

            for (DataElement de : program.getDataElements()) {
                dataElements.put(de.getUid(), de.getDisplayName());
            }

            metaData.put(META_DATA_KEY_DE, dataElements);
        }

        return metaData;
    }

    private IdSchemes getIdSchemesFromParameters(IdSchemes idSchemes, Map<String, List<String>> params) {
        String idScheme = getParamValue(params, "idScheme");

        if (idScheme != null) {
            idSchemes.setIdScheme(idScheme);
        }

        String programStageInstanceIdScheme = getParamValue(params, "programStageInstanceIdScheme");

        if (programStageInstanceIdScheme != null) {
            idSchemes.setProgramStageInstanceIdScheme(programStageInstanceIdScheme);
        }

        return idSchemes;
    }

    private String getParamValue(Map<String, List<String>> params, String key) {
        return params.get(key) != null ? params.get(key).get(0) : null;
    }

    private void setParamBasedOnFieldParameters(EventSearchParams params, List<String> fields) {
        String joined = Joiner.on("").join(fields);

        if (joined.contains("*")) {
            params.setIncludeAllDataElements(true);
            params.setIncludeAttributes(true);
            params.setIncludeRelationships(true);
        }

        if (joined.contains("relationships")) {
            params.setIncludeRelationships(true);
        }
    }
}
