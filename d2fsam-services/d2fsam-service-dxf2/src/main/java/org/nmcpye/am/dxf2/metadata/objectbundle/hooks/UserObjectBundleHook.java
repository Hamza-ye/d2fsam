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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.adapter.BaseIdentifiableObject_;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.preheat.PreheatIdentifier;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.user.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@AllArgsConstructor
public class UserObjectBundleHook extends AbstractObjectBundleHook<User> {
    public static final String USERNAME = "username";

    private final UserServiceExt userService;

    private final FileResourceServiceExt fileResourceService;

    private final CurrentUserService currentUserService;

    private final AclService aclService;

//    private final SecurityService securityService;

    private final UserSettingService userSettingService;

    @Override
    public void validate(User user, ObjectBundle bundle,
                         Consumer<ErrorReport> addReports) {
        // TODO: To remove when we remove old UserCredentials compatibility
//        populateUserCredentialsDtoFields(user);

        if (bundle.getImportMode().isCreate() && !ValidationUtils.usernameIsValid(user.getUsername()/*,
            user.isInvitation()*/)) {
            addReports.accept(
                new ErrorReport(User.class, ErrorCode.E4049, USERNAME, user.getUsername())
                    .setErrorProperty(USERNAME));
        }

        boolean usernameExists = userService.getUserByUsername(user.getUsername()) != null;

        if ((bundle.getImportMode().isCreate() && usernameExists)) {
            addReports.accept(
                new ErrorReport(User.class, ErrorCode.E4054, USERNAME, user.getUsername())
                    .setErrorProperty(USERNAME));
        }

        User existingUser = userService.getUser(user.getUid());

        if (bundle.getImportMode().isUpdate() && existingUser != null && user.getUsername() != null &&
            !user.getUsername().equals(existingUser.getUsername())) {
            addReports.accept(
                new ErrorReport(User.class, ErrorCode.E4056, USERNAME, user.getUsername())
                    .setErrorProperty(USERNAME));
        }

        if (user.getUserAuthorityGroups() == null || user.getUserAuthorityGroups().isEmpty()) {
            addReports.accept(
                new ErrorReport(User.class, ErrorCode.E4055, USERNAME, user.getUsername())
                    .setErrorProperty(USERNAME));
        }

        if (user.getWhatsApp() != null && !ValidationUtils.validateWhatsapp(user.getWhatsApp())) {
            addReports.accept(
                new ErrorReport(User.class, ErrorCode.E4027, user.getWhatsApp(), "whatsApp")
                    .setErrorProperty("whatsApp"));
        }
    }

    @Override
    public void preCreate(User user, ObjectBundle bundle) {
        if (user == null)
            return;

        User currentUser = currentUserService.getCurrentUser();

//        if (currentUser != null) {
//            user.getCogsDimensionConstraints().addAll(
//                currentUser.getCogsDimensionConstraints());
//
//            user.getCatDimensionConstraints().addAll(
//                currentUser.getCatDimensionConstraints());
//        }
    }

    @Override
    public void postCreate(User user, ObjectBundle bundle) {
        if (!StringUtils.isEmpty(user.getPassword())) {
            userService.encodeAndSetPassword(user, user.getPassword());
        }

        if (user.getAvatar() != null) {
            FileResource fileResource = fileResourceService.getFileResource(user.getAvatar().getUid());
            fileResource.setAssigned(true);
            fileResourceService.updateFileResource(fileResource);
        }

        preheatService.connectReferences(user, bundle.getPreheat(), bundle.getPreheatIdentifier());
        sessionFactory.getCurrentSession().update(user);
//        userSettingService.saveUserSettings(user.getSettings(), user);
    }

    @Override
    public void preUpdate(User user, User persisted, ObjectBundle bundle) {
        if (user == null)
            return;

        bundle.putExtras(user, "preUpdateUser", user);

        if (persisted.getAvatar() != null
            && (user.getAvatar() == null || !persisted.getAvatar().getUid().equals(user.getAvatar().getUid()))) {
            FileResource fileResource = fileResourceService.getFileResource(persisted.getAvatar().getUid());
            fileResourceService.updateFileResource(fileResource);

            if (user.getAvatar() != null) {
                fileResource = fileResourceService.getFileResource(user.getAvatar().getUid());
                fileResource.setAssigned(true);
                fileResourceService.updateFileResource(fileResource);
            }
        }

//        securityService.validate2FAUpdate(persisted.getTwoFA(), user.getTwoFA(), persisted);
    }

