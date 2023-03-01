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
package org.nmcpye.am.webapi.controller.tracker.imports;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.OpenApi;
import org.nmcpye.am.commons.util.StreamUtils;
import org.nmcpye.am.dxf2.events.event.csv.CsvEventService;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.scheduling.JobType;
import org.nmcpye.am.system.notification.Notification;
import org.nmcpye.am.system.notification.Notifier;
import org.nmcpye.am.tracker.TrackerBundleReportMode;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.job.TrackerJobWebMessageResponse;
import org.nmcpye.am.tracker.report.ImportReport;
import org.nmcpye.am.tracker.report.Status;
import org.nmcpye.am.user.CurrentUser;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.exception.InvalidEnumValueException;
import org.nmcpye.am.webapi.controller.exception.NotFoundException;
import org.nmcpye.am.webapi.controller.tracker.view.Event;
import org.nmcpye.am.webapi.service.ContextService;
import org.nmcpye.am.webapi.utils.ContextUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.ok;
import static org.nmcpye.am.webapi.controller.tracker.TrackerControllerSupport.RESOURCE_PATH;
import static org.nmcpye.am.webapi.utils.ContextUtils.setNoStore;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@OpenApi.Tags("tracker")
@RestController
@RequestMapping(value = "/" + RESOURCE_PATH)
@RequiredArgsConstructor
public class TrackerImportController {
    static final String TRACKER_JOB_ADDED = "Tracker job added";

    private final TrackerImporter trackerImporter;

    private final TrackerImportService trackerImportService;

    private final CsvEventService<Event> csvEventService;

    private final ContextService contextService;

    private final Notifier notifier;

    @OpenApi.Param(TrackerBundleParams.class)
    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage asyncPostJsonTracker(HttpServletRequest request, HttpServletResponse response,
                                           @CurrentUser User currentUser,
                                           @RequestBody TrackerBundleParams trackerBundleParams)
        throws InvalidEnumValueException {

        String jobId = CodeGenerator.generateUid();
        TrackerImportRequest trackerImportRequest = TrackerImportRequest.builder()
            .trackerBundleParams(trackerBundleParams)
            .contextService(contextService)
            .userUid(currentUser.getUid())
            .isAsync(true)
            .uid(jobId)
            .authentication(SecurityContextHolder.getContext().getAuthentication())
            .build();

        TrackerImportParamsValidator.validateRequest(trackerImportRequest);

        trackerImporter.importTracker(trackerImportRequest);

        String location = ContextUtils.getRootPath(request) + "/tracker/jobs/" + jobId;

        return ok(TRACKER_JOB_ADDED)
            .setLocation("/tracker/jobs/" + jobId)
            .setResponse(TrackerJobWebMessageResponse.builder().id(jobId).location(location).build());
    }

