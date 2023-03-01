package org.nmcpye.am.user;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.aerogear.security.otp.api.Base32;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.common.AuditLogUtil;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.UserOrgUnitType;
import org.nmcpye.am.commons.filter.FilterUtils;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.security.SecurityService;
import org.nmcpye.am.security.SecurityUtils;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.system.filter.UserAuthorityGroupCanIssueFilter;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamServiceExt;
import org.nmcpye.am.util.DateUtils;
import org.nmcpye.am.util.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.now;
import static org.nmcpye.am.common.IdentifiableObjectUtils.getUids;

@Service("org.nmcpye.am.user.UserServiceExt")
@Slf4j
@Lazy
public class UserServiceExtImpl implements UserServiceExt {

    private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    private final UserRepositoryExt userRepositoryExt;

    private final CurrentUserService currentUserService;

    private final UserGroupServiceExt userGroupService;

    private final TeamServiceExt teamServiceExt;

    private final SystemSettingManager systemSettingManager;

    private final UserAuthorityGroupRepositoryExt userAuthorityGroupRepositoryExt;

    private final SessionRegistry sessionRegistry;

    protected final PasswordEncoder passwordEncoder;

    private final Cache<String> userDisplayNameCache;

    private final SecurityService securityService;

    private final AclService aclService;

    public UserServiceExtImpl(
        @Lazy // Fix circular dependency
            PasswordEncoder passwordEncoder,
        UserRepositoryExt userRepositoryExt,
        CurrentUserService currentUserService, UserGroupServiceExt userGroupService,
        @Lazy TeamServiceExt teamServiceExt, SystemSettingManager systemSettingManager,
        UserAuthorityGroupRepositoryExt userAuthorityGroupRepositoryExt,
        @Lazy SessionRegistry sessionRegistry, CacheProvider cacheProvider,
        @Lazy SecurityService securityService, AclService aclService) {
        checkNotNull(userRepositoryExt);
        checkNotNull(userGroupService);
        checkNotNull(userAuthorityGroupRepositoryExt);
        checkNotNull(systemSettingManager);
        checkNotNull(passwordEncoder);
        checkNotNull(sessionRegistry);
        checkNotNull(securityService);
        checkNotNull(aclService);

        this.sessionRegistry = sessionRegistry;
        this.securityService = securityService;
        this.userRepositoryExt = userRepositoryExt;
        this.currentUserService = currentUserService;
        this.userGroupService = userGroupService;
        this.teamServiceExt = teamServiceExt;
        this.systemSettingManager = systemSettingManager;
        this.userAuthorityGroupRepositoryExt = userAuthorityGroupRepositoryExt;
        this.passwordEncoder = passwordEncoder;
        this.userDisplayNameCache = cacheProvider.createUserDisplayNameCache();
        this.aclService = aclService;
    }


