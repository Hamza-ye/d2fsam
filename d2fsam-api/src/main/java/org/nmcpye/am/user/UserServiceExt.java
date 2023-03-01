package org.nmcpye.am.user;

import org.nmcpye.am.feedback.ErrorReport;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public interface UserServiceExt {
    String ID = UserServiceExt.class.getName();

    /**
     * Adds a User.
     *
     * @param user the User to add.
     * @return the generated identifier.
     */
    Long addUser(User user);

    /**
     * Updates a User.
     *
     * @param user the User to update.
     */
    void updateUser(User user);

    /**
     * Deletes a User.
     *
     * @param user the User to delete.
     */
    void deleteUser(User user);

    /**
     * Retrieves the User with the given identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser(Long id);

    /**
     * Retrieves the User with the given unique identifier.
     *
     * @param uid the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser(String uid);

    /**
     * Retrieves the User with the given UUID.
     *
     * @param uuid the UUID of the User to retrieve.
     * @return the User.
     */
    User getUserByUuid(UUID uuid);

    /**
     * Retrieves the User with the given username.
     *
     * @param username the username of the User to retrieve.
     * @return the User.
     */
    User getUserByUsername(String username);

    List<User> getUsersByPhoneNumber(String phoneNumber);

    /**
     * Retrieves the User by attempting to look up by various identifiers
     * in the following order:
     *
     * <ul>
     * <li>UID</li>
     * <li>UUID</li>
     * <li>Username</li>
     * </ul>
     *
     * @param id the User identifier.
     * @return the User, or null if not found.
     */
    User getUserByIdentifier(String id);

    /**
     * Retrieves a collection of User with the given unique identifiers.
     *
     * @param uids the identifiers of the collection of Users to retrieve.
     * @return the User.
     */
    List<User> getUsers(Collection<String> uids);

    /**
     * Returns a list of users based on the given query parameters. The default
     * order of last name and first name will be applied.
     *
     * @param params the user query parameters.
     * @return a List of users.
     */
    List<User> getUsers(UserQueryParams params);

    /**
     * Returns a list of users based on the given query parameters. If the
     * specified list of orders are empty, default order of last name and first
     * name will be applied.
     *
     * @param params the user query parameters.
     * @param orders the already validated order strings (e.g. email:asc).
     * @return a List of users.
     */
    List<User> getUsers(UserQueryParams params, @Nullable List<String> orders);

    /**
     * Returns number of all users
     *
     * @return number of users
     */
    int getUserCount();

    /**
     * Returns the number of users based on the given query parameters.
     *
     * @param params the user query parameters.
     * @return number of users.
     */
    int getUserCount(UserQueryParams params);

    /**
     * Returns a List of all Users.
     *
     * @return a Collection of Users.
     */
    List<User> getAllUsers();

    User getCurrentUser();

    /**
     * Adds a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     * @return the generated identifier.
     */
    Long addUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup);

    /**
     * Updates a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     */
    void updateUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup);

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param id the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup(Long id);

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param uid the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup(String uid);

    /**
     * Retrieves the UserAuthorityGroup with the given name.
     *
     * @param name the name of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroupByName(String name);

    /**
     * Deletes a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup to delete.
     */
    void deleteUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup);

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a List of UserAuthorityGroups.
     */
    List<UserAuthorityGroup> getAllUserAuthorityGroups();

    /**
     * Get user display name by concat( firstname,' ', surname ) Return null if
     * User doesn't exist
     */
    String getDisplayName(String userUid);

    /**
     * Given an Authorities's name, retrieves a list of users that has that
     * authority.
     */
    List<User> getUsersWithAuthority(String authority);

    boolean isAccountExpired(User user);

    List<User> getUsersByUsernames(Collection<String> usernames);

    /**
     * Retrieves all Users with first name, surname or user name like the given
     * name.
     *
     * @param name  the name.
     * @param first the first item to return.
     * @param max   the max number of item to return.
     * @return a list of Users.
     */
    List<User> getAllUsersBetweenByName(String name, int first, int max);

    void canIssueFilter(Collection<UserAuthorityGroup> userRoles);

    List<ErrorReport> validateUser(User user, User currentUser);

    /**
     * Encodes and sets the password of the User. Due to business logic required
     * on password updates the password for a user should only be changed using
     * this method or {@link #encodeAndSetPassword(User, String)
     * encodeAndSetPassword} and not directly on the User or User object.
     * <p>
     * Note that the changes made to the User object are not persisted.
     *
     * @param user        the User
     * @param rawPassword the raw password.
     */
    void encodeAndSetPassword(User user, String rawPassword);

