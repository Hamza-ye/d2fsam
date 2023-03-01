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

import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Chau Thu Tran
 */
class ProgramInstanceServiceTest extends TransactionalIntegrationTest {

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private TrackedEntityInstanceServiceExt entityInstanceService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private ProgramServiceExt programService;

    @Autowired
    private ProgramStageServiceExt programStageService;

    @Autowired
    private ProgramStageInstanceServiceExt programStageInstanceService;

    @Autowired
    private ActivityServiceExt activityServiceExt;

    private LocalDateTime incidentDate;

    private LocalDateTime enrollmentDate;

    private Program programA;

    private Program programB;

    private Program programC;

    private Activity activityA;

    private Activity activityB;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private ProgramStageInstance programStageInstanceA;

    private ProgramInstance programInstanceA;

    private ProgramInstance programInstanceB;

    private ProgramInstance programInstanceC;

    private ProgramInstance programInstanceD;

    private TrackedEntityInstance entityInstanceA;

    @Override
    public void setUpTest() {
        activityA = createActivity('A');
        activityB = createActivity('B');
        activityServiceExt.addActivity(activityA);
        activityServiceExt.addActivity(activityB);
        organisationUnitA = createOrganisationUnit('A');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitA);
        organisationUnitB = createOrganisationUnit('B');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitB);
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programService.addProgram(programA);
        ProgramStage stageA = createProgramStage('A', programA);
        stageA.setSortOrder(1);
        programStageService.saveProgramStage(stageA);
        ProgramStage stageB = createProgramStage('B', programA);
        stageB.setSortOrder(2);
        programStageService.saveProgramStage(stageB);
        Set<ProgramStage> programStages = new HashSet<>();
        programStages.add(stageA);
        programStages.add(stageB);
        programA.setProgramStages(programStages);
        programService.updateProgram(programA);
        programB = createProgram('B', new HashSet<>(), organisationUnitA);
        programService.addProgram(programB);
        programC = createProgram('C', new HashSet<>(), organisationUnitA);
        programService.addProgram(programC);
        entityInstanceA = createTrackedEntityInstance(organisationUnitA);
        entityInstanceService.addTrackedEntityInstance(entityInstanceA);
        TrackedEntityInstance entityInstanceB = createTrackedEntityInstance(organisationUnitB);
        entityInstanceService.addTrackedEntityInstance(entityInstanceB);
        DateTime testDate1 = DateTime.now();
        testDate1.withTimeAtStartOfDay();
        testDate1 = testDate1.minusDays(70);
        incidentDate = DateUtils.localDateTimeFromDate(testDate1.toLocalDateTime().toDate());
        DateTime testDate2 = DateTime.now();
        testDate2.withTimeAtStartOfDay();
        enrollmentDate = DateUtils.localDateTimeFromDate(testDate2.toDate());
        programInstanceA = new ProgramInstance(enrollmentDate, incidentDate, entityInstanceA, programA);
        programInstanceA.setUid("UID-A");
        programInstanceA.setOrganisationUnit(organisationUnitA);
        programInstanceA.setActivity(activityA);
        programStageInstanceA = new ProgramStageInstance(programInstanceA, stageA);
        programStageInstanceA.setUid("UID-PSI-A");
        programStageInstanceA.setOrganisationUnit(organisationUnitA);
        programStageInstanceA.setActivity(activityA);
        programInstanceB = new ProgramInstance(enrollmentDate, incidentDate, entityInstanceA, programB);
        programInstanceB.setUid("UID-B");
        programInstanceB.setStatus(ProgramStatus.CANCELLED);
        programInstanceB.setOrganisationUnit(organisationUnitB);
        programInstanceB.setActivity(activityB);
        programInstanceC = new ProgramInstance(enrollmentDate, incidentDate, entityInstanceA, programC);
        programInstanceC.setUid("UID-C");
        programInstanceC.setStatus(ProgramStatus.COMPLETED);
        programInstanceC.setOrganisationUnit(organisationUnitA);
        programInstanceC.setActivity(activityA);
        programInstanceD = new ProgramInstance(enrollmentDate, incidentDate, entityInstanceB, programA);
        programInstanceD.setUid("UID-D");
        programInstanceD.setOrganisationUnit(organisationUnitB);
        programInstanceD.setActivity(activityB);
    }

    @Test
    void testAddProgramInstance() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idB = programInstanceService.addProgramInstance(programInstanceB);
        assertNotNull(programInstanceService.getProgramInstance(idA));
        assertNotNull(programInstanceService.getProgramInstance(idB));
    }

    @Test
    void testDeleteProgramInstance() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idB = programInstanceService.addProgramInstance(programInstanceB);
        assertNotNull(programInstanceService.getProgramInstance(idA));
        assertNotNull(programInstanceService.getProgramInstance(idB));
        programInstanceService.deleteProgramInstance(programInstanceA);
        assertNull(programInstanceService.getProgramInstance(idA));
        assertNotNull(programInstanceService.getProgramInstance(idB));
        programInstanceService.deleteProgramInstance(programInstanceB);
        assertNull(programInstanceService.getProgramInstance(idA));
        assertNull(programInstanceService.getProgramInstance(idB));
    }

    @Test
    void testSoftDeleteProgramInstanceAndLinkedProgramStageInstance() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long psiIdA = programStageInstanceService.addProgramStageInstance(programStageInstanceA);
        programInstanceA.setProgramStageInstances(Sets.newHashSet(programStageInstanceA));
        programInstanceService.updateProgramInstance(programInstanceA);
        assertNotNull(programInstanceService.getProgramInstance(idA));
        assertNotNull(programStageInstanceService.getProgramStageInstance(psiIdA));
        programInstanceService.deleteProgramInstance(programInstanceA);
        assertNull(programInstanceService.getProgramInstance(idA));
        assertNull(programStageInstanceService.getProgramStageInstance(psiIdA));
    }

    @Test
    void testUpdateProgramInstance() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        assertNotNull(programInstanceService.getProgramInstance(idA));
        programInstanceA.setIncidentDate(enrollmentDate);
        programInstanceService.updateProgramInstance(programInstanceA);
        assertEquals(enrollmentDate, programInstanceService.getProgramInstance(idA).getIncidentDate());
    }

    @Test
    void testGetProgramInstanceById() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idB = programInstanceService.addProgramInstance(programInstanceB);
        assertEquals(programInstanceA, programInstanceService.getProgramInstance(idA));
        assertEquals(programInstanceB, programInstanceService.getProgramInstance(idB));
    }

    @Test
    void testGetProgramInstanceByUid() {
        programInstanceService.addProgramInstance(programInstanceA);
        programInstanceService.addProgramInstance(programInstanceB);
        assertEquals("UID-A", programInstanceService.getProgramInstance("UID-A").getUid());
        assertEquals("UID-B", programInstanceService.getProgramInstance("UID-B").getUid());
    }

    @Test
    void testGetProgramInstancesByProgram() {
        programInstanceService.addProgramInstance(programInstanceA);
        programInstanceService.addProgramInstance(programInstanceB);
        programInstanceService.addProgramInstance(programInstanceD);
        List<ProgramInstance> programInstances = programInstanceService.getProgramInstances(programA);
        assertEquals(2, programInstances.size());
        assertTrue(programInstances.contains(programInstanceA));
        assertTrue(programInstances.contains(programInstanceD));
        programInstances = programInstanceService.getProgramInstances(programB);
        assertEquals(1, programInstances.size());
        assertTrue(programInstances.contains(programInstanceB));
    }

    @Test
    void testGetProgramInstancesByEntityInstanceProgramStatus() {
        programInstanceService.addProgramInstance(programInstanceA);
        ProgramInstance programInstance1 = programInstanceService.enrollTrackedEntityInstance(activityA, entityInstanceA,
            programA, enrollmentDate, incidentDate, organisationUnitA);
        programInstance1.setStatus(ProgramStatus.COMPLETED);
        programInstanceService.updateProgramInstance(programInstance1);
        ProgramInstance programInstance2 = programInstanceService.enrollTrackedEntityInstance(activityA, entityInstanceA,
            programA, enrollmentDate, incidentDate, organisationUnitA);
        programInstance2.setStatus(ProgramStatus.COMPLETED);
        programInstanceService.updateProgramInstance(programInstance2);
        List<ProgramInstance> programInstances = programInstanceService.getProgramInstances(entityInstanceA, programA,
            ProgramStatus.COMPLETED);
        assertEquals(2, programInstances.size());
        assertTrue(programInstances.contains(programInstance1));
        assertTrue(programInstances.contains(programInstance2));
        programInstances = programInstanceService.getProgramInstances(entityInstanceA, programA,
            ProgramStatus.ACTIVE);
        assertEquals(1, programInstances.size());
        assertTrue(programInstances.contains(programInstanceA));
    }

    @Test
    void testGetProgramInstancesByOuProgram() {
        programInstanceService.addProgramInstance(programInstanceA);
        programInstanceService.addProgramInstance(programInstanceC);
        programInstanceService.addProgramInstance(programInstanceD);
        List<ProgramInstance> programInstances = programInstanceService
            .getProgramInstances(new ProgramInstanceQueryParams().setProgram(programA)
                .setOrganisationUnits(Sets.newHashSet(organisationUnitA))
                .setOrganisationUnitMode(OrganisationUnitSelectionMode.SELECTED));
        assertEquals(1, programInstances.size());
        assertTrue(programInstances.contains(programInstanceA));
    }

    @Test
    void testEnrollTrackedEntityInstance() {
        ProgramInstance programInstance = programInstanceService.enrollTrackedEntityInstance(activityA, entityInstanceA, programB,
            enrollmentDate, incidentDate, organisationUnitA);
        assertNotNull(programInstanceService.getProgramInstance(programInstance.getId()));
    }

    @Test
    void testCompleteProgramInstanceStatus() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idD = programInstanceService.addProgramInstance(programInstanceD);
        programInstanceService.completeProgramInstanceStatus(programInstanceA);
        programInstanceService.completeProgramInstanceStatus(programInstanceD);
        assertEquals(ProgramStatus.COMPLETED, programInstanceService.getProgramInstance(idA).getStatus());
        assertEquals(ProgramStatus.COMPLETED, programInstanceService.getProgramInstance(idD).getStatus());
    }

    @Test
    void testIncompleteProgramInstanceStatus() {
        programInstanceA.setStatus(ProgramStatus.COMPLETED);
        programInstanceD.setStatus(ProgramStatus.COMPLETED);
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idD = programInstanceService.addProgramInstance(programInstanceD);
        programInstanceService.incompleteProgramInstanceStatus(programInstanceA);
        programInstanceService.incompleteProgramInstanceStatus(programInstanceD);
        assertEquals(ProgramStatus.ACTIVE, programInstanceService.getProgramInstance(idA).getStatus());
        assertEquals(ProgramStatus.ACTIVE, programInstanceService.getProgramInstance(idD).getStatus());
    }

    @Test
    void testCancelProgramInstanceStatus() {
        long idA = programInstanceService.addProgramInstance(programInstanceA);
        long idD = programInstanceService.addProgramInstance(programInstanceD);
        programInstanceService.cancelProgramInstanceStatus(programInstanceA);
        programInstanceService.cancelProgramInstanceStatus(programInstanceD);
        assertEquals(ProgramStatus.CANCELLED, programInstanceService.getProgramInstance(idA).getStatus());
        assertEquals(ProgramStatus.CANCELLED, programInstanceService.getProgramInstance(idD).getStatus());
    }
}