    @Override
    @Transactional
    public Long addUser(User user) {
        String currentUsername = currentUserService.getCurrentUsername();
        AuditLogUtil.infoWrapper(log, currentUsername, user, AuditLogUtil.ACTION_CREATE);
        userRepositoryExt.saveObject(user);

        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepositoryExt.update(user);

        AuditLogUtil.infoWrapper(log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_UPDATE);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        AuditLogUtil.infoWrapper(log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_DELETE);

        userRepositoryExt.deleteObject(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String uid) {
        return userRepositoryExt.getByUidNoAcl(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUuid(UUID uuid) {
        return userRepositoryExt.getUserByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepositoryExt.getUserByUsername(username, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByPhoneNumber(String phoneNumber) {
        UserQueryParams params = new UserQueryParams();
        params.setPhoneNumber(phoneNumber);
        return getUsers(params);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdentifier(String id) {
        User user = null;

        if (CodeGenerator.isValidUid(id) && (user = getUser(id)) != null) {
            return user;
        }

        if (ValidationUtils.uuidIsValid(id) && (user = getUserByUuid(UUID.fromString(id))) != null) {
            return user;
        }

        if ((user = getUserByUsername(id)) != null) {
            return user;
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(Collection<String> uids) {
        return userRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(UserQueryParams params) {
        return getUsers(params, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(UserQueryParams params, @Nullable List<String> orders) {
        handleUserQueryParams(params);

        if (!validateUserQueryParams(params)) {
            return Lists.newArrayList();
        }

        return userRepositoryExt.getUsers(params, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCount() {
        return userRepositoryExt.getUserCount();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCount(UserQueryParams params) {
        handleUserQueryParams(params);

        if (!validateUserQueryParams(params)) {
            return 0;
        }

        return userRepositoryExt.getUserCount(params);
    }

    private void handleUserQueryParams(UserQueryParams params) {
        boolean canSeeOwnRoles = params.isCanSeeOwnRoles()
            || systemSettingManager.getBoolSetting(SettingKey.CAN_GRANT_OWN_USER_ROLES);
        params.setDisjointRoles(!canSeeOwnRoles);

        if (!params.hasUser()) {
            params.setUser(currentUserService.getCurrentUser());
        }

        if (params.hasUser() && params.getUser().isSuper()) {
            params.setCanManage(false);
            params.setAuthSubset(false);
            params.setDisjointRoles(false);
        }

        if (params.getInactiveMonths() != null) {
            Calendar cal = PeriodType.createCalendarInstance();
            cal.add(Calendar.MONTH, (params.getInactiveMonths() * -1));
            params.setInactiveSince(cal.getTime());
        }

        if (params.hasUser()) {
            UserOrgUnitType orgUnitBoundary = params.getOrgUnitBoundary();
            if (params.isUserOrgUnits() || orgUnitBoundary == UserOrgUnitType.DATA_CAPTURE) {
                params.setOrganisationUnits(params.getUser().getOrganisationUnits());
                params.setOrgUnitBoundary(UserOrgUnitType.DATA_CAPTURE);
            } else if (orgUnitBoundary == UserOrgUnitType.DATA_OUTPUT) {
                params.setOrganisationUnits(params.getUser().getDataViewOrganisationUnits());
            } else if (orgUnitBoundary == UserOrgUnitType.TEI_SEARCH) {
                params.setOrganisationUnits(params.getUser().getTeiSearchOrganisationUnits());
            }
        }
    }

    private boolean validateUserQueryParams(UserQueryParams params) {
        if (params.isCanManage() && (params.getUser() == null
            || !(params.getUser().hasManagedGroups() || params.getUser().hasManagedTeams()))) {
            log.warn("Cannot get managed users as user does not have any managed groups or Teams");
            return false;
        }

        if (params.isAuthSubset()
            && (params.getUser() == null || !params.getUser().hasAuthorities())) {
            log.warn("Cannot get users with authority subset as user does not have any authorities");
            return false;
        }

        if (params.isDisjointRoles()
            && (params.getUser() == null || !params.getUser().hasUserAuthorityGroups())) {
            log.warn("Cannot get users with disjoint roles as user does not have any user roles");
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String username = SecurityUtils.getCurrentUserLogin().orElse(null);
        return userRepositoryExt.getUserByUsername(username, false);
    }

    @Override
    @Transactional
    public Long addUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
        userAuthorityGroupRepositoryExt.saveObject(userAuthorityGroup);
        return userAuthorityGroup.getId();
    }

    @Override
    @Transactional
    public void updateUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
        userAuthorityGroupRepositoryExt.update(userAuthorityGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAuthorityGroup getUserAuthorityGroup(Long id) {
        return userAuthorityGroupRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAuthorityGroup getUserAuthorityGroup(String uid) {
        return userAuthorityGroupRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAuthorityGroup getUserAuthorityGroupByName(String name) {
        return userAuthorityGroupRepositoryExt.getByName(name);
    }

    @Override
    @Transactional
    public void deleteUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
        userAuthorityGroupRepositoryExt.deleteObject(userAuthorityGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAuthorityGroup> getAllUserAuthorityGroups() {
        return userAuthorityGroupRepositoryExt.getAll();
    }

    @Override
    public String getDisplayName(String userUid) {
        return userDisplayNameCache.get(userUid, c -> userRepositoryExt.getDisplayName(userUid));
    }

    @Override
    public List<User> getUsersWithAuthority(String authority) {
        return userRepositoryExt.getHasAuthority(authority);
    }

    @Override
    public boolean isAccountExpired(User user) {
        return !user.isAccountNonExpired();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByUsernames(Collection<String> usernames) {
        return userRepositoryExt.getUserByUsernames(usernames);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsersBetweenByName(String name, int first, int max) {
        UserQueryParams params = new UserQueryParams();
        params.setQuery(name);
        params.setFirst(first);
        params.setMax(max);

        return userRepositoryExt.getUsers(params);
    }

    @Override
    @Transactional(readOnly = true)
    public void canIssueFilter(Collection<UserAuthorityGroup> userAuthorityGroups) {
        User user = currentUserService.getCurrentUser();

        boolean canGrantOwnUserRoles = systemSettingManager
            .getBoolSetting(SettingKey.CAN_GRANT_OWN_USER_ROLES);

        FilterUtils.filter(userAuthorityGroups, new UserAuthorityGroupCanIssueFilter(user, false));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorReport> validateUser(User user, User currentUser) {
        List<ErrorReport> errors = new ArrayList<>();

        if (currentUser == null || user == null) {
            return errors;
        }

        // Validate user role

        boolean canGrantOwnUserRoles = systemSettingManager
            .getBoolSetting(SettingKey.CAN_GRANT_OWN_USER_ROLES);

        Set<UserAuthorityGroup> userRoles = user.getUserAuthorityGroups();

        if (userRoles != null) {
            List<UserAuthorityGroup> roles = userAuthorityGroupRepositoryExt.getByUid(
                userRoles.stream().map(BaseIdentifiableObject::getUid).collect(Collectors.toList()));

            roles.forEach(ur -> {
                if (ur == null) {
                    errors.add(new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3028, user.getUsername()));
                } else if (!currentUser.canIssueUserAuthorityGroup(ur, canGrantOwnUserRoles)) {
                    errors.add(new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3003, currentUser.getUsername(),
                        ur.getName()));
                }
            });
        }

        // Validate user group
        boolean canAdd = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD);

        if (canAdd) {
            return errors;
        }

        boolean canAddInGroup = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD_IN_GROUP);

        if (!canAddInGroup) {
            errors.add(new ErrorReport(UserGroup.class, ErrorCode.E3004, currentUser));
            return errors;
        }

        user.getGroups().forEach(ug -> {
            if (!(currentUser.canManage(ug) || userGroupService.canAddOrRemoveMember(ug.getUid()))) {
                errors.add(new ErrorReport(UserGroup.class, ErrorCode.E3005, currentUser, ug));
            }
        });

        user.getTeams().forEach(team -> {
            if (!(currentUser.canManage(team) || teamServiceExt.canAddOrRemoveMember(team.getUid()))) {
                errors.add(new ErrorReport(Team.class, ErrorCode.E1003, currentUser, team));
            }
        });

        return errors;
    }

    @Override
    @Transactional
    public void encodeAndSetPassword(User user, String rawPassword) {
        if (StringUtils.isEmpty(rawPassword) /*&& !user.isExternalAuth()*/) {
            return; // Leave unchanged if internal authentication and no
            // password supplied
        }

//        if (user.isExternalAuth()) {
//            user.setPassword(UserService.PW_NO_INTERNAL_LOGIN);
//
//            return; // Set unusable, not-encoded password if external
//            // authentication
//        }

        boolean isNewPassword = StringUtils.isBlank(user.getPassword()) ||
            !passwordEncoder.matches(rawPassword, user.getPassword());

        if (isNewPassword) {
            user.setPasswordLastUpdated(LocalDateTime.now());
        }

        // Encode and set password
        Matcher matcher = this.BCRYPT_PATTERN.matcher(rawPassword);
        if (matcher.matches()) {
            throw new IllegalArgumentException("Raw password look like BCrypt: " + rawPassword);
        }

        String encode = passwordEncoder.encode(rawPassword);
        user.setPassword(encode);
        user.getPreviousPasswords().add(encode);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public User createUserDetails(User user, String password, /*boolean accountNonLocked,*/
//                                  boolean credentialsNonExpired) {
//        User userDetail = new User();
//        userDetail.setUid(user.getUid());
//        userDetail.setUsername(user.getUsername());
//        userDetail.setPassword(user.getPassword());
//        userDetail.setActivated(user.isActivated());
//        userDetail.setEmail(user.getEmail());
////        User userDetail = User.builder()
////            .uid(user.getUid())
////            .login(user.getUsername())
////            .password(user.getPassword())
////            .activated(user.isEnabled())
////            .accountNonExpired(user.isAccountNonExpired())
////            .accountNonLocked(accountNonLocked)
////            .credentialsNonExpired(credentialsNonExpired)
////            .authorities(user.getAuthorities())
////            .userSettings(new HashMap<>())
////            .userGroupIds(currentUserService.getCurrentUserGroupsInfo(user.getUid()).getUserGroupUIDs())
////            .isSuper(user.isSuper())
////            .build();
//        userDetail.setUserAuthorityGroups(user.getUserAuthorityGroups());
//        userDetail.setGrantedAuthorities(user.getGrantedAuthorities());
//
//        return userDetail;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public User validateAndCreateUserDetails(User user, String password) {
//        Objects.requireNonNull(user);
//
//        String username = user.getUsername();
//        boolean enabled = user.isEnabled();
//        boolean credentialsNonExpired = userNonExpired(user);
//        boolean accountNonExpired = !isAccountExpired(user);
//
//        if (ObjectUtils.anyIsFalse(enabled, credentialsNonExpired,/* accountNonLocked, */accountNonExpired)) {
//            log.info(String.format(
//                "Login attempt for disabled/locked user: '%s', enabled: %b, account non-expired: %b, user non-expired: %b",
//                username, enabled, accountNonExpired, credentialsNonExpired/*, accountNonLocked*/));
//        }
//
//        return createUserDetails(user, password, /*accountNonLocked, */credentialsNonExpired);
//    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserDetails validateAndCreateUserDetails(User user, String password) {
        Objects.requireNonNull(user);

        String username = user.getUsername();
        boolean enabled = !user.isDisabled();
        boolean credentialsNonExpired = userNonExpired(user);
        boolean accountNonLocked = !securityService.isLocked(user.getUsername());
        boolean accountNonExpired = !isAccountExpired(user);

        if (ObjectUtils.anyIsFalse(enabled, credentialsNonExpired, accountNonLocked, accountNonExpired)) {
            log.info(String.format(
                "Login attempt for disabled/locked user: '%s', enabled: %b, account non-expired: %b, user non-expired: %b, account non-locked: %b",
                username, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked));
        }

        return createUserDetails(user, password, accountNonLocked, credentialsNonExpired);
    }

    @Override
    public CurrentUserDetailsImpl createUserDetails(User user, String password,
                                                    boolean accountNonLocked,
                                                    boolean credentialsNonExpired) {
        return CurrentUserDetailsImpl.builder()
            .uid(user.getUid())
            .username(user.getUsername())
            .password(user.getPassword())
            .enabled(user.isEnabled())
            .accountNonExpired(user.isAccountNonExpired())
            .accountNonLocked(accountNonLocked)
            .credentialsNonExpired(credentialsNonExpired)
            .authorities(user.getAuthorities())
            .userSettings(new HashMap<>())
            .userGroupIds(currentUserService.getCurrentUserGroupsInfo(user.getUid()).getUserGroupUIDs())
            .userTeamIds(currentUserService.getCurrentUserGroupsInfo(user.getUid()).getTeamUIDs())
            .userTeamGroupIds(currentUserService.getCurrentUserGroupsInfo(user.getUid()).getTeamGroupUIDs())
            .isSuper(user.isSuper())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userNonExpired(User user) {
        int credentialsExpires = systemSettingManager.credentialsExpires();

        if (credentialsExpires == 0) {
            return true;
        }

        if (user == null || user.getPasswordLastUpdated() == null) {
            return true;
        }

        int months = DateUtils.monthsBetween(
            DateUtils.fromLocalDateTime(user.getPasswordLastUpdated()), new Date());

        return months < credentialsExpires;
    }

    @Override
    @Transactional
    public int disableUsersInactiveSince(LocalDateTime inactiveSince) {
        if (ZonedDateTime.of(inactiveSince, systemDefault()).plusMonths(1).isAfter(now())) {
            // we never disable users that have been active during last month
            return 0;
        }
        return userRepositoryExt.disableUsersInactiveSince(inactiveSince);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrUpdateUserInGroups(Collection<String> userGroups) {
        return canAddOrUpdateUserInGroups(userGroups, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrUpdateUserInGroups(Collection<String> userGroups, User currentUser) {
        if (currentUser == null) {
            return false;
        }

        boolean canAdd = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD);

        if (canAdd) {
            return true;
        }

        boolean canAddInGroup = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD_IN_GROUP);

        if (!canAddInGroup) {
            return false;
        }

        boolean canManageAnyGroup = false;

        for (String uid : userGroups) {
            UserGroup userGroup = userGroupService.getUserGroup(uid);

            if (currentUser.canManage(userGroup)) {
                canManageAnyGroup = true;
                break;
            }
        }

        return canManageAnyGroup;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrUpdateUserInTeams(Collection<String> teams) {
        return canAddOrUpdateUserInGroups(teams, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrUpdateUserInTeams(Collection<String> teams, User currentUser) {
        if (currentUser == null) {
            return false;
        }

        boolean canAdd = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD);

        if (canAdd) {
            return true;
        }

        boolean canAddInGroup = currentUser.isAuthorized(UserGroup.AUTH_USER_ADD_IN_GROUP);

        if (!canAddInGroup) {
            return false;
        }

        boolean canManageAnyTeam = false;

        for (String uid : teams) {
            Team team = teamServiceExt.getTeam(uid);

            if (currentUser.canManage(team)) {
                canManageAnyTeam = true;
                break;
            }
        }

        return canManageAnyTeam;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLastSuperUser(User user) {
        if (!user.isSuper()) {
            return false; // Cannot be last if not superuser
        }

        Collection<User> allUsers = userRepositoryExt.getAll();

        for (User u : allUsers) {
            if (u.isSuper() && !u.equals(user)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void expireActiveSessions(User user) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);

        sessions.forEach(SessionInformation::expireNow);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByOpenId(String openId) {
        User user = userRepositoryExt.getUserByOpenId(openId);

        if (user != null) {
            user.getAllAuthorities();
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByLdapId(String ldapId) {
        return userRepositoryExt.getUserByLdapId(ldapId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserWithEagerFetchAuthorities(String username) {
        User user = userRepositoryExt.getUserByUsername(username, true);

        if (user != null) {
            user.getAllAuthorities();
        }

        return user;
    }

    @Override
    @Transactional
    public void set2FA(User user, boolean enable) {
        // Do nothing if current state is the same as the requested state
        if (user.getTwoFA() == enable) {
            return;
        }

        if (user.getSecret() == null) {
            throw new IllegalStateException("User secret not set!");
        }

        if (!enable) {
            user.setSecret(null);
        }

        user.setTwoFA(enable);

        updateUser(user);
    }

    @Override
    @Transactional
    public void generateTwoFactorSecret(User user) {
        user.setSecret(Base32.random());
        updateUser(user);
    }

    @Override
    @Transactional
    public void disableTwoFA(User currentUser, String userUid, Consumer<ErrorReport> errors) {
        User user = getUser(userUid);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (currentUser.getUid().equals(user.getUid())) {
            // Cannot disable 2FA for yourself with this API endpoint.
            errors.accept(new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3021, currentUser.getUsername(),
                user.getName()));
        }

        if (!canCurrentUserCanModify(currentUser, user, errors)) {
            return;
        }

        set2FA(user, false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCurrentUserCanModify(User currentUser, User userToModify, Consumer<ErrorReport> errors) {
        if (!aclService.canUpdate(currentUser, userToModify)) {
            errors.accept(new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3001, currentUser.getUsername(),
                userToModify.getName()));
            return false;
        }

        if (!(canAddOrUpdateUserInGroups(getUids(userToModify.getGroups()), currentUser)
            || canAddOrUpdateUserInTeams(getUids(userToModify.getTeams()), currentUser))
            || !currentUser.canModifyUser(userToModify)) {
            errors.accept(new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3020, userToModify.getName()));
            return false;
        }

        return true;
    }

    @Override
    public List<UserAccountExpiryInfo> getExpiringUserAccounts(int inDays) {
        return userRepositoryExt.getExpiringUserAccounts(inDays);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Optional<Locale>> findNotifiableUsersWithLastLoginBetween(LocalDateTime from, LocalDateTime to) {
        return userRepositoryExt.findNotifiableUsersWithLastLoginBetween(from, to);
    }

}
