package org.nmcpye.am.organisationunit;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ObjectUtils;
import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.assignment.AssignmentRepositoryExt;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.commons.collection.ListUtils;
import org.nmcpye.am.commons.filter.FilterUtils;
import org.nmcpye.am.commons.util.TextUtils;
import org.nmcpye.am.hierarchy.HierarchyViolationException;
import org.nmcpye.am.organisationunit.comparator.OrganisationUnitLevelComparator;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.system.filter.OrganisationUnitPolygonCoveringCoordinateFilter;
import org.nmcpye.am.system.util.GeoUtils;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserGroup;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link OrganisationUnit}.
 */
@Service("org.nmcpye.am.organisationunit.OrganisationUnitServiceExt")
public class OrganisationUnitServiceExtImpl implements OrganisationUnitServiceExt {

    private static final String LEVEL_PREFIX = "Level ";

    String UID_EXPRESSION = "[a-zA-Z]\\w{10}";

    String INT_EXPRESSION = "^(0|-?[1-9]\\d*)$";

    private final Cache<Boolean> inUserOrgUnitHierarchyCache;

    private final Cache<Boolean> inUserOrgUnitViewHierarchyCache;

    private final Cache<Boolean> inUserOrgUnitSearchHierarchyCache;

    private final Cache<Boolean> userCaptureOrgCountThresholdCache;

    private final OrganisationUnitRepositoryExt organisationUnitRepositoryExt;

    private final CurrentUserService currentUserService;

    private final UserServiceExt userServiceExt;

    private final OrganisationUnitLevelRepositoryExt organisationUnitLevelRepositoryExt;

    private final AssignmentRepositoryExt assignmentRepositoryExt;

    public OrganisationUnitServiceExtImpl(
        OrganisationUnitRepositoryExt organisationUnitRepositoryExt,
        CurrentUserService currentUserService,
        UserServiceExt userServiceExt, OrganisationUnitLevelRepositoryExt organisationUnitLevelRepositoryExt,
        CacheProvider cacheProvider,
        AssignmentRepositoryExt assignmentRepositoryExt) {
        this.organisationUnitRepositoryExt = organisationUnitRepositoryExt;
        this.currentUserService = currentUserService;
        this.userServiceExt = userServiceExt;
        this.organisationUnitLevelRepositoryExt = organisationUnitLevelRepositoryExt;

        this.inUserOrgUnitHierarchyCache = cacheProvider.createInUserOrgUnitHierarchyCache();
        this.inUserOrgUnitSearchHierarchyCache = cacheProvider.createInUserSearchOrgUnitHierarchyCache();
        this.userCaptureOrgCountThresholdCache = cacheProvider.createUserCaptureOrgUnitThresholdCache();
        this.inUserOrgUnitViewHierarchyCache = cacheProvider.createInUserViewOrgUnitHierarchyCache();
        this.assignmentRepositoryExt = assignmentRepositoryExt;
    }

