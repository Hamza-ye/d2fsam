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
package org.nmcpye.am.dxf2.metadata.objectbundle;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.dxf2.metadata.AtomicMode;
import org.nmcpye.am.dxf2.metadata.objectbundle.feedback.ObjectBundleValidationReport;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.preheat.PreheatIdentifier;
import org.nmcpye.am.render.RenderFormat;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.user.sharing.UserAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class ObjectBundleServiceUserTest extends TransactionalIntegrationTest {

    @Autowired
    private ObjectBundleService objectBundleService;

    @Autowired
    private ObjectBundleValidationService objectBundleValidationService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private RenderService _renderService;

    @Autowired
    private UserServiceExt _userService;

    @Override
    protected void setUpTest()
        throws Exception {
        renderService = _renderService;
        userService = _userService;
    }

    @Test
    @Disabled("NMCP Temp")
    void testCreateUsers()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE, AtomicMode.NONE,
            "dxf2/users.json");
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertEquals(1, validate.getErrorReportsCountByCode(UserAuthorityGroup.class, ErrorCode.E5003));
        objectBundleService.commit(bundle);
        List<User> users = manager.getAll(User.class);
        assertEquals(4, users.size());
        User userA = userService.getUser("sPWjoHSY03y");
        User userB = userService.getUser("MwhEJUnTHkn");
        assertEquals("usera", userA.getUsername());
        assertEquals("admin", userA.getCreatedBy().getUsername());
        assertEquals("user@a.org", userA.getEmail());
//        assertEquals(Integer.valueOf(3), userA.getDataViewMaxOrganisationUnitLevel());
        assertEquals(1, userA.getOrganisationUnits().size());
        assertEquals("userb", userB.getUsername());
        assertEquals("admin", userB.getCreatedBy().getUsername());
        assertEquals("user@b.org", userB.getEmail());
