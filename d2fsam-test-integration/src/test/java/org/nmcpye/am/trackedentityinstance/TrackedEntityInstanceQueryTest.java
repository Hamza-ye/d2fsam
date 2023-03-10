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
package org.nmcpye.am.trackedentityinstance;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Lars Helge Overland
 */
class TrackedEntityInstanceQueryTest extends SingleSetupIntegrationTestBase {

    @Autowired
    private TrackedEntityInstanceServiceExt instanceService;

    @Test
    void testValidateNoOrgUnitsModeAll() {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        TrackedEntityType trackedEntityTypeA = createTrackedEntityType('A');
        params.setTrackedEntityType(trackedEntityTypeA);
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        instanceService.validate(params);
    }

    @Test
    void testTeiQueryParamsWithoutEitherProgramOrTrackedEntityType() {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        IllegalQueryException exception = assertThrows(IllegalQueryException.class,
            () -> instanceService.validate(params));
        assertEquals("Either Program or Tracked entity type should be specified", exception.getMessage());
    }

    @Test
    void testIfUniqueFiltersArePresentInAttributesOrFilters() {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        QueryItem nonUniq1 = new QueryItem(new TrackedEntityAttribute(), null, ValueType.TEXT, AggregationType.NONE,
            null, false);
        QueryItem nonUniq2 = new QueryItem(new TrackedEntityAttribute(), null, ValueType.TEXT, AggregationType.NONE,
            null, false);
        QueryItem uniq1 = new QueryItem(new TrackedEntityAttribute(), null, ValueType.TEXT, AggregationType.NONE, null,
            true);
        QueryFilter qf = new QueryFilter(QueryOperator.EQ, "test");
        nonUniq1.getFilters().add(qf);
        nonUniq2.getFilters().add(qf);
        params.addAttribute(nonUniq1);
        params.addAttribute(nonUniq2);
        params.addAttribute(uniq1);
        assertEquals(params.hasUniqueFilter(), false);
        uniq1.getFilters().add(qf);
        assertEquals(params.hasUniqueFilter(), true);
        params.getAttributes().clear();
        params.addFilter(nonUniq1);
        params.addFilter(nonUniq2);
        assertEquals(params.hasUniqueFilter(), false);
        params.addFilter(uniq1);
        assertEquals(params.hasUniqueFilter(), true);
    }
}
