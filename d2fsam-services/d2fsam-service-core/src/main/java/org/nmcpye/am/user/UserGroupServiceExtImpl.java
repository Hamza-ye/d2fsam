package org.nmcpye.am.user;

import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.cache.HibernateCacheManager;
import org.nmcpye.am.security.acl.AclService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service Implementation for managing {@link UserGroup}.
 */
@Service("org.nmcpye.am.user.UserGroupServiceExt")
public class UserGroupServiceExtImpl implements UserGroupServiceExt {

    private final UserGroupRepositoryExt userGroupRepositoryExt;

    private final CurrentUserService currentUserService;

    private final AclService aclService;

    private final HibernateCacheManager hibernateCacheManager;

    private Cache<String> userGroupNameCache;

    public UserGroupServiceExtImpl(
        UserGroupRepositoryExt userGroupRepositoryExt,
        CurrentUserService currentUserService,
        AclService aclService,
        HibernateCacheManager hibernateCacheManager,
        CacheProvider cacheProvider) {

        checkNotNull(userGroupRepositoryExt);
        checkNotNull(currentUserService);
        checkNotNull(aclService);
        checkNotNull(hibernateCacheManager);

        this.userGroupRepositoryExt = userGroupRepositoryExt;
        this.currentUserService = currentUserService;
        this.aclService = aclService;
        this.hibernateCacheManager = hibernateCacheManager;
        userGroupNameCache = cacheProvider.createUserGroupNameCache();
    }

    @Override
    @Transactional(readOnly = true)
    public UserGroup getUserGroup(String uid) {
        return userGroupRepositoryExt.getByUid(uid);
    }

    // NMCP
    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> getUserGroups(Collection<String> uids) {
        return userGroupRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional
    public Long addUserGroup(UserGroup userGroup) {
        userGroupRepositoryExt.saveObject(userGroup);

        return userGroup.getId();
    }

    @Override
    @Transactional
    public void deleteUserGroup(UserGroup userGroup) {
        userGroupRepositoryExt.deleteObject(userGroup);
    }

    @Override
    @Transactional
    public void updateUserGroup(UserGroup userGroup) {
        userGroupRepositoryExt.update(userGroup);
        // Clear query cache due to sharing and user group membership
        hibernateCacheManager.clearQueryCache();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> getAllUserGroups() {
        return userGroupRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserGroup getUserGroup(Long userGroupId) {
        return userGroupRepositoryExt.get(userGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrRemoveMember(String uid) {
        return canAddOrRemoveMember(uid, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddOrRemoveMember(String uid, User currentUser) {
        UserGroup userGroup = getUserGroup(uid);

        if (userGroup == null || currentUser == null) {
            return false;
        }

        boolean canUpdate = aclService.canUpdate(currentUser, userGroup);
        boolean canAddMember = currentUser.isAuthorized(UserGroup.AUTH_ADD_MEMBERS_TO_READ_ONLY_USER_GROUPS);

        return canUpdate || canAddMember;
    }

    @Override
    @Transactional
    public void addUserToGroups(User user, Collection<String> uids) {
        addUserToGroups(user, uids, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional
    public void addUserToGroups(User user, Collection<String> uids, User currentUser) {
        for (String uid : uids) {
            if (canAddOrRemoveMember(uid, currentUser)) {
                UserGroup userGroup = getUserGroup(uid);
                userGroup.addMember(user);
                userGroupRepositoryExt.updateNoAcl(userGroup);
            }
        }
    }

    @Override
    @Transactional
    public void removeUserFromGroups(User user, Collection<String> uids) {
        for (String uid : uids) {
            if (canAddOrRemoveMember(uid)) {
                UserGroup userGroup = getUserGroup(uid);
                userGroup.removeMember(user);
                userGroupRepositoryExt.updateNoAcl(userGroup);
            }
        }
    }

    @Override
    @Transactional
    public void updateUserGroups(User user, Collection<String> uids) {
        updateUserGroups(user, uids, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional
    public void updateUserGroups(User user, Collection<String> uids, User currentUser) {
        Collection<UserGroup> updates = getUserGroupsByUid(uids);

        for (UserGroup userGroup : new HashSet<>(user.getGroups())) {
            if (!updates.contains(userGroup) && canAddOrRemoveMember(userGroup.getUid(), currentUser)) {
                userGroup.removeMember(user);
            }
        }

        for (UserGroup userGroup : updates) {
            if (canAddOrRemoveMember(userGroup.getUid(), currentUser)) {
                userGroup.addMember(user);
                userGroupRepositoryExt.updateNoAcl(userGroup);
            }
        }
    }

    private Collection<UserGroup> getUserGroupsByUid(Collection<String> uids) {
        return userGroupRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> getUserGroupByName(String name) {
        return userGroupRepositoryExt.getAllEqName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserGroupCount() {
        return userGroupRepositoryExt.getCount();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserGroupCountByName(String name) {
        return userGroupRepositoryExt.getCountLikeName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> getUserGroupsBetween(int first, int max) {
        return userGroupRepositoryExt.getAllOrderedName(first, max);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> getUserGroupsBetweenByName(String name, int first, int max) {
        return userGroupRepositoryExt.getAllLikeName(name, first, max, false);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDisplayName(String uid) {
        return userGroupNameCache.get(uid, n -> userGroupRepositoryExt.getByUidNoAcl(uid).getDisplayName());
    }
}
