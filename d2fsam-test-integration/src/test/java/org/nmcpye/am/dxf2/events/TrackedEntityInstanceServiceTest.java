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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.Objects;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.enrollment.Enrollment;
import org.nmcpye.am.dxf2.events.enrollment.EnrollmentStatus;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.trackedentity.Attribute;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummaries;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstanceServiceExt;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.textpattern.TextPattern;
import org.nmcpye.am.textpattern.TextPatternMethod;
import org.nmcpye.am.textpattern.TextPatternSegment;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class TrackedEntityInstanceServiceTest extends TransactionalIntegrationTest {

    /// NMCP
    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;
    /////////

    @Autowired
    private TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Autowired
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Autowired
    private org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt teiDaoService;

    @Autowired
    private TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    @Autowired
    private TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private SessionFactory sessionFactory;

    private Activity activityA;

    private org.nmcpye.am.trackedentity.TrackedEntityInstance maleA;

    private org.nmcpye.am.trackedentity.TrackedEntityInstance maleB;

    private org.nmcpye.am.trackedentity.TrackedEntityInstance femaleA;

    private org.nmcpye.am.trackedentity.TrackedEntityInstance femaleB;

    private org.nmcpye.am.trackedentity.TrackedEntityInstance dateConflictsMaleA;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private Program programA;

    private ProgramStage programStageA1;

    private ProgramStage programStageA2;

    private TrackedEntityInstance teiMaleA;

    private TrackedEntityInstance teiMaleB;

    private TrackedEntityInstance teiFemaleA;

    private TrackedEntityAttribute uniqueIdAttribute;

    private TrackedEntityAttribute trackedEntityAttributeB;

    private TrackedEntityType trackedEntityType;

    @Override
    protected void setUpTest()
        throws Exception {
        Project project = createProject('A');
        activityA = createActivity('A');
        organisationUnitA = createOrganisationUnit('A');
        organisationUnitB = createOrganisationUnit('B');
        organisationUnitB.setParent(organisationUnitA);
        uniqueIdAttribute = createTrackedEntityAttribute('A');
        uniqueIdAttribute.setGenerated(true);
        // uniqueIdAttribute.setPattern( "RANDOM(#####)" );
        TextPattern textPattern = new TextPattern(
            Lists.newArrayList(new TextPatternSegment(TextPatternMethod.RANDOM, "RANDOM(#####)")));
        textPattern.setOwnerObject(Objects.TRACKEDENTITYATTRIBUTE);
        textPattern.setOwnerUid(uniqueIdAttribute.getUid());
        uniqueIdAttribute.setTextPattern(textPattern);
        trackedEntityAttributeService.addTrackedEntityAttribute(uniqueIdAttribute);
        trackedEntityAttributeB = createTrackedEntityAttribute('B');
        trackedEntityAttributeService.addTrackedEntityAttribute(trackedEntityAttributeB);
        trackedEntityType = createTrackedEntityType('A');
        TrackedEntityTypeAttribute trackedEntityTypeAttribute = new TrackedEntityTypeAttribute();
        trackedEntityTypeAttribute.setTrackedEntityAttribute(uniqueIdAttribute);
        trackedEntityTypeAttribute.setTrackedEntityType(trackedEntityType);
        trackedEntityType.setTrackedEntityTypeAttributes(Lists.newArrayList(trackedEntityTypeAttribute));
        trackedEntityTypeService.addTrackedEntityType(trackedEntityType);
        maleA = createTrackedEntityInstance(organisationUnitA);
        maleB = createTrackedEntityInstance(organisationUnitB);
        femaleA = createTrackedEntityInstance(organisationUnitA);
        femaleB = createTrackedEntityInstance(organisationUnitB);
        dateConflictsMaleA = createTrackedEntityInstance(organisationUnitA);
        TrackedEntityAttributeValue uniqueId = createTrackedEntityAttributeValue('A', maleA, uniqueIdAttribute);
        uniqueId.setValue("12345");
        maleA.setTrackedEntityType(trackedEntityType);
        maleA.setTrackedEntityAttributeValues(Sets.newHashSet(uniqueId));
        maleB.setTrackedEntityType(trackedEntityType);
        femaleA.setTrackedEntityType(trackedEntityType);
        femaleB.setTrackedEntityType(trackedEntityType);
        dateConflictsMaleA.setTrackedEntityType(trackedEntityType);
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programA.setProgramType(ProgramType.WITH_REGISTRATION);
        programStageA1 = createProgramStage('1', programA);
        programStageA2 = createProgramStage('2', programA);
        programA.setProgramStages(
            Stream.of(programStageA1, programStageA2).collect(Collectors.toCollection(HashSet::new)));
        manager.save(project);
        activityA.setProject(project);
        manager.save(activityA);
        manager.save(organisationUnitA);
        manager.save(organisationUnitB);
        manager.save(maleA);
        manager.save(maleB);
        manager.save(femaleA);
        manager.save(femaleB);
        manager.save(dateConflictsMaleA);
        manager.save(programA);
        manager.save(programStageA1);
        manager.save(programStageA2);
        teiMaleA = trackedEntityInstanceService.getTrackedEntityInstance(maleA);
        teiMaleB = trackedEntityInstanceService.getTrackedEntityInstance(maleB);
        teiFemaleA = trackedEntityInstanceService.getTrackedEntityInstance(femaleA);
        trackedEntityAttributeValueService.addTrackedEntityAttributeValue(uniqueId);
        programInstanceService.enrollTrackedEntityInstance(activityA, maleA, programA, null, null, organisationUnitA);
        programInstanceService.enrollTrackedEntityInstance(activityA, femaleA, programA,
            DateUtils.localDateTimeFromDate(DateTime.now().plusMonths(1).toDate()),
            null, organisationUnitA);
        programInstanceService.enrollTrackedEntityInstance(activityA, dateConflictsMaleA, programA,
            DateUtils.localDateTimeFromDate(DateTime.now().plusMonths(1).toDate()),
            DateUtils.localDateTimeFromDate(DateTime.now().plusMonths(2).toDate()), organisationUnitA);

        organisationUnitServiceExt.updatePaths();
    }

    @Test
    void getPersonByUid() {
        assertEquals(maleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()).getTrackedEntityInstance());
        assertEquals(femaleB.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(femaleB.getUid()).getTrackedEntityInstance());
        assertNotEquals(femaleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(femaleB.getUid()).getTrackedEntityInstance());
        assertNotEquals(maleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(maleB.getUid()).getTrackedEntityInstance());
    }

    @Test
    void getPersonByPatient() {
        assertEquals(maleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(maleA).getTrackedEntityInstance());
        assertEquals(femaleB.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(femaleB).getTrackedEntityInstance());
        assertNotEquals(femaleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(femaleB).getTrackedEntityInstance());
        assertNotEquals(maleA.getUid(),
            trackedEntityInstanceService.getTrackedEntityInstance(maleB).getTrackedEntityInstance());
    }

    @Test
    void testUpdatePerson() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        // person.setName( "UPDATED_NAME" );
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        // assertEquals( "UPDATED_NAME", personService.getTrackedEntityInstance(
        // maleA.getUid() ).getName() );
    }

    @Test
    void testUpdateTeiByCompletingExistingEnrollmentAndOpeningNewEnrollment() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        Enrollment enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        enrollment1.setStatus(EnrollmentStatus.COMPLETED);
        enrollment1.setCompletedBy("test");
        enrollment1.setCompletedDate(LocalDateTime.now());
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setTrackedEntityInstance(maleA.getUid());
        enrollment2.setEnrollmentDate(LocalDateTime.now());
        enrollment2.setOrgUnit(organisationUnitA.getUid());
        enrollment2.setActivity(activityA.getUid());
        enrollment2.setProgram(programA.getUid());
        enrollment2.setStatus(EnrollmentStatus.ACTIVE);
        trackedEntityInstance.getEnrollments().add(enrollment2);
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
    }

    @Test
    void testUpdateTeiAfterChangingTextPatternForGeneratedAttribute() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        Enrollment enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        enrollment1.setStatus(EnrollmentStatus.COMPLETED);
        enrollment1.setCompletedBy("test");
        enrollment1.setCompletedDate(LocalDateTime.now());
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setTrackedEntityInstance(maleA.getUid());
        TextPattern textPattern = new TextPattern(
            Lists.newArrayList(new TextPatternSegment(TextPatternMethod.RANDOM, "RANDOM(#######)")));
        textPattern.setOwnerUid("owneruid");
        textPattern.setOwnerObject(Objects.CONSTANT);
        uniqueIdAttribute.setTextPattern(textPattern);
        trackedEntityAttributeService.updateTrackedEntityAttribute(uniqueIdAttribute);
        enrollment2.setEnrollmentDate(LocalDateTime.now());
        enrollment2.setOrgUnit(organisationUnitA.getUid());
        enrollment2.setActivity(activityA.getUid());
        enrollment2.setProgram(programA.getUid());
        enrollment2.setStatus(EnrollmentStatus.ACTIVE);
        trackedEntityInstance.getEnrollments().add(enrollment2);
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
    }

    @Test
    void testUpdateTeiByCompletingExistingEnrollmentAndAddNewEventsToSameEnrollment() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        Enrollment enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        enrollment1.setStatus(EnrollmentStatus.COMPLETED);
        enrollment1.setCompletedBy("test");
        enrollment1.setCompletedDate(LocalDateTime.now());
        Event event1 = new Event();
        event1.setEnrollment(enrollment1.getEnrollment());
        event1
            .setEventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));
        event1.setOrgUnit(organisationUnitA.getUid());
        event1.setActivity(activityA.getUid());
        event1.setProgram(programA.getUid());
        event1.setProgramStage(programStageA1.getUid());
        event1.setStatus(EventStatus.COMPLETED);
        event1.setTrackedEntityInstance(maleA.getUid());
        Event event2 = new Event();
        event2.setEnrollment(enrollment1.getEnrollment());
        event2
            .setEventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));
        event2.setOrgUnit(organisationUnitA.getUid());
        event2.setActivity(activityA.getUid());
        event2.setProgram(programA.getUid());
        event2.setProgramStage(programStageA2.getUid());
        event2.setStatus(EventStatus.ACTIVE);
        event2.setTrackedEntityInstance(maleA.getUid());
        enrollment1.setEvents(Arrays.asList(event1, event2));
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
        assertEquals(ImportStatus.SUCCESS,
            importSummary.getEnrollments().getImportSummaries().get(0).getEvents().getStatus());
    }

    @Test
    void testSyncTeiFutureDatesForEnrollmentAndIncident() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(dateConflictsMaleA.getUid());
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, new ImportOptions().setImportStrategy(ImportStrategy.SYNC), true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(2, importSummary.getEnrollments().getImportSummaries().get(0).getConflictCount());
        assertEquals(trackedEntityInstance.getEnrollments().get(0).getEnrollment(),
            importSummary.getEnrollments().getImportSummaries().get(0).getReference());
    }

    /**
     * FIXME luciano: this is ignored because there is a bug in tracker, so that
     * new events that fail to validate are reported as success.
     */
    @Test
    void testUpdateTeiByCompletingExistingEnrollmentAndUpdateExistingEventsInSameEnrollment() {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        Enrollment enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        Event event1 = new Event();
        event1.setEnrollment(enrollment1.getEnrollment());
        event1
            .setEventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));
        event1.setOrgUnit(organisationUnitA.getUid());
        event1.setActivity(activityA.getUid());
        event1.setProgram(programA.getUid());
        event1.setProgramStage(programStageA1.getUid());
        event1.setStatus(EventStatus.ACTIVE);
        event1.setTrackedEntityInstance(maleA.getUid());
        enrollment1.setEvents(singletonList(event1));
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
        assertEquals(ImportStatus.SUCCESS,
            importSummary.getEnrollments().getImportSummaries().get(0).getEvents().getStatus());
        // This is required because the Event creation takes place using JDBC,
        // therefore Hibernate does not
        // "see" the new event in the context of this session
        sessionFactory.getCurrentSession().clear();

        trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        assertNotNull(trackedEntityInstance.getEnrollments().get(0).getEvents());
        assertEquals(1, trackedEntityInstance.getEnrollments().get(0).getEvents().size());
        enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        enrollment1.setStatus(EnrollmentStatus.COMPLETED);
        enrollment1.setCompletedBy("test");
        enrollment1.setCompletedDate(LocalDateTime.now());
        event1 = enrollment1.getEvents().get(0);
        event1.setStatus(EventStatus.COMPLETED);
        event1.setCompletedBy("test");
        event1.setCompletedDate(
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));
        importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance, null, null,
            true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
        assertEquals(ImportStatus.SUCCESS,
            importSummary.getEnrollments().getImportSummaries().get(0).getEvents().getStatus());
    }

    @Test
    void testUpdateTeiByDeletingExistingEventAndAddNewEventForSameProgramStage() {
        // Making program stage repeatable
        programStageA2.setRepeatable(true);
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        Enrollment enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        Event event1 = new Event();
        event1.setEnrollment(enrollment1.getEnrollment());
        event1
            .setEventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));
        event1.setOrgUnit(organisationUnitA.getUid());
        event1.setActivity(activityA.getUid());
        event1.setProgram(programA.getUid());
        event1.setProgramStage(programStageA1.getUid());
        event1.setStatus(EventStatus.COMPLETED);
        event1.setTrackedEntityInstance(maleA.getUid());
        Event event2 = new Event();
        event2.setEnrollment(enrollment1.getEnrollment());
        event2.setOrgUnit(organisationUnitA.getUid());
        event2.setActivity(activityA.getUid());
        event2.setProgram(programA.getUid());
        event2.setProgramStage(programStageA2.getUid());
        event2.setStatus(EventStatus.SCHEDULE);
        event2.setDueDate(
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now().plusDays(10)));
        event2.setTrackedEntityInstance(maleA.getUid());
        enrollment1.setEvents(Arrays.asList(event1, event2));
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, null, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
        assertEquals(ImportStatus.SUCCESS,
            importSummary.getEnrollments().getImportSummaries().get(0).getEvents().getStatus());
        manager.flush();

        sessionFactory.getCurrentSession().clear();

        trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid());
        assertNotNull(trackedEntityInstance.getEnrollments());
        assertEquals(1, trackedEntityInstance.getEnrollments().size());
        assertNotNull(trackedEntityInstance.getEnrollments().get(0).getEvents());
        assertEquals(2, trackedEntityInstance.getEnrollments().get(0).getEvents().size());
        enrollment1 = trackedEntityInstance.getEnrollments().get(0);
        event2 = enrollment1.getEvents().stream().filter(e -> e.getProgramStage().equals(programStageA2.getUid()))
            .findFirst().get();
        event2.setDeleted(true);
        Event event3 = new Event();
        event3.setEnrollment(enrollment1.getEnrollment());
        event3.setOrgUnit(organisationUnitA.getUid());
        event3.setActivity(activityA.getUid());
        event3.setProgram(programA.getUid());
        event3.setProgramStage(programStageA2.getUid());
        event3.setStatus(EventStatus.SCHEDULE);
        event3.setDueDate(
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now().plusDays(11)));
        event3.setTrackedEntityInstance(maleA.getUid());
        enrollment1.getEvents().add(event3);
        ImportOptions importOptions = new ImportOptions();
        importOptions.setImportStrategy(ImportStrategy.SYNC);
        importSummary = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance, null,
            importOptions, true);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
        assertEquals(ImportStatus.SUCCESS, importSummary.getEnrollments().getStatus());
        assertEquals(ImportStatus.SUCCESS,
            importSummary.getEnrollments().getImportSummaries().get(0).getEvents().getStatus());
    }

    @Test
    void testSavePerson() {
        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setTrackedEntityInstance(CodeGenerator.generateUid());
        trackedEntityInstance.setOrgUnit(organisationUnitA.getUid());
        trackedEntityInstance.setTrackedEntityType(trackedEntityType.getUid());
        ImportSummary importSummary = trackedEntityInstanceService.addTrackedEntityInstance(trackedEntityInstance,
            null);
        assertEquals(ImportStatus.SUCCESS, importSummary.getStatus());
    }

    @Test
    void testDeletePerson() {
        trackedEntityInstanceService.deleteTrackedEntityInstance(maleA.getUid());
        assertNull(trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()));
        assertNotNull(trackedEntityInstanceService.getTrackedEntityInstance(maleB.getUid()));
        boolean existsDeleted = teiDaoService.trackedEntityInstanceExistsIncludingDeleted(maleA.getUid());
        assertTrue(existsDeleted);
    }

    @Test
    void testDeleteTrackedEntityInstances() {
        List<TrackedEntityInstance> teis = Lists.newArrayList(
            trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()),
            trackedEntityInstanceService.getTrackedEntityInstance(maleB.getUid()));
        ImportOptions importOptions = new ImportOptions();
        importOptions.setImportStrategy(ImportStrategy.DELETE);
        trackedEntityInstanceService.deleteTrackedEntityInstances(teis, importOptions);
        assertNull(trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()));
        assertNull(trackedEntityInstanceService.getTrackedEntityInstance(maleB.getUid()));
    }

    @Test
    void testTooLongTrackedEntityAttributeValue() {
        TrackedEntityInstance tei = new TrackedEntityInstance();
        String testValue = StringUtils.repeat("x", 1201);
        Attribute attribute = new Attribute(testValue);
        attribute.setAttribute(trackedEntityAttributeB.getUid());
        tei.getAttributes().add(attribute);
        tei.setTrackedEntityType(trackedEntityType.getUid());
        List<TrackedEntityInstance> teis = Lists.newArrayList(tei);
        ImportOptions importOptions = new ImportOptions();
        importOptions.setImportStrategy(ImportStrategy.UPDATE);
        ImportSummaries importSummaries = trackedEntityInstanceService.addTrackedEntityInstances(teis, importOptions);
        assertEquals(ImportStatus.ERROR, importSummaries.getStatus());
        ImportSummary importSummary = importSummaries.getImportSummaries().get(0);
        assertEquals(1, importSummary.getConflictCount());
        assertEquals(String.format("Value exceeds the character limit of 1200 characters: '%s...'",
            testValue.substring(0, 25)), importSummary.getConflicts().iterator().next().getValue());
    }

    @Test
    void testAddAlreadyDeletedTei() {
        ImportOptions importOptions = new ImportOptions();
        trackedEntityInstanceService.addTrackedEntityInstance(teiMaleA, importOptions);
        trackedEntityInstanceService.deleteTrackedEntityInstance(teiMaleA.getTrackedEntityInstance());
        manager.flush();
        importOptions.setImportStrategy(ImportStrategy.CREATE);
        teiMaleA.setDeleted(true);
        ImportSummary importSummary = trackedEntityInstanceService.addTrackedEntityInstance(teiMaleA, importOptions);
        assertEquals(ImportStatus.ERROR, importSummary.getStatus());
        assertEquals(1, importSummary.getImportCount().getIgnored());
        assertTrue(importSummary.getDescription().contains("already exists or was deleted earlier"));
    }

    @Test
    void testAddAlreadyDeletedTeiInBulk() {
        ImportOptions importOptions = new ImportOptions();
        trackedEntityInstanceService.addTrackedEntityInstance(teiMaleA, importOptions);
        trackedEntityInstanceService.deleteTrackedEntityInstance(teiMaleA.getTrackedEntityInstance());
        manager.flush();
        importOptions.setImportStrategy(ImportStrategy.CREATE);
        teiMaleA.setDeleted(true);
        teiMaleB.setTrackedEntityInstance("teiUid00002");
        teiFemaleA.setTrackedEntityInstance("teiUid00003");
        List<TrackedEntityInstance> teis = new ArrayList<>();
        teis.add(teiMaleA);
        teis.add(teiMaleB);
        teis.add(teiFemaleA);
        ImportSummaries importSummaries = trackedEntityInstanceService.addTrackedEntityInstances(teis, importOptions);
        assertEquals(ImportStatus.ERROR, importSummaries.getStatus());
        assertEquals(1, importSummaries.getIgnored());
        assertEquals(2, importSummaries.getImported());
        assertTrue(importSummaries.getImportSummaries().stream()
            .anyMatch(is -> is.getDescription().contains("already exists or was deleted earlier")));
        manager.flush();
        List<String> uids = new ArrayList<>();
        uids.add(teiMaleA.getTrackedEntityInstance());
        uids.add(teiMaleB.getTrackedEntityInstance());
        uids.add(teiFemaleA.getTrackedEntityInstance());
        List<String> fetchedUids = teiDaoService.getTrackedEntityInstancesUidsIncludingDeleted(uids);
        assertTrue(Sets.difference(new HashSet<>(uids), new HashSet<>(fetchedUids)).isEmpty());
    }

    @Test
    void testAddTrackedEntityInstancePotentialDuplicateFlag() {
        TrackedEntityInstance tei = new TrackedEntityInstance();
        tei.setOrgUnit(organisationUnitA.getUid());
        tei.setTrackedEntityInstance(CodeGenerator.generateUid());
        Attribute attribute = new Attribute("value");
        attribute.setAttribute(trackedEntityAttributeB.getUid());
        tei.getAttributes().add(attribute);
        tei.setTrackedEntityType(trackedEntityType.getUid());
        tei.setPotentialDuplicate(true);
        List<TrackedEntityInstance> teis = Lists.newArrayList(tei);
        ImportOptions importOptions = new ImportOptions();
        importOptions.setImportStrategy(ImportStrategy.CREATE);
        ImportSummaries importSummaries = trackedEntityInstanceService.addTrackedEntityInstances(teis, importOptions);
        assertEquals(ImportStatus.SUCCESS, importSummaries.getStatus());
        assertTrue(trackedEntityInstanceService.getTrackedEntityInstance(tei.getTrackedEntityInstance())
            .isPotentialDuplicate());
    }

    @Test
    void testUpdateTrackedEntityInstancePotentialDuplicateFlag() {
        assertFalse(trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()).isPotentialDuplicate());
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService
            .getTrackedEntityInstance(maleA.getUid());
        trackedEntityInstance.setPotentialDuplicate(true);
        ImportOptions importOptions = new ImportOptions();
        importOptions.setImportStrategy(ImportStrategy.UPDATE);
        ImportSummary importSummaries = trackedEntityInstanceService.updateTrackedEntityInstance(trackedEntityInstance,
            null, importOptions, true);
        assertEquals(ImportStatus.SUCCESS, importSummaries.getStatus());
        assertTrue(trackedEntityInstanceService.getTrackedEntityInstance(maleA.getUid()).isPotentialDuplicate());
    }
}
