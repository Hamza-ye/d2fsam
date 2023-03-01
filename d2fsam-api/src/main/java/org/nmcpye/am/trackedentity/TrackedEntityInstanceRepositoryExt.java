package org.nmcpye.am.trackedentity;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.dxf2.events.event.EventContext;
import org.nmcpye.am.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TrackedEntityInstanceRepositoryExt
    extends IdentifiableObjectStore<TrackedEntityInstance> {
    String ID = TrackedEntityInstanceRepositoryExt.class.getName();

    List<TrackedEntityInstance> getTrackedEntityInstances(TrackedEntityInstanceQueryParams params);

    List<Long> getTrackedEntityInstanceIds(TrackedEntityInstanceQueryParams params);

    List<Map<String, String>> getTrackedEntityInstancesGrid(TrackedEntityInstanceQueryParams params);

    int getTrackedEntityInstanceCountForGrid(TrackedEntityInstanceQueryParams params);

    int getTrackedEntityInstanceCountForGridWithMaxTeiLimit(TrackedEntityInstanceQueryParams params);

    /**
     * Set lastSynchronized timestamp to provided timestamp for provided TEIs
     *
     * @param trackedEntityInstanceUIDs UIDs of Tracked entity instances where
     *                                  the lastSynchronized flag should be updated
     * @param lastSynchronized          The date of last successful sync
     */
    void updateTrackedEntityInstancesSyncTimestamp(List<String> trackedEntityInstanceUIDs, Instant lastSynchronized);

    void updateTrackedEntityInstancesLastUpdated(Set<String> trackedEntityInstanceUIDs, Instant lastUpdated);

    List<TrackedEntityInstance> getTrackedEntityInstancesByUid(List<String> uids, User user);

    List<EventContext.TrackedEntityOuInfo> getTrackedEntityOuInfoByUid(List<String> uids, User user);

    /**
     * Returns UIDs of existing TrackedEntityInstances (including deleted) from
     * the provided UIDs
     *
     * @param uids TEI UIDs to check
     * @return List containing UIDs of existing TEIs (including deleted)
     */
    List<String> getUidsIncludingDeleted( List<String> uids );

    /**
     * Fetches TrackedEntityInstances matching the given list of UIDs
     *
     * @param uids a List of UID
     * @return a List containing the TrackedEntityInstances matching the given
     * parameters list
     */
    List<TrackedEntityInstance> getIncludingDeleted(List<String> uids);

    /**
     * Checks for the existence of a TEI by UID. Deleted TEIs are not taken into
     * account.
     *
     * @param uid PSI UID to check for.
     * @return true/false depending on result.
     */
    boolean exists(String uid);

    /**
     * Checks for the existence of a TEI by UID. Takes into account also the
     * deleted TEIs.
     *
     * @param uid PSI UID to check for.
     * @return true/false depending on result.
     */
    boolean existsIncludingDeleted(String uid);
}
