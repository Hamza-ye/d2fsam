package org.nmcpye.am.trackedentity;

import org.nmcpye.am.common.Grid;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.dxf2.events.event.EventContext;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Service Interface for managing {@link TrackedEntityInstance}.
 */
public interface TrackedEntityInstanceServiceExt {
    String ID = TrackedEntityInstanceServiceExt.class.getName();

    int ERROR_NONE = 0;

    int ERROR_DUPLICATE_IDENTIFIER = 1;

    int ERROR_ENROLLMENT = 2;

    String SEPARATOR = "_";

    /**
     * Returns a grid with tracked entity instance values based on the given
     * TrackedEntityInstanceQueryParams.
     *
     * @param params the TrackedEntityInstanceQueryParams.
     * @return a grid.
     */
    Grid getTrackedEntityInstancesGrid(TrackedEntityInstanceQueryParams params);

    /**
     * Returns a list with tracked entity instance values based on the given
     * TrackedEntityInstanceQueryParams.
     *
     * @param params                    the TrackedEntityInstanceQueryParams.
     * @param skipAccessValidation      If true, access validation is skipped. Should
     *                                  be set to true only for internal tasks (e.g. currently used by
     *                                  synchronization job)
     * @param skipSearchScopeValidation if true, search scope validation is
     *                                  skipped.
     * @return List of TEIs matching the params
     */
    List<TrackedEntityInstance> getTrackedEntityInstances(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    );

    /**
     * Returns a list tracked entity instance primary key ids based on the given
     * TrackedEntityInstanceQueryParams.
     *
     * @param params                    the TrackedEntityInstanceQueryParams.
     * @param skipAccessValidation      If true, access validation is skipped. Should
     *                                  be set to true only for internal tasks (e.g. currently used by
     *                                  synchronization job)
     * @param skipSearchScopeValidation if true, search scope validation is
     *                                  skipped.
     * @return List of TEI IDs matching the params
     */
    List<Long> getTrackedEntityInstanceIds(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    );

    /**
     * Return the count of the Tracked entity instances that meet the criteria
     * specified in params
     *
     * @param params                    Parameteres that specify searching criteria
     * @param skipAccessValidation      If true, the access validation is skipped
     * @param skipSearchScopeValidation If true, the search scope validation is
     *                                  skipped
     * @return the count of the Tracked entity instances that meet the criteria
     * specified in params
     */
    int getTrackedEntityInstanceCount(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    );

    /**
     * Decides whether current user is authorized to perform the given query.
     * IllegalQueryException is thrown if not.
     *
     * @param params the TrackedEntityInstanceQueryParams.
     */
    void decideAccess(TrackedEntityInstanceQueryParams params);

    /**
     * Validates scope of given TrackedEntityInstanceQueryParams. The params is
     * considered valid if no exception are thrown and the method returns
     * normally.
     *
     * @param params       the TrackedEntityInstanceQueryParams.
     * @param isGridSearch specifies whether search is made for a Grid response
     * @throws IllegalQueryException if the given params is invalid.
     */
    void validateSearchScope(TrackedEntityInstanceQueryParams params, boolean isGridSearch) throws IllegalQueryException;

    /**
     * Validates the given TrackedEntityInstanceQueryParams. The params is
     * considered valid if no exception are thrown and the method returns
     * normally.
     *
     * @param params the TrackedEntityInstanceQueryParams.
     * @throws IllegalQueryException if the given params is invalid.
     */
    void validate(TrackedEntityInstanceQueryParams params) throws IllegalQueryException;

    /**
     * Adds an {@link TrackedEntityInstance}
     *
     * @param entityInstance The to TrackedEntityInstance add.
     * @return A generated unique id of the added {@link TrackedEntityInstance}.
     */
    Long addTrackedEntityInstance(TrackedEntityInstance entityInstance);

    /**
     * Soft deletes a {@link TrackedEntityInstance}.
     *
     * @param entityInstance the TrackedEntityInstance to delete.
     */
    void deleteTrackedEntityInstance(TrackedEntityInstance entityInstance);

    /**
     * Updates a {@link TrackedEntityInstance}.
     *
     * @param entityInstance the TrackedEntityInstance to update.
     */
    void updateTrackedEntityInstance(TrackedEntityInstance entityInstance);

    void updateTrackedEntityInstance(TrackedEntityInstance instance, User user);

    /**
     * Updates a last sync timestamp on specified TrackedEntityInstances
     *
     * @param trackedEntityInstanceUIDs UIDs of Tracked entity instances where
     *                                  the lastSynchronized flag should be updated
     * @param lastSynchronized          The date of last successful sync
     */
    void updateTrackedEntityInstancesSyncTimestamp(List<String> trackedEntityInstanceUIDs, Instant lastSynchronized);

    void updateTrackedEntityInstanceLastUpdated(Set<String> trackedEntityInstanceUIDs, Instant lastUpdated);

    /**
     * Returns a {@link TrackedEntityInstance}.
     *
     * @param id the id of the TrackedEntityInstanceAttribute to return.
     * @return the TrackedEntityInstanceAttribute with the given id
     */
    TrackedEntityInstance getTrackedEntityInstance(Long id);

    /**
     * Returns the {@link org.nmcpye.am.domain.DataElement} TrackedEntityAttribute
     * with the given UID.
     *
     * @param uid the UID.
     * @return the TrackedEntityInstanceAttribute with the given UID, or null if
     * no match.
     */
    TrackedEntityInstance getTrackedEntityInstance(String uid);

    /**
     * Returns the {@link org.nmcpye.am.domain.DataElement} TrackedEntityAttribute
     * with the given UID.
     *
     * @param uid  the UID.
     * @param user User
     * @return the TrackedEntityInstanceAttribute with the given UID, or null if
     * no match.
     */
    TrackedEntityInstance getTrackedEntityInstance(String uid, User user);

    /**
     * Checks for the existence of a TEI by UID. Deleted values are not taken
     * into account.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    Boolean trackedEntityInstanceExists(String uid);

    /**
     * Checks for the existence of a TEI by UID. Takes into account also the
     * deleted values.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    Boolean trackedEntityInstanceExistsIncludingDeleted(String uid);

    /**
     * Returns UIDs of existing TrackedEntityInstances (including deleted) from
     * the provided UIDs
     *
     * @param uids TEI UIDs to check
     * @return Set containing UIDs of existing TEIs (including deleted)
     */
    List<String> getTrackedEntityInstancesUidsIncludingDeleted(List<String> uids);

    /**
     * Register a new entityInstance
     *
     * @param entityInstance  TrackedEntityInstance
     * @param attributeValues Set of attribute values
     * @return The error code after registering entityInstance
     */
    Long createTrackedEntityInstance(TrackedEntityInstance entityInstance, Set<TrackedEntityAttributeValue> attributeValues);

    List<TrackedEntityInstance> getTrackedEntityInstancesByUid(List<String> uids, User user);

    List<EventContext.TrackedEntityOuInfo> getTrackedEntityOuInfoByUid(List<String> uids, User user);
}
