package org.nmcpye.am.program;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.eventdatavalue.EventDataValue;
import org.nmcpye.am.i18n.I18nFormat;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service Interface for managing {@link ProgramStageInstance}.
 */
public interface ProgramStageInstanceServiceExt {
    String ID = ProgramStageServiceExt.class.getName();

    /**
     * Adds a {@link ProgramStageInstance}
     *
     * @param programStageInstance The ProgramStageInstance to add.
     * @return A generated unique id of the added {@link ProgramStageInstance}.
     */
    long addProgramStageInstance(ProgramStageInstance programStageInstance);

    /**
     * Adds a {@link ProgramStageInstance}
     *
     * @param programStageInstance The ProgramStageInstance to add.
     * @param user                 the current user.
     * @return A generated unique id of the added {@link ProgramStageInstance}.
     */
    long addProgramStageInstance(ProgramStageInstance programStageInstance, User user);

    /**
     * Soft deletes a {@link ProgramStageInstance}.
     */
    void deleteProgramStageInstance(ProgramStageInstance programStageInstance);

    /**
     * Updates a {@link ProgramStageInstance}.
     *
     * @param programStageInstance the ProgramStageInstance to update.
     */
    void updateProgramStageInstance(ProgramStageInstance programStageInstance);

    /**
     * Updates a {@link ProgramStageInstance}.
     *
     * @param programStageInstance the ProgramStageInstance to update.
     * @param user                 the current user.
     */
    void updateProgramStageInstance(ProgramStageInstance programStageInstance, User user);

    /**
     * Updates a last sync timestamp on specified ProgramStageInstances
     *
     * @param programStageInstanceUIDs UIDs of ProgramStageInstances where the
     *                                 lastSynchronized flag should be updated
     * @param lastSynchronized         The date of last successful sync
     */
    void updateProgramStageInstancesSyncTimestamp(List<String> programStageInstanceUIDs, Instant lastSynchronized);

    /**
     * Checks whether a {@link ProgramStageInstance} with the given identifier
     * exists. Doesn't take into account the deleted values.
     *
     * @param uid the identifier.
     */
    boolean programStageInstanceExists(String uid);

    /**
     * Checks whether a {@link ProgramStageInstance} with the given identifier
     * exists. Takes into accound also the deleted values.
     *
     * @param uid the identifier.
     */
    boolean programStageInstanceExistsIncludingDeleted(String uid);

    /**
     * Returns UIDs of existing ProgramStageInstances (including deleted) from
     * the provided UIDs
     *
     * @param uids PSI UIDs to check
     * @return Set containing UIDs of existing PSIs (including deleted)
     */
    List<String> getProgramStageInstanceUidsIncludingDeleted(List<String> uids);

    /**
     * Returns a {@link ProgramStageInstance}.
     *
     * @param id the id of the ProgramStageInstance to return.
     * @return the ProgramStageInstance with the given id.
     */
    ProgramStageInstance getProgramStageInstance(Long id);

    /**
     * Returns a List of {@link ProgramStageInstance}.
     *
     * @param uids a List of {@link ProgramStageInstance} uids
     * @return a List of {@link ProgramStageInstance} matching the provided uids
     */
    List<ProgramStageInstance> getProgramStageInstancesByUids(List<String> uids);

    /**
     * Returns the {@link ProgramStageInstance} with the given UID.
     *
     * @param uid the UID.
     * @return the ProgramStageInstance with the given UID, or null if no match.
     */
    ProgramStageInstance getProgramStageInstance(String uid);

    /**
     * Retrieve an event on a ProgramInstance and a ProgramStage. For repeatable
     * stages, the system returns the last event.
     *
     * @param programInstance the ProgramInstance.
     * @param programStage    the ProgramStage.
     * @return the ProgramStageInstance corresponding to the given
     * programInstance and ProgramStage, or null if no match.
     */
    ProgramStageInstance getProgramStageInstance(ProgramInstance programInstance, ProgramStage programStage);

    /**
     * Gets the number of ProgramStageInstances added since the given number of
     * days.
     *
     * @param days number of days.
     * @return the number of ProgramStageInstances.
     */
    long getProgramStageInstanceCount(int days);

    /**
     * Complete an event. Besides, program template messages will be sent if it
     * was defined for sending upon completion.
     *
     * @param programStageInstance the ProgramStageInstance.
     * @param skipNotifications    whether to send prgram stage notifications or
     *                             not.
     * @param format               the I18nFormat for the notification messages.
     * @param completedDate        the completedDate for the event. If null, the
     *                             current date is set as the completed date.
     */
    void completeProgramStageInstance(
        ProgramStageInstance programStageInstance,
        boolean skipNotifications,
        I18nFormat format,
        LocalDateTime completedDate
    );

    /**
     * Creates a program stage instance.
     *
     * @param programInstance  the ProgramInstance.
     * @param programStage     the ProgramStage.
     * @param enrollmentDate   the enrollment date.
     * @param incidentDate     date of the incident.
     * @param organisationUnit the OrganisationUnit where the event took place.
     * @return ProgramStageInstance the ProgramStageInstance which was created.
     */
    ProgramStageInstance createProgramStageInstance(
        ProgramInstance programInstance,
        ProgramStage programStage,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit organisationUnit
    );

    /**
     * Handles files for File EventDataValues and creates audit logs for the
     * upcoming changes. DOES NOT PERSIST the changes to the PSI object
     *
     * @param newDataValues        EventDataValues to add
     * @param updatedDataValues    EventDataValues to update
     * @param removedDataValues    EventDataValues to remove
     * @param dataElementsCache    DataElements cache map with DataElements
     *                             required for creating audit logs for changed EventDataValues
     * @param programStageInstance programStageInstance to which the
     *                             EventDataValues belongs to
     * @param singleValue          specifies whether the update is a single value update
     */
    void auditDataValuesChangesAndHandleFileDataValues(
        Set<EventDataValue> newDataValues,
        Set<EventDataValue> updatedDataValues,
        Set<EventDataValue> removedDataValues,
        Map<String, DataElement> dataElementsCache,
        ProgramStageInstance programStageInstance,
        boolean singleValue
    );

    /**
     * Validates EventDataValues, handles files for File EventDataValues and
     * creates audit logs for the upcoming create/save changes. DOES PERSIST the
     * changes to the PSI object.
     *
     * @param programStageInstance         the ProgramStageInstance that EventDataValues
     *                                     belong to
     * @param dataElementEventDataValueMap the map of DataElements and related
     *                                     EventDataValues to update
     */
    void saveEventDataValuesAndSaveProgramStageInstance(
        ProgramStageInstance programStageInstance,
        Map<DataElement, EventDataValue> dataElementEventDataValueMap
    );
}
