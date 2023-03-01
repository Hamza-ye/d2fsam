package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityType;

import java.util.List;

/**
 * Spring Data JPA repository for the Program entity.
 * <p>
 * When extending this class, extend ProgramRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface ProgramRepositoryExt
//    extends ProgramRepositoryExtCustom, JpaRepository<Program, Long> {
//}
public interface ProgramRepositoryExt extends IdentifiableObjectStore<Program> {

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
