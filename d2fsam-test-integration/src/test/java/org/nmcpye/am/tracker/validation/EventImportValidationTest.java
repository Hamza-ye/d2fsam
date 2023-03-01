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
package org.nmcpye.am.tracker.validation;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.program.ProgramStageInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.tracker.*;
import org.nmcpye.am.tracker.Assertions;
import org.nmcpye.am.tracker.report.ImportReport;
import org.nmcpye.am.tracker.report.TrackerTypeReport;
import org.nmcpye.am.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.nmcpye.am.tracker.Assertions.assertHasOnlyErrors;
import static org.nmcpye.am.tracker.Assertions.assertNoErrors;
import static org.nmcpye.am.tracker.TrackerImportStrategy.*;
import static org.nmcpye.am.tracker.validation.Users.USER_2;
import static org.nmcpye.am.tracker.validation.Users.USER_6;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventImportValidationTest extends TrackerTest {
    @Autowired
    protected TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    @Autowired
    private ProgramStageInstanceServiceExt programStageServiceInstance;

    @Autowired
    protected TrackerImportService trackerImportService;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/tracker_basic_metadata.json");
        injectAdminUser();

        assertNoErrors(trackerImportService.importTracker(fromJson(
            "tracker/validations/enrollments_te_te-data.json")));
        assertNoErrors(trackerImportService
            .importTracker(fromJson("tracker/validations/enrollments_te_enrollments-data.json")));
    }

    @Test
    @Order(1)
    void testInvalidEnrollmentPreventsValidEventFromBeingCreated()
        throws IOException {
        ImportReport importReport = trackerImportService
            .importTracker(fromJson("tracker/validations/invalid_enrollment_with_valid_event.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1070, ValidationCode.E5000);
    }

    @Test
    @Order(2)
    void failValidationWhenTrackedEntityAttributeHasWrongOptionValue()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events-with_invalid_option_value.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1125);
    }

    @Test
    @Order(3)
    void successWhenTrackedEntityAttributeHasValidOptionValue()
        throws IOException {
        ImportReport importReport = trackerImportService
            .importTracker(fromJson("tracker/validations/events-with_valid_option_value.json"));

        assertNoErrors(importReport);
    }

    @Test
    @Order(4)
    void testEventValidationOkAll()
        throws IOException {
        ImportReport importReport = trackerImportService
            .importTracker(fromJson("tracker/validations/events-with-registration.json"));

        assertNoErrors(importReport);
    }

    @Test
    @Order(5)
    void testEventValidationOkWithoutAttributeOptionCombo()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events-without-attribute-option-combo.json"));

        assertNoErrors(importReport);
    }

    @Test
    @Order(6)
    void testTrackerAndProgramEventUpdateSuccess()
        throws IOException {
        TrackerImportParams trackerBundleParams = fromJson(
            "tracker/validations/program_and_tracker_events.json");
        assertNoErrors(trackerImportService.importTracker(trackerBundleParams));

        trackerBundleParams.setImportStrategy(UPDATE);
        ImportReport importReport = trackerImportService.importTracker(trackerBundleParams);

        assertNoErrors(importReport);
    }

    @Disabled
    @Test
    void testCantWriteAccessCatCombo()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson(
            "tracker/validations/events-cat-write-access.json");
        User user = userService.getUser(USER_6);
        trackerImportParams.setUser(user);

        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);

        assertHasOnlyErrors(importReport, ValidationCode.E1099, ValidationCode.E1104,
            ValidationCode.E1096, ValidationCode.E1095);
    }

    @Test
    @Order(7)
    void testNoWriteAccessToOrg()
        throws IOException {
        TrackerImportParams trackerBundleParams = fromJson(
            "tracker/validations/events-with-registration.json");
        User user = userService.getUser(USER_2);
        trackerBundleParams.setUser(user);
        ImportReport importReport = trackerImportService.importTracker(trackerBundleParams);
        assertHasOnlyErrors(importReport, ValidationCode.E1000);
    }

    @Test
    @Order(8)
    void testNonRepeatableProgramStage()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson(
            "tracker/validations/events_non-repeatable-programstage_part1.json");

        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);

        assertNoErrors(importReport);

        trackerImportParams = fromJson("tracker/validations/events_non-repeatable-programstage_part2.json");

        importReport = trackerImportService.importTracker(trackerImportParams);

        assertHasOnlyErrors(importReport, ValidationCode.E1039);
    }

    @Test
    @Order(9)
    void testWrongScheduledDateString() {
        assertThrows(IOException.class,
            () -> fromJson("tracker/validations/events_error-no-wrong-date.json"));
    }

    @Disabled
    @Test
    void testEventProgramHasNonDefaultCategoryCombo()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_non-default-combo.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1055);
    }

    @Disabled
    @Test
    void testCategoryOptionComboNotFound()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_cant-find-cat-opt-combo.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1115);
    }

    @Disabled
    @Test
    void testCategoryOptionComboNotFoundGivenSubsetOfCategoryOptions()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_cant-find-aoc-with-subset-of-cos.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1117);
    }

    @Disabled
    @Test
    void testCOFoundButAOCNotFound()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_cant-find-aoc-but-co-exists.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1115);
    }

    @Disabled
    @Test
    void testCategoryOptionsNotFound()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_cant-find-cat-option.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1116);
    }

    @Disabled
    @Test
    void testAttributeCategoryOptionNotInProgramCC()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events-aoc-not-in-program-cc.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1054);
    }

    @Disabled
    @Test
    void testAttributeCategoryOptionAndCODoNotMatch()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events-aoc-and-co-dont-match.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1117);
    }

    @Disabled
    @Test
    void testAttributeCategoryOptionCannotBeFoundForEventProgramCCAndGivenCategoryOption()
        throws IOException {
        ImportReport importReport = trackerImportService.importTracker(fromJson(
            "tracker/validations/events_cant-find-cat-option-combo-for-given-cc-and-co.json"));

        assertHasOnlyErrors(importReport, ValidationCode.E1117);
    }

