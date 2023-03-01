package org.nmcpye.am.trackedentity;

import java.util.List;

/**
 * Service Interface for managing {@link TrackedEntityType}.
 */
public interface TrackedEntityTypeServiceExt {
    String ID = TrackedEntityTypeServiceExt.class.getName();

    /**
     * Adds an {@link TrackedEntityType}
     *
     * @param trackedEntityType The to TrackedEntityType add.
     * @return A generated unique id of the added {@link TrackedEntityType}.
     */
    long addTrackedEntityType(TrackedEntityType trackedEntityType);

    /**
     * Deletes a {@link TrackedEntityType}.
     *
     * @param trackedEntityType the TrackedEntityType to delete.
     */
    void deleteTrackedEntityType(TrackedEntityType trackedEntityType);

    /**
     * Updates a {@link TrackedEntityType}.
     *
     * @param trackedEntityType the TrackedEntityType to update.
     */
    void updateTrackedEntityType(TrackedEntityType trackedEntityType);

    /**
     * Returns a {@link TrackedEntityType}.
     *
     * @param id the id of the TrackedEntityType to return.
     * @return the TrackedEntityType with the given id
     */
    TrackedEntityType getTrackedEntityType(Long id);

    /**
     * Returns a {@link TrackedEntityType}.
     *
     * @param uid the identifier of the TrackedEntityType to return.
     * @return the TrackedEntityType with the given id
     */
    TrackedEntityType getTrackedEntityType(String uid);

    /**
     * Returns a {@link TrackedEntityType} with a given name.
     *
     * @param name the name of the TrackedEntityType to return.
     * @return the TrackedEntityType with the given name, or null if no match.
     */
    TrackedEntityType getTrackedEntityByName(String name);

    /**
     * Returns all {@link TrackedEntityType}
     *
     * @return a List of all TrackedEntityType, or an empty List if there are no
     * TrackedEntitys.
     */
    List<TrackedEntityType> getAllTrackedEntityType();
}
