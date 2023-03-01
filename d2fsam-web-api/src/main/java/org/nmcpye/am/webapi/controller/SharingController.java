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
package org.nmcpye.am.webapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.common.*;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.security.acl.AccessStringHelper;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.team.TeamGroupServiceExt;
import org.nmcpye.am.team.TeamServiceExt;
import org.nmcpye.am.user.*;
import org.nmcpye.am.user.sharing.TeamAccess;
import org.nmcpye.am.user.sharing.TeamGroupAccess;
import org.nmcpye.am.user.sharing.UserAccess;
import org.nmcpye.am.user.sharing.UserGroupAccess;
import org.nmcpye.am.util.SharingUtils;
import org.nmcpye.am.webapi.webdomain.sharing.*;
import org.nmcpye.am.webapi.webdomain.sharing.comparator.SharingTeamAccessNameComparator;
import org.nmcpye.am.webapi.webdomain.sharing.comparator.SharingTeamGroupAccessNameComparator;
import org.nmcpye.am.webapi.webdomain.sharing.comparator.SharingUserGroupAccessNameComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;
import static org.springframework.http.CacheControl.noCache;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@OpenApi.Tags("metadata")
@Controller
@RequestMapping(value = SharingController.RESOURCE_PATH)
@Slf4j

public class SharingController {
    public static final String RESOURCE_PATH = "/sharing";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private UserGroupServiceExt userGroupService;

    @Autowired
    private TeamServiceExt teamServiceExt;

    @Autowired
    private TeamGroupServiceExt teamGroupServiceExt;

    @Autowired
    private UserServiceExt userService;

    @Autowired
    private AclService aclService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private SchemaService schemaService;

