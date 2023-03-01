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
package org.nmcpye.am.program;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.TestCache;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Chau Thu Tran
 */
class ProgramStageInstanceServiceTest extends TransactionalIntegrationTest {

    @Autowired
    private ProgramStageInstanceServiceExt programStageInstanceService;

    @Autowired
    private ProgramStageDataElementServiceExt programStageDataElementService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private DataElementServiceExt dataElementService;

    @Autowired
    private TrackedEntityAttributeServiceExt attributeServiceExt;

    @Autowired
    private ProgramServiceExt programService;

    @Autowired
    private ProgramStageServiceExt programStageService;

    @Autowired
    private TrackedEntityInstanceServiceExt entityInstanceService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

//    @Autowired
//    private TrackedEntityAttributeServiceExt attributeService;

    @Autowired
    private TrackedEntityAttributeValueServiceExt attributeValueService;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private ProgramStage stageA;

    private ProgramStage stageB;

    private ProgramStage stageC;

    private ProgramStage stageD;

    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private ProgramStageDataElement stageDataElementA;

    private ProgramStageDataElement stageDataElementB;

    private ProgramStageDataElement stageDataElementC;

    private ProgramStageDataElement stageDataElementD;

    private LocalDateTime incidenDate;

    private LocalDateTime enrollmentDate;

    private ProgramInstance programInstanceA;

    private ProgramInstance programInstanceB;

    private ProgramStageInstance programStageInstanceA;

    private ProgramStageInstance programStageInstanceB;

    private ProgramStageInstance programStageInstanceC;

    private ProgramStageInstance programStageInstanceD1;

    private ProgramStageInstance programStageInstanceD2;

    private TrackedEntityInstance entityInstanceA;

    private TrackedEntityInstance entityInstanceB;

    private Program programA;

    private Cache<DataElement> dataElementMap = new TestCache<>();

