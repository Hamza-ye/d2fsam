package org.nmcpye.am.trackedentity;

import org.nmcpye.am.common.GenericStore;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the TrackedEntityProgramOwner entity.
 */
public interface TrackedEntityProgramOwnerRepositoryExt
    extends GenericStore<TrackedEntityProgramOwner> {
    String ID = TrackedEntityProgramOwnerRepositoryExt.class.getName();

    /**
     * Get tracked entity program owner entity for the tei-program combination.
     *
     * @param teiId     The tracked entity instance id.
     * @param programId the program id
     * @return matching tracked entity program owner entity
     */
    TrackedEntityProgramOwner getTrackedEntityProgramOwner(Long teiId, Long programId);

    /**
     * Get all Tracked entity program owner entities for the list of teis.
     *
     * @param teiIds The list of tracked entity instance ids.
     * @return matching tracked entity program owner entities.
     */
    List<TrackedEntityProgramOwner> getTrackedEntityProgramOwners(List<Long> teiIds);

    /**
     * Get all Tracked entity program owner entities for the list of teis and
     * program.
     *
     * @param teiIds    The list of tracked entity instance ids.
     * @param programId The program id
     * @return matching tracked entity program owner entities.
     */
    List<TrackedEntityProgramOwner> getTrackedEntityProgramOwners(List<Long> teiIds, Long programId);

    List<TrackedEntityProgramOwnerIds> getTrackedEntityProgramOwnersUids(List<Long> teiIds, Long programId);

    List<TrackedEntityProgramOwnerOrgUnit> getTrackedEntityProgramOwnerOrgUnits(Set<Long> teiIds);
}
