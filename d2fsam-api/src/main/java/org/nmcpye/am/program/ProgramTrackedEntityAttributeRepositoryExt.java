package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;

import java.util.List;

/**
 * Spring Data JPA repository for the ProgramTrackedEntityAttribute entity.
 */
//@Repository
//public interface ProgramTrackedEntityAttributeRepositoryExt
//    extends ProgramTrackedEntityAttributeRepositoryExtCustom,
//    JpaRepository<ProgramTrackedEntityAttribute, Long> {
//}

public interface ProgramTrackedEntityAttributeRepositoryExt
    extends IdentifiableObjectStore<ProgramTrackedEntityAttribute> {

    ProgramTrackedEntityAttribute get(Program program, TrackedEntityAttribute attribute);

    /**
     * Get all TrackedEntityAttribute filtered by given list of Program
     *
     * @param programs
     * @return List of TrackedEntityAttribute
     */
    List<TrackedEntityAttribute> getAttributes(List<Program> programs);

}
