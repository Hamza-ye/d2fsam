package org.nmcpye.am.user;

import java.util.Collection;
import java.util.List;

/**
 * Service Interface for managing {@link UserGroup}.
 */
public interface UserGroupServiceExt {
    String ID = UserGroupServiceExt.class.getName();

    String USERS_GROUPS_BY_UID_CACHE = "gruopsByUserUid";

    String USERS_MANAGED_GROUPS_BY_UID_CACHE = "managedGroupsByUserUid";

    UserGroup getUserGroup(String uid);

    // NMCP
    List<UserGroup> getUserGroups(Collection<String> uids);

//    Set<UserGroup> getUserGroups(String userUid);
//
//    Set<UserGroup> getUserManagedGroups(String userUid);

    Long addUserGroup(UserGroup userGroup);

    void updateUserGroup(UserGroup userGroup);

    void deleteUserGroup(UserGroup userGroup);

    UserGroup getUserGroup(Long userGroupId);

    /**
     * Indicates whether the current user can add or remove members for the user
     * group with the given UID. To to so the current user must have write
     * access to the group or have read access as well as the
     * F_USER_GROUPS_READ_ONLY_ADD_MEMBERS authority.
     *
     * @param uid the user group UID.
     * @return true if the current user can add or remove members of the user
     * group.
     */
    boolean canAddOrRemoveMember(String uid);

    boolean canAddOrRemoveMember(String uid, User currentUser);

    void addUserToGroups(User user, Collection<String> uids);

    void addUserToGroups(User user, Collection<String> uids, User currentUser);

    void removeUserFromGroups(User user, Collection<String> uids);

    void updateUserGroups(User user, Collection<String> uids);

    void updateUserGroups(User user, Collection<String> uids, User currentUser);

    List<UserGroup> getAllUserGroups();

    List<UserGroup> getUserGroupByName(String name);

    List<UserGroup> getUserGroupsBetween(int first, int max);

    List<UserGroup> getUserGroupsBetweenByName(String name, int first, int max);

    int getUserGroupCount();

    int getUserGroupCountByName(String name);

    /**
     * Get UserGroup's display name by given userGroup uid Return null if
     * UserGroup does not exist
     */
    String getDisplayName(String uid);
}