//    User createUserDetails(User user, String password, /*boolean accountNonLocked,*/
//                           boolean credentialsNonExpired);
//
//    User validateAndCreateUserDetails(User user, String password);

    CurrentUserDetails validateAndCreateUserDetails(User user, String password);

    CurrentUserDetailsImpl createUserDetails(User user, String password, boolean accountNonLocked,
                                             boolean credentialsNonExpired);

    boolean userNonExpired(User user);

    /**
     * Sets {@link User#setDisabled(boolean)} (boolean)} to {@code true} for all users
     * where the {@link User#getLastLogin()} is before or equal to the provided
     * pivot {@link Date}.
     *
     * @param inactiveSince the most recent point in time that is considered
     *                      inactive together with accounts only active further in the past.#
     * @return number of users disabled
     */
    int disableUsersInactiveSince(LocalDateTime inactiveSince);

    /**
     * Tests whether the current user is allowed to create a user associated
     * with the given user group identifiers. Returns true if current user has
     * the F_USER_ADD authority. Returns true if the current user has the
     * F_USER_ADD_WITHIN_MANAGED_GROUP authority and can manage any of the given
     * user groups. Returns false otherwise.
     *
     * @param userGroups the user group identifiers.
     * @return true if the current user can create user, false if not.
     */
    boolean canAddOrUpdateUserInGroups(Collection<String> userGroups);

    boolean canAddOrUpdateUserInGroups(Collection<String> userGroups, User currentUser);

    /**
     * Tests whether the current user is allowed to create a user associated
     * with the given user group identifiers. Returns true if current user has
     * the F_USER_ADD authority. Returns true if the current user has the
     * F_USER_ADD_WITHIN_MANAGED_GROUP authority and can manage any of the given
     * user groups. Returns false otherwise.
     *
     * @param teams the user group identifiers.
     * @return true if the current user can create user, false if not.
     */
    boolean canAddOrUpdateUserInTeams(Collection<String> teams);

    boolean canAddOrUpdateUserInTeams(Collection<String> teams, User currentUser);

    /**
     * Checks if the given user represents the last user with ALL authority.
     *
     * @param user the user.
     * @return true if the given user represents the last user with ALL
     * authority.
     */
    boolean isLastSuperUser(User user);

    /**
     * Expire a user's active sessions retrieved from the Spring security's
     * org.springframework.security.core.session.SessionRegistry
     *
     * @param user the user
     */
    void expireActiveSessions(User user);

    /**
     * Retrieves the User associated with the User with the given OpenID.
     *
     * @param openId the openId of the User.
     * @return the User or null if there is no match
     */
    User getUserByOpenId(String openId);

    /**
     * Retrieves the User associated with the User with the given LDAP ID.
     *
     * @param ldapId the ldapId of the User.
     * @return the User.
     */
    User getUserByLdapId(String ldapId);

    User getUserWithEagerFetchAuthorities(String username);

    void generateTwoFactorSecret(User user);

    /**
     * "If the current user is not the user being modified, and the current user
     * has the authority to modify the user, then disable two-factor
     * authentication for the user."
     * <p>
     * The first thing we do is get the user object from the database. If the
     * user doesn't exist, we throw an exception
     *
     * @param currentUser The user who is making the request.
     * @param userUid     The user UID of the user to disable 2FA for.
     * @param errors      A Consumer<ErrorReport> object that will be called if there
     *                    is an error.
     */
    void disableTwoFA(User currentUser, String userUid, Consumer<ErrorReport> errors);

    void set2FA(User user, boolean twoFA);

    boolean canCurrentUserCanModify(User currentUser, User userToModify, Consumer<ErrorReport> errors);

    /**
     * @param inDays number of days to include
     * @return list of those users that are about to expire in the provided
     * number of days (or less) and which have an email configured
     */
    List<UserAccountExpiryInfo> getExpiringUserAccounts(int inDays);

    /**
     * Selects all not disabled users where the {@link User#getLastLogin()} is
     * within the given time-frame and which have an email address.
     *
     * @param from start of the selected time-frame (inclusive)
     * @param to   end of the selected time-frame (exclusive)
     * @return user emails having a last login within the given time-frame as
     * keys and if available their preferred locale as value
     */
    Map<String, Optional<Locale>> findNotifiableUsersWithLastLoginBetween(LocalDateTime from, LocalDateTime to);
}
