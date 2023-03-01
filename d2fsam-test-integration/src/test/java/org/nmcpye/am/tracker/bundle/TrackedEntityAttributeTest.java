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
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.nmcpye.am.tracker.TrackerTest;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.preheat.TrackerPreheatService;
import org.nmcpye.am.tracker.report.ImportReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.nmcpye.am.tracker.Assertions.assertNoErrors;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class TrackedEntityAttributeTest extends TrackerTest {

    @Autowired
    private TrackerPreheatService trackerPreheatService;

    @Autowired
    private TrackerImportService trackerImportService;

    @Autowired
    private TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Override
    protected void initTest()
        throws IOException {
        setUpMetadata("tracker/te_with_tea_metadata.json");
        injectAdminUser();
    }

    @Test
    void testTrackedAttributePreheater()
        throws IOException {
        TrackerImportParams trackerImportParams = fromJson("tracker/te_with_tea_data.json");
        TrackerPreheat preheat = trackerPreheatService.preheat(trackerImportParams);
        assertNotNull(preheat.get(OrganisationUnit.class, "cNEZTkdAvmg"));
        assertNotNull(preheat.get(TrackedEntityType.class, "KrYIdvLxkMb"));
        assertNotNull(preheat.get(TrackedEntityAttribute.class, "sYn3tkL3XKa"));
        assertNotNull(preheat.get(TrackedEntityAttribute.class, "TsfP85GKsU5"));
        assertNotNull(preheat.get(TrackedEntityAttribute.class, "sTGqP5JNy6E"));
    }

    @Test
    void testTrackedAttributeValueBundleImporter()
        throws IOException {
        ImportReport importReport = trackerImportService
            .importTracker(fromJson("tracker/te_with_tea_data.json"));
        assertNoErrors(importReport);

        List<TrackedEntityInstance> trackedEntityInstances = manager.getAll(TrackedEntityInstance.class);
        assertEquals(1, trackedEntityInstances.size());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(0);
        List<TrackedEntityAttributeValue> attributeValues = trackedEntityAttributeValueService
            .getTrackedEntityAttributeValues(trackedEntityInstance);
        assertEquals(3, attributeValues.size());
    }
}
