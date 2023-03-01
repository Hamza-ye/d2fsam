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
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.TrackerImportStrategy;
import org.nmcpye.am.tracker.TrackerTest;
import org.nmcpye.am.tracker.report.ImportReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.nmcpye.am.tracker.Assertions.assertNoErrors;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class TrackedEntityProgramAttributeTest extends TrackerTest {

    @Autowired
    private TrackerImportService trackerImportService;

    @Autowired
    private TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/te_program_with_tea_metadata.json");
        injectAdminUser();
    }

    @Test
    void testTrackedEntityProgramAttributeValue()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/te_program_with_tea_data.json");
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(0);
        List<TrackedEntityAttributeValue> attributeValues = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());
    }

    @Test
    void testTrackedEntityProgramAttributeValueUpdate()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/te_program_with_tea_data.json");
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(0);
        List<TrackedEntityAttributeValue> attributeValues = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());
        manager.clear();
        // update
        trackerImportParams = fromJson("tracker/te_program_with_tea_update_data.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.CREATE_AND_UPDATE);
        importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        trackedEntityInstance = trackedEntityInstances.get(0);
        attributeValues = trackedEntityAttributeValueService.getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());
    }

    @Test
    void testTrackedEntityProgramAttributeValueUpdateAndDelete()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/te_program_with_tea_data.json");
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(0);
        List<TrackedEntityAttributeValue> attributeValues = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());
        manager.clear();
        // update
        trackerImportParams = fromJson("tracker/te_program_with_tea_update_data.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.CREATE_AND_UPDATE);
        importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        trackedEntityInstance = trackedEntityInstances.get(0);
        attributeValues = trackedEntityAttributeValueService.getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(5, attributeValues.size());
        manager.clear();
        // delete
        trackerImportParams = fromJson("tracker/te_program_with_tea_delete_data.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.DELETE);
        importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(0, trackedEntityInstances.size());
    }
}
