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
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.TrackerImportStrategy;
import org.nmcpye.am.tracker.TrackerTest;
import org.nmcpye.am.tracker.domain.*;
import org.nmcpye.am.tracker.report.ImportReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.nmcpye.am.tracker.Assertions.assertNoErrors;

class LastUpdateImportTest extends TrackerTest {
    @Autowired
    private TrackerImportService trackerImportService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    private TrackedEntity trackedEntity;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/simple_metadata.json");
        injectAdminUser();
        TrackerImportParams trackerImportParams = fromJson("tracker/single_tei.json");
        assertNoErrors(trackerImportService.importTracker(trackerImportParams));
        trackedEntity = trackerImportParams.getTrackedEntities().get(0);
        assertNoErrors(trackerImportService.importTracker(fromJson("tracker/single_enrollment.json")));
        manager.flush();
    }

    @Test
    void shouldUpdateTeiIfTeiIsUpdated()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/single_tei.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.UPDATE);
        Attribute attribute = Attribute.builder()
            .attribute(MetadataIdentifier.ofUid("toUpdate000"))
            .value("value")
            .build();
        trackedEntity.setAttributes(Collections.singletonList(attribute));
        Instant lastUpdateBefore = trackedEntityInstanceService
            .getTrackedEntityInstance(trackedEntity.getTrackedEntity()).getUpdated();
        assertNoErrors(trackerImportService.importTracker(trackerImportParams));
        assertTrue(manager.get(TrackedEntityInstance.class, trackedEntity.getTrackedEntity()).getUpdated()
            .toEpochMilli() > lastUpdateBefore.toEpochMilli());
    }

    @Test
    void shouldUpdateTeiIfEventIsUpdated()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/event_with_data_values.json");
        Instant lastUpdateBefore = trackedEntityInstanceService
            .getTrackedEntityInstance(trackedEntity.getTrackedEntity()).getUpdated();
        assertNoErrors(trackerImportService.importTracker(trackerImportParams));

        trackerImportParams = fromJson("tracker/event_with_updated_data_values.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.UPDATE);
        assertNoErrors(trackerImportService.importTracker(trackerImportParams));
        manager.clear();
        assertTrue(manager.get(TrackedEntityInstance.class, trackedEntity.getTrackedEntity()).getUpdated()
            .toEpochMilli() > lastUpdateBefore.toEpochMilli());
    }

    @Test
    void shouldUpdateTeiIfEnrollmentIsUpdated()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/single_enrollment.json");
        Instant lastUpdateBefore = trackedEntityInstanceService
            .getTrackedEntityInstance(trackedEntity.getTrackedEntity()).getUpdated();
        Enrollment enrollment = trackerImportParams.getEnrollments().get(0);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        trackerImportParams.setImportStrategy(TrackerImportStrategy.UPDATE);
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);
        manager.clear();
        assertTrue(manager.get(TrackedEntityInstance.class, trackedEntity.getTrackedEntity()).getUpdated()
            .toEpochMilli() > lastUpdateBefore.toEpochMilli());
    }
}
