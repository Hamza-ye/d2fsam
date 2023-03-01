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
package org.nmcpye.am.dxf2.events.security;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.event.DataValue;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.EventService;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.*;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.security.acl.AccessStringHelper;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class EventSecurityTest extends TransactionalIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private ProgramStageInstanceServiceExt programStageInstanceService;

    @Autowired
    private ProgramStageDataElementServiceExt programStageDataElementService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private UserServiceExt _userService;

//    @Autowired
//    private CategoryService _categoryService;

    private Activity activity;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    private OrganisationUnit organisationUnitA;

    private DataElement dataElementA;

    private Program programA;

    private ProgramStage programStageA;

    @Override
    protected void setUpTest() {
        userService = _userService;
//        categoryService = _categoryService;
        createAndInjectAdminUser();
        Project project = createProject('A');
        manager.save(project);
        activity = createActivity('A');
        activity.setProject(project);
        manager.save(activity);
        organisationUnitA = createOrganisationUnit('A');
        manager.save(organisationUnitA);
        dataElementA = createDataElement('A');
        dataElementA.setValueType(ValueType.INTEGER);
        manager.save(dataElementA);
        programStageA = createProgramStage('A', 0);
        manager.save(programStageA);
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programA.setProgramType(ProgramType.WITHOUT_REGISTRATION);
        manager.save(programA);
        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setDataElement(dataElementA);
        programStageDataElement.setProgramStage(programStageA);
        programStageDataElementService.addProgramStageDataElement(programStageDataElement);
        programStageA.getProgramStageDataElements().add(programStageDataElement);
        programStageA.setProgram(programA);
        programA.getProgramStages().add(programStageA);
        manager.update(programStageA);
        manager.update(programA);
        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setProgram(programA);
        programInstance.setActivity(activity);
        programInstance.setIncidentDate(LocalDateTime.now());
        programInstance.setEnrollmentDate(LocalDateTime.now());
        programInstanceService.addProgramInstance(programInstance);
        manager.update(programA);
        manager.flush();
        // NMCP for Null pointers
        organisationUnitServiceExt.forceUpdatePaths();
    }

    @Test
    void testAddEventSuperuser() {
        programA.setPublicAccess(AccessStringHelper.DEFAULT);
        programStageA.setPublicAccess(AccessStringHelper.DEFAULT);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertFalse(importSummary.hasConflicts());
    }

    @Test
    void testAddEventSimpleUser() {
        programA.setPublicAccess(AccessStringHelper.DEFAULT);
        programStageA.setPublicAccess(AccessStringHelper.DEFAULT);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1");
        injectSecurityContext(user);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.ERROR, importSummary.getStatus());
    }

    /**
     * program = DATA READ/WRITE programStage = DATA READ/WRITE orgUnit =
     * Accessible status = SUCCESS
     */
    @Test
    void testAddEventSimpleUserFullAccess1() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.updateNoAcl(programA);
        manager.updateNoAcl(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        userService.addUser(user);
        injectSecurityContext(user);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        // make sure data is flushed, so event service can access it
        manager.flush();
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
    }

    /**
     * program = DATA READ programStage = DATA READ/WRITE orgUnit = Accessible
     * status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess2() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.ERROR, importSummary.getStatus());
    }

    /**
     * program = DATA READ/WRITE programStage = DATA READ orgUnit = Accessible
     * status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess3() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        // make sure data is flushed, so event service can access it
        manager.flush();
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
    }

    /**
     * program = DATA READ/WRITE programStage = DATA READ/WRITE orgUnit = Not
     * Accessible status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess4() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1");
        injectSecurityContext(user);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.ERROR, importSummary.getStatus());
    }

    /**
     * program = DATA READ programStage = DATA READ orgUnit = Accessible status
     * = SUCCESS
     */
    @Test
    void testAddEventSimpleUserFullAccess5() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DATA_READ);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        Event eventFromPsi = eventService.getEvent(programStageInstance, false);
        assertNotNull(eventFromPsi);
        assertEquals(event.getUid(), eventFromPsi.getEvent());
    }

    /**
     * program = DATA WRITE programStage = DATA WRITE orgUnit = Accessible
     * status = SUCCESS
     */
    @Test
    void testAddEventSimpleUserFullAccess6() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DATA_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        Event eventFromPsi = eventService.getEvent(programStageInstance, false);
        assertNotNull(eventFromPsi);
        assertEquals(event.getUid(), eventFromPsi.getEvent());
    }

    /**
     * program = DATA WRITE programStage = DATA WRITE orgUnit = Not Accessible
     * status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess7() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DATA_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1");
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        assertThrows(IllegalQueryException.class, () -> eventService.getEvent(programStageInstance, false));
    }

    /**
     * program = DATA READ programStage = DATA READ orgUnit = Not Accessible
     * status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess8() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DATA_READ);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1");
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        assertThrows(IllegalQueryException.class, () -> eventService.getEvent(programStageInstance, false));
    }

    /**
     * program = programStage = DATA READ orgUnit = Accessible status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess9() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DEFAULT);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        assertThrows(IllegalQueryException.class, () -> eventService.getEvent(programStageInstance, false));
    }

    /**
     * program = DATA READ programStage = orgUnit = Accessible status = ERROR
     */
    @Test
    void testAddEventSimpleUserFullAccess10() {
        programA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        programStageA.setPublicAccess(AccessStringHelper.DATA_READ_WRITE);
        manager.update(programA);
        manager.update(programStageA);
        Event event = createEvent(programA.getUid(), programStageA.getUid(), organisationUnitA.getUid());
        ImportSummary importSummary = eventService.addEvent(event, ImportOptions.getDefaultImportOptions(), false);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(event.getEvent(), importSummary.getReference());
        programA.setPublicAccess(AccessStringHelper.DATA_READ);
        programStageA.setPublicAccess(AccessStringHelper.DEFAULT);
        manager.update(programA);
        manager.update(programStageA);
        User user = createUserWithAuth("user1").setOrganisationUnits(Sets.newHashSet(organisationUnitA));
        injectSecurityContext(user);
        assertTrue(programStageInstanceService.programStageInstanceExists(event.getEvent()));
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance(event.getUid());
        assertNotNull(programStageInstance);
        Event eventFromPsi = eventService.getEvent(programStageInstance, false);
        assertNotNull(eventFromPsi);
        assertEquals(event.getUid(), eventFromPsi.getEvent());
    }

    private Event createEvent(String program, String programStage, String orgUnit) {
        Event event = new Event();
        event.setUid(CodeGenerator.generateUid());
        event.setEvent(event.getUid());
        event.setActivity(activity.getUid());
        event.setProgram(program);
        event.setProgramStage(programStage);
        event.setOrgUnit(orgUnit);
        event.setEventDate("2013-01-01");
        event.getDataValues().add(new DataValue(dataElementA.getUid(), "10"));
        return event;
    }
}