    @Override
    public void postUpdate(User persistedUser, ObjectBundle bundle) {
        final User preUpdateUser = (User) bundle.getExtras(persistedUser, "preUpdateUser");

        if (!StringUtils.isEmpty(preUpdateUser.getPassword())) {
            userService.encodeAndSetPassword(persistedUser, preUpdateUser.getPassword());
            sessionFactory.getCurrentSession().update(persistedUser);
        }

        bundle.removeExtras(persistedUser, "preUpdateUser");
        userSettingService.saveUserSettings(persistedUser.getSettings(), persistedUser);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void postCommit(ObjectBundle bundle) {
        Iterable<User> objects = bundle.getObjects(User.class);
        Map<String, Map<String, Object>> userReferences = bundle.getObjectReferences(User.class);

        if (userReferences == null || userReferences.isEmpty()) {
            return;
        }

        for (User identifiableObject : objects) {
            User user = identifiableObject;

            user = bundle.getPreheat().get(bundle.getPreheatIdentifier(), user);

            Map<String, Object> userReferenceMap = userReferences.get(identifiableObject.getUid());

            if (user == null || userReferenceMap == null || userReferenceMap.isEmpty()) {
                continue;
            }

            Set<UserAuthorityGroup> userRoles = (Set<UserAuthorityGroup>) userReferenceMap.get("userAuthorityGroups");
            user.setUserAuthorityGroups(Objects.requireNonNullElseGet(userRoles, HashSet::new));

            Set<OrganisationUnit> organisationUnits = (Set<OrganisationUnit>) userReferenceMap
                .get("organisationUnits");
            user.setOrganisationUnits(organisationUnits);

            Set<OrganisationUnit> dataViewOrganisationUnits = (Set<OrganisationUnit>) userReferenceMap
                .get("dataViewOrganisationUnits");
            user.setDataViewOrganisationUnits(dataViewOrganisationUnits);

            Set<OrganisationUnit> teiSearchOrganisationUnits = (Set<OrganisationUnit>) userReferenceMap
                .get("teiSearchOrganisationUnits");
            user.setTeiSearchOrganisationUnits(teiSearchOrganisationUnits);

            user.setCreatedBy((User) userReferenceMap.get(BaseIdentifiableObject_.CREATED_BY));

            if (user.getCreatedBy() == null) {
                user.setCreatedBy(bundle.getUser());
            }

            user.setUpdatedBy(bundle.getUser());

            preheatService.connectReferences(user, bundle.getPreheat(), bundle.getPreheatIdentifier());

            handleNoAccessRoles(user, bundle, userRoles);

            sessionFactory.getCurrentSession().update(user);
        }
    }

    /**
     * If currentUser doesn't have read access to a UserRole, and it is included
     * in the payload, then that UserRole should not be removed from updating
     * User.
     *
     * @param user   the updating User.
     * @param bundle the ObjectBundle.
     */
    private void handleNoAccessRoles(User user, ObjectBundle bundle, Set<UserAuthorityGroup> userRoles) {
        Set<UserAuthorityGroup> roles = user
            .getUserAuthorityGroups();
        Set<String> currentRoles = roles.stream().map(BaseIdentifiableObject::getUid)
            .collect(Collectors.toSet());

        if (userRoles != null) {
            userRoles.stream()
                .filter(role -> !currentRoles.contains(role.getUid()))
                .forEach(role -> {
                    UserAuthorityGroup persistedRole = bundle.getPreheat().get(PreheatIdentifier.UID, role);

                    if (persistedRole == null) {
                        persistedRole = manager.getNoAcl(UserAuthorityGroup.class, role.getUid());
                    }

                    if (!aclService.canRead(bundle.getUser(), persistedRole)) {
                        roles.add(persistedRole);
                    }
                });
        }
    }
}
