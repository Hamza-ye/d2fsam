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
package org.nmcpye.am.user;

import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This interface defined methods for getting access to the currently logged in
 * user and clearing the logged in state. If no user is logged in or the auto
 * access admin is active, all user access methods will return null.
 *
 * @author Torgeir Lorange Ostby
 */
@Service("org.nmcpye.am.user.CurrentUserService")
public class CurrentUserService {

    private final UserRepositoryExt userStore;

    private final Cache<CurrentUserGroupInfo> currentUserGroupInfoCache;

    public CurrentUserService(@Lazy UserRepositoryExt userStore, CacheProvider cacheProvider) {
        checkNotNull(userStore);

        this.userStore = userStore;
        this.currentUserGroupInfoCache = cacheProvider.createCurrentUserGroupInfoCache();
    }

    /**
     * @return the username of the currently logged in user. If no user is
     * logged in or the auto access admin is active, null is returned.
     */
    public String getCurrentUsername() {
        return CurrentUserUtil.getCurrentUsername();
    }

    public User getCurrentUser() {
        String username = CurrentUserUtil.getCurrentUsername();

        return userStore.getUserByUsername(username, true);
    }

    @Transactional(readOnly = true)
    public boolean currentUserIsSuper() {
        User user = getCurrentUser();

        return user != null && user.isSuper();
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserOrganisationUnits() {
        User user = getCurrentUser();

        return user != null ? new HashSet<>(user.getOrganisationUnits()) : new HashSet<>();
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserTeamsOrganisationUnits() {
        User user = getCurrentUser();
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        if (user != null) {
            organisationUnits =
                user
//                    .getUserData()
                    .getTeams()
                    .stream()
                    .filter(team -> !team.getInactive())
                    .filter(team -> !team.getActivity().getInactive())
                    .map(Team::getAssignments)
                    .flatMap(Collection::stream)
                    .map(Assignment::getOrganisationUnit)
                    .collect(Collectors.toSet());
        }
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserManagedTeamsOrganisationUnits() {
        User user = getCurrentUser();
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        if (user != null) {
            organisationUnits =
                user
//                    .getUserData()
                    .getTeams()
                    .stream()
                    .filter(team -> !team.getInactive())
                    .filter(team -> !team.getActivity().getInactive())
                    .map(Team::getManagedTeams)
                    .flatMap(Collection::stream)
                    .map(Team::getAssignments)
                    .flatMap(Collection::stream)
                    .map(Assignment::getOrganisationUnit)
                    .collect(Collectors.toSet());
        }
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserTeamGroupsOrganisationUnits() {
        User user = getCurrentUser();
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        if (user != null) {
            organisationUnits =
                user
                    .getTeams()
                    .stream()
                    .filter(team -> !team.getInactive())
                    .filter(team -> !team.getActivity().getInactive())
                    .map(Team::getGroups)
                    .flatMap(Collection::stream)
                    .filter(teamGroup -> !teamGroup.getInactive())
                    .map(TeamGroup::getMembers)
                    .flatMap(Collection::stream)
                    .map(Team::getAssignments)
                    .flatMap(Collection::stream)
                    .map(Assignment::getOrganisationUnit)
                    .collect(Collectors.toSet());
        }
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserGroupsOrganisationUnits() {
        User user = getCurrentUser();
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        if (user != null) {
            organisationUnits =
                user
                    .getGroups()
                    .stream()
                    .filter(userGroup -> !userGroup.getInactive())
                    .map(UserGroup::getMembers)
                    .flatMap(Collection::stream)
                    .map(User::getOrganisationUnits)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getCurrentUserManagedGroupsOrganisationUnits() {
        User user = getCurrentUser();
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        if (user != null) {
            organisationUnits =
                user
                    .getGroups()
                    .stream()
                    .filter(userGroup -> !userGroup.getInactive())
                    .map(UserGroup::getManagedGroups)
                    .flatMap(Collection::stream)
                    .map(UserGroup::getMembers)
                    .flatMap(Collection::stream)
                    .map(User::getOrganisationUnits)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getAllCurrentUserAccessibleOrganisationUnits() {
        Set<OrganisationUnit> organisationUnits = getCurrentUserTeamsOrganisationUnits();
        organisationUnits.addAll(getCurrentUserManagedTeamsOrganisationUnits());
        organisationUnits.addAll(getCurrentUserTeamGroupsOrganisationUnits());
//        organisationUnits.addAll(getCurrentUserGroupsOrganisationUnits());
        organisationUnits.addAll(getCurrentUserManagedGroupsOrganisationUnits());
        return organisationUnits;
    }

    @Transactional(readOnly = true)
    public boolean currentUserIsAuthorized(String auth) {
        User user = getCurrentUser();

        return user != null && user.isAuthorized(auth);
    }

//    @Transactional(readOnly = true)
//    public CurrentUserGroupInfo getCurrentUserGroupsInfo() {
//        User user = CurrentUserUtil.getCurrentUserDetails();
//
//        return user == null ? null : getCurrentUserGroupsInfo(user.getUid());
//    }

    @Transactional(readOnly = true)
    public CurrentUserGroupInfo getCurrentUserGroupsInfo(String userUID) {
        return currentUserGroupInfoCache.get(userUID, userStore::getCurrentUserGroupInfo);
    }

    @Transactional(readOnly = true)
    public void invalidateUserGroupCache(String userUID) {
        try {
            currentUserGroupInfoCache.invalidate(userUID);
        } catch (NullPointerException exception) {
            // Ignore if key doesn't exist
        }
    }
}
