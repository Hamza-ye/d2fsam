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
package org.nmcpye.am.trackedentity;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.common.QueryItem;
import org.nmcpye.am.common.QueryOperator;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstanceServiceExt;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Lars Helge Overland
 */
class TrackedEntityInstanceStoreTest extends TransactionalIntegrationTest {

    @Autowired
    private TrackedEntityInstanceRepositoryExt teiStore;

    @Autowired
    private ActivityServiceExt activityServiceExt;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private TrackedEntityAttributeValueServiceExt attributeValueService;

    @Autowired
    private TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    @Autowired
    private ProgramServiceExt programService;

    // NMCP
    @Autowired
    private TrackedEntityProgramOwnerRepositoryExt trackedEntityProgramOwnerRepositoryExt;

    private TrackedEntityInstance teiA;

    private TrackedEntityInstance teiB;

    private TrackedEntityInstance teiC;

    private TrackedEntityInstance teiD;

    private TrackedEntityInstance teiE;

    private TrackedEntityInstance teiF;

    private TrackedEntityAttribute atA;

    private TrackedEntityAttribute atB;

    private TrackedEntityAttribute atC;

    private Activity activityA;

    private OrganisationUnit ouA;

    private OrganisationUnit ouB;

    private OrganisationUnit ouC;

    private Program prA;

    private Program prB;

    @Override
    public void setUpTest() {
        activityA = createActivity('A');
        activityServiceExt.addActivity(activityA);
        atA = createTrackedEntityAttribute('A');
        atB = createTrackedEntityAttribute('B');
        atC = createTrackedEntityAttribute('C', ValueType.ORGANISATION_UNIT);
        atB.setUniqueAttribute(true);
        trackedEntityAttributeService.addTrackedEntityAttribute(atA);
        trackedEntityAttributeService.addTrackedEntityAttribute(atB);
        trackedEntityAttributeService.addTrackedEntityAttribute(atC);
        ouA = createOrganisationUnit('A');
        ouB = createOrganisationUnit('B', ouA);
        ouC = createOrganisationUnit('C', ouB);
        organisationUnitServiceExt.addOrganisationUnit(ouA);
        organisationUnitServiceExt.addOrganisationUnit(ouB);
        organisationUnitServiceExt.addOrganisationUnit(ouC);
        prA = createProgram('A', null, null);
        prB = createProgram('B', null, null);
        programService.addProgram(prA);
        programService.addProgram(prB);
        teiA = createTrackedEntityInstance(ouA);
        teiB = createTrackedEntityInstance(ouB);
        teiC = createTrackedEntityInstance(ouB);
        teiD = createTrackedEntityInstance(ouC);
        teiE = createTrackedEntityInstance(ouC);
        teiF = createTrackedEntityInstance(ouC);

        organisationUnitServiceExt.forceUpdatePaths();
    }

    @Test
    void testTrackedEntityInstanceExists() {
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        dbmsManager.flushSession();
        assertTrue(teiStore.exists(teiA.getUid()));
        assertTrue(teiStore.exists(teiB.getUid()));
        assertFalse(teiStore.exists("aaaabbbbccc"));
    }

    @Test
    void testAddGet() {
        teiStore.saveObject(teiA);
        long idA = teiA.getId();
        teiStore.saveObject(teiB);
        long idB = teiB.getId();
        assertNotNull(teiStore.get(idA));
        assertNotNull(teiStore.get(idB));
    }

    @Test
    void testAddGetbyOu() {
        teiStore.saveObject(teiA);
        long idA = teiA.getId();
        teiStore.saveObject(teiB);
        long idB = teiB.getId();
        assertEquals(teiA.getName(), teiStore.get(idA).getName());
        assertEquals(teiB.getName(), teiStore.get(idB).getName());
    }

