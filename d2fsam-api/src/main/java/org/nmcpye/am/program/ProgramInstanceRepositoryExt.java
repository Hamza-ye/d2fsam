package org.nmcpye.am.program;

import org.apache.commons.lang3.tuple.Pair;
import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import java.util.List;

///**
// * Spring Data JPA repository for the ProgramInstance entity.
// */
//@Repository
//public interface ProgramInstanceRepositoryExt
//    extends ProgramInstanceRepositoryExtCustom,
//    JpaRepository<ProgramInstance, Long> {
//}

public interface ProgramInstanceRepositoryExt
    extends IdentifiableObjectStore<ProgramInstance> {
    String ID = ProgramInstanceRepositoryExt.class.getName();

    /**
     * Count all program instances by PI query params.
     *
     * @param params ProgramInstanceQueryParams to use
     * @return Count of matching PIs
     */
    int countProgramInstances(ProgramInstanceQueryParams params);

    /**
     * Get all program instances by PI query params.
     *
     * @param params ProgramInstanceQueryParams to use
     * @return PIs matching params
     */
    List<ProgramInstance> getProgramInstances(ProgramInstanceQueryParams params);

    /**
     * Retrieve program instances on a program
     *
     * @param program Program
     * @return ProgramInstance list
     */
    List<ProgramInstance> get(Program program);

    /**
     * Retrieve program instances on a program by status
     *
     * @param program Program
     * @param status  Status of program-instance, include STATUS_ACTIVE,
     *                STATUS_COMPLETED and STATUS_CANCELLED
     * @return ProgramInstance list
     */
    List<ProgramInstance> get(Program program, ProgramStatus status);

    /**
     * Retrieve program instances on a TrackedEntityInstance with a status by a
     * program
     *
     * @param entityInstance TrackedEntityInstance
     * @param program        Program
     * @param status         Status of program-instance, include STATUS_ACTIVE,
     *                       STATUS_COMPLETED and STATUS_CANCELLED
     * @return ProgramInstance list
     */
    List<ProgramInstance> get(TrackedEntityInstance entityInstance, Program program, ProgramStatus status);

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
     * Returns UIDs of existing ProgramInstances (including deleted) from the
     * provided UIDs
     *
     * @param uids PI UIDs to check
     * @return List containing UIDs of existing PIs (including deleted)
     */
    List<String> getUidsIncludingDeleted(List<String> uids);

    /**
     * Fetches ProgramInstances matching the given list of UIDs
     *
     * @param uids a List of UID
     * @return a List containing the ProgramInstances matching the given
     * parameters list
     */
    List<ProgramInstance> getIncludingDeleted(List<String> uids);

    //    /**
    //     * Get all ProgramInstances which have notifications with the given
    //     * ProgramNotificationTemplate scheduled on the given date.
    //     *
    //     * @param template the template.
    //     * @param notificationDate the Date for which the notification is scheduled.
    //     * @return a list of ProgramInstance.
    //     */
    //    List<ProgramInstance> getWithScheduledNotifications( ProgramNotificationTemplate template, Date notificationDate );

    /**
     * Return all program instance linked to programs.
     *
     * @param programs Programs to fetch by
     * @return List of all PIs that that are linked to programs
     */
    List<ProgramInstance> getByPrograms(List<Program> programs);

    //    /**
    //     * Return all program instance by type.
    //     * <p>
    //     * Warning: this is meant to be used for WITHOUT_REGISTRATION programs only,
    //     * be careful if you need it for other uses.
    //     *
    //     * @param type ProgramType to fetch by
    //     * @return List of all PIs that matches the wanted type
    //     */
    //    List<ProgramInstance> getByType( ProgramType type );

    /**
     * Hard deletes a {@link ProgramInstance}.
     *
     * @param programInstance the ProgramInstance to delete.
     */
    void hardDelete(ProgramInstance programInstance);

    /**
     * Executes a query to fetch all {@see ProgramInstance} matching the
     * Program/TrackedEntityInstance list.
     * <p>
     * Resulting SQL query:
     *
     * <pre>
     * {@code
     *  select id, programid, trackedentityinstanceid
     *      from program_instance
     *      where (programid = 726 and trackedentityinstanceid = 19 and status = 'ACTIVE')
     *         or (programid = 726 and trackedentityinstanceid = 18 and status = 'ACTIVE')
     *         or (programid = 726 and trackedentityinstanceid = 17 and status = 'ACTIVE')
     * }
     * </pre>
     *
     * @param programTeiPair a List of Pair, where the left side is a
     *                       {@see Program} and the right side is a
     *                       {@see TrackedEntityInstance}
     * @param programStatus  filter on the status of all the Program
     * @return a List of {@see ProgramInstance}
     */
    List<ProgramInstance> getByProgramAndTrackedEntityInstance(
        List<Pair<Program, TrackedEntityInstance>> programTeiPair,
        ProgramStatus programStatus
    );
}
