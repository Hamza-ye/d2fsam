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
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.program.ProgramStageInstanceRepositoryExt;
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
class TrackerEventBundleServiceTest extends TrackerTest {
    @Autowired
    private TrackerImportService trackerImportService;

    @Autowired
    private ProgramStageInstanceRepositoryExt programStageInstanceStore;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/event_metadata.json");
        injectAdminUser();
    }

    @Test
    void testCreateSingleEventData()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/event_events_and_enrollment.json");
        assertEquals(8, trackerImportParams.getEvents().size());
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        List<ProgramStageInstance> programStageInstances = programStageInstanceStore.getAll();
        assertEquals(8, programStageInstances.size());
    }

    @Test
    void testUpdateSingleEventData()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/event_events_and_enrollment.json");
        trackerImportParams.setImportStrategy(TrackerImportStrategy.CREATE_AND_UPDATE);
        ImportReport importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);
        assertEquals(8, programStageInstanceStore.getAll().size());

        importReport = trackerImportService.importTracker(trackerImportParams);
        assertNoErrors(importReport);

        assertEquals(8, programStageInstanceStore.getAll().size());
    }
}
