package org.nmcpye.am.program;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.eventdatavalue.EventDataValue;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.i18n.I18nFormat;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.security.SecurityUtils;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAudit;
import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.nmcpye.am.external.conf.ConfigurationKey.CHANGELOG_TRACKER;

/**
 * Service Implementation for managing {@link ProgramStageInstance}.
 */
@Service("org.nmcpye.am.program.ProgramStageInstanceServiceExt")
@Slf4j
public class ProgramStageInstanceServiceExtImpl
    implements ProgramStageInstanceServiceExt {

    private final ProgramStageInstanceRepositoryExt programStageInstanceStore;

    private final ProgramInstanceServiceExt programInstanceService;

    private final CurrentUserService currentUserService;

    private final TrackedEntityDataValueAuditServiceExt dataValueAuditService;

    private final FileResourceServiceExt fileResourceService;

    private final AmConfigurationProvider config;

    public ProgramStageInstanceServiceExtImpl(
        ProgramStageInstanceRepositoryExt programStageInstanceStore,
        ProgramInstanceServiceExt programInstanceService,
        CurrentUserService currentUserService,
        TrackedEntityDataValueAuditServiceExt dataValueAuditService,
        FileResourceServiceExt fileResourceService,
        AmConfigurationProvider config) {
        this.programStageInstanceStore = programStageInstanceStore;
        this.programInstanceService = programInstanceService;
        this.currentUserService = currentUserService;
        this.dataValueAuditService = dataValueAuditService;
        this.fileResourceService = fileResourceService;
        this.config = config;
    }

    @Override
    public long addProgramStageInstance(ProgramStageInstance programStageInstance) {
        programStageInstance.setAutoFields();
        programStageInstanceStore.saveObject(programStageInstance);
        return programStageInstance.getId();
    }

    @Override
    @Transactional
    public long addProgramStageInstance(ProgramStageInstance programStageInstance, User user) {
        programStageInstance.setAutoFields();
        programStageInstanceStore.save(programStageInstance, user);
        return programStageInstance.getId();
    }

    @Override
    @Transactional
    public void deleteProgramStageInstance(ProgramStageInstance programStageInstance) {
        programStageInstanceStore.deleteObject(programStageInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStageInstance getProgramStageInstance(Long id) {
        return programStageInstanceStore.get(id);
    }

    @Override
    public List<ProgramStageInstance> getProgramStageInstancesByUids(List<String> uids) {
        return programStageInstanceStore.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStageInstance getProgramStageInstance(String uid) {
        return programStageInstanceStore.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStageInstance getProgramStageInstance(ProgramInstance programInstance, ProgramStage programStage) {
        return programStageInstanceStore.get(programInstance, programStage);
    }

    @Override
    @Transactional
    public void updateProgramStageInstance(ProgramStageInstance programStageInstance) {
        programStageInstance.setAutoFields();
        programStageInstanceStore.update(programStageInstance);
    }

    @Override
    @Transactional
    public void updateProgramStageInstance(ProgramStageInstance programStageInstance, User user) {
        programStageInstance.setAutoFields();
        programStageInstanceStore.update(programStageInstance, user);
    }

    @Override
    @Transactional
    public void updateProgramStageInstancesSyncTimestamp(List<String> programStageInstanceUIDs, Instant lastSynchronized) {
        programStageInstanceStore.updateProgramStageInstancesSyncTimestamp(programStageInstanceUIDs, lastSynchronized);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean programStageInstanceExists(String uid) {
        return programStageInstanceStore.exists(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean programStageInstanceExistsIncludingDeleted(String uid) {
        return programStageInstanceStore.existsIncludingDeleted(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProgramStageInstanceUidsIncludingDeleted(List<String> uids) {
        return programStageInstanceStore.getUidsIncludingDeleted(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProgramStageInstanceCount(int days) {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add(Calendar.DAY_OF_YEAR, (days * -1));

        return programStageInstanceStore.getProgramStageInstanceCountLastUpdatedAfter(cal.getTime());
    }

    @Override
    @Transactional
    public void completeProgramStageInstance(
        ProgramStageInstance programStageInstance,
        boolean skipNotifications,
        I18nFormat format,
        LocalDateTime completedDate
    ) {
        Calendar today = Calendar.getInstance();
        PeriodType.clearTimeOfDay(today);
        LocalDateTime todayDate = DateUtils.localDateTimeFromDate(today.getTime());

        programStageInstance.setStatus(EventStatus.COMPLETED);

        if (completedDate == null) {
            programStageInstance.setCompletedDate(todayDate);
        } else {
            programStageInstance.setCompletedDate(completedDate);
        }
        if (StringUtils.isEmpty(programStageInstance.getCompletedBy())) {
            programStageInstance.setCompletedBy(SecurityUtils.getCurrentUserLogin().get());
        }

        // ---------------------------------------------------------------------
        // Update the event
        // ---------------------------------------------------------------------

        updateProgramStageInstance(programStageInstance);

        // ---------------------------------------------------------------------
        // Check Completed status for all of ProgramStageInstance of
        // ProgramInstance
        // ---------------------------------------------------------------------

        //        if (programStageInstance.getProgramInstance().getProgram().isRegistration()) {
        boolean canComplete = programInstanceService.canAutoCompleteProgramInstanceStatus(programStageInstance.getProgramInstance());

        if (canComplete) {
            programInstanceService.completeProgramInstanceStatus(programStageInstance.getProgramInstance());
        }
        //        }
    }

    @Override
    @Transactional
    public ProgramStageInstance createProgramStageInstance(
        ProgramInstance programInstance,
        ProgramStage programStage,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit organisationUnit
    ) {
        ProgramStageInstance programStageInstance = null;
        Date currentDate = new Date();
        LocalDateTime dateCreatedEvent;

        if (programStage.getGeneratedByEnrollmentDate()) {
            dateCreatedEvent = enrollmentDate;
        } else {
            dateCreatedEvent = incidentDate;
        }

        Date dueDate = DateUtils
            .getDateAfterAddition(DateUtils.fromLocalDateTime(dateCreatedEvent),
                programStage.getMinDaysFromStart());

        if (!programInstance.getProgram().getIgnoreOverDueEvents() || dueDate.before(currentDate)) {
            programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance(programInstance);
            programStageInstance.setProgramStage(programStage);
            programStageInstance.setOrganisationUnit(organisationUnit);
            programStageInstance.setDueDate(DateUtils.localDateTimeFromDate(dueDate));
            programStageInstance.setStatus(EventStatus.SCHEDULE);

            if (
                programStage.getOpenAfterEnrollment() ||
                    /*|| programInstance.getProgram().isWithoutRegistration()*/programStage.getPeriodType() != null
            ) {
                programStageInstance.setExecutionDate(DateUtils.localDateTimeFromDate(dueDate));
                programStageInstance.setStatus(EventStatus.ACTIVE);
            }

            addProgramStageInstance(programStageInstance);
        }

        return programStageInstance;
    }

    @Override
    @Transactional
    public void auditDataValuesChangesAndHandleFileDataValues(
        Set<EventDataValue> newDataValues,
        Set<EventDataValue> updatedDataValues,
        Set<EventDataValue> removedDataValues,
        Map<String, DataElement> dataElementsCache,
        ProgramStageInstance programStageInstance,
        boolean singleValue
    ) {
        Set<EventDataValue> updatedOrNewDataValues = Sets.union(newDataValues, updatedDataValues);

        if (singleValue) {
            // If it is only a single value update, I don't won't to miss the
            // values that
            // are missing in the payload but already present in the DB
            Set<EventDataValue> changedDataValues = Sets.union(updatedOrNewDataValues, removedDataValues);
            Set<EventDataValue> unchangedDataValues = Sets.difference(programStageInstance.getEventDataValues(), changedDataValues);

            programStageInstance.setEventDataValues(Sets.union(unchangedDataValues, updatedOrNewDataValues));
        } else {
            programStageInstance.setEventDataValues(updatedOrNewDataValues);
        }

        auditDataValuesChanges(newDataValues, updatedDataValues, removedDataValues, dataElementsCache, programStageInstance);
        handleFileDataValueChanges(newDataValues, updatedDataValues, removedDataValues, dataElementsCache);
    }

    @Override
    @Transactional
    public void saveEventDataValuesAndSaveProgramStageInstance(
        ProgramStageInstance programStageInstance,
        Map<DataElement, EventDataValue> dataElementEventDataValueMap
    ) {
        validateEventDataValues(dataElementEventDataValueMap);
        Set<EventDataValue> eventDataValues = new HashSet<>(dataElementEventDataValueMap.values());
        programStageInstance.setEventDataValues(eventDataValues);
        addProgramStageInstance(programStageInstance);

        for (Map.Entry<DataElement, EventDataValue> entry : dataElementEventDataValueMap.entrySet()) {
            entry.getValue().setAutoFields();
            createAndAddAudit(entry.getValue(), entry.getKey(), programStageInstance, AuditType.CREATE);
            handleFileDataValueSave(entry.getValue(), entry.getKey());
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private String validateEventDataValue(DataElement dataElement, EventDataValue eventDataValue) {
        if (StringUtils.isEmpty(eventDataValue.getStoredBy())) {
            return "Stored by is null or empty";
        }

        if (StringUtils.isEmpty(eventDataValue.getDataElement())) {
            return "Data element is null or empty";
        }

        if (!dataElement.getUid().equals(eventDataValue.getDataElement())) {
            throw new IllegalQueryException(
                "DataElement " +
                    dataElement.getUid() +
                    " assigned to EventDataValues does not match with one EventDataValue: " +
                    eventDataValue.getDataElement()
            );
        }

        String result = ValidationUtils.dataValueIsValid(eventDataValue.getValue(), dataElement.getValueType());

        return result == null ? null : "Value is not valid:  " + result;
    }

    private void validateEventDataValues(Map<DataElement, EventDataValue> dataElementEventDataValueMap) {
        String result;
        for (Map.Entry<DataElement, EventDataValue> entry : dataElementEventDataValueMap.entrySet()) {
            result = validateEventDataValue(entry.getKey(), entry.getValue());
            if (result != null) {
                throw new IllegalQueryException(result);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Audit
    // -------------------------------------------------------------------------

    private void auditDataValuesChanges(
        Set<EventDataValue> newDataValues,
        Set<EventDataValue> updatedDataValues,
        Set<EventDataValue> removedDataValues,
        Map<String, DataElement> dataElementsCache,
        ProgramStageInstance programStageInstance
    ) {
        newDataValues.forEach(dv ->
            createAndAddAudit(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null), programStageInstance, AuditType.CREATE)
        );
        updatedDataValues.forEach(dv ->
            createAndAddAudit(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null), programStageInstance, AuditType.UPDATE)
        );
        removedDataValues.forEach(dv ->
            createAndAddAudit(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null), programStageInstance, AuditType.DELETE)
        );
    }

    private void createAndAddAudit(
        EventDataValue dataValue,
        DataElement dataElement,
        ProgramStageInstance programStageInstance,
        AuditType auditType) {
        if (!config.isEnabled(CHANGELOG_TRACKER) || dataElement == null) {
            return;
        }

        TrackedEntityDataValueAudit dataValueAudit = new TrackedEntityDataValueAudit(dataElement, programStageInstance,
            dataValue.getValue(), dataValue.getStoredBy(), dataValue.getProvidedElsewhere(), auditType);

        dataValueAuditService.addTrackedEntityDataValueAudit(dataValueAudit);
    }

    // -------------------------------------------------------------------------
    // File data values
    // -------------------------------------------------------------------------

    private void handleFileDataValueChanges(
        Set<EventDataValue> newDataValues,
        Set<EventDataValue> updatedDataValues,
        Set<EventDataValue> removedDataValues,
        Map<String, DataElement> dataElementsCache
    ) {
        removedDataValues.forEach(dv -> handleFileDataValueDelete(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null)));
        updatedDataValues.forEach(dv -> handleFileDataValueUpdate(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null)));
        newDataValues.forEach(dv -> handleFileDataValueSave(dv, dataElementsCache.getOrDefault(dv.getDataElement(), null)));
    }

    private void handleFileDataValueUpdate(EventDataValue dataValue, DataElement dataElement) {
        if (dataElement == null) {
            return;
        }
        String previousFileResourceUid = dataValue.getAuditValue();

        if (previousFileResourceUid == null || previousFileResourceUid.equals(dataValue.getValue())) {
            return;
        }

        FileResource fileResource = fetchFileResource(dataValue, dataElement);

        if (fileResource == null) {
            return;
        }

        fileResourceService.deleteFileResource(previousFileResourceUid);

        setAssigned(fileResource);
    }

    /**
     * Update FileResource with 'assigned' status.
     */
    private void handleFileDataValueSave(EventDataValue dataValue, DataElement dataElement) {
        if (dataElement == null) {
            return;
        }

        FileResource fileResource = fetchFileResource(dataValue, dataElement);

        if (fileResource == null) {
            return;
        }

        setAssigned(fileResource);
    }

    /**
     * Delete associated FileResource if it exists.
     */
    private void handleFileDataValueDelete(EventDataValue dataValue, DataElement dataElement) {
        if (dataElement == null) {
            return;
        }
        FileResource fileResource = fetchFileResource(dataValue, dataElement);

        if (fileResource == null) {
            return;
        }

        fileResourceService.deleteFileResource(fileResource.getUid());
    }

    private FileResource fetchFileResource(EventDataValue dataValue, DataElement dataElement) {
        if (!dataElement.isFileType()) {
            return null;
        }

        return fileResourceService.getFileResource(dataValue.getValue());
    }

    private void setAssigned(FileResource fileResource) {
        fileResource.setAssigned(true);
        fileResourceService.updateFileResource(fileResource);
    }
}
