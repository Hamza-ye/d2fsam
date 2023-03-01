package org.nmcpye.am.organisationunit;

import org.nmcpye.am.hierarchy.HierarchyViolationException;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.user.User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service Interface for managing {@link OrganisationUnit}.
 */
public interface OrganisationUnitServiceExt {
    String ID = OrganisationUnitServiceExt.class.getName();

    String USERS_ACCESSIBLE_ORG_UNITS_CACHE = "userAccessibleOrgUnits";

    //    Set<OrganisationUnit> findOrgUnitsBelongingToUserGroups(List<UserGroup> groups);

    boolean isDescendant(OrganisationUnit organisationUnit, Set<OrganisationUnit> ancestors);

    boolean isDescendant(OrganisationUnit organisationUnit, OrganisationUnit ancestor);

    boolean isInUserHierarchy(String uid, Set<OrganisationUnit> organisationUnits);

    Set<OrganisationUnit> getUserTeamsOrganisationUnits(User userUid);

    Set<OrganisationUnit> getUserManagedTeamsOrganisationUnits(User userUid);

    Set<OrganisationUnit> getUserGroupsOrganisationUnits(User userUid);

    Set<OrganisationUnit> getUserManagedGroupsOrganisationUnits(User userUid);

    Set<OrganisationUnit> getAllUserAccessibleOrganisationUnits(User user);

    /**
     * Adds an OrganisationUnit to the hierarchy.
     *
     * @param organisationUnit the OrganisationUnit to add.
     * @return a generated unique id of the added OrganisationUnit.
     */
    Long addOrganisationUnit(OrganisationUnit organisationUnit);

    /**
     * Updates an OrganisationUnit.
     *
     * @param organisationUnit the OrganisationUnit to update.
     */
    void updateOrganisationUnit(OrganisationUnit organisationUnit);

    /**
     * Updates an OrganisationUnit.
     *
     * @param organisationUnit the organisationUnit to update.
     * @param updateHierarchy  indicate whether the OrganisationUnit hierarchy
     *                         has been updated.
     */
    void updateOrganisationUnit(OrganisationUnit organisationUnit, boolean updateHierarchy);

    /**
     * Deletes an OrganisationUnit. OrganisationUnits with children cannot be
     * deleted.
     *
     * @param organisationUnit the OrganisationUnit to delete.
     * @throws HierarchyViolationException if the OrganisationUnit has children.
     */
    void deleteOrganisationUnit(OrganisationUnit organisationUnit) throws HierarchyViolationException;

    /**
     * Returns an OrganisationUnit.
     *
     * @param id the id of the OrganisationUnit to return.
     * @return the OrganisationUnit with the given id, or null if no match.
     */
    OrganisationUnit getOrganisationUnit(Long id);

    /**
     * Returns the OrganisationUnit with the given UID.
     *
     * @param uid the UID of the OrganisationUnit to return.
     * @return the OrganisationUnit with the given UID, or null if no match.
     */
    OrganisationUnit getOrganisationUnit(String uid);

    /**
     * Returns the OrganisationUnit with the given code.
     *
     * @param code the code of the OrganisationUnit to return.
     * @return the OrganisationUnit with the given code, or null if no match.
     */
    OrganisationUnit getOrganisationUnitByCode(String code);

    /**
     * Returns all OrganisationUnits.
     *
     * @return a list of all OrganisationUnits, or an empty list if there are no
     * OrganisationUnits.
     */
    List<OrganisationUnit> getAllOrganisationUnits();

    /**
     * Returns all OrganisationUnits by lastUpdated.
     *
     * @param lastUpdated OrganisationUnits from this date
     * @return a list of all OrganisationUnits, or an empty list if there are no
     * OrganisationUnits.
     */
    List<OrganisationUnit> getAllOrganisationUnitsByLastUpdated(Instant lastUpdated);

    /**
     * Returns all OrganisationUnits with corresponding identifiers.
     *
     * @param identifiers the collection of identifiers.
     * @return a list of OrganisationUnits.
     */
    List<OrganisationUnit> getOrganisationUnits(Collection<Long> identifiers);

