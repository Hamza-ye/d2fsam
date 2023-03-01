package org.nmcpye.am.relationship;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link RelationshipType}.
 */
@Service("org.nmcpye.am.relationship.RelationshipTypeServiceExt")
public class RelationshipTypeServiceExtImpl implements RelationshipTypeServiceExt {

    private final RelationshipTypeRepositoryExt relationshipTypeRepositoryExt;

    public RelationshipTypeServiceExtImpl(
        RelationshipTypeRepositoryExt relationshipTypeRepositoryExt) {
        this.relationshipTypeRepositoryExt = relationshipTypeRepositoryExt;
    }

    @Override
    @Transactional
    public void deleteRelationshipType(RelationshipType relationshipType) {
        relationshipTypeRepositoryExt.deleteObject(relationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> getAllRelationshipTypes() {
        return relationshipTypeRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getRelationshipType(Long id) {
        return relationshipTypeRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getRelationshipType(String uid) {
        return relationshipTypeRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional
    public Long addRelationshipType(RelationshipType relationshipType) {
        relationshipTypeRepositoryExt.saveObject(relationshipType);
        return relationshipType.getId();
    }

    @Override
    @Transactional
    public void updateRelationshipType(RelationshipType relationshipType) {
        relationshipTypeRepositoryExt.update(relationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getRelationshipType(String aIsToB, String bIsToA) {
        return relationshipTypeRepositoryExt.getRelationshipType(aIsToB, bIsToA);
    }

}