    @Test
    void testDelete() {
        teiStore.saveObject(teiA);
        long idA = teiA.getId();
        teiStore.saveObject(teiB);
        long idB = teiB.getId();
        String uid = teiB.getUid();
        assertNotNull(teiStore.get(idA));
        assertNotNull(teiStore.get(idB));
        teiStore.deleteObject(teiA);
        assertTrue(teiStore.existsIncludingDeleted(uid));
        assertNull(teiStore.get(idA));
        assertNotNull(teiStore.get(idB));
        teiStore.deleteObject(teiB);
        assertNull(teiStore.get(idA));
        assertNull(teiStore.get(idB));
    }

    @Test
    void testGetAll() {
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        assertTrue(equals(teiStore.getAll(), teiA, teiB));
    }

    @Test
    void testQuery() {
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        teiStore.saveObject(teiC);
        teiStore.saveObject(teiD);
        teiStore.saveObject(teiE);
        teiStore.saveObject(teiF);
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiD, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiE, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiF, "Female"));
        programInstanceService.enrollTrackedEntityInstance(activityA, teiB, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        programInstanceService.enrollTrackedEntityInstance(activityA, teiE, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        // Get all
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        List<TrackedEntityInstance> teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(6, teis.size());
        // Filter by attribute with EQ
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EQ, "Male", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));
        // Filter by attribute with EQ
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EQ, "Female", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(1, teis.size());
        assertTrue(teis.contains(teiF));

        // Filter by attribute with STARTS
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "ma", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "al", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "ale", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        // Filter by attribute with ENDS
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "emale", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(1, teis.size());
        assertTrue(teis.contains(teiF));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "male", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(3, teis.size());
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));
        assertTrue(teis.contains(teiF));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "fem", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "em", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        // Filter by selected org units
        params = new TrackedEntityInstanceQueryParams().addOrganisationUnit(ouB)
            .setOrganisationUnitMode(OrganisationUnitSelectionMode.SELECTED);
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertTrue(teis.contains(teiB));
        assertTrue(teis.contains(teiC));
        // Filter by descendants org units
        params = new TrackedEntityInstanceQueryParams().addOrganisationUnit(ouB)
            .setOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS);
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(5, teis.size());
        assertTrue(teis.contains(teiB));
        assertTrue(teis.contains(teiC));
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));
        assertTrue(teis.contains(teiF));
        // Filter by program enrollment
        List<TrackedEntityProgramOwner> programOwners = trackedEntityProgramOwnerRepositoryExt.getAll();

        params = new TrackedEntityInstanceQueryParams().setProgram(prA);
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertTrue(teis.contains(teiB));
        assertTrue(teis.contains(teiE));
    }

    @Test
    void testStartsWithQueryOperator() {
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        teiStore.saveObject(teiC);
        teiStore.saveObject(teiD);
        teiStore.saveObject(teiE);
        teiStore.saveObject(teiF);
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiD, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiE, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiF, "Female"));
        programInstanceService.enrollTrackedEntityInstance(activityA, teiB, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        programInstanceService.enrollTrackedEntityInstance(activityA, teiE, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        List<TrackedEntityInstance> teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(6, teis.size());

        // Filter by attribute with STARTS
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "ma", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "al", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.SW, "ale", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());
    }

    @Test
    void testEndsWithQueryOperator() {
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        teiStore.saveObject(teiC);
        teiStore.saveObject(teiD);
        teiStore.saveObject(teiE);
        teiStore.saveObject(teiF);
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiD, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiE, "Male"));
        attributeValueService.addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atA, teiF, "Female"));
        programInstanceService.enrollTrackedEntityInstance(activityA, teiB, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        programInstanceService.enrollTrackedEntityInstance(activityA, teiE, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        List<TrackedEntityInstance> teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(6, teis.size());

        // Filter by attribute with ENDS
        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "emale", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(1, teis.size());
        assertTrue(teis.contains(teiF));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "male", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(3, teis.size());
        assertTrue(teis.contains(teiD));
        assertTrue(teis.contains(teiE));
        assertTrue(teis.contains(teiF));

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "fem", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());

        params = new TrackedEntityInstanceQueryParams()
            .addFilter(new QueryItem(atA, QueryOperator.EW, "em", ValueType.TEXT, AggregationType.NONE, null));
        teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(0, teis.size());
    }

    @Test
    void testQueryOrderByIdInsteadOfCreatedDate() {
        LocalDate now = LocalDate.now();
//        Date today = Date.from(now.atStartOfDay().toInstant(ZoneOffset.UTC));
//        Date tomorrow = Date.from(now.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
        Instant today = now.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant tomorrow = now.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        teiA.setCreated(tomorrow);
        teiB.setCreated(today);
        teiStore.saveObject(teiA);
        teiStore.saveObject(teiB);
        programInstanceService.enrollTrackedEntityInstance(activityA, teiB, prA, LocalDateTime.now(), LocalDateTime.now(), ouB);
        // Get all
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setOrders(
            List.of(new OrderParam(TrackedEntityInstanceQueryParams.CREATED_ID, OrderParam.SortDirection.ASC)));
        List<TrackedEntityInstance> teis = teiStore.getTrackedEntityInstances(params);
        assertEquals(2, teis.size());
        assertEquals(teiA.getUid(), teis.get(0).getUid());
        assertEquals(teiB.getUid(), teis.get(1).getUid());
    }

    @Test
    void testPotentialDuplicateInGridQuery() {
        TrackedEntityType trackedEntityTypeA = createTrackedEntityType('A');
        trackedEntityTypeService.addTrackedEntityType(trackedEntityTypeA);
        teiA.setTrackedEntityType(trackedEntityTypeA);
        teiA.setPotentialDuplicate(true);
        teiStore.saveObject(teiA);
        teiB.setTrackedEntityType(trackedEntityTypeA);
        teiB.setPotentialDuplicate(true);
        teiStore.saveObject(teiB);
        teiC.setTrackedEntityType(trackedEntityTypeA);
        teiStore.saveObject(teiC);
        teiD.setTrackedEntityType(trackedEntityTypeA);
        teiStore.saveObject(teiD);
        dbmsManager.flushSession();
        // Get all
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setTrackedEntityType(trackedEntityTypeA);
        List<Map<String, String>> teis = teiStore.getTrackedEntityInstancesGrid(params);
        assertEquals(4, teis.size());
        teis.forEach(teiMap -> {
            if (teiMap.get(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID).equals(teiA.getUid())
                || teiMap.get(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID).equals(teiB.getUid())) {
                assertTrue(
                    Boolean.parseBoolean(teiMap.get(TrackedEntityInstanceQueryParams.POTENTIAL_DUPLICATE)));
            } else {
                assertFalse(
                    Boolean.parseBoolean(teiMap.get(TrackedEntityInstanceQueryParams.POTENTIAL_DUPLICATE)));
            }
        });
    }

    @Test
    void testProgramAttributeOfTypeOrgUnitIsResolvedToOrgUnitName() {
        TrackedEntityType trackedEntityTypeA = createTrackedEntityType('A');
        trackedEntityTypeService.addTrackedEntityType(trackedEntityTypeA);
        teiA.setTrackedEntityType(trackedEntityTypeA);
        teiStore.saveObject(teiA);
        attributeValueService
            .addTrackedEntityAttributeValue(new TrackedEntityAttributeValue(atC, teiA, ouC.getUid()));
        programInstanceService.enrollTrackedEntityInstance(activityA, teiA, prA,
            LocalDateTime.now(), LocalDateTime.now(), ouA);
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setTrackedEntityType(trackedEntityTypeA);
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        QueryItem queryItem = new QueryItem(atC);
        queryItem.setValueType(atC.getValueType());
        params.setAttributes(Collections.singletonList(queryItem));
        List<Map<String, String>> grid = teiStore.getTrackedEntityInstancesGrid(params);
        assertThat(grid, hasSize(1));
        assertThat(grid.get(0).keySet(), hasSize(9));
        assertThat(grid.get(0).get(atC.getUid()), is("OrganisationUnitC"));
    }
}