    /**
     * Returns all OrganisationUnits with corresponding identifiers.
     *
     * @param uids the collection of uids.
     * @return a list of OrganisationUnits.
     */
    List<OrganisationUnit> getOrganisationUnitsByUid(Collection<String> uids);

    /**
     * Returns a list of OrganisationUnits based on the given params.
     *
     * @param params the params.
     * @return a list of OrganisationUnits.
     */
    List<OrganisationUnit> getOrganisationUnitsByQuery(OrganisationUnitQueryParams params);

    /**
     * Returns an OrganisationUnit with a given name.
     *
     * @param name the name of the OrganisationUnit to return.
     * @return the OrganisationUnit with the given name, or null if not match.
     */
    List<OrganisationUnit> getOrganisationUnitByName(String name);

    /**
     * Returns all root OrganisationUnits. A root OrganisationUnit is an
     * OrganisationUnit with no parent/the parent set to null.
     *
     * @return a list containing all root OrganisationUnits, or an empty list if
     * there are no OrganisationUnits.
     */
    List<OrganisationUnit> getRootOrganisationUnits();

    /**
     * Returns the intersection of the members of the given
     * OrganisationUnitGroups and the OrganisationUnits which are children of
     * the given collection of parents in the hierarchy. If the given parent
     * collection is null or empty, the members of the group are returned.
     *
     * @param groups  the collection of OrganisationUnitGroups.
     * @param parents the collection of OrganisationUnit parents in the
     *                hierarchy.
     * @return A list of OrganisationUnits.
     */
    List<OrganisationUnit> getOrganisationUnits(Collection<OrganisationUnitGroup> groups, Collection<OrganisationUnit> parents);

    /**
     * Returns an OrganisationUnit and all its children.
     *
     * @param uid the uid of the parent OrganisationUnit in the subtree.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitWithChildren(String uid);

    /**
     * Returns an OrganisationUnit and all its children.
     *
     * @param uid       the uid of the parent OrganisationUnit in the subtree.
     * @param maxLevels the max number of levels to return relative to the given
     *                  root, inclusive.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitWithChildren(String uid, Integer maxLevels);

    /**
     * Returns an OrganisationUnit and all its children.
     *
     * @param id the id of the parent OrganisationUnit in the subtree.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitWithChildren(Long id);

    /**
     * Returns an OrganisationUnit and all its children.
     *
     * @param id        the id of the parent OrganisationUnit in the subtree.
     * @param maxLevels the max number of levels to return relative to the given
     *                  root, inclusive.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitWithChildren(Long id, Integer maxLevels);

    /**
     * Returns the OrganisationUnits and all their children.
     *
     * @param uids the uids of the parent OrganisationUnits.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitsWithChildren(Collection<String> uids);

    /**
     * Returns the OrganisationUnits and all their children.
     *
     * @param uids      the uids of the parent OrganisationUnits.
     * @param maxLevels the max number of levels to return relative to the given
     *                  root, inclusive.
     * @return a list containing the OrganisationUnit with the given id and all
     * its children, or an empty list if no OrganisationUnits match.
     */
    List<OrganisationUnit> getOrganisationUnitsWithChildren(Collection<String> uids, Integer maxLevels);

    /**
     * Returns OrganisationUnits which are associated with the given Program.
     *
     * @param program the {@link Program}.
     * @return
     */
    List<OrganisationUnit> getOrganisationUnitsWithProgram(Program program);

    /**
     * Returns all OrganisationUnits at a given hierarchical level. The root
     * OrganisationUnits are at level 1.
     *
     * @param level the hierarchical level.
     * @return a list of all OrganisationUnits at a given hierarchical level, or
     * an empty list if the level is empty.
     * @throws IllegalArgumentException if the level is zero or negative.
     */
    List<OrganisationUnit> getOrganisationUnitsAtLevel(int level);

