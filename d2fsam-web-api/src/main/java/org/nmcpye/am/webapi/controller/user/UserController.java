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
package org.nmcpye.am.webapi.controller.user;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.MergeMode;
import org.nmcpye.am.common.Pager;
import org.nmcpye.am.common.UserOrgUnitType;
import org.nmcpye.am.commons.collection.CollectionUtils;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatch;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatchOperation;
import org.nmcpye.am.commons.jackson.jsonpatch.operations.AddOperation;
import org.nmcpye.am.dbms.DbmsManager;
import org.nmcpye.am.dxf2.common.TranslateParams;
import org.nmcpye.am.dxf2.metadata.MetadataImportParams;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReport;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReportMode;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.feedback.ObjectReport;
import org.nmcpye.am.feedback.Status;
import org.nmcpye.am.hibernate.exception.CreateAccessDeniedException;
import org.nmcpye.am.hibernate.exception.UpdateAccessDeniedException;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.node.fieldfilter.Defaults;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.patch.Mutation;
import org.nmcpye.am.patch.Patch;
import org.nmcpye.am.query.Order;
import org.nmcpye.am.query.Pagination;
import org.nmcpye.am.query.Query;
import org.nmcpye.am.query.QueryParserException;
import org.nmcpye.am.schema.MergeParams;
import org.nmcpye.am.schema.descriptors.UserSchemaDescriptor;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.team.TeamServiceExt;
import org.nmcpye.am.user.*;
import org.nmcpye.am.webapi.controller.AbstractCrudController;
import org.nmcpye.am.webapi.webdomain.WebMetadata;
import org.nmcpye.am.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nmcpye.am.common.IdentifiableObjectUtils.getUids;
import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Slf4j
@Controller
@RequestMapping(value = UserSchemaDescriptor.API_ENDPOINT)
public class UserController
    extends AbstractCrudController<User> {
    public static final String INVITE_PATH = "/invite";

    public static final String BULK_INVITE_PATH = "/invites";

    private static final String KEY_USERNAME = "username";

    private static final String KEY_PASSWORD = "password";

    @Autowired
    protected DbmsManager dbmsManager;

    @Autowired
    private UserServiceExt userService;

    @Autowired
    private UserGroupServiceExt userGroupService;

    @Autowired
    private TeamServiceExt teamService;

//    @Autowired
//    private UserControllerUtils userControllerUtils;
//
//    @Autowired
//    private SecurityService securityService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private UserSettingService userSettingService;

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    protected List<User> getEntityList(WebMetadata metadata, WebOptions options, List<String> filters,
                                       List<Order> orders)
        throws QueryParserException {
        UserQueryParams params = makeUserQueryParams(options);

        String ou = options.get("ou");

        if (ou != null) {
            params.addOrganisationUnit(organisationUnitServiceExt.getOrganisationUnit(ou));
        }

        if (options.isManage()) {
            params.setCanManage(true);
            params.setAuthSubset(true);
        }

        boolean hasUserGroupFilter = filters.stream().anyMatch(f -> f.startsWith("userGroups."));
        params.setPrefetchUserGroups(hasUserGroupFilter);

        boolean hasUserTeamFilter = filters.stream().anyMatch(f -> f.startsWith("teams."));
        params.setPrefetchTeams(hasUserTeamFilter);

        if (filters.isEmpty() && options.hasPaging()) {
            metadata.setPager(makePager(options, params));
        }

        Query query = makeQuery(options, filters, orders, params);

        return (List<User>) queryService.query(query);
    }

    private Pager makePager(WebOptions options, UserQueryParams params) {
        long count = userService.getUserCount(params);

        Pager pager = new Pager(options.getPage(), count, options.getPageSize());
        params.setFirst(pager.getOffset());
        params.setMax(pager.getPageSize());

        return pager;
    }

    private Query makeQuery(WebOptions options, List<String> filters,
                            List<Order> orders, UserQueryParams params) {
        Pagination pagination = CollectionUtils.isEmpty(filters) ? new Pagination() : getPaginationData(options);

        List<String> ordersAsString = (orders == null) ? null
            : orders.stream()
            .map(Order::toOrderString)
            .collect(Collectors.toList());

        /*
         * Keep the memory query on the result
         */
        Query query = queryService
            .getQueryFromUrl(getEntityClass(), filters, orders, pagination, options.getRootJunction());

        // Fetches all users if there are no query, i.e only filters...
        List<User> users = userService.getUsers(params, ordersAsString);
        query.setObjects(users);
        query.setDefaults(Defaults.valueOf(options.get("defaults", DEFAULTS)));
        query.setDefaultOrder();

        return query;
    }

    private UserQueryParams makeUserQueryParams(WebOptions options) {
        UserQueryParams params = new UserQueryParams();
        params.setQuery(StringUtils.trimToNull(options.get("query")));
        params.setPhoneNumber(StringUtils.trimToNull(options.get("phoneNumber")));
        params.setCanManage(options.isTrue("canManage"));
        params.setAuthSubset(options.isTrue("authSubset"));
        params.setLastLogin(options.getDate("lastLogin"));
        params.setInactiveMonths(options.getInt("inactiveMonths"));
        params.setInactiveSince(options.getDate("inactiveSince"));
        params.setSelfRegistered(options.isTrue("selfRegistered"));
        params.setInvitationStatus(UserInvitationStatus.fromValue(options.get("invitationStatus")));
        params.setUserOrgUnits(options.isTrue("userOrgUnits"));
        params.setIncludeOrgUnitChildren(options.isTrue("includeChildren"));
        params.setOrgUnitBoundary(UserOrgUnitType.fromValue(options.get("orgUnitBoundary")));

        return params;
    }

    @Override
    protected List<User> getEntity(String uid, WebOptions options) {
        List<User> users = Lists.newArrayList();
        Optional<User> user = Optional.ofNullable(userService.getUser(uid));

        user.ifPresent(users::add);

        return users;
    }

    @Override
    @GetMapping("/{uid}/{property}")
    public @ResponseBody
    ResponseEntity<ObjectNode> getObjectProperty(
        @PathVariable("uid") String pvUid, @PathVariable("property") String pvProperty,
        @RequestParam Map<String, String> rpParameters,
        TranslateParams translateParams,
        @CurrentUser User currentUser,
        HttpServletResponse response)
        throws Exception {
//        if (!"dataApprovalWorkflows".equals(pvProperty)) {
        return super.getObjectProperty(pvUid, pvProperty, rpParameters, translateParams, currentUser, response);
//        }

//        User user = userService.getUser(pvUid);
//
//        if (user == null) {
//            throw new WebMessageException(conflict("User not found: " + pvUid));
//        }
//
//        if (!aclService.canRead(currentUser, user)) {
//            throw new CreateAccessDeniedException("You don't have the proper permissions to access this user.");
//        }
//
//        return ResponseEntity.ok(userControllerUtils.getUserDataApprovalWorkflows(user));
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

//    @Override
//    @PostMapping(consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
//    @ResponseBody
//    public WebMessage postXmlObject(HttpServletRequest request)
//        throws Exception {
//        return postObject(renderService.fromXml(request.getInputStream(), getEntityClass()));
//    }

    @Override
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage postJsonObject(HttpServletRequest request)
        throws Exception {
        return postObject(renderService.fromJson(request.getInputStream(), getEntityClass()));
    }

    private WebMessage postObject(User user)
        throws WebMessageException {
//        // TODO: To remove when we remove old UserCredentials compatibility
//        populateUserCredentialsDtoFields(user);

        User currentUser = currentUserService.getCurrentUser();

        validateCreateUser(user, currentUser);

        return postObject(getObjectReport(createUser(user, currentUser)));
    }

//    @PostMapping(value = INVITE_PATH, consumes = APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public WebMessage postJsonInvite(HttpServletRequest request)
//        throws Exception {
//        User user = renderService.fromJson(request.getInputStream(), getEntityClass());
//        return postInvite(request, user);
//    }

//    @PostMapping(value = INVITE_PATH, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
//    @ResponseBody
//    public WebMessage postXmlInvite(HttpServletRequest request)
//        throws Exception {
//        User user = renderService.fromXml(request.getInputStream(), getEntityClass());
//        return postInvite(request, user);
//    }
//
//    private WebMessage postInvite(HttpServletRequest request, User user)
//        throws WebMessageException {
////        // TODO: To remove when we remove old UserCredentials compatibility
////        populateUserCredentialsDtoFields(user);
//
//        User currentUser = currentUserService.getCurrentUser();
//
//        validateInviteUser(user, currentUser);
//
//        return postObject(inviteUser(user, currentUser, request));
//    }

//    @PostMapping(value = BULK_INVITE_PATH, consumes = APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void postJsonInvites(HttpServletRequest request)
//        throws Exception {
//        Users users = renderService.fromJson(request.getInputStream(), Users.class);
//        postInvites(request, users);
//    }

//    @PostMapping(value = BULK_INVITE_PATH, consumes = {APPLICATION_XML_VALUE,
//        TEXT_XML_VALUE})
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void postXmlInvites(HttpServletRequest request)
//        throws Exception {
//        Users users = renderService.fromXml(request.getInputStream(), Users.class);
//        postInvites(request, users);
//    }
//
//    private void postInvites(HttpServletRequest request, Users users)
//        throws WebMessageException {
//        User currentUser = currentUserService.getCurrentUser();
//
//        // TODO: To remove when we remove old UserCredentials compatibility
//        for (User user : users.getUsers()) {
//            populateUserCredentialsDtoFields(user);
//        }
//
//        for (User user : users.getUsers()) {
//            validateInviteUser(user, currentUser);
//        }
//
//        for (User user : users.getUsers()) {
//            inviteUser(user, currentUser, request);
//        }
//    }

//    @PostMapping(value = "/{id}" + INVITE_PATH)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void resendInvite(@PathVariable String id, HttpServletRequest request)
//        throws Exception {
//        User user = userService.getUser(id);
//        if (user == null) {
//            throw new WebMessageException(conflict("User not found: " + id));
//        }
//
//        if (!user.isInvitation()) {
//            throw new WebMessageException(conflict("User account is not an invitation: " + id));
//        }
//
//        ErrorCode errorCode = securityService.validateRestore(user);
//        if (errorCode != null) {
//            throw new IllegalQueryException(errorCode);
//        }
//
//        if (!securityService
//            .sendRestoreOrInviteMessage(user, ContextUtils.getContextPath(request),
//                securityService.getRestoreOptions(user.getRestoreToken()))) {
//            throw new WebMessageException(error("Failed to send invite message"));
//        }
//    }

//    @PostMapping("/{id}/reset")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void resetToInvite(@PathVariable String id, HttpServletRequest request)
//        throws Exception {
//        User user = userService.getUser(id);
//        if (user == null) {
//            throw NotFoundException.notFoundUid(id);
//        }
//        ErrorCode errorCode = securityService.validateRestore(user);
//        if (errorCode != null) {
//            throw new IllegalQueryException(errorCode);
//        }
//        User currentUser = currentUserService.getCurrentUser();
//        if (!aclService.canUpdate(currentUser, user)) {
//            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this user.");
//        }
//        if (!userService.canAddOrUpdateUserInGroups(getUids(user.getGroups()), currentUser)) {
//            throw new UpdateAccessDeniedException(
//                "You must have permissions manage at least one user group for the user.");
//        }
//
//        securityService.prepareUserForInvite(user);
//        securityService.sendRestoreOrInviteMessage(user, ContextUtils.getContextPath(request),
//            RestoreOptions.RECOVER_PASSWORD_OPTION);
//    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ALL') or hasRole('F_REPLICATE_USER')")
    @PostMapping("/{uid}/replica")
    @ResponseBody
    public WebMessage replicateUser(@PathVariable String uid,
                                    HttpServletRequest request, HttpServletResponse response)
        throws IOException,
        WebMessageException {
        User existingUser = userService.getUser(uid);
        if (existingUser == null) {
            return conflict("User not found: " + uid);
        }

        User currentUser = currentUserService.getCurrentUser();

        validateCreateUser(existingUser, currentUser);

        Map<String, String> auth = renderService.fromJson(request.getInputStream(), Map.class);

        String username = StringUtils.trimToNull(auth != null ? auth.get(KEY_USERNAME) : null);
        String password = StringUtils.trimToNull(auth != null ? auth.get(KEY_PASSWORD) : null);

        if (auth == null || username == null) {
            return conflict("Username must be specified");
        }

        if (userService.getUserByUsername(username) != null) {
            return conflict("Username already taken: " + username);
        }

        if (password == null) {
            return conflict("Password must be specified");
        }

        if (!ValidationUtils.passwordIsValid(password)) {
            return conflict("Password must have at least 8 characters, one digit, one uppercase");
        }

        User userReplica = new User();
        mergeService.merge(new MergeParams<>(existingUser, userReplica)
            .setMergeMode(MergeMode.MERGE));
        copyAttributeValues(userReplica);
        userReplica.setId(null);
        userReplica.setUuid(UUID.randomUUID());
        userReplica.setUid(CodeGenerator.generateUid());
        userReplica.setCode(null);
        userReplica.setCreated(Instant.now());
//        userReplica.setLdapId(null);
//        userReplica.setOpenId(null);
        userReplica.setUsername(username);
        userService.encodeAndSetPassword(userReplica, password);

        userService.addUser(userReplica);

        userGroupService.addUserToGroups(userReplica, getUids(existingUser.getGroups()),
            currentUser);

        // ---------------------------------------------------------------------
        // Replicate user settings
        // ---------------------------------------------------------------------

        List<UserSetting> settings = userSettingService.getUserSettings(existingUser);
        for (UserSetting setting : settings) {
            Optional<UserSettingKey> key = UserSettingKey.getByName(setting.getName());
            key.ifPresent(userSettingKey -> userSettingService.saveUserSetting(userSettingKey, setting.getValue(),
                userReplica));
        }

        return created("User replica created")
            .setLocation(UserSchemaDescriptor.API_ENDPOINT + "/" + userReplica.getUid());
    }

    @PostMapping("/{uid}/enabled")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable("uid") String uid)
        throws Exception {
        setDisabled(uid, false);
    }

    @PostMapping("/{uid}/disabled")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void disableUser(@PathVariable("uid") String uid)
        throws Exception {
        setDisabled(uid, true);
    }

    @PostMapping("/{uid}/expired")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void expireUser(@PathVariable("uid") String uid, @RequestParam("date") LocalDateTime accountExpiry)
        throws Exception {
        setExpires(uid, accountExpiry);
    }

    @PostMapping("/{uid}/unexpired")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unexpireUser(@PathVariable("uid") String uid)
        throws Exception {
        setExpires(uid, null);
    }

//    /**
//     * "Disable two-factor authentication for the user with the given uid."
//     * <p>
//     *
//     * @param uid         The uid of the user to disable two-factor authentication for.
//     * @param currentUser This is the user that is currently logged in.
//     * @return A WebMessage object.
//     */
//    @PostMapping("/{uid}/twoFA/disabled")
//    @ResponseBody
//    public WebMessage disableTwoFA(@PathVariable("uid") String uid, @CurrentUser User currentUser) {
//        List<ErrorReport> errors = new ArrayList<>();
//        userService.disableTwoFA(currentUser, uid, errors::add);
//
//        if (errors.isEmpty()) {
//            return WebMessageUtils.ok();
//        }
//
//        return WebMessageUtils.errorReports(errors);
//    }

    // -------------------------------------------------------------------------
    // PATCH
    //

    /**
     * > This function is used to PATCH a user object
     *
     * @param pvUid        The user's uid
     * @param rpParameters The request parameters
     * @param currentUser  The user that is currently logged in.
     * @param request      The request object
     */
    @PatchMapping(value = "/{uid}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Override
    public void partialUpdateObject(
        @PathVariable("uid") String pvUid, @RequestParam Map<String, String> rpParameters,
        @CurrentUser User currentUser, HttpServletRequest request)
        throws Exception {
        WebOptions options = new WebOptions(rpParameters);
        List<User> entities = getEntity(pvUid, options);

        if (entities.isEmpty()) {
            throw new WebMessageException(notFound(getEntityClass(), pvUid));
        }

        User persistedObject = entities.get(0);

//        boolean twoFABefore = persistedObject.getTwoFA();

        if (!aclService.canUpdate(currentUser, persistedObject)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        Patch patch = diff(request);

        mergeUserCredentialsMutations(patch);

        prePatchEntity(persistedObject, persistedObject);
        patchService.apply(patch, persistedObject);

//        boolean twoFAfter = persistedObject.getTwoFA();
//
//        securityService.validate2FAUpdate(twoFABefore, twoFAfter, persistedObject);

        validateAndThrowErrors(() -> schemaValidator.validate(persistedObject));

        manager.update(persistedObject);

        postPatchEntity(null, persistedObject);
    }

    /*
     * This method is used to merge the user credentials with the user object.
     */
    private void mergeUserCredentialsMutations(Patch patch) {
        List<Mutation> mutations = patch.getMutations();
        List<Mutation> filteredMutations = new ArrayList<>();
        for (Mutation mutation : mutations) {
            Mutation.Operation operation = mutation.getOperation();
            String path = mutation.getPath();
            Object value = mutation.getValue();

            if (path.startsWith("userCredentials")) {
                path = path.replace("userCredentials.", "");

                Mutation filtered = new Mutation(path, value, operation);
                filteredMutations.add(filtered);
            } else {
                filteredMutations.add(mutation);
            }

            patch.setMutations(filteredMutations);
        }
    }

    // -------------------------------------------------------------------------
    // PUT
    // -------------------------------------------------------------------------

//    @Override
//    @PutMapping(value = "/{uid}", consumes = {APPLICATION_XML_VALUE,
//        TEXT_XML_VALUE}, produces = APPLICATION_XML_VALUE)
//    @ResponseBody
//    public WebMessage putXmlObject(@PathVariable("uid") String pvUid, @CurrentUser User currentUser,
//                                   HttpServletRequest request,
//                                   HttpServletResponse response)
//        throws Exception {
//        User parsed = renderService.fromXml(request.getInputStream(), getEntityClass());
//
//        return importReport(updateUser(pvUid, parsed))
//            .withPlainResponseBefore(DhisApiVersion.V38);
//    }

    @Override
    @PutMapping(value = "/{uid}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putJsonObject(@PathVariable("uid") String pvUid, @CurrentUser User currentUser,
                                    HttpServletRequest request)
        throws Exception {
        User inputUser = renderService.fromJson(request.getInputStream(), getEntityClass());

        List<User> users = getEntity(pvUid, NO_WEB_OPTIONS);
        if (users.isEmpty()) {
            throw new WebMessageException(
                conflict(getEntityName() + " does not exist: " + pvUid));
        }

//        // TODO: To remove when we remove old UserCredentials compatibility
//        populateUserCredentialsDtoCopyOnlyChanges(users.get(0), inputUser);

        return importReport(updateUser(pvUid, inputUser))
            /*.withPlainResponseBefore(DhisApiVersion.V38)*/;
    }

    protected ImportReport updateUser(String userUid, User inputUser)
        throws WebMessageException {
        List<User> users = getEntity(userUid, NO_WEB_OPTIONS);

        if (users.isEmpty()) {
            throw new WebMessageException(
                conflict(getEntityName() + " does not exist: " + userUid));
        }

        User currentUser = currentUserService.getCurrentUser();

        if (!aclService.canUpdate(currentUser, users.get(0))) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this user.");
        }

        // force initialization of all authorities of current user in order to
        // prevent cases where user must be reloaded later
        // (in case it gets detached)
        currentUser.getAllAuthorities();

        inputUser.setId(users.get(0).getId());
        inputUser.setUid(userUid);
        mergeLastLoginAttribute(users.get(0), inputUser);

        boolean isPasswordChangeAttempt = inputUser.getPassword() != null;

        List<String> groupsUids = getUids(inputUser.getGroups());

        if (!userService.canAddOrUpdateUserInGroups(groupsUids, currentUser)
            || !currentUser.canModifyUser(users.get(0))) {
            throw new WebMessageException(conflict(
                "You must have permissions to create user, " +
                    "or ability to manage at least one user group for the user."));
        }

        MetadataImportParams params = importService.getParamsFromMap(contextService.getParameterValuesMap());
        params.setImportReportMode(ImportReportMode.FULL);
        params.setImportStrategy(ImportStrategy.UPDATE);
        params.addObject(inputUser);

        ImportReport importReport = importService.importMetadata(params);

        if (importReport.getStatus() == Status.OK && importReport.getStats().getUpdated() == 1) {
            updateUserGroups(userUid, inputUser, currentUser);

            // If it was a pw change attempt (input.pw != null) and update was
            // success we assume password has changed...
            // We chose to expire the special case if password is set to the
            // same. i.e. no before & after equals pw check
            if (isPasswordChangeAttempt) {
                userService.expireActiveSessions(inputUser);
            }
        }

        return importReport;
    }

    protected void updateUserGroups(String userUid, User parsed, User currentUser) {
        User user = userService.getUser(userUid);

        if (currentUser != null && currentUser.getId() == user.getId()) {
            currentUser = currentUserService.getCurrentUser();
        }

        List<String> uids = getUids(parsed.getGroups());

        userGroupService.updateUserGroups(user, uids, currentUser);
    }

    // -------------------------------------------------------------------------
    // PATCH
    // -------------------------------------------------------------------------

    @Override
    protected void prePatchEntity(User oldEntity, User newEntity) {
//        // TODO: To remove when we remove old UserCredentials compatibility
//        populateUserCredentialsDtoCopyOnlyChanges(oldEntity, newEntity);
    }

    @Override
    protected void postPatchEntity(JsonPatch patch, User entityAfter) {
        // Make sure we always expire all the user's active sessions if we
        // have disabled the user.
        if (entityAfter != null && entityAfter.isDisabled()) {
            userService.expireActiveSessions(entityAfter);
        }

        if (entityAfter != null && patch != null) {
            for (JsonPatchOperation op : patch.getOperations()) {
                JsonPointer userGroups = op.getPath().matchProperty("userGroups");
                String opName = op.getOp();
                if (userGroups != null && (opName.equals("add") || opName.equals("replace"))) {
                    AddOperation addOp = (AddOperation) op;
                    Stream<JsonNode> targetStream = CollectionUtils.iterableToStream(addOp.getValue().elements());
                    List<String> uids = targetStream.map(node -> node.get("id").asText())
                        .collect(Collectors.toList());

                    userGroupService.updateUserGroups(entityAfter, uids, currentUserService.getCurrentUser());
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    protected void preDeleteEntity(User entity)
        throws Exception {
        User currentUser = currentUserService.getCurrentUser();

        if (!(userService.canAddOrUpdateUserInGroups(getUids(entity.getGroups()), currentUser)
            || userService.canAddOrUpdateUserInGroups(getUids(entity.getTeams()), currentUser))
            || !currentUser.canModifyUser(entity)) {
            throw new WebMessageException(conflict(
                "You must have permissions to create user, or ability to manage at least one user group or Team for the user."));
        }

        if (userService.isLastSuperUser(entity)) {
            throw new WebMessageException(conflict("Can not remove the last super user."));
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Validates whether the given user can be created.
     *
     * @param user the user.
     */
    private void validateCreateUser(User user, User currentUser)
        throws WebMessageException {
        if (!aclService.canCreate(currentUser, getEntityClass())) {
            throw new CreateAccessDeniedException("You don't have the proper permissions to create this object.");
        }

        if (!(userService.canAddOrUpdateUserInGroups(getUids(user.getGroups()), currentUser)
            || userService.canAddOrUpdateUserInTeams(getUids(user.getTeams()), currentUser))) {
            throw new WebMessageException(conflict(
                "You must have permissions to create user, or ability to manage at least one user group or Team for the user."));
        }

        List<String> groupsUids = getUids(user.getGroups());

        for (String uid : groupsUids) {
            if (!userGroupService.canAddOrRemoveMember(uid, currentUser)) {
                throw new WebMessageException(
                    conflict("You don't have permissions to add user to user group: " + uid));
            }
        }

        List<String> teams = getUids(user.getTeams());

        for (String uid : teams) {
            if (!teamService.canAddOrRemoveMember(uid, currentUser)) {
                throw new WebMessageException(
                    conflict("You don't have permissions to add user to team: " + uid));
            }
        }
    }

    /**
     * Creates a user.
     *
     * @param user user object parsed from the POST request.
     */
    private ImportReport createUser(User user, User currentUser) {
        MetadataImportParams importParams = new MetadataImportParams()
            .setImportReportMode(ImportReportMode.FULL)
            .setImportStrategy(ImportStrategy.CREATE)
            .addObject(user);

        ImportReport importReport = importService.importMetadata(importParams);

        if (importReport.getStatus() == Status.OK && importReport.getStats().getCreated() == 1) {
            userGroupService.addUserToGroups(user, getUids(user.getGroups()), currentUser);
        }

        return importReport;
    }

//    /**
//     * Validates whether a user can be invited / created.
//     *
//     * @param user the user.
//     */
//    private void validateInviteUser(User user, User currentUser)
//        throws WebMessageException {
//        if (user == null) {
//            throw new WebMessageException(conflict("User is not present"));
//        }
//
//        validateCreateUser(user, currentUser);
//
//        ErrorCode errorCode = securityService.validateInvite(user);
//
//        if (errorCode != null) {
//            throw new IllegalQueryException(errorCode);
//        }
//    }

//    private ObjectReport inviteUser(User user, User currentUser, HttpServletRequest request) {
//        RestoreOptions restoreOptions = user.getUsername() == null || user.getUsername().isEmpty()
//            ? RestoreOptions.INVITE_WITH_USERNAME_CHOICE
//            : RestoreOptions.INVITE_WITH_DEFINED_USERNAME;
//
//        securityService.prepareUserForInvite(user);
//
//        ImportReport importReport = createUser(user, currentUser);
//        ObjectReport objectReport = getObjectReport(importReport);
//
//        if (importReport.getStatus() == Status.OK &&
//            importReport.getStats().getCreated() == 1 &&
//            objectReport != null) {
//            securityService
//                .sendRestoreOrInviteMessage(user, ContextUtils.getContextPath(request),
//                    restoreOptions);
//
//            log.info(String.format("An invite email was successfully sent to: %s", user.getEmail()));
//        }
//
//        return objectReport;
//    }

    private static ObjectReport getObjectReport(ImportReport importReport) {
        return importReport.getFirstObjectReport();
    }

    /**
     * Make a copy of any existing attribute values, so they can be saved as new
     * attribute values. Don't copy unique values.
     *
     * @param userReplica user for which to copy attribute values.
     */
    private void copyAttributeValues(User userReplica) {
        if (userReplica.getAttributeValues() == null) {
            return;
        }

        Set<AttributeValue> newAttributeValues = new HashSet<>();

        for (AttributeValue oldValue : userReplica.getAttributeValues()) {
            if (!oldValue.getAttribute().isUnique()) {
                AttributeValue newValue = new AttributeValue(oldValue.getValue(), oldValue.getAttribute());

                newAttributeValues.add(newValue);
            }
        }

        if (newAttributeValues.isEmpty()) {
            userReplica.setAttributeValues(null);
        }

        userReplica.setAttributeValues(newAttributeValues);
    }

    private User mergeLastLoginAttribute(User source, User target) {
        if (target == null) {
            return target;
        }

        if (target.getLastLogin() != null) {
            return target;
        }

        if (source != null && source.getLastLogin() != null) {
            target.setLastLogin(source.getLastLogin());
        }

        return target;
    }

    /**
     * Either disable or enable a user account
     *
     * @param uid     the unique id of the user to enable or disable
     * @param disable boolean value, true for disable, false for enable
     * @throws WebMessageException thrown if "current" user is not allowed to
     *                             modify the user
     */
    private void setDisabled(String uid, boolean disable)
        throws WebMessageException {
        User userToModify = userService.getUser(uid);
        checkCurrentUserCanModify(userToModify);

        if (userToModify.isDisabled() == disable) {
            userToModify.setDisabled(disable);
            userService.updateUser(userToModify);
        }

        if (disable) {
            userService.expireActiveSessions(userToModify);
        }
    }

    private void checkCurrentUserCanModify(User userToModify)
        throws WebMessageException {
        User currentUser = currentUserService.getCurrentUser();

        if (!aclService.canUpdate(currentUser, userToModify)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        if (!userService.canAddOrUpdateUserInGroups(getUids(userToModify.getGroups()), currentUser)
            || !currentUser.canModifyUser(userToModify)) {
            throw new WebMessageException(conflict(
                "You must have permissions to create user, or ability to manage at least one user group for the user."));
        }
    }

    private void setExpires(String uid, LocalDateTime accountExpiry)
        throws WebMessageException {
        User userToModify = userService.getUser(uid);
        checkCurrentUserCanModify(userToModify);

        User user = userToModify;
        user.setAccountExpiry(accountExpiry);
        userService.updateUser(user);

        if (userService.isAccountExpired(user)) {
            userService.expireActiveSessions(user);
        }
    }
}
