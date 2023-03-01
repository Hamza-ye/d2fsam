package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityType;

import java.util.List;

public interface ProgramRepositoryExtCustom extends IdentifiableObjectStore<Program> {
    String ID = ProgramRepositoryExtCustom.class.getName();

    /**
     * Get {@link Program} by a type
     *
     * @param type The type of program. There are three types, include Multi
     *             events with registration, Single event with registration and
     *             Single event without registration
     * @return Program list by a type specified
     */
    List<Program> getByType(ProgramType type);

    /**
     * Get {@link Program} assigned to an {@link OrganisationUnit} by a type
     *
     * @param organisationUnit Where programs assigned
     * @return Program list by a type specified
     */
    List<Program> get(OrganisationUnit organisationUnit);

    /**
     * Get {@link Program} by TrackedEntityType
     *
     * @param trackedEntityType {@link TrackedEntityType}
     */
    List<Program> getByTrackedEntityType(TrackedEntityType trackedEntityType);

    //    /**
    //     * Get all Programs associated with the given DataEntryForm.
    //     *
    //     * @param dataEntryForm the DataEntryForm.
    //     * @return a list of {@link Program}
    //     */
    //    List<Program> getByDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Checks whether the given {@link OrganisationUnit} belongs to the
     * specified {@link Program}
     */
    Boolean hasOrgUnit(Program program, OrganisationUnit organisationUnit);
}