    /**
     * Returns all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical level. The root OrganisationUnits are at
     * level 1. If parent is null, then all OrganisationUnits at the given level
     * are returned.
     *
     * @param level  the hierarchical level.
     * @param parent the parent unit.
     * @return all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical level.
     * @throws IllegalArgumentException if the level is illegal.
     */
    List<OrganisationUnit> getOrganisationUnitsAtLevel(int level, OrganisationUnit parent);

    /**
     * Returns all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical levels. The root OrganisationUnits are at
     * level 1.
     *
     * @param levels  the OrganisationUnitLevels.
     * @param parents the parent units.
     * @return all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical level.
     * @throws IllegalArgumentException if the level is illegal.
     */
    List<OrganisationUnit> getOrganisationUnitsAtOrgUnitLevels(
        Collection<OrganisationUnitLevel> levels,
        Collection<OrganisationUnit> parents
    );

    /**
     * Returns all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical levels. The root OrganisationUnits are at
     * level 1.
     *
     * @param levels  the hierarchical levels.
     * @param parents the parent units.
     * @return all OrganisationUnits which are children of the given unit and
     * are at the given hierarchical level.
     * @throws IllegalArgumentException if the level is illegal.
     */
    List<OrganisationUnit> getOrganisationUnitsAtLevels(Collection<Integer> levels, Collection<OrganisationUnit> parents);

    /**
     * Returns the number of levels in the OrganisationUnit hierarchy.
     *
     * @return the number of hierarchical levels.
     */
    int getNumberOfOrganisationalLevels();

    /**
     * Returns the count of OrganisationUnits which are part of the
     * sub-hierarchy of the given parent OrganisationUnit and members of the
     * given object based on the collection of the given collection name.
     *
     * @param parent         the parent OrganisationUnit.
     * @param member         the member object.
     * @param collectionName the name of the collection.
     * @return the count of member OrganisationUnits.
     */
    Long getOrganisationUnitHierarchyMemberCount(OrganisationUnit parent, Object member, String collectionName);

    /**
     * Returns the level of the given org unit level. The level parameter string
     * can either represent a numerical level, or a UID referring to an
     * {@link OrganisationUnitLevel} object.
     *
     * @param level the level string, either a numeric level or UID.
     * @return the level of the corresponding {@link OrganisationUnitLevel}, or
     * null if not found or if the parameter was invalid.
     */
    Integer getOrganisationUnitLevelByLevelOrUid(String level);

    /**
     * Retrieves all the org units within the distance from center location.
     *
     * @param longitude The longitude of the center location.
     * @param latitude  The latitude of the center location.
     * @param distance  The distance from center location.
     * @return a list of objects.
     */
    List<OrganisationUnit> getOrganisationUnitWithinDistance(double longitude, double latitude, double distance);

    /**
     * Retrieves the orgunit(s) by coordinate.
     *
     * @param longitude     The longitude of the location.
     * @param latitude      The latitude of the location.
     * @param topOrgUnitUid Optional. Uid of the search top level org unit (ex.
     *                      Country level orgunit)
     * @param targetLevel   Optional. The level being searched.
     * @return list of objects.
     */
    List<OrganisationUnit> getOrganisationUnitByCoordinate(double longitude, double latitude, String topOrgUnitUid, Integer targetLevel);

    /**
     * Returns all OrganisationUnits which are not a member of any
     * OrganisationUnitGroups.
     *
     * @return all OrganisationUnits which are not a member of any
     * OrganisationUnitGroups.
     */
    List<OrganisationUnit> getOrganisationUnitsWithoutGroups();

