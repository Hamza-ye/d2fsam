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
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramInstanceServiceExt;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.project.ProjectServiceExt;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.nmcpye.am.utils.Assertions.assertContainsOnly;

/**
 * @author Zubair Asghar
 */
class TrackedEntityInstanceQueryLimitTest
    extends /*TransactionalIntegrationTest*/SingleSetupIntegrationTestBase {

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Autowired
    private TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    @Autowired
    private ProgramServiceExt programService;

    @Autowired
    private ProgramInstanceServiceExt programInstanceService;

    @Autowired
    private ProjectServiceExt projectServiceExt;

    @Autowired
    private ActivityServiceExt activityServiceExt;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private UserServiceExt _userService;

    private Activity activityA;

    private OrganisationUnit orgUnitA;

    private Program program;

    private ProgramInstance pi1;

    private ProgramInstance pi2;

    private ProgramInstance pi3;

    private ProgramInstance pi4;

    private TrackedEntityInstance tei1;

    private TrackedEntityInstance tei2;

    private TrackedEntityInstance tei3;

    private TrackedEntityInstance tei4;

    private TrackedEntityType teiType;

    private User user;

    @Override
    protected void setUpTest()
        throws Exception {
        userService = _userService;
        user = createAndInjectAdminUser();

        Project project = createProject('A');
        projectServiceExt.addProject(project);
        activityA = createActivity('A');
        activityA.setProject(project);
        activityServiceExt.addActivity(activityA);

        orgUnitA = createOrganisationUnit("A");
        organisationUnitServiceExt.addOrganisationUnit(orgUnitA);

        user.getOrganisationUnits().add(orgUnitA);

        teiType = createTrackedEntityType('P');
        trackedEntityTypeService.addTrackedEntityType(teiType);

        program = createProgram('P');
        programService.addProgram(program);

        tei1 = createTrackedEntityInstance(orgUnitA);
        tei2 = createTrackedEntityInstance(orgUnitA);
        tei3 = createTrackedEntityInstance(orgUnitA);
        tei4 = createTrackedEntityInstance(orgUnitA);
        tei1.setTrackedEntityType(teiType);
        tei2.setTrackedEntityType(teiType);
        tei3.setTrackedEntityType(teiType);
        tei4.setTrackedEntityType(teiType);

        trackedEntityInstanceService.addTrackedEntityInstance(tei1);
        trackedEntityInstanceService.addTrackedEntityInstance(tei2);
        trackedEntityInstanceService.addTrackedEntityInstance(tei3);
        trackedEntityInstanceService.addTrackedEntityInstance(tei4);

        pi1 = createProgramInstance(program, tei1, orgUnitA);
        pi2 = createProgramInstance(program, tei2, orgUnitA);
        pi3 = createProgramInstance(program, tei3, orgUnitA);
        pi4 = createProgramInstance(program, tei4, orgUnitA);

        programInstanceService.addProgramInstance(pi1);
        programInstanceService.addProgramInstance(pi2);
        programInstanceService.addProgramInstance(pi3);
        programInstanceService.addProgramInstance(pi4);

        programInstanceService.enrollTrackedEntityInstance(activityA, tei1, program,
            LocalDateTime.now(), LocalDateTime.now(), orgUnitA);
        programInstanceService.enrollTrackedEntityInstance(activityA, tei2, program,
            LocalDateTime.now(), LocalDateTime.now(), orgUnitA);
        programInstanceService.enrollTrackedEntityInstance(activityA, tei3, program,
            LocalDateTime.now(), LocalDateTime.now(), orgUnitA);
        programInstanceService.enrollTrackedEntityInstance(activityA, tei4, program,
            LocalDateTime.now(), LocalDateTime.now(), orgUnitA);

        userService.addUser(user);
    }

    @Test
    void testConfiguredPositiveMaxTeiLimit() {
        systemSettingManager.saveSystemSetting(SettingKey.TRACKED_ENTITY_MAX_LIMIT, 3);
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setProgram(program);
        params.setOrganisationUnits(Set.of(orgUnitA));
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        params.setUserWithAssignedUsers( null, user, null );
        params.setSkipPaging(true);

        List<Long> teis = trackedEntityInstanceService.getTrackedEntityInstanceIds(params,
            false, false);

        assertNotNull(teis);
        assertEquals(3, teis.size(), "Size cannot be more than configured Tei max limit");
    }

    @Test
    void testConfiguredNegativeMaxTeiLimit() {
        systemSettingManager.saveSystemSetting(SettingKey.TRACKED_ENTITY_MAX_LIMIT, -1);

        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setProgram(program);
        params.setOrganisationUnits(Set.of(orgUnitA));
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        params.setUserWithAssignedUsers( null, user, null );
        params.setSkipPaging(true);

        List<Long> teis = trackedEntityInstanceService.getTrackedEntityInstanceIds(params,
            false, false);

        assertContainsOnly(List.of(tei1.getId(), tei2.getId(), tei3.getId(), tei4.getId()), teis);
    }

    @Test
    void testDefaultMaxTeiLimit() {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setProgram(program);
        params.setOrganisationUnits(Set.of(orgUnitA));
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        params.setUserWithAssignedUsers( null, user, null );
        params.setSkipPaging(true);

        List<Long> teis = trackedEntityInstanceService.getTrackedEntityInstanceIds(params,
            false, false);

        assertContainsOnly(List.of(tei1.getId(), tei2.getId(), tei3.getId(), tei4.getId()), teis);
    }

    @Test
    void testDisabledMaxTeiLimit() {
        systemSettingManager.saveSystemSetting(SettingKey.TRACKED_ENTITY_MAX_LIMIT, 0);

        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();
        params.setProgram(program);
        params.setOrganisationUnits(Set.of(orgUnitA));
        params.setOrganisationUnitMode(OrganisationUnitSelectionMode.ALL);
        params.setUserWithAssignedUsers( null, user, null );
        params.setSkipPaging(true);

        List<Long> teis = trackedEntityInstanceService.getTrackedEntityInstanceIds(params,
            false, false);

        assertContainsOnly(List.of(tei1.getId(), tei2.getId(), tei3.getId(), tei4.getId()), teis);
    }
}
