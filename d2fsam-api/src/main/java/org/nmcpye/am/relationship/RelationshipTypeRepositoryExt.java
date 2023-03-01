package org.nmcpye.am.relationship;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the RelationshipType entity.
 */
//@Repository
//public interface RelationshipTypeRepositoryExt
//    extends RelationshipTypeRepositoryExtCustom, JpaRepository<RelationshipType, Long> {
//}

public interface RelationshipTypeRepositoryExt
    extends IdentifiableObjectStore<RelationshipType> {
    /**
     * Retrieves a relationship.
     *
     * @param aIsToB The A side
     * @param bIsToA The B side
     * @return RelationshipType
     */
    RelationshipType getRelationshipType(String aIsToB, String bIsToA);
}