    @Override
    public void setUpTest() {
        organisationUnitA = createOrganisationUnit('A');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitA);
        organisationUnitB = createOrganisationUnit('B');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitB);
        entityInstanceA = createTrackedEntityInstance(organisationUnitA);
        entityInstanceService.addTrackedEntityInstance(entityInstanceA);
        entityInstanceB = createTrackedEntityInstance(organisationUnitB);
        entityInstanceService.addTrackedEntityInstance(entityInstanceB);
        TrackedEntityAttribute attribute = createTrackedEntityAttribute('A');
        attribute.setValueType(ValueType.PHONE_NUMBER);
        attributeServiceExt.addTrackedEntityAttribute(attribute);
        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue('A', entityInstanceA,
            attribute);
        attributeValue.setValue("123456789");
        attributeValueService.addTrackedEntityAttributeValue(attributeValue);
        entityInstanceA.getTrackedEntityAttributeValues().add(attributeValue);
        entityInstanceService.updateTrackedEntityInstance(entityInstanceA);
        /**
         * Program A
         */
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programService.addProgram(programA);
        stageA = createProgramStage('A', 0);
        stageA.setProgram(programA);
        stageA.setSortOrder(1);
        programStageService.saveProgramStage(stageA);
        stageB = new ProgramStage("B", programA);
        stageB.setSortOrder(2);
        programStageService.saveProgramStage(stageB);
        Set<ProgramStage> programStages = new HashSet<>();
        programStages.add(stageA);
        programStages.add(stageB);
        programA.setProgramStages(programStages);
        programService.updateProgram(programA);
        dataElementA = createDataElement('A');
        dataElementB = createDataElement('B');
        dataElementC = createDataElement('C');
        dataElementD = createDataElement('D');
        dataElementService.addDataElement(dataElementA);
        dataElementService.addDataElement(dataElementB);
        dataElementService.addDataElement(dataElementC);
        dataElementService.addDataElement(dataElementD);
        stageDataElementA = new ProgramStageDataElement(stageA, dataElementA, false, 1);
        stageDataElementB = new ProgramStageDataElement(stageA, dataElementB, false, 2);
        stageDataElementC = new ProgramStageDataElement(stageB, dataElementA, false, 1);
        stageDataElementD = new ProgramStageDataElement(stageB, dataElementB, false, 2);
        programStageDataElementService.addProgramStageDataElement(stageDataElementA);
        programStageDataElementService.addProgramStageDataElement(stageDataElementB);
        programStageDataElementService.addProgramStageDataElement(stageDataElementC);
        programStageDataElementService.addProgramStageDataElement(stageDataElementD);
        /*
         * Program B
         */
        Program programB = createProgram('B', new HashSet<>(), organisationUnitB);
        programService.addProgram(programB);
        stageC = new ProgramStage("C", programB);
        stageC.setSortOrder(1);
        programStageService.saveProgramStage(stageC);
        stageD = new ProgramStage("D", programB);
        stageB.setSortOrder(2);
        stageC.setRepeatable(true);
        programStageService.saveProgramStage(stageD);
        programStages = new HashSet<>();
        programStages.add(stageC);
        programStages.add(stageD);
        programB.setProgramStages(programStages);
        programService.updateProgram(programB);
        /**
         * Program Instance and Program Stage Instance
         */
        DateTime testDate1 = DateTime.now();
        testDate1.withTimeAtStartOfDay();
        testDate1 = testDate1.minusDays(70);
        incidenDate = DateUtils.localDateTimeFromDate(testDate1.toDate());
        DateTime testDate2 = DateTime.now();
        testDate2.withTimeAtStartOfDay();
        enrollmentDate = DateUtils.localDateTimeFromDate(testDate2.toDate());
        programInstanceA = new ProgramInstance(enrollmentDate, incidenDate, entityInstanceA, programA);
        programInstanceA.setUid("UID-PIA");
        programInstanceService.addProgramInstance(programInstanceA);
        programInstanceB = new ProgramInstance(enrollmentDate, incidenDate, entityInstanceB, programB);
        programInstanceService.addProgramInstance(programInstanceB);
        programStageInstanceA = new ProgramStageInstance(programInstanceA, stageA);
        programStageInstanceA.setDueDate(enrollmentDate);
        programStageInstanceA.setUid("UID-A");
        programStageInstanceB = new ProgramStageInstance(programInstanceA, stageB);
        programStageInstanceB.setDueDate(enrollmentDate);
        programStageInstanceB.setUid("UID-B");

        programStageInstanceC = new ProgramStageInstance(programInstanceB, stageC);
        programStageInstanceC.setDueDate(enrollmentDate);
        programStageInstanceC.setUid("UID-C");

        programStageInstanceD1 = new ProgramStageInstance(programInstanceB, stageD);
        programStageInstanceD1.setDueDate(enrollmentDate);
        programStageInstanceD1.setUid("UID-D1");

        programStageInstanceD2 = new ProgramStageInstance(programInstanceB, stageD);
        programStageInstanceD2.setDueDate(enrollmentDate);
        programStageInstanceD2.setUid("UID-D2");
        /*
         * Prepare data for EventDataValues manipulation tests
         */
        programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        // Check that there are no EventDataValues assigned to PSI
        ProgramStageInstance tempPsiA = programStageInstanceService
            .getProgramStageInstance(programStageInstanceA.getUid());
        assertEquals(0, tempPsiA.getEventDataValues().size());
        // Prepare EventDataValues to manipulate with
        dataElementMap.put(dataElementA.getUid(), dataElementA);
        dataElementMap.put(dataElementB.getUid(), dataElementB);
        dataElementMap.put(dataElementC.getUid(), dataElementC);
        dataElementMap.put(dataElementD.getUid(), dataElementD);
    }

    @Test
    void testAddProgramStageInstance() {
        long idA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        long idB = programStageInstanceService.addProgramStageInstance(programStageInstanceB);
        assertNotNull(programStageInstanceService.getProgramStageInstance(idA));
        assertNotNull(programStageInstanceService.getProgramStageInstance(idB));
    }

    @Test
    void testDeleteProgramStageInstance() {
        long idA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        long idB = programStageInstanceService.addProgramStageInstance(programStageInstanceB);
        assertNotNull(programStageInstanceService.getProgramStageInstance(idA));
        assertNotNull(programStageInstanceService.getProgramStageInstance(idB));
        programStageInstanceService.deleteProgramStageInstance(programStageInstanceA);
        assertNull(programStageInstanceService.getProgramStageInstance(idA));
        assertNotNull(programStageInstanceService.getProgramStageInstance(idB));
        programStageInstanceService.deleteProgramStageInstance(programStageInstanceB);
        assertNull(programStageInstanceService.getProgramStageInstance(idA));
        assertNull(programStageInstanceService.getProgramStageInstance(idB));
    }

    @Test
    void testUpdateProgramStageInstance() {
        long idA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        assertNotNull(programStageInstanceService.getProgramStageInstance(idA));
        programStageInstanceA.setName("B");
        programStageInstanceService.updateProgramStageInstance(programStageInstanceA);
        assertEquals("B", programStageInstanceService.getProgramStageInstance(idA).getName());
    }

    @Test
    void testGetProgramStageInstanceById() {
        long idA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        long idB = programStageInstanceService.addProgramStageInstance(programStageInstanceB);
        assertEquals(programStageInstanceA, programStageInstanceService.getProgramStageInstance(idA));
        assertEquals(programStageInstanceB, programStageInstanceService.getProgramStageInstance(idB));
    }

    @Test
    void testGetProgramStageInstanceByUid() {
        long idA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        long idB = programStageInstanceService.addProgramStageInstance(programStageInstanceB);
        assertEquals(programStageInstanceA, programStageInstanceService.getProgramStageInstance(idA));
        assertEquals(programStageInstanceB, programStageInstanceService.getProgramStageInstance(idB));
        assertEquals(programStageInstanceA, programStageInstanceService.getProgramStageInstance("UID-A"));
        assertEquals(programStageInstanceB, programStageInstanceService.getProgramStageInstance("UID-B"));
    }
}
