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
package org.nmcpye.am.tracker.bundle;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentityattributevalue.*;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.TrackerImportStrategy;
import org.nmcpye.am.tracker.TrackerTest;
import org.nmcpye.am.tracker.report.ImportReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.nmcpye.am.tracker.Assertions.assertNoErrors;

/**
 * @author Zubair Asghar
 */
class TrackedEntityAttributeValueAuditTest extends TrackerTest {
    @Autowired
    private TrackerImportService trackerImportService;

    @Autowired
    private TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private TrackedEntityAttributeValueAuditServiceExt attributeValueAuditService;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/te_program_with_tea_allow_audit_metadata.json");
        injectAdminUser();
    }

    @Test
    void testTrackedEntityAttributeValueAuditCreate()
        throws IOException {
        assertNoErrors(
            trackerImportService.importTracker(fromJson("tracker/te_program_with_tea_data.json")));

        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(0);
        List<TrackedEntityAttributeValue> attributeValues = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());

        List<TrackedEntityAttribute> attributes = attributeValues.stream()
            .map(TrackedEntityAttributeValue::getAttribute).collect(Collectors.toList());
        List<TrackedEntityAttributeValueAudit> attributeValueAudits = attributeValueAuditService
            .getTrackedEntityAttributeValueAudits(new TrackedEntityAttributeValueAuditQueryParams()
                .setTrackedEntityAttributes(attributes)
                .setTrackedEntityInstances(trackedEntityInstances)
                .setAuditTypes(List.of(AuditType.CREATE)));
        assertEquals(5, attributeValueAudits.size());
    }

    @Test
    void testTrackedEntityAttributeValueAuditDelete()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/te_program_with_tea_data.json");

        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);
        List<TrackedEntityAttributeValue> attributeValues1 = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(manager.getAll(TrackedEntityInstance.class).get(0));
        List<TrackedEntityAttribute> attributes1 = attributeValues1.stream()
            .map(TrackedEntityAttributeValue::getAttribute).collect(Collectors.toList());
        trackerImportParams = fromJson("tracker/te_program_with_tea_null_data.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.CREATE_AND_UPDATE);
        importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);
        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        List<TrackedEntityAttributeValueAudit> attributeValueAudits = attributeValueAuditService
            .getTrackedEntityAttributeValueAudits(new TrackedEntityAttributeValueAuditQueryParams()
                .setTrackedEntityAttributes(attributes1)
                .setTrackedEntityInstances(trackedEntityInstances)
                .setAuditTypes(List.of(AuditType.DELETE)));
        assertEquals(1, attributeValueAudits.size());

        attributeValueAudits = attributeValueAuditService.getTrackedEntityAttributeValueAudits(
            new TrackedEntityAttributeValueAuditQueryParams()
                .setTrackedEntityAttributes(attributes1)
                .setTrackedEntityInstances(trackedEntityInstances)
                .setAuditTypes(List.of(AuditType.UPDATE)));
        assertEquals(1, attributeValueAudits.size());
    }
}