//        assertEquals(Integer.valueOf(4), userB.getDataViewMaxOrganisationUnitLevel());
        assertEquals(1, userB.getOrganisationUnits().size());
    }

    @Test
    @Disabled("NMCP Temp")
    void testUpdateUsers()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE, AtomicMode.NONE,
            "dxf2/users.json");
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertEquals(1, validate.getErrorReportsCountByCode(UserAuthorityGroup.class, ErrorCode.E5003));
        objectBundleService.commit(bundle);
        params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.UPDATE, AtomicMode.NONE,
            "dxf2/users_update.json");
        bundle = objectBundleService.create(params);
        validate = objectBundleValidationService.validate(bundle);
        assertEquals(1, validate.getErrorReportsCountByCode(UserAuthorityGroup.class, ErrorCode.E5001));
        objectBundleService.commit(bundle);
        List<User> users = manager.getAll(User.class);
        assertEquals(4, users.size());
        User userA = manager.get(User.class, "sPWjoHSY03y");
        User userB = manager.get(User.class, "MwhEJUnTHkn");
        assertEquals("usera", userA.getUsername());
        assertEquals("admin", userA.getCreatedBy().getUsername());
        assertEquals("userb", userB.getUsername());
        assertEquals("admin", userB.getCreatedBy().getUsername());
    }

    @Test
    @Disabled("NMCP Temp")
    void testUpdateUsersRunsSchemaValidation()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE, AtomicMode.NONE,
            "dxf2/users.json");
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertEquals(1, validate.getErrorReportsCountByCode(UserAuthorityGroup.class, ErrorCode.E5003));
        objectBundleService.commit(bundle);
        params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.UPDATE, AtomicMode.NONE,
            "dxf2/users_illegal_update.json");
        bundle = objectBundleService.create(params);
        ObjectBundleValidationReport report = objectBundleValidationService.validate(bundle);
        assertEquals(1, report.getErrorReportsCountByCode(User.class, ErrorCode.E4003));
        assertTrue(report.hasErrorReport(error -> "email".equals(error.getErrorProperty())));
    }

    @Test
    void testCreateMetadataWithDuplicateUsername()
        throws IOException {
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE_AND_UPDATE,
            AtomicMode.NONE, "dxf2/user_duplicate_username.json");
        ObjectBundle bundle = objectBundleService.create(params);
        objectBundleValidationService.validate(bundle);
        objectBundleService.commit(bundle);
        assertEquals(1, manager.getAll(User.class).size());
    }

    @Test
    void testCreateMetadataWithDuplicateUsernameAndInjectedUser()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE_AND_UPDATE,
            AtomicMode.NONE, "dxf2/user_duplicate_username.json");
        ObjectBundle bundle = objectBundleService.create(params);
        objectBundleValidationService.validate(bundle);
        objectBundleService.commit(bundle);
        assertEquals(2, manager.getAll(User.class).size());
    }

    @Test
    @Disabled("NMCP Temp")
    void testUpdateAdminUser()
        throws IOException {
        createAndInjectAdminUser();
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.UPDATE, AtomicMode.ALL,
            "dxf2/user_admin.json");
        ObjectBundle bundle = objectBundleService.create(params);
        var validation = objectBundleValidationService.validate(bundle);
        assertEquals(0, validation.getErrorReportsCount());
    }

    @Test
    void testCreateUsersWithInvalidPasswords()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.VALIDATE, ImportStrategy.CREATE,
            AtomicMode.ALL, "dxf2/users_passwords.json");
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertEquals(1, validate.getErrorReportsCountByCode(User.class, ErrorCode.E4005));
    }

    @Test
    void testUpdateUserWithNoAccessUserRole()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE_AND_UPDATE,
            AtomicMode.ALL, "dxf2/user_userrole.json");
        ObjectBundle bundle1 = objectBundleService.create(params);
        objectBundleService.commit(bundle1);
        User userB = manager.get(User.class, "MwhEJUnTHkn");
        User userA = manager.get(User.class, "sPWjoHSY03y");
        assertEquals(2, userA.getUserAuthorityGroups().size());
        assertEquals(2, userB.getUserAuthorityGroups().size());
        UserAuthorityGroup userManagerRole = manager.get(UserAuthorityGroup.class, "xJZBzAHI88H");
        assertNotNull(userManagerRole);
        userManagerRole.getSharing().resetUserAccesses();
        userManagerRole.getSharing().addUserAccess(new UserAccess(userB, "rw------"));
        userManagerRole.setPublicAccess("--------");
        userManagerRole.setCreatedBy(userB);
        manager.update(userManagerRole);
        SecurityContextHolder.clearContext();
        userA.setPassword("passwordUserA");
        manager.update(userA);
        injectSecurityContext(userA);
        params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE_AND_UPDATE, AtomicMode.ALL,
            "dxf2/user_userrole_update.json");
        ObjectBundle bundle2 = objectBundleService.create(params);
        objectBundleService.commit(bundle2);
        assertEquals(2, userA.getUserAuthorityGroups().size());
        assertEquals(2, userB.getUserAuthorityGroups().size());
    }

    @Test
    void testCreateUserRoleWithCode()
        throws IOException {
        createUserAndInjectSecurityContext(true);
        ObjectBundleParams params = createBundleParams(ObjectBundleMode.COMMIT, ImportStrategy.CREATE, AtomicMode.ALL,
            "dxf2/user_userrole_code.json");
        params.setPreheatIdentifier(PreheatIdentifier.CODE);
        ObjectBundle bundle = objectBundleService.create(params);
        objectBundleService.commit(bundle);
        User userA = manager.get(User.class, "sPWjoHSY03y");
        assertNotNull(userA);
        assertEquals(1, userA.getUserAuthorityGroups().size());
        assertEquals(1, userA.getDataViewOrganisationUnits().size());
        UserAuthorityGroup userManagerRole = manager.get(UserAuthorityGroup.class, "xJZBzAHI88H");
        assertNotNull(userManagerRole);
    }

    private ObjectBundleParams createBundleParams(ObjectBundleMode bundleMode, ImportStrategy importStrategy,
                                                  AtomicMode atomicMode, String fileName)
        throws IOException {
        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode(bundleMode);
        params.setImportStrategy(importStrategy);
        params.setAtomicMode(atomicMode);
        params.setObjects(getMetadataFromFile(fileName));
        return params;
    }

    private Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> getMetadataFromFile(String fileName)
        throws IOException {
        return renderService.fromMetadata(new ClassPathResource(fileName).getInputStream(), RenderFormat.JSON);
    }
}