//    @Test
//    void testWrongDatesInCatCombo()
//        throws IOException {
//        ImportReport importReport = trackerImportService.importTracker(fromJson(
//            "tracker/validations/events_combo-date-wrong.json"));
//
//        assertHasOnlyErrors(importReport, ValidationCode.E1056, ValidationCode.E1057);
//    }

    @Test
    @Order(10)
    void testValidateAndAddNotesToEvent()
        throws IOException {
        Instant now = Instant.now();
        // When
        ImportReport importReport = createEvent("tracker/validations/events-with-notes-data.json");
        // Then
        // Fetch the UID of the newly created event
        final ProgramStageInstance programStageInstance = getEventFromReport(importReport);
        assertThat(programStageInstance.getComments(), hasSize(3));
        // Validate note content
        Stream.of("first note", "second note", "third note").forEach(t -> {
            Comment comment = getByComment(programStageInstance.getComments(), t);
            assertTrue(CodeGenerator.isValidUid(comment.getUid()));
            assertTrue(comment.getCreated().toEpochMilli() > now.toEpochMilli());
            assertTrue(comment.getUpdated().toEpochMilli() > now.toEpochMilli());
            assertNull(comment.getCreator());
            assertEquals(ADMIN_USER_UID, comment.getUpdatedBy().getUid());
        });
    }

    @Test
    @Order(11)
    void testValidateAndAddNotesToUpdatedEvent()
        throws IOException {
        Instant now = Instant.now();
        // Given -> Creates an event with 3 notes
        createEvent("tracker/validations/events-with-notes-data.json");
        // When -> Update the event and adds 3 more notes
        ImportReport importReport = createEvent(
            "tracker/validations/events-with-notes-update-data.json");
        // Then
        final ProgramStageInstance programStageInstance = getEventFromReport(importReport);
        assertThat(programStageInstance.getComments(), hasSize(6));
        // validate note content
        Stream.of("first note", "second note", "third note", "4th note", "5th note", "6th note").forEach(t -> {
            Comment comment = getByComment(programStageInstance.getComments(), t);
            assertTrue(CodeGenerator.isValidUid(comment.getUid()));
            assertTrue(comment.getCreated().toEpochMilli() > now.toEpochMilli());
            assertTrue(comment.getUpdated().toEpochMilli() > now.toEpochMilli());
            assertNull(comment.getCreator());
            assertEquals(ADMIN_USER_UID, comment.getUpdatedBy().getUid());
        });
    }

    @Test
    @Order(12)
    void testUpdateDeleteEventFails() {
        testDeletedEventFails(UPDATE);
    }

    @Test
    @Order(13)
    void testInsertDeleteEventFails() {
        testDeletedEventFails(CREATE_AND_UPDATE);
    }

    @SneakyThrows
    private void testDeletedEventFails(TrackerImportStrategy importStrategy) {
        // Given -> Creates an event
        createEvent("tracker/validations/events-with-notes-data.json");
        ProgramStageInstance event = programStageServiceInstance.getProgramStageInstance("uLxFbxfYDQE");
        assertNotNull(event);
        // When -> Soft-delete the event
        programStageServiceInstance.deleteProgramStageInstance(event);
        TrackerImportParams trackerBundleParams = fromJson(
            "tracker/validations/events-with-notes-data.json");
        trackerBundleParams.setImportStrategy(importStrategy);
        // When
        ImportReport importReport = trackerImportService.importTracker(trackerBundleParams);

        assertHasOnlyErrors(importReport, ValidationCode.E1082);
    }

    @Test
    @Order(14)
    void testEventDeleteOk()
        throws IOException {
        TrackerImportParams trackerBundleParams = fromJson(
            "tracker/validations/events-with-registration.json");

        ImportReport importReport = trackerImportService.importTracker(trackerBundleParams);

        assertNoErrors(importReport);

        manager.flush();
        manager.clear();

        TrackerImportParams paramsDelete = fromJson(
            "tracker/validations/event-data-delete.json");
        paramsDelete.setImportStrategy(DELETE);

        ImportReport importReportDelete = trackerImportService.importTracker(paramsDelete);
        assertNoErrors(importReportDelete);
        assertEquals(1, importReportDelete.getStats().getDeleted());
    }

    private ImportReport createEvent(String jsonPayload)
        throws IOException {
        // Given
        TrackerImportParams trackerBundleParams = fromJson(jsonPayload);
        trackerBundleParams.setImportStrategy(CREATE_AND_UPDATE);
        // When
        ImportReport importReport = trackerImportService.importTracker(trackerBundleParams);
        // Then
        assertNoErrors(importReport);
        return importReport;
    }

    private Comment getByComment(List<Comment> comments, String commentText) {
        for (Comment comment : comments) {
            if (comment.getCommentText().startsWith(commentText)
                || comment.getCommentText().endsWith(commentText)) {
                return comment;
            }
        }
        fail("Can't find a comment starting or ending with " + commentText);
        return null;
    }

    private ProgramStageInstance getEventFromReport(ImportReport importReport) {
        final Map<TrackerType, TrackerTypeReport> typeReportMap = importReport.getPersistenceReport()
            .getTypeReportMap();
        String newEvent = typeReportMap.get(TrackerType.EVENT).getEntityReportMap().get(0).getUid();
        return programStageServiceInstance.getProgramStageInstance(newEvent);
    }
}
