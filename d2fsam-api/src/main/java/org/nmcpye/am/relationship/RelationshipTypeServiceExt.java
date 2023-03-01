package org.nmcpye.am.relationship;

import java.util.List;

/**
 * Service Interface for managing {@link RelationshipType}.
 */
public interface RelationshipTypeServiceExt {
    /**
     * Adds an {@link RelationshipType}
     *
     * @param relationshipType The to RelationshipType add.
     * @return A generated unique id of the added {@link RelationshipType}.
     */
    Long addRelationshipType(RelationshipType relationshipType);

    /**
     * Deletes a {@link RelationshipType}.
     *
     * @param relationshipType the RelationshipType to delete.
     */
    void deleteRelationshipType(RelationshipType relationshipType);

    /**
     * Updates an {@link RelationshipType}.
     *
     * @param relationshipType the RelationshipType to update.
     */
    void updateRelationshipType(RelationshipType relationshipType);

    /**
     * Returns a {@link RelationshipType}.
     *
     * @param id the id of the RelationshipType to return.
     * @return the RelationshipType with the given id
     */
    RelationshipType getRelationshipType(Long id);

    /**
     * Returns a {@link RelationshipType}.
     *
     * @param uid the uid of the RelationshipType to return.
     * @return the RelationshipType with the given id
     */
    RelationshipType getRelationshipType(String uid);

    /**
     * Retrieve a relationship
     *
     * @param aIsToB The A side
     * @param bIsToA The B side
     * @return RelationshipType
     */
    RelationshipType getRelationshipType(String aIsToB, String bIsToA);

    /**
     * Returns all {@link RelationshipType}
     *
     * @return a collection of all RelationshipType, or an empty collection if
     * there are no RelationshipTypes.
     */
    List<RelationshipType> getAllRelationshipTypes();
}
