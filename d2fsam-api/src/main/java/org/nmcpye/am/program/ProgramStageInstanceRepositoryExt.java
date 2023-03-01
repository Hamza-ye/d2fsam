package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.EventStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Spring Data JPA repository for the ProgramStageInstance entity.
 */
//@Repository
//public interface ProgramStageInstanceRepositoryExt
//    extends ProgramStageInstanceRepositoryExtCustom, JpaRepository<ProgramStageInstance, Long> {
//}
public interface ProgramStageInstanceRepositoryExt
    extends IdentifiableObjectStore<ProgramStageInstance> {
    String ID = ProgramStageInstanceRepositoryExt.class.getName();

    /**
     * Retrieve an event on a program instance and a program stage. For
     * repeatable stage, the system returns the last event
     *
     * @param programInstance ProgramInstance
     * @param programStage    ProgramStage
     * @return ProgramStageInstance
     */
    ProgramStageInstance get(ProgramInstance programInstance, ProgramStage programStage);

    /**
     * Checks for the existence of a PSI by UID. The deleted PSIs are not taken
     * into account.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    boolean exists(String uid);

    /**
     * Checks for the existence of a PSI by UID. It takes into account also the
     * deleted PSIs.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    boolean existsIncludingDeleted(String uid);

    /**
     * Returns UIDs of existing ProgramStageInstances (including deleted) from
     * the provided UIDs
     *
     * @param uids PSI UIDs to check
     * @return List containing UIDs of existing PSIs (including deleted)
     */
    List<String> getUidsIncludingDeleted(List<String> uids);

    /**
     * Fetches ProgramStageInstance matching the given list of UIDs
     *
     * @param uids a List of UID
     * @return a List containing the ProgramStageInstance matching the given
     * parameters list
     */
    List<ProgramStageInstance> getIncludingDeleted(List<String> uids);

    /**
     * Retrieve an event list on program instance list with a certain status
     *
     * @param programInstances ProgramInstance list
     * @param status           EventStatus
     * @return ProgramStageInstance list
     */
    List<ProgramStageInstance> get(Collection<ProgramInstance> programInstances, EventStatus status);

    /**
     * Get the number of ProgramStageInstances updates since the given Date.
     *
     * @param time the time.
     * @return the number of ProgramStageInstances.
     */
    long getProgramStageInstanceCountLastUpdatedAfter(Date time);

    //    /**
    //     * Get all ProgramStageInstances which have notifications with the given
    //     * ProgramNotificationTemplate scheduled on the given date.
    //     *
    //     * @param template the template.
    //     * @param notificationDate the Date for which the notification is scheduled.
    //     * @return a list of ProgramStageInstance.
    //     */
    //    List<ProgramStageInstance> getWithScheduledNotifications( ProgramNotificationTemplate template,
    //                                                              Date notificationDate );

    /**
     * Set lastSynchronized timestamp to provided timestamp for provided PSIs
     *
     * @param programStageInstanceUIDs UIDs of ProgramStageInstances where the
     *                                 lastSynchronized flag should be updated
     * @param lastSynchronized         The date of last successful sync
     */
    void updateProgramStageInstancesSyncTimestamp(List<String> programStageInstanceUIDs, Instant lastSynchronized);
}
