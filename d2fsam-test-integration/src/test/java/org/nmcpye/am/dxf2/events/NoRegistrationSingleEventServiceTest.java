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
package org.nmcpye.am.dxf2.events;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dxf2.events.event.DataValue;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.EventService;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.*;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class NoRegistrationSingleEventServiceTest extends TransactionalIntegrationTest {

    /// NMCP
    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;
    /////////

    @Autowired
    private EventService eventService;

    @Autowired
    private ProgramStageDataElementServiceExt programStageDataElementService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private ProgramStageInstanceServiceExt programStageInstanceService;

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private UserServiceExt _userService;

    private Activity activityA;

    private OrganisationUnit organisationUnitA;

    private DataElement dataElementA;

    private Program programA;

    private ProgramStage programStageA;

    @Override
    protected void setUpTest()
        throws Exception {
        userService = _userService;
        organisationUnitA = createOrganisationUnit('A');
        identifiableObjectManager.save(organisationUnitA);

        Project project = createProject('A');
        identifiableObjectManager.save(project);
        activityA = createActivity('A');
        activityA.setProject(project);
        identifiableObjectManager.save(activityA);

        dataElementA = createDataElement('A');
        dataElementA.setValueType(ValueType.INTEGER);
        identifiableObjectManager.save(dataElementA);
        programStageA = createProgramStage('A', 0);
        identifiableObjectManager.save(programStageA);
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programA.setProgramType(ProgramType.WITHOUT_REGISTRATION);
        identifiableObjectManager.save(programA);
        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setDataElement(dataElementA);
        programStageDataElement.setProgramStage(programStageA);
        programStageDataElementService.addProgramStageDataElement(programStageDataElement);
        programStageA.getProgramStageDataElements().add(programStageDataElement);
        programStageA.setProgram(programA);
        programA.getProgramStages().add(programStageA);
        identifiableObjectManager.update(programStageA);
        identifiableObjectManager.update(programA);
        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setProgram(programA);
        programInstance.setIncidentDate(java.time.LocalDateTime.now());
        programInstance.setEnrollmentDate(java.time.LocalDateTime.now());
        programInstance.setActivity(activityA);
        programInstanceService.addProgramInstance(programInstance);
        identifiableObjectManager.update(programA);
        organisationUnitServiceExt.updatePaths();
        createUserAndInjectSecurityContext(true);
        identifiableObjectManager.flush();
    }

    @Test
    void testGetPersonsByProgramStageInstance() {
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, null, false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertNotNull(importSummary.getReference());
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(importSummary.getReference());
        assertNotNull(programStageInstance);
        assertNotNull(eventService.getEvent(programStageInstance, false));
    }

    @Test
    void testGetEventByUid() {
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, null, false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertNotNull(importSummary.getReference());
        assertNotNull(programStageInstanceService.getProgramStageInstance(importSummary.getReference()));
    }

    @Test
    void testSaveEvent() {
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, null, false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(0, importSummary.getConflictCount());
        assertNotNull(importSummary.getReference());
        event = eventService
            .getEvent(programStageInstanceService.getProgramStageInstance(importSummary.getReference()), false);
        assertNotNull(event);
        assertEquals(1, event.getDataValues().size());
    }

    @Test
    void testDeleteEvent() {
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, null, false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertNotNull(importSummary.getReference());
        assertNotNull(programStageInstanceService.getProgramStageInstance(importSummary.getReference()));
        eventService.deleteEvent(event.getEvent());
        assertNull(programStageInstanceService.getProgramStageInstance(importSummary.getReference()));
    }

    private Event createEvent(String program, String programStage, String orgUnit) {
        Event event = new Event();
        event.setActivity(activityA.getUid());
        event.setProgram(program);
        event.setProgramStage(programStage);
        event.setOrgUnit(orgUnit);
        event.setEventDate("2013-01-01");
        event.getDataValues().add(new DataValue(dataElementA.getUid(), "10"));
        return event;
    }
}
