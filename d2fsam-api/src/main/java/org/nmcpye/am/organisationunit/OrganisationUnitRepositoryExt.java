package org.nmcpye.am.organisationunit;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.program.Program;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface OrganisationUnitRepositoryExt
    extends IdentifiableObjectStore<OrganisationUnit> {

    final String USER_ORG_UNIT_MEMBERSHIP_RELATION_NAME = "user__organisation_unit";
    final String USER_ORG_UNIT_TEI_SEARCH_RELATION_NAME = "user__tei_search_organisation_unit";

    /**
     * Returns all root OrganisationUnits. A root OrganisationUnit is an
     * OrganisationUnit with no parent/has the parent set to null.
     *
     * @return a list containing all root OrganisationUnits, or an empty
     * list if there are no OrganisationUnits.
     */
    List<OrganisationUnit> getRootOrganisationUnits();

    /**
     * Returns all OrganisationUnits which are not a member of any OrganisationUnitGroups.
     *
     * @return all OrganisationUnits which are not a member of any OrganisationUnitGroups.
     */
    List<OrganisationUnit> getOrganisationUnitsWithoutGroups();

    /**
     * Returns the count of OrganisationUnits which are part of the
     * sub-hierarchy of the given parent OrganisationUnit and members of
     * the given object based on the collection of the given collection name.
     *
     * @param parent         the parent OrganisationUnit.
     * @param member         the member object.
     * @param collectionName the name of the collection.
     * @return the count of member OrganisationUnits.
     */
    Long getOrganisationUnitHierarchyMemberCount(OrganisationUnit parent, Object member, String collectionName);

    /**
     * Returns all OrganisationUnits by lastUpdated.
     *
     * @param lastUpdated OrganisationUnits from this date
     * @return a list of all OrganisationUnits, or an empty list if there are no
     * OrganisationUnits.
     */
    List<OrganisationUnit> getAllOrganisationUnitsByLastUpdated(Instant lastUpdated);

    /**
     * Returns a list of OrganisationUnits based on the given params.
     *
     * @param params the params.
     * @return a list of OrganisationUnits.
     */
    List<OrganisationUnit> getOrganisationUnits(OrganisationUnitQueryParams params);

    //    /**
    //     * Creates a mapping between organisation unit UID and set of data set UIDs
    //     * being assigned to the organisation unit.
    //     *
    //     * @param organisationUnits the parent organisation units of the hierarchy to include,
    //     *                          ignored if null.
    //     * @param dataSets          the data set to include, ignored if null.
    //     * @return a map of sets.
    //     */
    //    Map<String, Set<String>> getOrganisationUnitDataSetAssocationMap(Collection<OrganisationUnit> organisationUnits, Collection<DataSet> dataSets);

    /**
     * Retrieves the objects where its coordinate is within the 4 area points.
     * 4 area points are
     * Index 0: Maximum latitude (north edge of box shape)
     * Index 1: Maxium longitude (east edge of box shape)
     * Index 2: Minimum latitude (south edge of box shape)
     * Index 3: Minumum longitude (west edge of box shape)
     *
     * @param box the 4 area points.
     * @return a list of objects.
     */
    List<OrganisationUnit> getWithinCoordinateArea(double[] box);

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    /**
     * Get the OrganisationUnit hierarchy.
     *
     * @return a  with OrganisationUnitRelationship entries.
     */
    OrganisationUnitHierarchy getOrganisationUnitHierarchy();

    /**
     * Returns OrganisationUnits which are associated with the given Program.
     *
     * @param program the {@link Program}.
     * @return
     */
    List<OrganisationUnit> getOrganisationUnitsWithProgram(Program program);

    /**
     * Updates the parent id of the organisation unit with the given id.
     *
     * @param organisationUnitId the child organisation unit identifier.
     * @param parentId           the parent organisation unit identifier.
     */
    void updateOrganisationUnitParent(Long organisationUnitId, Long parentId);

    void updatePaths();

    void forceUpdatePaths();

    /**
     * Returns the number of organsiation unit levels in the database based on
     * the organisation unit hierarchy.
     *
     * @return number of levels, 0 if no organisation units are present.
     */
    int getMaxLevel();

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

    /**
     * Get list of organisation unit uids satisfying the query params. Note:
     * groups, maxLevels and levels are not supported yet.
     *
     * @param params The Org unit query params
     * @return the list of org unit uids satisfying the params criteria
     */
    List<String> getOrganisationUnitUids(OrganisationUnitQueryParams params);

    /**
     * Check if the number of orgunits that satisfies the conditions in the
     * queryParams is greater than the threshold provided. Note: groups,
     * maxLevels and levels are not supported yet.
     *
     * @param params    The Org unit query params
     * @param threshold the threshold count to check against
     * @return true if the org units satisfying the params criteria is above the
     * threshold, false otherwise.
     */
    boolean isOrgUnitCountAboveThreshold(OrganisationUnitQueryParams params, int threshold);
}
