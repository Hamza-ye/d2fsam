package org.nmcpye.am.organisationunit;

import java.util.List;
import java.util.Set;

/**
 * Service Interface for managing {@link OrganisationUnitGroup}.
 */
public interface OrganisationUnitGroupServiceExt {
    String ID = OrganisationUnitGroupServiceExt.class.getName();
    // -------------------------------------------------------------------------
    // OrganisationUnitGroup
    // -------------------------------------------------------------------------

    /**
     * Adds an OrganisationUnitGroup.
     *
     * @param organisationUnitGroup the OrganisationUnitGroup to add.
     * @return a generated unique id of the added OrganisationUnitGroup.
     */
    Long addOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup);

    /**
     * Updates an OrganisationUnitGroup.
     *
     * @param organisationUnitGroup the OrganisationUnitGroup to update.
     */
    void updateOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup);

    /**
     * Deletes an OrganisationUnitGroup.
     *
     * @param organisationUnitGroup the OrganisationUnitGroup to delete.
     */
    void deleteOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup);

    /**
     * Returns an OrganisationUnitGroup.
     *
     * @param id the id of the OrganisationUnitGroup.
     * @return the OrganisationGroup with the given id, or null if no match.
     */
    OrganisationUnitGroup getOrganisationUnitGroup(Long id);

    /**
     * Returns the OrganisationUnitGroup with the given UID.
     *
     * @param uid the UID of the OrganisationUnitGroup.
     * @return the OrganisationGroup with the given UID, or null if no match.
     */
    OrganisationUnitGroup getOrganisationUnitGroup(String uid);

    /**
     * Returns all OrganisationUnitGroups.
     *
     * @return a list of all the OrganisationUnitGroups, or an empty list if no
     * OrganisationUnitGroup exists.
     */
    List<OrganisationUnitGroup> getAllOrganisationUnitGroups();

    /**
     * Returns all OrganisationUnitGroups which have a OrganisationUnitGroupSet.
     *
     * @return a collection of OrganisationUnitGroups.
     */
    List<OrganisationUnitGroup> getOrganisationUnitGroupsWithGroupSets();

    /**
     * Gets all organisation unit groups which are not assigned to any group
     * set.
     */
    List<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets();
    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSet
    // -------------------------------------------------------------------------

    /**
     * Adds an OrganisationUnitGroupSet.
     *
     * @param organisationUnitGroupSet the OrganisationUnitGroupSet to add.
     * @return the generated unique id of the added OrganisationUnitGroupSet.
     */
    Long addOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet);

    /**
     * Updates an OrganisationUnitGroupSet.
     *
     * @param organisationUnitGroupSet the OrganisationUnitGroupSet to update.
     */
    void updateOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet);

    /**
     * Deletes an OrganisationUnitGroupSet.
     *
     * @param organisationUnitGroupSet the OrganisationUnitGroupSet to delete.
     */
    void deleteOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet);

    /**
     * Returns an OrganisationUnitGroupSet.
     *
     * @param id the id of the OrganisationUnitGroupSet to return.
     * @return the OrganisationUnitGroupSet with the given id, or null if no
     * match.
     */
    OrganisationUnitGroupSet getOrganisationUnitGroupSet(Long id);

    /**
     * Returns an OrganisationUnitGroupSet.
     *
     * @param uid the id of the OrganisationUnitGroupSet to return.
     * @return the OrganisationUnitGroupSet with the given uid, or null if no
     * match.
     */
    OrganisationUnitGroupSet getOrganisationUnitGroupSet(String uid);

    /**
     * Returns all OrganisationUnitGroupSets.
     *
     * @return a list of all OrganisationUnitGroupSets, or an empty collection
     * if no OrganisationUnitGroupSet exists.
     */
    List<OrganisationUnitGroupSet> getAllOrganisationUnitGroupSets();

    /**
     * Returns all compulsory OrganisationUnitGroupSets.
     *
     * @return a list of all compulsory OrganisationUnitGroupSets, or an empty
     * collection if there are no compulsory OrganisationUnitGroupSets.
     */
    List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSets();

    /**
     * Returns all compulsory OrganisationUnitGroupSets which have one ore more
     * members.
     *
     * @return a list of all OrganisationUnitGroupSets, or an empty collection
     * if there are no compulsory OrganisationUnitGroupSets.
     */
    List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSetsWithMembers();

    /**
     * Returns a Collection of compulsory OrganisationUnitGroupSets which groups
     * the given OrganisationUnit is not a member of.
     *
     * @param organisationUnit the OrganisationUnit.
     * @return a Collection of OrganisationUnitGroupSets.
     */
    List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSetsNotAssignedTo(OrganisationUnit organisationUnit);

    //    void mergeWithCurrentUserOrganisationUnits(OrganisationUnitGroup organisationUnitGroup,
    //                                               Collection<OrganisationUnit> mergeOrganisationUnits);

    OrganisationUnitGroup getOrgUnitGroupInGroupSet(Set<OrganisationUnitGroup> groups, OrganisationUnitGroupSet groupSet);
}
