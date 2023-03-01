package org.nmcpye.am.relationship;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.webapi.controller.event.webrequest.PagingAndSortingCriteriaAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service Implementation for managing {@link Relationship}.
 */
@Service("org.nmcpye.am.relationship.RelationshipServiceExt")
@Slf4j
public class RelationshipServiceExtImpl implements RelationshipServiceExt {

    private final RelationshipRepositoryExt relationshipRepositoryExt;

    public RelationshipServiceExtImpl(RelationshipRepositoryExt relationshipRepositoryExt) {
        this.relationshipRepositoryExt = relationshipRepositoryExt;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteRelationship(Relationship relationship) {
        relationshipRepositoryExt.deleteObject(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public Relationship getRelationship(Long id) {
        return relationshipRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExists(String uid) {
        return relationshipRepositoryExt.getByUid(uid) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExistsIncludingDeleted(String uid) {
        return relationshipRepositoryExt.existsIncludingDeleted(uid);
    }

    @Override
    @Transactional
    public Long addRelationship(Relationship relationship) {
        relationship.getFrom().setRelationship(relationship);
        relationship.getTo().setRelationship(relationship);
        relationshipRepositoryExt.saveObject(relationship);

        return relationship.getId();
    }

    @Override
    @Transactional
    public void updateRelationship(Relationship relationship) {
        // TODO: Do we need next 2 lines? relationship never changes during
        // update
        relationship.getFrom().setRelationship(relationship);
        relationship.getTo().setRelationship(relationship);
        relationshipRepositoryExt.update(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public Relationship getRelationship(String uid) {
        return relationshipRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public Relationship getRelationshipIncludeDeleted(String uid) {
        return relationshipRepositoryExt.getByUidsIncludeDeleted(List.of(uid))
            .stream()
            .findAny()
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getRelationships(List<String> uids) {
        return relationshipRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getRelationshipsByTrackedEntityInstance(TrackedEntityInstance tei,
                                                                      PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter,
                                                                      boolean skipAccessValidation) {
        return relationshipRepositoryExt.getByTrackedEntityInstance(tei, pagingAndSortingCriteriaAdapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getRelationshipsByProgramInstance(ProgramInstance pi,
                                                                PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter,
                                                                boolean skipAccessValidation) {
        return relationshipRepositoryExt.getByProgramInstance(pi, pagingAndSortingCriteriaAdapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getRelationshipsByProgramStageInstance(ProgramStageInstance psi,
                                                                     PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter,
                                                                     boolean skipAccessValidation) {
        return relationshipRepositoryExt.getByProgramStageInstance(psi, pagingAndSortingCriteriaAdapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getRelationshipsByRelationshipType(RelationshipType relationshipType) {
        return relationshipRepositoryExt.getByRelationshipType(relationshipType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Relationship> getRelationshipByRelationship(Relationship relationship) {
        checkNotNull(relationship.getFrom());
        checkNotNull(relationship.getTo());
        checkNotNull(relationship.getRelationshipType());

        return Optional.ofNullable(relationshipRepositoryExt.getByRelationship(relationship));
    }
}
