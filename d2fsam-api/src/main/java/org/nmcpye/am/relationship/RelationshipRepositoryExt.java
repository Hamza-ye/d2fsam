package org.nmcpye.am.relationship;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.webapi.controller.event.webrequest.PagingAndSortingCriteriaAdapter;

import java.util.List;

/**
 * Spring Data JPA repository for the Relationship entity.
 */
//@Repository
//public interface RelationshipRepositoryExt
//    extends RelationshipRepositoryExtCustom,
//    JpaRepository<Relationship, Long> {
//}

public interface RelationshipRepositoryExt
    extends IdentifiableObjectStore<Relationship> {
    String ID = RelationshipRepositoryExt.class.getName();

    default List<Relationship> getByTrackedEntityInstance(TrackedEntityInstance tei) {
        return getByTrackedEntityInstance(tei, null);
    }

    List<Relationship> getByTrackedEntityInstance(TrackedEntityInstance tei,
                                                  PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter);

    default List<Relationship> getByProgramInstance(ProgramInstance pi) {
        return getByProgramInstance(pi, null);
    }

    List<Relationship> getByProgramInstance(ProgramInstance pi,
                                            PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter);

    default List<Relationship> getByProgramStageInstance(ProgramStageInstance psi) {
        return getByProgramStageInstance(psi, null);
    }

    List<Relationship> getByProgramStageInstance(ProgramStageInstance psi,
                                                 PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter);

    List<Relationship> getByRelationshipType(RelationshipType relationshipType);

    /**
     * Fetches a {@link Relationship} based on a relationship identifying
     * attributes: - relationship type - from - to
     *
     * @param relationship A valid Relationship
     * @return a {@link Relationship} or null if no Relationship is found
     * matching the identifying criterias
     */
    Relationship getByRelationship(Relationship relationship);

    /**
     * Checks if relationship for given UID exists (including deleted
     * relationships).
     *
     * @param uid Relationship UID to check for.
     * @return return true if relationship exists, false otherwise.
     */
    boolean existsIncludingDeleted(String uid);

    List<String> getUidsByRelationshipKeys(List<String> relationshipKeyList);

    List<Relationship> getByUidsIncludeDeleted(List<String> uids);
}
