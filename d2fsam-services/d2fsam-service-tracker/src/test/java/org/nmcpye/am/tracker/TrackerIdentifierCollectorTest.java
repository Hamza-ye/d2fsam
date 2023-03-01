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
package org.nmcpye.am.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.programrule.ProgramRuleService;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.tracker.domain.*;

import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.nmcpye.am.tracker.domain.MetadataIdentifier.*;
import static org.nmcpye.am.utils.Assertions.assertContainsOnly;

class TrackerIdentifierCollectorTest {

    private TrackerIdentifierCollector collector;

    @BeforeEach
    void setUp() {

        ProgramRuleService programRuleService = mock(ProgramRuleService.class);
        collector = new TrackerIdentifierCollector(programRuleService);
    }

    @Test
    void collectTrackedEntities() {

        TrackerIdSchemeParams idSchemes = TrackerIdSchemeParams.builder()
            .idScheme(TrackerIdSchemeParam.ofAttribute("NTVsGflP5Ix"))
            .orgUnitIdScheme(TrackerIdSchemeParam.NAME)
            .build();

        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity(uid())
            .trackedEntityType(ofAttribute("NTVsGflP5Ix", "sunshine"))
            .orgUnit(ofName("ward"))
            .attributes(teAttributes("VohJnvWfvyo", "qv9xOw8fBzy"))
            .build();

        TrackerImportParams params = params(idSchemes)
            .trackedEntities(singletonList(trackedEntity))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertContainsOnly(Set.of(trackedEntity.getTrackedEntity()), ids.get(TrackedEntity.class));
        assertContainsOnly(Set.of("sunshine"), ids.get(TrackedEntityType.class));
        assertContainsOnly(Set.of("ward"), ids.get(OrganisationUnit.class));
        assertContainsOnly(Set.of("VohJnvWfvyo", "qv9xOw8fBzy"), ids.get(TrackedEntityAttribute.class));
    }

    @Test
    void collectEnrollments() {

        TrackerIdSchemeParams idSchemes = TrackerIdSchemeParams.builder()
            .activityIdScheme(TrackerIdSchemeParam.NAME)
            .orgUnitIdScheme(TrackerIdSchemeParam.NAME)
            .programIdScheme(TrackerIdSchemeParam.ofAttribute("NTVsGflP5Ix"))
            .build();

        Enrollment enrollment = Enrollment.builder()
            .enrollment(uid())
            .trackedEntity(uid())
            .activity(ofName("active"))
            .program(ofAttribute("NTVsGflP5Ix", "sunshine"))
            .orgUnit(ofName("ward"))
            .attributes(teAttributes("VohJnvWfvyo", "qv9xOw8fBzy"))
            .build();

        TrackerImportParams params = params(idSchemes)
            .enrollments(singletonList(enrollment))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertContainsOnly(Set.of(enrollment.getUid()), ids.get(Enrollment.class));
        assertContainsOnly(Set.of(enrollment.getTrackedEntity()), ids.get(TrackedEntity.class));
        assertContainsOnly(Set.of("sunshine"), ids.get(Program.class));
        assertContainsOnly(Set.of("active"), ids.get(Activity.class));
        assertContainsOnly(Set.of("ward"), ids.get(OrganisationUnit.class));
        // NMCP
        assertContainsOnly(Set.of("VohJnvWfvyo", "qv9xOw8fBzy"), ids.get(TrackedEntityAttribute.class));
    }

