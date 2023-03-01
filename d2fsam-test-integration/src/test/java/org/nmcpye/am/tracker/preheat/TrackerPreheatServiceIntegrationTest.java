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
package org.nmcpye.am.tracker.preheat;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeService;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeServiceExt;
import org.nmcpye.am.tracker.TrackerIdScheme;
import org.nmcpye.am.tracker.TrackerIdSchemeParam;
import org.nmcpye.am.tracker.TrackerIdSchemeParams;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.domain.Enrollment;
import org.nmcpye.am.tracker.domain.MetadataIdentifier;
import org.nmcpye.am.tracker.domain.TrackedEntity;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrackerPreheatServiceIntegrationTest extends TransactionalIntegrationTest {

    private final String TET_UID = "TET12345678";

    private final String ATTRIBUTE_UID = "ATTR1234567";

    @Autowired
    private TrackerPreheatService trackerPreheatService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Autowired
    private ProgramServiceExt programService;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private UserServiceExt _userService;

    private User currentUser;

    private Program program;

    private String programAttribute;

    @Override
    public void setUpTest()
        throws Exception {
        userService = _userService;
        currentUser = createAndInjectAdminUser("ALL");
        // Set up placeholder OU; We add Code for testing idScheme.
        OrganisationUnit orgUnit = createOrganisationUnit('A');
        orgUnit.setCode("OUA");
        organisationUnitServiceExt.addOrganisationUnit(orgUnit);
        // Set up placeholder TET
        TrackedEntityType trackedEntityType = createTrackedEntityType('A');
        trackedEntityType.setUid(TET_UID);
        trackedEntityTypeService.addTrackedEntityType(trackedEntityType);
        // Set up attribute for program, to be used for testing idScheme.
        Attribute attributeA = createAttribute('A');
        attributeA.setUid(ATTRIBUTE_UID);
        attributeA.setUnique(true);
        attributeA.setProgramAttribute(true);
        attributeService.addAttribute(attributeA);
        // Set up placeholder Program, with attributeValue
        program = createProgram('A');
        program.addOrganisationUnit(orgUnit);
        program.setTrackedEntityType(trackedEntityType);
        program.setProgramType(ProgramType.WITH_REGISTRATION);
        programAttribute = "PROGRAM1";
        program.setAttributeValues(Sets.newHashSet(new AttributeValue(programAttribute, attributeA)));
        programService.addProgram(program);
    }

    @Test
    void testPreheatWithDifferentIdSchemes() {
        TrackedEntity teA = TrackedEntity.builder()
            .orgUnit(MetadataIdentifier.ofCode("OUA"))
            .trackedEntityType(MetadataIdentifier.ofUid(TET_UID))
            .build();
        Enrollment enrollmentA = Enrollment.builder()
            .orgUnit(MetadataIdentifier.ofCode("OUA"))
            .program(MetadataIdentifier.ofAttribute(ATTRIBUTE_UID, programAttribute))
            .trackedEntity("TE123456789")
            .build();

        TrackerImportParams params = TrackerImportParams.builder()
            .user(currentUser)
            .trackedEntities(Lists.newArrayList(teA))
            .enrollments(Lists.newArrayList(enrollmentA))
            .idSchemes(TrackerIdSchemeParams.builder()
                .idScheme(TrackerIdSchemeParam.UID)
                .orgUnitIdScheme(TrackerIdSchemeParam.CODE)
                .programIdScheme(TrackerIdSchemeParam.of(TrackerIdScheme.ATTRIBUTE, ATTRIBUTE_UID))
                .build())
            .build();

        TrackerPreheat preheat = trackerPreheatService.preheat(params);

        assertNotNull(preheat);
        // asserting on specific fields instead of plain assertEquals since
        // PreheatMappers are not mapping all fields
        assertEquals("OUA", preheat.getOrganisationUnit("OUA").getCode());
        assertEquals(TET_UID, preheat.getTrackedEntityType(TET_UID).getUid());
        Program actualProgram = preheat.getProgram(programAttribute);
        assertNotNull(actualProgram);
        assertEquals(program.getUid(), actualProgram.getUid());
    }
}
