package org.nmcpye.am.program;

import org.apache.commons.collections4.SetValuedMap;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Service Interface for managing {@link Program}.
 */
public interface ProgramServiceExt {
    String ID = ProgramServiceExt.class.getName();

    /**
     * Adds an {@link Program}
     *
     * @param program The to Program add.
     * @return A generated unique id of the added {@link Program}.
     */
    Long addProgram(Program program);

    /**
     * Updates an {@link Program}.
     *
     * @param program the Program to update.
     */
    void updateProgram(Program program);

    /**
     * Deletes a {@link Program}. All {@link ProgramStage},
     * {@link ProgramInstance} and {@link ProgramStageInstance} belong to this
     * program are removed
     *
     * @param program the Program to delete.
     */
    void deleteProgram(Program program);

    /**
     * Returns a {@link Program}.
     *
     * @param id the id of the Program to return.
     * @return the Program with the given id
     */
    Program getProgram(Long id);

    /**
     * Returns all {@link Program}.
     *
     * @return a collection of all Program, or an empty collection if there are
     * no Programs.
     */
    List<Program> getAllPrograms();

    Collection<Program> getPrograms(Collection<String> uids);

    /**
     * Get all {@link Program} belong to a orgunit
     *
     * @param organisationUnit {@link OrganisationUnit}
     * @return The program list
     */
    List<Program> getPrograms(OrganisationUnit organisationUnit);

    /**
     * Returns the {@link Program} with the given UID.
     *
     * @param uid the UID.
     * @return the Program with the given UID, or null if no match.
     */
    Program getProgram(String uid);

    /**
     * Get {@link TrackedEntityType} by TrackedEntityType
     *
     * @param trackedEntityType {@link TrackedEntityType}
     */
    List<Program> getProgramsByTrackedEntityType(TrackedEntityType trackedEntityType);

    //    /**
    //     * Get all Programs with the given DataEntryForm.
    //     *
    //     * @param dataEntryForm the DataEntryForm.
    //     * @return a list of Programs.
    //     */
    //    List<Program> getProgramsByDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Get {@link Program} by the current user. Returns all programs if current
     * user is superuser. Returns an empty list if there is no current user.
     *
     * @return Immutable set of programs associated with the current user.
     */
    List<Program> getCurrentUserPrograms();

    //    /**
    //     * Returns a list of generated, non-persisted program data elements for the
    //     * program with the given identifier.
    //     *
    //     * @param programUid the program identifier.
    //     * @return a list of program data elements.
    //     */
    //    List<ProgramDataElementDimensionItem> getGeneratedProgramDataElements( String programUid );

    /**
     * Checks whether the given {@link OrganisationUnit} belongs to the
     * specified {@link Program}
     */
    Boolean hasOrgUnit(Program program, OrganisationUnit organisationUnit);

    /**
     * Get all the organisation unit associated for a set of program uids. This
     * method uses jdbc to directly fetch the associated org unit uids for every
     * program uid. This method also filters the results to respect the current
     * users organisation unit scope and sharing settings.
     *
     * @param programUids A set of program uids
     * @return A object of {@link SetValuedMap} containing
     * association for each programUid
     */
    SetValuedMap<String, String> getProgramOrganisationUnitsAssociationsForCurrentUser(Set<String> programUids);

    /**
     * Get all the organisation unit associated for a set of program uids. This
     * method uses jdbc to directly fetch the associated org unit uids for every
     * program uid. This method returns all the associations irrespective of the
     * sharing settings or org unit scopes.
     *
     * @param programUids A set of program uids
     * @return A object of {@link SetValuedMap} containing
     * association for each programUid
     */
    SetValuedMap<String, String> getProgramOrganisationUnitsAssociations(Set<String> programUids);
}