    @Test
    void collectEvents() {

        TrackerIdSchemeParams idSchemes = TrackerIdSchemeParams.builder()
            .activityIdScheme(TrackerIdSchemeParam.NAME)
            .orgUnitIdScheme(TrackerIdSchemeParam.NAME)
            .programIdScheme(TrackerIdSchemeParam.ofAttribute("NTVsGflP5Ix"))
            .dataElementIdScheme(TrackerIdSchemeParam.UID)
            .categoryOptionComboIdScheme(TrackerIdSchemeParam.CODE)
            .build();

        Event event = Event.builder()
            .event(uid())
            .enrollment(uid())
            .program(ofAttribute("NTVsGflP5Ix", "sunshine"))
            .programStage(ofAttribute("NTVsGflP5Ix", "flowers"))
            .activity(ofName("active"))
            .orgUnit(ofName("ward"))
            .dataValues(dataValues("VohJnvWfvyo", "qv9xOw8fBzy"))
            .attributeOptionCombo(ofCode("rgb"))
            .attributeCategoryOptions(Set.of(ofCode("red"), ofCode("green"), ofCode("blue")))
            .notes(List.of(Note.builder().note("i1vviSlidJE").value("nice day!").build()))
            .build();

        TrackerImportParams params = params(idSchemes)
            .events(singletonList(event))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertContainsOnly(Set.of(event.getUid()), ids.get(Event.class));
        assertContainsOnly(Set.of(event.getEnrollment()), ids.get(Enrollment.class));
        assertContainsOnly(Set.of("sunshine"), ids.get(Program.class));
        assertContainsOnly(Set.of("flowers"), ids.get(ProgramStage.class));
        assertContainsOnly(Set.of("active"), ids.get(Activity.class));
        assertContainsOnly(Set.of("ward"), ids.get(OrganisationUnit.class));
        assertContainsOnly(Set.of("VohJnvWfvyo", "qv9xOw8fBzy"), ids.get(DataElement.class));
//        assertContainsOnly(Set.of("rgb"), ids.get(CategoryOptionCombo.class));
//        assertContainsOnly(Set.of("red", "green", "blue"), ids.get(CategoryOption.class));
        assertContainsOnly(Set.of("i1vviSlidJE"), ids.get(Comment.class));
    }

    @Test
    void collectEventsSkipsNotesWithoutAnId() {
        Event event = Event.builder()
            .notes(List.of(Note.builder().value("nice day!").build()))
            .build();

        TrackerImportParams params = params(TrackerIdSchemeParams.builder().build())
            .events(singletonList(event))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertNull(ids.get(Comment.class));
    }

    @Test
    void collectEventsSkipsNotesWithoutAValue() {
        Event event = Event.builder()
            .notes(List.of(Note.builder().note("i1vviSlidJE").build()))
            .build();

        TrackerImportParams params = params(TrackerIdSchemeParams.builder().build())
            .events(singletonList(event))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertNull(ids.get(Comment.class));
    }

    @Test
    void collectRelationships() {

        TrackerIdSchemeParams idSchemes = TrackerIdSchemeParams.builder()
            .idScheme(TrackerIdSchemeParam.ofAttribute("NTVsGflP5Ix"))
            .orgUnitIdScheme(TrackerIdSchemeParam.NAME)
            .build();

        Relationship relationship = Relationship.builder()
            .relationship(uid())
            .relationshipType(ofAttribute("NTVsGflP5Ix", "sunshine"))
            .from(RelationshipItem.builder()
                .enrollment(uid())
                .build())
            .to(RelationshipItem.builder()
                .event(uid())
                .build())
            .build();

        TrackerImportParams params = params(idSchemes)
            .relationships(singletonList(relationship))
            .build();

        Map<Class<?>, Set<String>> ids = collector.collect(params);

        assertNotNull(ids);
        assertContainsOnly(Set.of(relationship.getRelationship()), ids.get(Relationship.class));
        assertContainsOnly(Set.of("sunshine"), ids.get(RelationshipType.class));
        assertContainsOnly(Set.of(relationship.getFrom().getEnrollment()), ids.get(Enrollment.class));
        assertContainsOnly(Set.of(relationship.getTo().getEvent()), ids.get(Event.class));
    }

    private String uid() {
        return CodeGenerator.generateUid();
    }

    private TrackerImportParams.TrackerImportParamsBuilder params(TrackerIdSchemeParams idSchemes) {
        return TrackerImportParams.builder().idSchemes(idSchemes);
    }

    private List<Attribute> teAttributes(String... uids) {

        List<Attribute> result = new ArrayList<>();
        for (String uid : uids) {
            result.add(teAttribute(uid));
        }
        return result;
    }

    private Attribute teAttribute(String uid) {
        return Attribute.builder()
            .attribute(ofUid(uid))
            .build();
    }

    private Set<DataValue> dataValues(String... dataElementUids) {

        Set<DataValue> result = new HashSet<>();
        for (String uid : dataElementUids) {
            result.add(dataValue(uid));
        }
        return result;
    }

    private DataValue dataValue(String dataElementUid) {
        return DataValue.builder()
            .dataElement(ofUid(dataElementUid))
            .build();
    }
}