    /////////////////////
    ///
    /// Extended Queries
    ///
    /////////////////////
//    @Override
//    @Transactional(readOnly = true)
//    @Cacheable(cacheNames = OrganisationUnitServiceExt.USERS_ACCESSIBLE_ORG_UNITS_CACHE)
//    public Set<OrganisationUnit> getAllUserAccessibleOrganisationUnits(User userUid) {
//        Set<OrganisationUnit> organisationUnits = getUserTeamsOrganisationUnits(userUid);
//        organisationUnits.addAll(getUserManagedTeamsOrganisationUnits(userUid));
//        organisationUnits.addAll(getUserManagedGroupsOrganisationUnits(userUid));
//        return organisationUnits;
//    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = OrganisationUnitServiceExt.USERS_ACCESSIBLE_ORG_UNITS_CACHE)
    public Set<OrganisationUnit> getAllUserAccessibleOrganisationUnits(User user) {
        List<Assignment> assignments = assignmentRepositoryExt.getAssignments(user, null,
            true, false);
        return assignments.stream()
            .map(Assignment::getOrganisationUnit)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public boolean isDescendant(OrganisationUnit organisationUnit, Set<OrganisationUnit> ancestors) {
        Objects.requireNonNull(organisationUnit, "organisationUnit is null");

        if (ancestors == null || ancestors.isEmpty()) {
            return false;
        }

        Set<String> ancestorsUid = new HashSet<>();
        for (OrganisationUnit ancestor : ancestors) {
            if (ancestor == null) {
                continue;
            }

            ancestorsUid.add(ancestor.getUid());
        }

        OrganisationUnit unit = getOrganisationUnit(organisationUnit.getUid());
        if (unit == null) {
            unit = organisationUnit;
        }

        while (unit != null) {
            if (ancestorsUid.contains(unit.getUid())) {
                return true;
            }

            unit = unit.getParent();
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDescendant(OrganisationUnit organisationUnit, OrganisationUnit ancestor) {
        if (ancestor == null) {
            return false;
        }

        OrganisationUnit unit = getOrganisationUnit(organisationUnit.getUid());

        while (unit != null) {
            if (ancestor.equals(unit)) {
                return true;
            }

            unit = unit.getParent();
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getUserTeamsOrganisationUnits(User userUid) {
        User user = userServiceExt.getUser(userUid.getUid());
        if (user != null) {
            return user.getTeams()
                .stream()
                .filter(team -> !team.getInactive())
                .filter(team -> !team.getActivity().getInactive())
                .map(Team::getAssignments)
                .flatMap(Collection::stream)
                .map(Assignment::getOrganisationUnit)
                .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getUserManagedTeamsOrganisationUnits(User userUid) {
        User user = userServiceExt.getUser(userUid.getUid());
        if (user != null) {

            return user.getTeams()
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

        return new HashSet<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getUserGroupsOrganisationUnits(User userUid) {
        User user = userServiceExt.getUser(userUid.getUid());
        if (user != null) {
            return user.getGroups()
                .stream()
                .filter(group -> !group.getInactive())
                .map(UserGroup::getMembers)
                .flatMap(Collection::stream)
                .map(User::getOrganisationUnits)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        }

        return new HashSet<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getUserManagedGroupsOrganisationUnits(User userUid) {
        User user = userServiceExt.getUser(userUid.getUid());
        if (user != null) {
            return user.getGroups()
                .stream()
                .filter(group -> !group.getInactive())
                .map(UserGroup::getManagedGroups)
                .flatMap(Collection::stream)
                .map(UserGroup::getMembers)
                .flatMap(Collection::stream)
                .map(User::getOrganisationUnits)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        }

        return new HashSet<>();
    }

    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long addOrganisationUnit(OrganisationUnit organisationUnit) {
        organisationUnitRepositoryExt.saveObject(organisationUnit);
        User user = currentUserService.getCurrentUser();

        if (organisationUnit.getParent() == null && user != null) {
            // Adding a new root node, add this node to the current user
            user.getOrganisationUnits().add(organisationUnit);
        }

        return organisationUnit.getId();
    }

    @Override
    @Transactional
    public void updateOrganisationUnit(OrganisationUnit organisationUnit) {
        organisationUnitRepositoryExt.update(organisationUnit);
    }

    @Override
    @Transactional
    public void updateOrganisationUnit(OrganisationUnit organisationUnit, boolean updateHierarchy) {
        updateOrganisationUnit(organisationUnit);
    }

    @Override
    @Transactional
    public void deleteOrganisationUnit(OrganisationUnit organisationUnit) throws HierarchyViolationException {
        organisationUnitRepositoryExt.deleteObject(organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnit getOrganisationUnit(Long id) {
        return organisationUnitRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getAllOrganisationUnits() {
        return organisationUnitRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getAllOrganisationUnitsByLastUpdated(Instant lastUpdated) {
        return organisationUnitRepositoryExt.getAllOrganisationUnitsByLastUpdated(lastUpdated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnits(Collection<Long> identifiers) {
        return organisationUnitRepositoryExt.getById(identifiers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsByUid(Collection<String> uids) {
        return organisationUnitRepositoryExt.getByUid(new HashSet<>(uids));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsByQuery(OrganisationUnitQueryParams params) {
        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnit getOrganisationUnit(String uid) {
        return organisationUnitRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitByName(String name) {
        return new ArrayList<>(organisationUnitRepositoryExt.getAllEqName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnit getOrganisationUnitByCode(String code) {
        return organisationUnitRepositoryExt.getByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getRootOrganisationUnits() {
        return organisationUnitRepositoryExt.getRootOrganisationUnits();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnits(Collection<OrganisationUnitGroup> groups, Collection<OrganisationUnit> parents) {
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setParents(Sets.newHashSet(parents));
        params.setGroups(Sets.newHashSet(groups));

        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsWithChildren(Collection<String> parentUids) {
        return getOrganisationUnitsWithChildren(parentUids, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsWithChildren(Collection<String> parentUids, Integer maxLevels) {
        List<OrganisationUnit> units = new ArrayList<>();

        for (String uid : parentUids) {
            units.addAll(getOrganisationUnitWithChildren(uid, maxLevels));
        }

        return units;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitWithChildren(String uid) {
        return getOrganisationUnitWithChildren(uid, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitWithChildren(String uid, Integer maxLevels) {
        OrganisationUnit unit = getOrganisationUnit(uid);

        Long id = unit != null ? unit.getId() : -1;

        return getOrganisationUnitWithChildren(id, maxLevels);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitWithChildren(Long id) {
        return getOrganisationUnitWithChildren(id, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitWithChildren(Long id, Integer maxLevels) {
        OrganisationUnit organisationUnit = getOrganisationUnit(id);

        if (organisationUnit == null) {
            return new ArrayList<>();
        }

        if (maxLevels != null && maxLevels <= 0) {
            return new ArrayList<>();
        }

        int rootLevel = organisationUnit.getLevel();

        Integer levels = maxLevels != null ? (rootLevel + maxLevels - 1) : null;
        //        SortProperty orderBy = SortProperty
        //            .fromValue(userSettingService.getUserSetting(UserSettingKey.ANALYSIS_DISPLAY_PROPERTY).toString());

        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setParents(Sets.newHashSet(organisationUnit));
        params.setMaxLevels(levels);
        params.setFetchChildren(true);
        //        params.setOrderBy(orderBy);

        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsWithProgram(Program program) {
        return organisationUnitRepositoryExt.getOrganisationUnitsWithProgram(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsAtLevel(int level) {
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setLevels(Sets.newHashSet(level));

        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsAtLevel(int level, OrganisationUnit parent) {
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setLevels(Sets.newHashSet(level));

        if (parent != null) {
            params.setParents(Sets.newHashSet(parent));
        }

        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsAtOrgUnitLevels(
        Collection<OrganisationUnitLevel> levels,
        Collection<OrganisationUnit> parents
    ) {
        return getOrganisationUnitsAtLevels(levels.stream().map(OrganisationUnitLevel::getLevel).collect(Collectors.toList()), parents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsAtLevels(Collection<Integer> levels, Collection<OrganisationUnit> parents) {
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setLevels(Sets.newHashSet(levels));
        params.setParents(Sets.newHashSet(parents));

        return organisationUnitRepositoryExt.getOrganisationUnits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumberOfOrganisationalLevels() {
        return organisationUnitRepositoryExt.getMaxLevel();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsWithoutGroups() {
        return organisationUnitRepositoryExt.getOrganisationUnitsWithoutGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrganisationUnit> getOrganisationUnitsWithCyclicReferences() {
        return organisationUnitRepositoryExt.getOrganisationUnitsWithCyclicReferences();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrphanedOrganisationUnits() {
        return organisationUnitRepositoryExt.getOrphanedOrganisationUnits();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets() {
        return organisationUnitRepositoryExt.getOrganisationUnitsViolatingExclusiveGroupSets();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrganisationUnitHierarchyMemberCount(OrganisationUnit parent, Object member, String collectionName) {
        return organisationUnitRepositoryExt.getOrganisationUnitHierarchyMemberCount(parent, member, collectionName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserHierarchy(OrganisationUnit organisationUnit) {
        return isInUserHierarchy(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserHierarchyCached(OrganisationUnit organisationUnit) {
        return isInUserHierarchyCached(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserHierarchyCached(User user, OrganisationUnit organisationUnit) {
        String cacheKey = TextUtils.joinHyphen(user.getUsername(), organisationUnit.getUid());
        user.getTeiSearchOrganisationUnitsWithFallback();
        return inUserOrgUnitHierarchyCache.get(cacheKey, ou -> isInUserHierarchy(user, organisationUnit));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserHierarchy(User user, OrganisationUnit organisationUnit) {
        if (user == null) {
            return false;
        }

//        if (
//            userData == null ||
//                userData.getTeiSearchOrganisationUnitsWithFallback() == null ||
//                userData.getTeiSearchOrganisationUnitsWithFallback().isEmpty()
//        ) {
//            return false;
//        }
        Set<OrganisationUnit> organisationUnits = getAllUserAccessibleOrganisationUnits(user);
        organisationUnits.addAll(user.getOrganisationUnits());
        if (organisationUnits.isEmpty()) {
            return false;
        }
//        return organisationUnit.isDescendant(userData.getOrganisationUnits());
        return organisationUnit.isDescendant(organisationUnits);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserDataViewHierarchy(OrganisationUnit organisationUnit) {
        return isInUserDataViewHierarchy(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserDataViewHierarchyCached(OrganisationUnit organisationUnit) {
        return isInUserDataViewHierarchy(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserDataViewHierarchy(User user, OrganisationUnit organisationUnit) {
        if (user == null) {
            return false;
        }
        if (user.getOrganisationUnits() == null || user.getOrganisationUnits().isEmpty()) {
            return false;
        }

        return organisationUnit.isDescendant(user.getDataViewOrganisationUnitsWithFallback());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserDataViewHierarchyCached(User user, OrganisationUnit organisationUnit) {
        String cacheKey = TextUtils.joinHyphen(user.getUsername(), organisationUnit.getUid());
        user.getDataViewOrganisationUnitsWithFallback();
        return inUserOrgUnitViewHierarchyCache.get(cacheKey, ou -> isInUserDataViewHierarchy(user, organisationUnit));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserSearchHierarchy(OrganisationUnit organisationUnit) {
        return isInUserSearchHierarchy(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserSearchHierarchyCached(OrganisationUnit organisationUnit) {
        return isInUserSearchHierarchyCached(currentUserService.getCurrentUser(), organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserSearchHierarchyCached(User user, OrganisationUnit organisationUnit) {
        String cacheKey = TextUtils.joinHyphen(user.getUsername(), organisationUnit.getUid());
        return inUserOrgUnitSearchHierarchyCache.get(cacheKey, ou -> isInUserSearchHierarchy(user, organisationUnit));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserSearchHierarchy(User user, OrganisationUnit organisationUnit) {
        if (user == null) {
            return false;
        }

        Set<OrganisationUnit> organisationUnits = new HashSet<>();

//        if (userData.getTeams() != null && !userData.getTeams().isEmpty()) {
        organisationUnits.addAll(getAllUserAccessibleOrganisationUnits(user));
//        }

//        if (
//            userData.getTeiSearchOrganisationUnitsWithFallback() != null && !userData.getTeiSearchOrganisationUnitsWithFallback().isEmpty()
//        ) {
        organisationUnits.addAll(user.getTeiSearchOrganisationUnitsWithFallback());
//        }
        return organisationUnit.isDescendant(organisationUnits);
        //        return organisationUnit.isDescendant(userData.getTeiSearchOrganisationUnitsWithFallback());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInUserHierarchy(String uid, Set<OrganisationUnit> organisationUnits) {
        OrganisationUnit organisationUnit = organisationUnitRepositoryExt.getByUid(uid);

        return organisationUnit != null && organisationUnit.isDescendant(organisationUnits);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCaptureOrganisationUnitUidsWithChildren() {
        User user = currentUserService.getCurrentUser();
        if (user == null) {
            return new ArrayList<>();
        }

        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setParents(user.getOrganisationUnits());
        params.setFetchChildren(true);
        return organisationUnitRepositoryExt.getOrganisationUnitUids(params);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCaptureOrgUnitCountAboveThreshold(int threshold) {
        User user = currentUserService.getCurrentUser();
        if (user == null) {
            return false;
        }

        return userCaptureOrgCountThresholdCache.get(
            user.getUsername(),
            ou -> {
                OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
                params.setParents(user.getOrganisationUnits());
                params.setFetchChildren(true);
                return organisationUnitRepositoryExt.isOrgUnitCountAboveThreshold(params, threshold);
            }
        );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long addOrganisationUnitLevel(OrganisationUnitLevel organisationUnitLevel) {
        organisationUnitLevelRepositoryExt.saveObject(organisationUnitLevel);
        return organisationUnitLevel.getId();
    }

    @Override
    @Transactional
    public void updateOrganisationUnitLevel(OrganisationUnitLevel organisationUnitLevel) {
        organisationUnitLevelRepositoryExt.update(organisationUnitLevel);
    }

    @Override
    @Transactional
    public void addOrUpdateOrganisationUnitLevel(OrganisationUnitLevel level) {
        OrganisationUnitLevel existing = getOrganisationUnitLevelByLevel(level.getLevel());

        if (existing == null) {
            addOrganisationUnitLevel(level);
        } else {
            existing.setName(level.getName());
            existing.setOfflineLevels(level.getOfflineLevels());

            updateOrganisationUnitLevel(existing);
        }
    }

    @Override
    @Transactional
    public void pruneOrganisationUnitLevels(Set<Integer> currentLevels) {
        for (OrganisationUnitLevel level : getOrganisationUnitLevels()) {
            if (!currentLevels.contains(level.getLevel())) {
                deleteOrganisationUnitLevel(level);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitLevel getOrganisationUnitLevel(Long id) {
        return organisationUnitLevelRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitLevel getOrganisationUnitLevel(String uid) {
        return organisationUnitLevelRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional
    public void deleteOrganisationUnitLevel(OrganisationUnitLevel organisationUnitLevel) {
        organisationUnitLevelRepositoryExt.deleteObject(organisationUnitLevel);
    }

    @Override
    @Transactional
    public void deleteOrganisationUnitLevels() {
        organisationUnitLevelRepositoryExt.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitLevel> getOrganisationUnitLevels() {
        return ListUtils.sort(organisationUnitLevelRepositoryExt.getAll(), OrganisationUnitLevelComparator.INSTANCE);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitLevel getOrganisationUnitLevelByLevel(Integer level) {
        return organisationUnitLevelRepositoryExt.getByLevel(level);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitLevel> getOrganisationUnitLevelByName(String name) {
        return new ArrayList<>(organisationUnitLevelRepositoryExt.getAllEqName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitLevel> getFilledOrganisationUnitLevels() {
        Map<Integer, OrganisationUnitLevel> levelMap = getOrganisationUnitLevelMap();

        List<OrganisationUnitLevel> levels = new ArrayList<>();

        int levelNo = getNumberOfOrganisationalLevels();

        for (int i = 0; i < levelNo; i++) {
            int level = i + 1;

            OrganisationUnitLevel filledLevel = ObjectUtils.firstNonNull(
                levelMap.get(level),
                new OrganisationUnitLevel(level, LEVEL_PREFIX + level)
            );

            levels.add(filledLevel);
        }

        return levels;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, OrganisationUnitLevel> getOrganisationUnitLevelMap() {
        Map<Integer, OrganisationUnitLevel> levelMap = new HashMap<>();

        List<OrganisationUnitLevel> levels = getOrganisationUnitLevels();

        for (OrganisationUnitLevel level : levels) {
            levelMap.put(level.getLevel(), level);
        }

        return levelMap;
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumberOfOrganisationUnits() {
        return organisationUnitRepositoryExt.getCount();
    }

    //    @Override
    //    @Transactional(readOnly = true)
    //    public int getOfflineOrganisationUnitLevels() {
    //        // ---------------------------------------------------------------------
    //        // Get level from organisation unit of current user
    //        // ---------------------------------------------------------------------
    //
    //        User user = userServiceExt.getCurrentUser();
    //
    //        if (user != null && user.hasOrganisationUnit()) {
    //            OrganisationUnit organisationUnit = user.getOrganisationUnit();
    //
    //            int level = organisationUnit.getLevel();
    //
    //            OrganisationUnitLevel orgUnitLevel = getOrganisationUnitLevelByLevel(level);
    //
    //            if (orgUnitLevel != null && orgUnitLevel.getOfflineLevels() != null) {
    //                return orgUnitLevel.getOfflineLevels();
    //            }
    //        }
    //
    //        // ---------------------------------------------------------------------
    //        // Get level from system configuration
    //        // ---------------------------------------------------------------------
    //
    //        OrganisationUnitLevel level = configurationService.getConfiguration().getOfflineOrganisationUnitLevel();
    //
    //        if (level != null) {
    //            return level.getLevel();
    //        }
    //
    //        // ---------------------------------------------------------------------
    //        // Get max level
    //        // ---------------------------------------------------------------------
    //
    //        int max = getOrganisationUnitLevels().size();
    //
    //        OrganisationUnitLevel maxLevel = getOrganisationUnitLevelByLevel(max);
    //
    //        if (maxLevel != null) {
    //            return maxLevel.getLevel();
    //        }
    //
    //        // ---------------------------------------------------------------------
    //        // Return 1 level as fall back
    //        // ---------------------------------------------------------------------
    //
    //        return 1;
    //    }

    @Override
    @Transactional
    public void updatePaths() {
        organisationUnitRepositoryExt.updatePaths();
    }

    @Override
    @Transactional
    public void forceUpdatePaths() {
        organisationUnitRepositoryExt.forceUpdatePaths();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getOrganisationUnitLevelByLevelOrUid(String level) {
        if (level.matches(INT_EXPRESSION)) {
            int orgUnitLevel = Integer.parseInt(level);

            return orgUnitLevel > 0 ? orgUnitLevel : null;
        } else if (level.matches(UID_EXPRESSION)) {
            OrganisationUnitLevel orgUnitLevel = getOrganisationUnitLevel(level);

            return orgUnitLevel != null ? orgUnitLevel.getLevel() : null;
        }

        return null;
    }

    /**
     * Get all the Organisation Units within the distance of a coordinate.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitWithinDistance(double longitude, double latitude, double distance) {
        List<OrganisationUnit> objects = organisationUnitRepositoryExt.getWithinCoordinateArea(
            GeoUtils.getBoxShape(longitude, latitude, distance)
        );

        // Go through the list and remove the ones located outside radius

        if (objects != null && objects.size() > 0) {
            Iterator<OrganisationUnit> iter = objects.iterator();

            Point2D centerPoint = new Point2D.Double(longitude, latitude);

            while (iter.hasNext()) {
                OrganisationUnit orgunit = iter.next();

                double distancebetween = GeoUtils.getDistanceBetweenTwoPoints(
                    centerPoint,
                    ValidationUtils.getCoordinatePoint2D(GeoUtils.getCoordinatesFromGeometry(orgunit.getGeometry()))
                );

                if (distancebetween > distance) {
                    iter.remove();
                }
            }
        }

        return objects;
    }

    /**
     * Get lowest level/target level Organisation Units that includes the
     * coordinates.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnit> getOrganisationUnitByCoordinate(
        double longitude,
        double latitude,
        String topOrgUnitUid,
        Integer targetLevel
    ) {
        List<OrganisationUnit> orgUnits = new ArrayList<>();

        if (GeoUtils.checkGeoJsonPointValid(longitude, latitude)) {
            OrganisationUnit topOrgUnit = null;

            if (topOrgUnitUid != null && !topOrgUnitUid.isEmpty()) {
                topOrgUnit = getOrganisationUnit(topOrgUnitUid);
            } else {
                // Get top search point through top level org unit which
                // contains coordinate

                List<OrganisationUnit> orgUnitsTopLevel = getTopLevelOrgUnitWithPoint(
                    longitude,
                    latitude,
                    1,
                    getNumberOfOrganisationalLevels() - 1
                );

                if (orgUnitsTopLevel.size() == 1) {
                    topOrgUnit = orgUnitsTopLevel.iterator().next();
                }
            }

            // Search children org units to get the lowest level org unit that
            // contains
            // coordinate

            if (topOrgUnit != null) {
                List<OrganisationUnit> orgUnitChildren;

                if (targetLevel != null) {
                    orgUnitChildren = getOrganisationUnitsAtLevel(targetLevel, topOrgUnit);
                } else {
                    orgUnitChildren = getOrganisationUnitWithChildren(topOrgUnit.getId());
                }

                FilterUtils.filter(orgUnitChildren, new OrganisationUnitPolygonCoveringCoordinateFilter(longitude, latitude));

                // Get org units with lowest level

                int bottomLevel = topOrgUnit.getLevel();

                for (OrganisationUnit ou : orgUnitChildren) {
                    if (ou.getLevel() > bottomLevel) {
                        bottomLevel = ou.getLevel();
                    }
                }

                for (OrganisationUnit ou : orgUnitChildren) {
                    if (ou.getLevel() == bottomLevel) {
                        orgUnits.add(ou);
                    }
                }
            }
        }

        return orgUnits;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Searches organisation units until finding one with polygon containing
     * point.
     */
    private List<OrganisationUnit> getTopLevelOrgUnitWithPoint(double longitude, double latitude, int searchLevel, int stopLevel) {
        for (int i = searchLevel; i <= stopLevel; i++) {
            List<OrganisationUnit> unitsAtLevel = new ArrayList<>(getOrganisationUnitsAtLevel(i));
            FilterUtils.filter(unitsAtLevel, new OrganisationUnitPolygonCoveringCoordinateFilter(longitude, latitude));

            if (unitsAtLevel.size() > 0) {
                return unitsAtLevel;
            }
        }

        return new ArrayList<>();
    }
}