    @OpenApi.Param(TrackerBundleParams.class)
    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE, params = {"async=false"})
    public ResponseEntity<ImportReport> syncPostJsonTracker(
        @RequestParam(defaultValue = "errors", required = false) String reportMode, @CurrentUser User currentUser,
        @RequestBody TrackerBundleParams trackerBundleParams)
        throws InvalidEnumValueException {

        TrackerImportRequest trackerImportRequest = TrackerImportRequest.builder()
            .trackerBundleParams(trackerBundleParams)
            .contextService(contextService)
            .userUid(currentUser.getUid())
            .trackerBundleReportMode(TrackerBundleReportMode.getTrackerBundleReportMode(reportMode))
            .uid(CodeGenerator.generateUid())
            .build();

        TrackerImportParamsValidator.validateRequest(trackerImportRequest);

        ImportReport importReport = trackerImporter.importTracker(trackerImportRequest);

        ResponseEntity.BodyBuilder builder = importReport.getStatus() == Status.ERROR
            ? ResponseEntity.status(HttpStatus.CONFLICT)
            : ResponseEntity.ok();

        return builder.body(importReport);
    }

    @PostMapping(value = "", consumes = {"application/csv", "text/csv"}, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage asyncPostCsvTracker(HttpServletRequest request,
                                          @CurrentUser User currentUser,
                                          @RequestParam(required = false, defaultValue = "true") boolean skipFirst)
        throws IOException,
        ParseException,
        InvalidEnumValueException {
        String jobId = CodeGenerator.generateUid();
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());

        List<Event> events = csvEventService.readEvents(inputStream, skipFirst);

        TrackerBundleParams trackerBundleParams = TrackerBundleParams.builder()
            .events(events)
            .build();
        TrackerImportRequest trackerImportRequest = TrackerImportRequest.builder()
            .trackerBundleParams(trackerBundleParams)
            .contextService(contextService)
            .userUid(currentUser.getUid())
            .isAsync(true)
            .uid(jobId)
            .authentication(SecurityContextHolder.getContext().getAuthentication())
            .build();

        TrackerImportParamsValidator.validateRequest(trackerImportRequest);

        trackerImporter.importTracker(trackerImportRequest);

        String location = ContextUtils.getRootPath(request) + "/tracker/jobs/" + jobId;

        return ok(TRACKER_JOB_ADDED)
            .setLocation("/tracker/jobs/" + jobId)
            .setResponse(TrackerJobWebMessageResponse.builder().id(jobId).location(location).build());
    }

    @PostMapping(value = "", consumes = {"application/csv",
        "text/csv"}, produces = APPLICATION_JSON_VALUE, params = {"async=false"})
    public ResponseEntity<ImportReport> syncPostCsvTracker(
        HttpServletRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean skipFirst,
        @RequestParam(defaultValue = "errors", required = false) String reportMode, @CurrentUser User currentUser)
        throws IOException,
        ParseException,
        InvalidEnumValueException {
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat(request.getInputStream());

        List<Event> events = csvEventService.readEvents(inputStream, skipFirst);
        TrackerBundleParams trackerBundleParams = TrackerBundleParams.builder()
            .events(events)
            .build();
        TrackerImportRequest trackerImportRequest = TrackerImportRequest.builder()
            .trackerBundleParams(trackerBundleParams)
            .contextService(contextService)
            .userUid(currentUser.getUid())
            .trackerBundleReportMode(TrackerBundleReportMode.getTrackerBundleReportMode(reportMode))
            .uid(CodeGenerator.generateUid())
            .build();

        TrackerImportParamsValidator.validateRequest(trackerImportRequest);

        ImportReport importReport = trackerImporter.importTracker(trackerImportRequest);

        ResponseEntity.BodyBuilder builder = importReport.getStatus() == Status.ERROR
            ? ResponseEntity.status(HttpStatus.CONFLICT)
            : ResponseEntity.ok();

        return builder.body(importReport);
    }

    @GetMapping(value = "/jobs/{uid}", produces = APPLICATION_JSON_VALUE)
    public Deque<Notification> getJob(@PathVariable String uid, HttpServletResponse response)
        throws HttpStatusCodeException {
        setNoStore(response);
        return notifier.getNotificationsByJobId(JobType.TRACKER_IMPORT_JOB, uid);
    }

    @GetMapping(value = "/jobs/{uid}/report", produces = APPLICATION_JSON_VALUE)
    public ImportReport getJobReport(@PathVariable String uid,
                                     @RequestParam(defaultValue = "errors", required = false) String reportMode,
                                     HttpServletResponse response)
        throws HttpStatusCodeException,
        NotFoundException {
        TrackerBundleReportMode trackerBundleReportMode = TrackerBundleReportMode
            .getTrackerBundleReportMode(reportMode);

        setNoStore(response);

        return Optional.ofNullable(notifier
            .getJobSummaryByJobId(JobType.TRACKER_IMPORT_JOB, uid))
            .map(report -> trackerImportService.buildImportReport((ImportReport) report,
                trackerBundleReportMode))
            .orElseThrow(() -> NotFoundException.notFoundUid(uid));
    }
}
