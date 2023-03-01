package org.nmcpye.am.program;

import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service Interface for managing {@link ProgramInstance}.
 */
public interface ProgramInstanceServiceExt {
    String ID = ProgramInstanceServiceExt.class.getName();

    /**
     * Adds an {@link ProgramInstance}
     *
     * @param programInstance The to ProgramInstance add.
     * @return A generated unique id of the added {@link ProgramInstance}.
     */
    Long addProgramInstance(ProgramInstance programInstance);

    /**
     * Adds an {@link ProgramInstance}
     *
     * @param programInstance The to ProgramInstance add.
     * @param user            the current user.
     * @return A generated unique id of the added {@link ProgramInstance}.
     */
    Long addProgramInstance(ProgramInstance programInstance, User user);

    /**
     * Soft deletes a {@link ProgramInstance}.
     *
     * @param programInstance the ProgramInstance to delete.
     */
    void deleteProgramInstance(ProgramInstance programInstance);

    /**
     * Hard deletes a {@link ProgramInstance}.
     *
     * @param programInstance the ProgramInstance to delete.
     */
    void hardDeleteProgramInstance(ProgramInstance programInstance);

    /**
     * Updates an {@link ProgramInstance}.
     *
     * @param programInstance the ProgramInstance to update.
     */
    void updateProgramInstance(ProgramInstance programInstance);

    void updateProgramInstance(ProgramInstance programInstance, User user);

    /**
     * Returns a {@link ProgramInstance}.
     *
     * @param id the id of the ProgramInstance to return.
     * @return the ProgramInstance with the given id
     */
    ProgramInstance getProgramInstance(Long id);

    /**
     * Returns the {@link ProgramInstance} with the given UID.
     *
     * @param uid the UID.
     * @return the ProgramInstance with the given UID, or null if no match.
     */
    ProgramInstance getProgramInstance(String uid);

    /**
     * Returns a list of existing ProgramInstances from the provided UIDs
     *
     * @param uids PSI UIDs to check
     * @return ProgramInstance list
     */
    List<ProgramInstance> getProgramInstances(List<String> uids);

    /**
     * Checks for the existence of a PI by UID. Deleted values are not taken
     * into account.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    boolean programInstanceExists(String uid);

    /**
     * Checks for the existence of a PI by UID. Takes into account also the
     * deleted values.
     *
     * @param uid PSI UID to check for
     * @return true/false depending on result
     */
    boolean programInstanceExistsIncludingDeleted(String uid);

    /**
     * Returns UIDs of existing ProgramInstances (including deleted) from the
     * provided UIDs
     *
     * @param uids PSI UIDs to check
     * @return Set containing UIDs of existing PSIs (including deleted)
     */
    List<String> getProgramInstancesUidsIncludingDeleted(List<String> uids);

    /**
     * Returns a list with program instance values based on the given
     * ProgramInstanceQueryParams.
     *
     * @param params the ProgramInstanceQueryParams.
     * @return List of PIs matching the params
     */
    List<ProgramInstance> getProgramInstances(ProgramInstanceQueryParams params);

    /**
     * Returns the number of program instance matches based on the given
     * ProgramInstanceQueryParams.
     *
     * @param params the ProgramInstanceQueryParams.
     * @return Number of PIs matching the params
     */
    int countProgramInstances(ProgramInstanceQueryParams params);

    /**
     * Decides whether current user is authorized to perform the given query.
     * IllegalQueryException is thrown if not.
     *
     * @param params the ProgramInstanceQueryParams.
     */
    void decideAccess(ProgramInstanceQueryParams params);

    /**
     * Validates the given ProgramInstanceQueryParams. The params is considered
     * valid if no exception are thrown and the method returns normally.
     *
     * @param params the ProgramInstanceQueryParams.
     * @throws IllegalQueryException if the given params is invalid.
     */
    void validate(ProgramInstanceQueryParams params) throws IllegalQueryException;

    /**
     * Retrieve program instances on a program
     *
     * @param program Program
     * @return ProgramInstance list
     */
    List<ProgramInstance> getProgramInstances(Program program);

    /**
     * Retrieve program instances on a program by status
     *
     * @param program Program
     * @param status  Status of program-instance, include STATUS_ACTIVE,
     *                STATUS_COMPLETED and STATUS_CANCELLED
     * @return ProgramInstance list
     */
    List<ProgramInstance> getProgramInstances(Program program, ProgramStatus status);

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
    List<ProgramInstance> getProgramInstances(TrackedEntityInstance entityInstance, Program program, ProgramStatus status);

    /**
     * Enroll a TrackedEntityInstance into a program. Must be run inside a
     * transaction.
     *
     * @param trackedEntityInstance TrackedEntityInstance
     * @param program               Program
     * @param enrollmentDate        The date of enrollment
     * @param incidentDate          The date of incident
     * @param orgunit               Organisation Unit
     * @param uid                   UID to use for new instance
     * @return ProgramInstance
     */
    ProgramInstance enrollTrackedEntityInstance(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit orgunit,
        String uid
    );

    /**
     * Enroll a TrackedEntityInstance into a program. Must be run inside a
     * transaction.
     *
     * @param trackedEntityInstance TrackedEntityInstance
     * @param program               Program
     * @param enrollmentDate        The date of enrollment
     * @param incidentDate          The date of incident
     * @param orgunit               Organisation Unit
     * @return ProgramInstance
     */
    ProgramInstance enrollTrackedEntityInstance(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit orgunit
    );

    /**
     * Check a program instance if it can be completed automatically. If there
     * is some event of this program-isntance uncompleted or this program has
     * any repeatable stage, then this program cannot be completed automatically
     *
     * @param programInstance ProgramInstance
     * @return True/False value
     */
    boolean canAutoCompleteProgramInstanceStatus(ProgramInstance programInstance);

    /**
     * Complete a program instance. Besides, program template messages will be
     * send if it was defined to send when to complete this program
     *
     * @param programInstance ProgramInstance
     */
    void completeProgramInstanceStatus(ProgramInstance programInstance);

    /**
     * Set status as skipped for overdue events; Remove scheduled events
     *
     * @param programInstance ProgramInstance
     */
    void cancelProgramInstanceStatus(ProgramInstance programInstance);

    /**
     * Incomplete a program instance. This is is possible only if there is no
     * other program instance with active status.
     *
     * @param programInstance ProgramInstance
     */
    void incompleteProgramInstanceStatus(ProgramInstance programInstance);

    /**
     * Prepare a ProgramInstance for storing
     *
     * @param trackedEntityInstance TrackedEntityInstance
     * @param program               Program
     * @param programStatus         ProgramStatus
     * @param enrollmentDate        The date of enrollment
     * @param incidentDate          The date of incident
     * @param orgUnit               Organisation Unit
     * @param uid                   UID to use for new instance
     * @return ProgramInstance
     */
    ProgramInstance prepareProgramInstance(
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        ProgramStatus programStatus,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit orgUnit,
        String uid
    );

    /**
     * Prepare a ProgramInstance for storing
     *
     * @param activity              TrackedEntityInstance
     * @param trackedEntityInstance TrackedEntityInstance
     * @param program               Program
     * @param programStatus         ProgramStatus
     * @param enrollmentDate        The date of enrollment
     * @param incidentDate          The date of incident
     * @param orgUnit               Organisation Unit
     * @param uid                   UID to use for new instance
     * @return ProgramInstance
     */
    ProgramInstance prepareProgramInstanceWithActivity(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        ProgramStatus programStatus,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit orgUnit,
        String uid
    );
}
