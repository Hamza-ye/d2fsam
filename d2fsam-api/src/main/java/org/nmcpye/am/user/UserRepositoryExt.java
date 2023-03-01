package org.nmcpye.am.user;

import org.nmcpye.am.common.IdentifiableObjectStore;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;

public interface UserRepositoryExt
    extends IdentifiableObjectStore<User> {

    String ID = UserRepositoryExt.class.getName();

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    String USERS_BY_LOGIN_EXT = "usersByLoginExt";

    //    String USERS_BY_LOGIN_CACHE_EXT = "usersByLoginExt";

    String USERS_BY_EMAIL_CACHE_EXT = "usersByEmailExt";

    /**
     * Returns a list of users based on the given query parameters.
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
     * Returns the number of users based on the given query parameters.
     *
     * @param params the user query parameters.
     * @return number of users.
     */
    int getUserCount(UserQueryParams params);

    /**
     * Returns number of all users
     *
     * @return number of users
     */
    int getUserCount();

    List<User> getExpiringUsers(UserQueryParams userQueryParams);

    /**
     * Returns User for given username.
     *
     * @param username   username for which the User will be returned
     * @param ignoreCase
     * @return User for given username or null
     */
    User getUserByUsername(String username, boolean ignoreCase);

    User getUserByEmail(String username, boolean ignoreCase);

    /**
     * Return CurrentUserGroupInfo used for ACL check in
     * {@link IdentifiableObjectStore}
     */
    CurrentUserGroupInfo getCurrentUserGroupInfo(String userUID);

    /**
     * Get user display name by concat( firstname,' ', surname ) Return null if
     * User doesn't exist
     */
    String getDisplayName(String userUid);

    List<User> getHasAuthority(String authority);

    /**
     * Retrieves a collection of User with the given usernames.
     *
     * @param usernames the usernames of the collection of Users to retrieve.
     * @return the User.
     */
    List<User> getUserByUsernames(Collection<String> usernames);

    /**
     * Sets {@link User#setDisabled(boolean)} (boolean)} to {@code true} for all users
     * where the {@link User#getLastLogin()} is before or equal to the provided
     * pivot {@link Date}.
     *
     * @param inactiveSince the most recent point in time that is considered
     *                      inactive together with accounts only active further in the past.
     * @return number of users disabled
     */
    int disableUsersInactiveSince(LocalDateTime inactiveSince);

    /**
     * Retrieves the User associated with the User with the given open ID.
     *
     * @param openId open ID.
     * @return the User or null if there is no match.
     */
    User getUserByOpenId(String openId);

    /**
     * Retrieves the User associated with the User with the given LDAP ID.
     *
     * @param ldapId LDAP ID.
     * @return the User.
     */
    User getUserByLdapId(String ldapId);

    /**
     * Retrieves the User associated with the User with the given id token.
     *
     * @param token the restore token of the User.
     * @return the User.
     */
    User getUserByIdToken(String token);

    /**
     * Retrieves the User with the given UUID.
     *
     * @param uuid UUID.
     * @return the User.
     */
    User getUserByUuid(UUID uuid);

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