    // -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Sharing> getSharing(@RequestParam String type, @RequestParam String id)
        throws WebMessageException {
        if (!aclService.isShareable(type)) {
            throw new WebMessageException(conflict("Type " + type + " is not supported."));
        }

        Class<? extends IdentifiableObject> klass = aclService.classForType(type);
        IdentifiableObject object = manager.getNoAcl(klass, id);

        if (object == null) {
            throw new WebMessageException(
                notFound("Object of type " + type + " with ID " + id + " was not found."));
        }

        User user = currentUserService.getCurrentUser();

        if (!aclService.canRead(user, object)) {
            throw new AccessDeniedException("You do not have manage access to this object.");
        }

        Sharing sharing = new Sharing();

        sharing.getMeta().setAllowPublicAccess(aclService.canMakePublic(user, object));
        sharing.getMeta().setAllowExternalAccess(aclService.canMakeExternal(user, object));

        sharing.getObject().setId(object.getUid());
        sharing.getObject().setName(object.getDisplayName());
        sharing.getObject().setDisplayName(object.getDisplayName());
        sharing.getObject().setExternalAccess(object.getExternalAccess());

        if (object.getPublicAccess() == null) {
            String access;

            if (aclService.canMakeClassPublic(user, klass)) {
                access = AccessStringHelper.newInstance().enable(AccessStringHelper.Permission.READ)
                    .enable(AccessStringHelper.Permission.WRITE).build();
            } else {
                access = AccessStringHelper.newInstance().build();
            }

            sharing.getObject().setPublicAccess(access);
        } else {
            sharing.getObject().setPublicAccess(object.getPublicAccess());
        }

        if (object.getCreatedBy() != null) {
            sharing.getObject().getUser().setId(object.getCreatedBy().getUid());
            sharing.getObject().getUser().setName(object.getCreatedBy().getDisplayName());
        }

        for (org.nmcpye.am.user.UserGroupAccess userGroupAccess : SharingUtils
            .getDtoUserGroupAccesses(object.getUserGroupAccesses(), object.getSharing())) {
            String userGroupDisplayName = userGroupService.getDisplayName(userGroupAccess.getId());

            if (userGroupDisplayName == null) {
                continue;
            }

            SharingUserGroupAccess sharingUserGroupAccess = new SharingUserGroupAccess();
            sharingUserGroupAccess.setId(userGroupAccess.getId());
            sharingUserGroupAccess.setName(userGroupDisplayName);
            sharingUserGroupAccess.setDisplayName(userGroupDisplayName);
            sharingUserGroupAccess.setAccess(userGroupAccess.getAccess());

            sharing.getObject().getUserGroupAccesses().add(sharingUserGroupAccess);
        }

        for (org.nmcpye.am.user.UserAccess userAccess : SharingUtils.getDtoUserAccesses(object.getUserAccesses(),
            object.getSharing())) {
            String userDisplayName = userService.getDisplayName(userAccess.getUid());

            if (userDisplayName == null)
                continue;

            SharingUserAccess sharingUserAccess = new SharingUserAccess();
            sharingUserAccess.setId(userAccess.getId());
            sharingUserAccess.setName(userDisplayName);
            sharingUserAccess.setDisplayName(userDisplayName);
            sharingUserAccess.setAccess(userAccess.getAccess());

            sharing.getObject().getUserAccesses().add(sharingUserAccess);
        }

        ///////////////////////////

        for (org.nmcpye.am.user.TeamAccess teamAccess : SharingUtils
            .getDtoTeamAccesses(object.getTeamAccesses(), object.getSharing())) {
            String teamDisplayName = teamServiceExt.getDisplayName(teamAccess.getId());

            if (teamDisplayName == null) {
                continue;
            }

            SharingTeamAccess sharingTeamAccess = new SharingTeamAccess();
            sharingTeamAccess.setId(teamAccess.getId());
            sharingTeamAccess.setName(teamDisplayName);
            sharingTeamAccess.setDisplayName(teamDisplayName);
            sharingTeamAccess.setAccess(teamAccess.getAccess());

            sharing.getObject().getTeamAccesses().add(sharingTeamAccess);
        }

        for (org.nmcpye.am.user.TeamGroupAccess teamGroupAccess : SharingUtils
            .getDtoTeamGroupAccesses(object.getTeamGroupAccesses(), object.getSharing())) {
            String teamGroupDisplayName = teamGroupServiceExt.getDisplayName(teamGroupAccess.getId());

            if (teamGroupDisplayName == null) {
                continue;
            }

            SharingTeamGroupAccess sharingTeamGroupAccess = new SharingTeamGroupAccess();
            sharingTeamGroupAccess.setId(teamGroupAccess.getId());
            sharingTeamGroupAccess.setName(teamGroupDisplayName);
            sharingTeamGroupAccess.setDisplayName(teamGroupDisplayName);
            sharingTeamGroupAccess.setAccess(teamGroupAccess.getAccess());

            sharing.getObject().getTeamGroupAccesses().add(sharingTeamGroupAccess);
        }
        /////////////////////////////////////////

        sharing.getObject().getUserGroupAccesses().sort(SharingUserGroupAccessNameComparator.INSTANCE);
        sharing.getObject().getTeamAccesses().sort(SharingTeamAccessNameComparator.INSTANCE);
        sharing.getObject().getTeamGroupAccesses().sort(SharingTeamGroupAccessNameComparator.INSTANCE);

        return ResponseEntity.ok().cacheControl(noCache()).body(sharing);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putSharing(@RequestParam String type, @RequestParam String id,
                                 HttpServletRequest request)
        throws Exception {
        return postSharing(type, id, request);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage postSharing(@RequestParam String type, @RequestParam String id, HttpServletRequest request)
        throws Exception {
        Class<? extends IdentifiableObject> sharingClass = aclService.classForType(type);

        if (sharingClass == null || !aclService.isClassShareable(sharingClass)) {
            return conflict("Type " + type + " is not supported.");
        }

        BaseIdentifiableObject object = (BaseIdentifiableObject) manager.getNoAcl(sharingClass, id);

        if (object == null) {
            return notFound("Object of type " + type + " with ID " + id + " was not found.");
        }

        if ((object instanceof SystemDefaultMetadataObject) && ((SystemDefaultMetadataObject) object).isDefault()) {
            return conflict(
                "Sharing settings of system default metadata object of type " + type + " cannot be modified.");
        }

        User user = currentUserService.getCurrentUser();

        if (!aclService.canManage(user, object)) {
            throw new AccessDeniedException("You do not have manage access to this object.");
        }

        Sharing sharing = renderService.fromJson(request.getInputStream(), Sharing.class);

        if (!AccessStringHelper.isValid(sharing.getObject().getPublicAccess())) {
            return conflict("Invalid public access string: " + sharing.getObject().getPublicAccess());
        }

        // ---------------------------------------------------------------------
        // Ignore externalAccess if user is not allowed to make objects external
        // ---------------------------------------------------------------------

        if (aclService.canMakeExternal(user, object)) {
            object.setExternalAccess(sharing.getObject().hasExternalAccess());
        }

        // ---------------------------------------------------------------------
        // Ignore publicAccess if user is not allowed to make objects public
        // ---------------------------------------------------------------------

        Schema schema = schemaService.getDynamicSchema(sharingClass);

        if (aclService.canMakePublic(user, object)) {
            object.setPublicAccess(sharing.getObject().getPublicAccess());
        }

        if (!schema.isDataShareable()) {
            if (AccessStringHelper.hasDataSharing(object.getSharing().getPublicAccess())) {
                object.getSharing()
                    .setPublicAccess(AccessStringHelper.disableDataSharing(object.getSharing().getPublicAccess()));
            }
        }

        if (object.getCreatedBy() == null) {
            object.setCreatedBy(user);
        }

        object.getSharing().getUserGroups().clear();

        for (SharingUserGroupAccess sharingUserGroupAccess : sharing.getObject().getUserGroupAccesses()) {
            UserGroupAccess userGroupAccess = new UserGroupAccess();

            if (!AccessStringHelper.isValid(sharingUserGroupAccess.getAccess())) {
                return conflict("Invalid user group access string: " + sharingUserGroupAccess.getAccess());
            }

            if (!schema.isDataShareable()) {
                if (AccessStringHelper.hasDataSharing(sharingUserGroupAccess.getAccess())) {
                    sharingUserGroupAccess
                        .setAccess(AccessStringHelper.disableDataSharing(sharingUserGroupAccess.getAccess()));
                }
            }

            userGroupAccess.setAccess(sharingUserGroupAccess.getAccess());

            UserGroup userGroup = manager.get(UserGroup.class, sharingUserGroupAccess.getId());

            if (userGroup != null) {
                userGroupAccess.setUserGroup(userGroup);
                object.getSharing().addUserGroupAccess(userGroupAccess);
            }
        }

        object.getSharing().getUsers().clear();

        for (SharingUserAccess sharingUserAccess : sharing.getObject().getUserAccesses()) {
            UserAccess userAccess = new UserAccess();

            if (!AccessStringHelper.isValid(sharingUserAccess.getAccess())) {
                return conflict("Invalid user access string: " + sharingUserAccess.getAccess());
            }

            if (!schema.isDataShareable()) {
                if (AccessStringHelper.hasDataSharing(sharingUserAccess.getAccess())) {
                    sharingUserAccess
                        .setAccess(AccessStringHelper.disableDataSharing(sharingUserAccess.getAccess()));
                }
            }

            userAccess.setAccess(sharingUserAccess.getAccess());

            User sharingUser = manager.get(User.class, sharingUserAccess.getId());

            if (sharingUser != null) {
                userAccess.setUser(sharingUser);
                object.getSharing().addUserAccess(userAccess);
            }
        }

        ///////////////////////////////////
        for (SharingTeamAccess sharingTeamAccess : sharing.getObject().getTeamAccesses()) {
            TeamAccess teamAccess = new TeamAccess();

            if (!AccessStringHelper.isValid(sharingTeamAccess.getAccess())) {
                return conflict("Invalid team access string: " + sharingTeamAccess.getAccess());
            }

            if (!schema.isDataShareable()) {
                if (AccessStringHelper.hasDataSharing(sharingTeamAccess.getAccess())) {
                    sharingTeamAccess
                        .setAccess(AccessStringHelper.disableDataSharing(sharingTeamAccess.getAccess()));
                }
            }

            teamAccess.setAccess(sharingTeamAccess.getAccess());

            Team team = manager.get(Team.class, sharingTeamAccess.getId());

            if (team != null) {
                teamAccess.setTeam(team);
                object.getSharing().addTeamAccess(teamAccess);
            }
        }

        for (SharingTeamGroupAccess sharingTeamGroupAccess : sharing.getObject().getTeamGroupAccesses()) {
            TeamGroupAccess teamGroupAccess = new TeamGroupAccess();

            if (!AccessStringHelper.isValid(sharingTeamGroupAccess.getAccess())) {
                return conflict("Invalid team group access string: " + sharingTeamGroupAccess.getAccess());
            }

            if (!schema.isDataShareable()) {
                if (AccessStringHelper.hasDataSharing(sharingTeamGroupAccess.getAccess())) {
                    sharingTeamGroupAccess
                        .setAccess(AccessStringHelper.disableDataSharing(sharingTeamGroupAccess.getAccess()));
                }
            }

            teamGroupAccess.setAccess(sharingTeamGroupAccess.getAccess());

            TeamGroup teamGroup = manager.get(TeamGroup.class, sharingTeamGroupAccess.getId());

            if (teamGroup != null) {
                teamGroupAccess.setTeamGroup(teamGroup);
                object.getSharing().addTeamGroupAccess(teamGroupAccess);
            }
        }

        ///////////////////////////////////////////

        manager.updateNoAcl(object);

        if (Program.class.isInstance(object)) {
            syncSharingForEventProgram((Program) object);
        }

        log.info(sharingToString(object));

        return ok("Access control set");
    }

    @GetMapping(value = "/search", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> searchUserGroups(@RequestParam String key,
                                                                @RequestParam(required = false) Integer pageSize)
        throws WebMessageException {
        if (key == null) {
            throw new WebMessageException(conflict("Search key not specified"));
        }

        int max = pageSize != null ? pageSize : Pager.DEFAULT_PAGE_SIZE;

        List<SharingUserGroupAccess> userGroupAccesses = getSharingUserGroups(key, max);
        List<SharingUserAccess> userAccesses = getSharingUser(key, max);

        List<SharingTeamAccess> teamAccesses = getSharingTeams(key, max);
        List<SharingTeamGroupAccess> teamGroupAccesses = getSharingTeamGroups(key, max);

        Map<String, Object> output = new HashMap<>();
        output.put("teams", teamAccesses);
        output.put("teamGroups", teamGroupAccesses);

        return ResponseEntity.ok().cacheControl(noCache()).body(output);
    }

    private List<SharingUserAccess> getSharingUser(String key, int max) {
        List<SharingUserAccess> sharingUsers = new ArrayList<>();
        List<User> users = userService.getAllUsersBetweenByName(key, 0, max);

        for (User user : users) {
            SharingUserAccess sharingUserAccess = new SharingUserAccess();
            sharingUserAccess.setId(user.getUid());
            sharingUserAccess.setName(user.getDisplayName());
            sharingUserAccess.setDisplayName(user.getDisplayName());
            sharingUserAccess.setUsername(user.getUsername());

            sharingUsers.add(sharingUserAccess);
        }

        return sharingUsers;
    }

    private List<SharingUserGroupAccess> getSharingUserGroups(@RequestParam String key, int max) {
        List<SharingUserGroupAccess> sharingUserGroupAccesses = new ArrayList<>();
        List<UserGroup> userGroups = userGroupService.getUserGroupsBetweenByName(key, 0, max);

        for (UserGroup userGroup : userGroups) {
            SharingUserGroupAccess sharingUserGroupAccess = new SharingUserGroupAccess();

            sharingUserGroupAccess.setId(userGroup.getUid());
            sharingUserGroupAccess.setName(userGroup.getDisplayName());
            sharingUserGroupAccess.setDisplayName(userGroup.getDisplayName());

            sharingUserGroupAccesses.add(sharingUserGroupAccess);
        }

        return sharingUserGroupAccesses;
    }

    private List<SharingTeamAccess> getSharingTeams(@RequestParam String key, int max) {
        List<SharingTeamAccess> sharingTeamAccesses = new ArrayList<>();
        List<Team> teams = teamServiceExt.getTeamsBetweenByName(key, 0, max);

        for (Team userGroup : teams) {
            SharingTeamAccess sharingTeamAccess = new SharingTeamAccess();

            sharingTeamAccess.setId(userGroup.getUid());
            sharingTeamAccess.setName(userGroup.getDisplayName());
            sharingTeamAccess.setDisplayName(userGroup.getDisplayName());

            sharingTeamAccesses.add(sharingTeamAccess);
        }

        return sharingTeamAccesses;
    }

    private List<SharingTeamGroupAccess> getSharingTeamGroups(@RequestParam String key, int max) {
        List<SharingTeamGroupAccess> sharingTeamGroupAccesses = new ArrayList<>();
        List<TeamGroup> teamGroups = teamGroupServiceExt.getTeamGroupsBetweenByName(key, 0, max);

        for (TeamGroup teamGroup : teamGroups) {
            SharingTeamGroupAccess sharingTeamGroupAccess = new SharingTeamGroupAccess();

            sharingTeamGroupAccess.setId(teamGroup.getUid());
            sharingTeamGroupAccess.setName(teamGroup.getDisplayName());
            sharingTeamGroupAccess.setDisplayName(teamGroup.getDisplayName());

            sharingTeamGroupAccesses.add(sharingTeamGroupAccess);
        }

        return sharingTeamGroupAccesses;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String sharingToString(BaseIdentifiableObject object) {
        StringBuilder builder = new StringBuilder()
            .append("'").append(currentUserService.getCurrentUsername()).append("'")
            .append(" update sharing on ").append(object.getClass().getName())
            .append(", uid: ").append(object.getUid())
            .append(", name: ").append(object.getName())
            .append(", publicAccess: ").append(object.getPublicAccess())
            .append(", externalAccess: ").append(object.getExternalAccess());

        if (!object.getUserGroupAccesses().isEmpty()) {
            builder.append(", userGroupAccesses: ");

            for (org.nmcpye.am.user.UserGroupAccess userGroupAccess : object.getUserGroupAccesses()) {
                builder.append("{uid: ").append(userGroupAccess.getUserGroup().getUid())
                    .append(", name: ").append(userGroupAccess.getUserGroup().getName())
                    .append(", access: ").append(userGroupAccess.getAccess())
                    .append("} ");
            }
        }

        if (!object.getUserAccesses().isEmpty()) {
            builder.append(", userAccesses: ");

            for (org.nmcpye.am.user.UserAccess userAccess : object.getUserAccesses()) {
                builder.append("{uid: ").append(userAccess.getUser().getUid())
                    .append(", name: ").append(userAccess.getUser().getName())
                    .append(", access: ").append(userAccess.getAccess())
                    .append("} ");
            }
        }

        if (!object.getTeamAccesses().isEmpty()) {
            builder.append(", teamAccesses: ");

            for (org.nmcpye.am.user.TeamAccess teamAccess : object.getTeamAccesses()) {
                builder.append("{uid: ").append(teamAccess.getTeam().getUid())
                    .append(", name: ").append(teamAccess.getTeam().getName())
                    .append(", access: ").append(teamAccess.getAccess())
                    .append("} ");
            }
        }

        if (!object.getTeamGroupAccesses().isEmpty()) {
            builder.append(", teamGroupAccesses: ");

            for (org.nmcpye.am.user.TeamGroupAccess teamGroupAccess : object.getTeamGroupAccesses()) {
                builder.append("{uid: ").append(teamGroupAccess.getTeamGroup().getUid())
                    .append(", name: ").append(teamGroupAccess.getTeamGroup().getName())
                    .append(", access: ").append(teamGroupAccess.getAccess())
                    .append("} ");
            }
        }

        return builder.toString();
    }

    private void syncSharingForEventProgram(Program program) {
        if (ProgramType.WITH_REGISTRATION == program.getProgramType()
            || program.getProgramStages().isEmpty()) {
            return;
        }

        ProgramStage programStage = program.getProgramStages().iterator().next();
        AccessStringHelper.copySharing(program, programStage);

        programStage.setCreatedBy(program.getCreatedBy());
        manager.update(programStage);
    }
}
