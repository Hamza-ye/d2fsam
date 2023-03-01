package org.nmcpye.am.relationship;

import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.webapi.controller.event.webrequest.PagingAndSortingCriteriaAdapter;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Relationship}.
 */
public interface RelationshipServiceExt {
    String ID = RelationshipServiceExt.class.getName();

    boolean relationshipExists(String uid);

    /**
     * Adds an {@link Relationship}
     *
     * @param relationship the relationship.
     * @return id of the added relationship.
     */
    Long addRelationship(Relationship relationship);

    /**
     * Returns a {@link Relationship}.
     *
     * @param relationship the relationship.
     */
    void deleteRelationship(Relationship relationship);

    /**
     * Updates a {@link Relationship}.
     *
     * @param relationship the relationship.
     */
    void updateRelationship(Relationship relationship);

    /**
     * Returns a {@link Relationship}.
     *
     * @param id the id of the relationship to return.
     * @return the relationship with the given identifier.
     */
    Relationship getRelationship(Long id);

    /**
     * Checks if relationship for given UID exists (including deleted
     * relationships).
     *
     * @param uid Relationship UID to check for.
     * @return return true if relationship exists, false otherwise.
     */
    boolean relationshipExistsIncludingDeleted(String uid);

    /**
     * Fetches a {@link Relationship} based on a relationship identifying
     * attributes:
     * <p>
     * - relationship type - from - to
     *
     * @param relationship A valid Relationship
     * @return an Optional Relationship
     */
    Optional<Relationship> getRelationshipByRelationship(Relationship relationship);

    Relationship getRelationship(String uid);

    Relationship getRelationshipIncludeDeleted(String uid);

    List<Relationship> getRelationships(List<String> uids);

    default List<Relationship> getRelationshipsByTrackedEntityInstance(TrackedEntityInstance tei,
                                                                       boolean skipAccessValidation) {
        return getRelationshipsByTrackedEntityInstance(tei, null, skipAccessValidation);
    }

    List<Relationship> getRelationshipsByTrackedEntityInstance(TrackedEntityInstance tei,
                                                               PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter,
                                                               boolean skipAccessValidation);

    default List<Relationship> getRelationshipsByProgramInstance(ProgramInstance pi, boolean skipAccessValidation) {
        return getRelationshipsByProgramInstance(pi, null, skipAccessValidation);
    }

    List<Relationship> getRelationshipsByProgramInstance(ProgramInstance pi,
                                                         PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter, boolean skipAccessValidation);

    default List<Relationship> getRelationshipsByProgramStageInstance(ProgramStageInstance psi,
                                                                      boolean skipAccessValidation) {
        return getRelationshipsByProgramStageInstance(psi, null, skipAccessValidation);
    }

    List<Relationship> getRelationshipsByProgramStageInstance(ProgramStageInstance psi,
                                                              PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter, boolean skipAccessValidation);

    List<Relationship> getRelationshipsByRelationshipType(RelationshipType relationshipType);
}
