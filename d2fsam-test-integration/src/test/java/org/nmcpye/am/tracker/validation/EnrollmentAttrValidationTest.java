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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramTrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.TrackerTest;
import org.nmcpye.am.tracker.report.ImportReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.nmcpye.am.tracker.Assertions.*;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Slf4j
class EnrollmentAttrValidationTest extends TrackerTest {

    @Autowired
    protected TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    @Autowired
    private TrackerImportService trackerImportService;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/tracker_basic_metadata_mandatory_attr.json");
        injectAdminUser();
        assertNoErrors(
            trackerImportService.importTracker(fromJson("tracker/validations/enrollments_te_te-data_2.json")));
        manager.flush();
    }

    @Test
    void failValidationWhenTrackedEntityAttributeHasWrongOptionValue()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_with_invalid_option_value.json");
        ImportReport importReport = trackerImportService.importTracker(params);

        assertHasOnlyErrors(importReport, ValidationCode.E1125);
    }

    @Test
    void successValidationWhenTrackedEntityAttributeHasValidOptionValue()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_with_valid_option_value.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertNoErrors(importReport);
    }

    @Test
    void testAttributesMissingUid()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_attr-missing-uuid.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertHasOnlyErrors(importReport, ValidationCode.E1075);
    }

    @Test
    void testAttributesMissingValues()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_attr-missing-value.json");
        ImportReport importReport = trackerImportService.importTracker(params);
        assertHasOnlyErrors(importReport, ValidationCode.E1076);
    }

    @Test
    void testAttributesMissingTeA()
        throws IOException {
        TrackerImportParams params = fromJson("tracker/validations/enrollments_te_attr-non-existing.json");
        ImportReport importReport = trackerImportService.importTracker(params);
        assertHasOnlyErrors(importReport, ValidationCode.E1006);
    }

    @Test
    void testAttributesMissingMandatory()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_attr-missing-mandatory.json");

        ImportReport importReport = trackerImportService.importTracker(params);
        assertHasOnlyErrors(importReport, ValidationCode.E1018);
    }

    @Test
    void testAttributesUniquenessInSameTei()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_unique_attr_same_tei.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertNoErrors(importReport);
    }

    @Test
    void testAttributesUniquenessAlreadyInDB()
        throws IOException {
        TrackerImportParams params = fromJson("tracker/validations/enrollments_te_te-data_3.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertNoErrors(importReport);

        manager.flush();
        manager.clear();

        params = fromJson("tracker/validations/enrollments_te_unique_attr_same_tei.json");

        importReport = trackerImportService.importTracker(params);

        assertNoErrors(importReport);

        manager.flush();
        manager.clear();

        params = fromJson("tracker/validations/enrollments_te_unique_attr_in_db.json");

        importReport = trackerImportService.importTracker(params);

        assertHasOnlyErrors(importReport, ValidationCode.E1064);
    }

    @Test
    void testAttributesUniquenessInDifferentTeis()
        throws IOException {
        TrackerImportParams params = fromJson("tracker/validations/enrollments_te_te-data_3.json");
        assertNoErrors(trackerImportService.importTracker(params));
        manager.flush();
        manager.clear();
        params = fromJson("tracker/validations/enrollments_te_unique_attr.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertHasErrors(importReport, 2, ValidationCode.E1064);
    }

    @Test
    void testAttributesOnlyProgramAttrAllowed()
        throws IOException {
        TrackerImportParams params = fromJson(
            "tracker/validations/enrollments_te_attr-only-program-attr.json");

        ImportReport importReport = trackerImportService.importTracker(params);

        assertHasOnlyErrors(importReport, ValidationCode.E1019);
    }
}