    /**
     * Indicates whether the given organisation unit is part of the hierarchy of
     * the organisation units of the current user.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserHierarchy(OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserHierarchy(OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserHierarchyCached(OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserHierarchy(User, OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param user             the user to check for.
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserHierarchyCached(User user, OrganisationUnit organisationUnit);

    boolean isInUserHierarchy(User user, OrganisationUnit organisationUnit);

    /**
     * Indicates whether the given organisation unit is part of the hierarchy of
     * the data view organisation units of the current user.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserDataViewHierarchy(OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserDataViewHierarchy(OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserDataViewHierarchyCached(OrganisationUnit organisationUnit);

    /**
     * Indicates whether the given organisation unit is part of the hierarchy of
     * the given user data view organisation units.
     *
     * @param user             the user to check for.
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the data view
     * hierarchy.
     */
    boolean isInUserDataViewHierarchy(User user, OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserDataViewHierarchy(User, OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param user             the user to check for.
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the data view
     * hierarchy.
     */
    boolean isInUserDataViewHierarchyCached(User user, OrganisationUnit organisationUnit);

    /**
     * Indicates whether the given organisation unit is part of the search
     * hierarchy of the organisation units of the current user.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the search
     * hierarchy.
     */
    boolean isInUserSearchHierarchy(OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserSearchHierarchy(OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserSearchHierarchyCached(OrganisationUnit organisationUnit);

    /**
     * Equal to
     * {@link OrganisationUnitServiceExt#isInUserSearchHierarchy(User, OrganisationUnit)}
     * except adds a caching layer on top. Use this method when performance is
     * imperative and the risk of a stale result is tolerable.
     *
     * @param user             the user to check for.
     * @param organisationUnit the organisation unit.
     * @return true if the given organisation unit is part of the hierarchy.
     */
    boolean isInUserSearchHierarchyCached(User user, OrganisationUnit organisationUnit);

    boolean isInUserSearchHierarchy(User user, OrganisationUnit organisationUnit);

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    Long addOrganisationUnitLevel(OrganisationUnitLevel level);

    void updateOrganisationUnitLevel(OrganisationUnitLevel level);

    void addOrUpdateOrganisationUnitLevel(OrganisationUnitLevel level);

    void pruneOrganisationUnitLevels(Set<Integer> currentLevels);

    OrganisationUnitLevel getOrganisationUnitLevel(Long id);

    OrganisationUnitLevel getOrganisationUnitLevel(String uid);

    void deleteOrganisationUnitLevel(OrganisationUnitLevel level);

    void deleteOrganisationUnitLevels();

    List<OrganisationUnitLevel> getOrganisationUnitLevels();

    OrganisationUnitLevel getOrganisationUnitLevelByLevel(Integer level);

    List<OrganisationUnitLevel> getOrganisationUnitLevelByName(String name);

    List<OrganisationUnitLevel> getFilledOrganisationUnitLevels();

    Map<Integer, OrganisationUnitLevel> getOrganisationUnitLevelMap();

    int getNumberOfOrganisationUnits();

    //    /**
    //     * Return the number of organisation unit levels to cache offline, e.g. for
    //     * organisation unit tree. Looks for level to return in the following order:
    //     * <p/>
    //     * <ul>
    //     * <li>Get level of organisation unit of the current user.</li>
    //     * <li>Get level from system configuration.</li>
    //     * <li>Get max level.</li>
    //     * <li>Return 1 as fall back.</li>
    //     * </ul>
    //     */
    //    int getOfflineOrganisationUnitLevels();

    /**
     * Update all OUs where paths is null.
     */
    void updatePaths();

    /**
     * Update all OUs (thus forcing update of path).
     */
    void forceUpdatePaths();

    /**
     * Check if the count of the organisation units in the capture scope of
     * current user is above a threshold
     *
     * @param threshold the threshold number to check against.
     * @return true if the count of the organisation units in capture scope of
     * current user is above the threshold, false otherwise
     */
    boolean isCaptureOrgUnitCountAboveThreshold(int threshold);

    /**
     * Get the full list of organisation unit uids that falls under the capture
     * scope of the current user
     *
     * @return list of org unit uids in capture scope of current user.
     */
    List<String> getCaptureOrganisationUnitUidsWithChildren();

    /**
     * Gets all organisation units which are related to each other in a cyclic
     * reference.
     */
    Set<OrganisationUnit> getOrganisationUnitsWithCyclicReferences();

    /**
     * Gets all organisation units with no parents or children.
     */
    List<OrganisationUnit> getOrphanedOrganisationUnits();

    /**
     * Gets all organisation units which are members of more than one group
     * which enter into an exclusive group set.
     */
    List<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets();
}
