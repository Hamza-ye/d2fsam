package org.nmcpye.am.program;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.notification.event.ProgramEnrollmentCompletionNotificationEvent;
import org.nmcpye.am.program.notification.event.ProgramEnrollmentNotificationEvent;
import org.nmcpye.am.programrule.engine.EnrollmentEvaluationEvent;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.trackedentity.TrackerOwnershipManager;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.nmcpye.am.common.OrganisationUnitSelectionMode.*;

/**
 * Service Implementation for managing {@link ProgramInstance}.
 */
@Service("org.nmcpye.am.program.ProgramInstanceServiceExt")
@Slf4j
public class ProgramInstanceServiceExtImpl implements ProgramInstanceServiceExt {

    private final ProgramInstanceRepositoryExt programInstanceRepositoryExt;

    private final ProgramStageInstanceRepositoryExt programStageInstanceRepositoryExt;

    private final CurrentUserService currentUserService;

    private final TrackedEntityInstanceServiceExt trackedEntityInstanceServiceExt;

    private final ApplicationEventPublisher eventPublisher;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final TrackerOwnershipManager trackerOwnershipAccessManager;

    private final AclService aclService;

    public ProgramInstanceServiceExtImpl(
        ProgramInstanceRepositoryExt programInstanceRepositoryExt,
        ProgramStageInstanceRepositoryExt programStageInstanceRepositoryExt,
        CurrentUserService currentUserService,
        TrackedEntityInstanceServiceExt trackedEntityInstanceServiceExt,
        ApplicationEventPublisher eventPublisher,
        OrganisationUnitServiceExt organisationUnitServiceExt,
        TrackerOwnershipManager trackerOwnershipAccessManager, AclService aclService) {
        this.programInstanceRepositoryExt = programInstanceRepositoryExt;
        this.programStageInstanceRepositoryExt = programStageInstanceRepositoryExt;
        this.currentUserService = currentUserService;
        this.trackedEntityInstanceServiceExt = trackedEntityInstanceServiceExt;
        this.eventPublisher = eventPublisher;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
        this.trackerOwnershipAccessManager = trackerOwnershipAccessManager;
        this.aclService = aclService;
    }

    @Override
    @Transactional
    public Long addProgramInstance(ProgramInstance programInstance) {
        programInstanceRepositoryExt.saveObject(programInstance);
        return programInstance.getId();
    }

    @Override
    @Transactional
    public Long addProgramInstance(ProgramInstance programInstance, User user) {
        programInstanceRepositoryExt.save(programInstance, user);
        return programInstance.getId();
    }

    @Override
    @Transactional
    public void deleteProgramInstance(ProgramInstance programInstance) {
        programInstance.setStatus(ProgramStatus.CANCELLED);
        programInstanceRepositoryExt.update(programInstance);
        programInstanceRepositoryExt.deleteObject(programInstance);
    }

    @Override
    @Transactional
    public void hardDeleteProgramInstance(ProgramInstance programInstance) {
        programInstanceRepositoryExt.hardDelete(programInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramInstance getProgramInstance(Long id) {
        return programInstanceRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramInstance getProgramInstance(String uid) {
        return programInstanceRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramInstance> getProgramInstances(List<String> uids) {
        return programInstanceRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean programInstanceExists(String uid) {
        return programInstanceRepositoryExt.existsIncludingDeleted(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean programInstanceExistsIncludingDeleted(String uid) {
        return programInstanceRepositoryExt.exists(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProgramInstancesUidsIncludingDeleted(List<String> uids) {
        return programInstanceRepositoryExt.getUidsIncludingDeleted(uids);
    }

    @Override
    @Transactional
    public void updateProgramInstance(ProgramInstance programInstance) {
        programInstanceRepositoryExt.update(programInstance);
    }

    @Override
    @Transactional
    public void updateProgramInstance(ProgramInstance programInstance, User user) {
        programInstanceRepositoryExt.update(programInstance, user);
    }

    // TODO consider security
    @Override
    @Transactional(readOnly = true)
    public List<ProgramInstance> getProgramInstances(ProgramInstanceQueryParams params) {
        decideAccess(params);
        validate(params);

        User user = currentUserService.getCurrentUser();

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

//        possibleSearchOrgUnits.addAll(user.getTeiSearchOrganisationUnits());

        if (user != null && params.isOrganisationUnitMode(ACCESSIBLE)) {
            possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                .getAllUserAccessibleOrganisationUnits(user));
//            possibleSearchOrgUnits.addAll(user.getOrganisationUnits());
            possibleSearchOrgUnits.addAll(user.getTeiSearchOrganisationUnitsWithFallback());
//            possibleSearchOrgUnits.addAll(user.getDataViewOrganisationUnits());

            params.setOrganisationUnits(possibleSearchOrgUnits);
            params.setOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS);
        } else if (params.isOrganisationUnitMode(OrganisationUnitSelectionMode.CHILDREN)) {
            Set<OrganisationUnit> organisationUnits = new HashSet<>(params.getOrganisationUnits());

            for (OrganisationUnit organisationUnit : params.getOrganisationUnits()) {
                organisationUnits.addAll(organisationUnit.getChildren());
            }

            params.setOrganisationUnits(organisationUnits);
        }

        if (!params.isPaging() && !params.isSkipPaging()) {
            params.setDefaultPaging();
        }

        return programInstanceRepositoryExt.getProgramInstances(params);
    }

    @Override
    @Transactional(readOnly = true)
    public int countProgramInstances(ProgramInstanceQueryParams params) {
        decideAccess(params);
        validate(params);

        User user = currentUserService.getCurrentUser(); //.orElseThrow(() -> new RuntimeException("User could not be found"));

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();


        if (user != null && params.isOrganisationUnitMode(ACCESSIBLE)) {
            possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                .getAllUserAccessibleOrganisationUnits(user));
//            possibleSearchOrgUnits.addAll(user.getOrganisationUnits());
            possibleSearchOrgUnits.addAll(user.getTeiSearchOrganisationUnits());
//            possibleSearchOrgUnits.addAll(user.getDataViewOrganisationUnits());
            params.setOrganisationUnits(possibleSearchOrgUnits);
            params.setOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS);
        } else if (params.isOrganisationUnitMode(OrganisationUnitSelectionMode.CHILDREN)) {
            Set<OrganisationUnit> organisationUnits = new HashSet<>();
            organisationUnits.addAll(params.getOrganisationUnits());

            for (OrganisationUnit organisationUnit : params.getOrganisationUnits()) {
                organisationUnits.addAll(organisationUnit.getChildren());
            }

            params.setOrganisationUnits(organisationUnits);
        }

        params.setSkipPaging(true);

        return programInstanceRepositoryExt.countProgramInstances(params);
    }

    @Override
    @Transactional(readOnly = true)
    public void decideAccess(ProgramInstanceQueryParams params) {
        if (params.hasProgram()) {
            if (!aclService.canDataRead(params.getUser(), params.getProgram())) {
                throw new IllegalQueryException("Current user is not authorized to read data from selected program:  "
                    + params.getProgram().getUid());
            }

            if (params.getProgram().getTrackedEntityType() != null
                && !aclService.canDataRead(params.getUser(), params.getProgram().getTrackedEntityType())) {
                throw new IllegalQueryException(
                    "Current user is not authorized to read data from selected program's tracked entity type:  "
                        + params.getProgram().getTrackedEntityType().getUid());
            }

        }

        if (params.hasTrackedEntityType()
            && !aclService.canDataRead(params.getUser(), params.getTrackedEntityType())) {
            throw new IllegalQueryException(
                "Current user is not authorized to read data from selected tracked entity type:  "
                    + params.getTrackedEntityType().getUid());
        }
    }

    @Override
    public void validate(ProgramInstanceQueryParams params) throws IllegalQueryException {
        String violation = null;

        if (params == null) {
            throw new IllegalQueryException("Params cannot be null");
        }

        User user = params.getUser();

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

        if (!params.hasOrganisationUnits()
            && !(params.isOrganisationUnitMode(ALL) || params.isOrganisationUnitMode(ACCESSIBLE)
            || params.isOrganisationUnitMode(CAPTURE))) {
            violation = "At least one organisation unit must be specified, or specify orgUnitSelection mode";
        }

        //        if (params.isOrganisationUnitMode(ACCESSIBLE)
        //            && (user == null || !user.hasDataViewOrganisationUnitWithFallback())) {
        //            violation = "Current user must be associated with at least one organisation unit when selection mode is ACCESSIBLE";
        //        }
        if (params.isOrganisationUnitMode(ACCESSIBLE)) {
            if (user == null) {
                violation = "Current user must be associated with at least one organisation unit when selection mode is ACCESSIBLE";
            } else {
                possibleSearchOrgUnits.addAll(user.getDataViewOrganisationUnitsWithFallback());
                possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                    .getAllUserAccessibleOrganisationUnits(user));
                if (possibleSearchOrgUnits.isEmpty()) {
                    violation = "Current user must be associated with at least one organisation unit when selection mode is ACCESSIBLE";
                }
            }
        }

        if (params.isOrganisationUnitMode(CAPTURE)) {
            if (user == null) {
                violation = "Current user must be associated with at least one organisation unit with write access when selection mode is CAPTURE";
            } else {
                possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                    .getAllUserAccessibleOrganisationUnits(user));
                possibleSearchOrgUnits.addAll(user.getOrganisationUnits());
                if (possibleSearchOrgUnits.isEmpty()) {
                    violation = "Current user must be associated with at least one organisation unit with write access when selection mode is CAPTURE";
                }
            }

        }

        if (params.hasProgram() && params.hasTrackedEntityType()) {
            violation = "Program and tracked entity cannot be specified simultaneously";
        }

        if (params.hasProgramStatus() && !params.hasProgram()) {
            violation = "Program must be defined when program status is defined";
        }

        if (params.hasFollowUp() && !params.hasProgram()) {
            violation = "Program must be defined when follow up status is defined";
        }

        if (params.hasProgramStartDate() && !params.hasProgram()) {
            violation = "Program must be defined when program start date is specified";
        }

        if (params.hasProgramEndDate() && !params.hasProgram()) {
            violation = "Program must be defined when program end date is specified";
        }

        if (params.hasLastUpdated() && params.hasLastUpdatedDuration()) {
            violation = "Last updated and last updated duration cannot be specified simultaneously";
        }

        if (params.hasLastUpdatedDuration() && DateUtils.getDuration(params.getLastUpdatedDuration()) == null) {
            violation = "Duration is not valid: " + params.getLastUpdatedDuration();
        }

        if (violation != null) {
            log.warn("Validation failed: " + violation);

            throw new IllegalQueryException(violation);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramInstance> getProgramInstances(Program program) {
        return programInstanceRepositoryExt.get(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramInstance> getProgramInstances(Program program, ProgramStatus status) {
        return programInstanceRepositoryExt.get(program, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramInstance> getProgramInstances(TrackedEntityInstance entityInstance, Program program, ProgramStatus status) {
        return programInstanceRepositoryExt.get(entityInstance, program, status);
    }

    @Override
    @Transactional
    public ProgramInstance prepareProgramInstance(
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        ProgramStatus programStatus,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit organisationUnit,
        String uid
    ) {
        if (
            program.getTrackedEntityType() != null && !program.getTrackedEntityType().equals(trackedEntityInstance.getTrackedEntityType())
        ) {
            throw new IllegalQueryException("Tracked entity instance must have same tracked entity as program: " + program.getUid());
        }

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setUid(CodeGenerator.isValidUid(uid) ? uid : CodeGenerator.generateUid());
        programInstance.setOrganisationUnit(organisationUnit);
        programInstance.enrollTrackedEntityInstance(trackedEntityInstance, program);

        programInstance.setEnrollmentDate(Objects.requireNonNullElseGet(enrollmentDate, LocalDateTime::now));

        programInstance.setIncidentDate(Objects.requireNonNullElseGet(incidentDate, LocalDateTime::now));

        programInstance.setStatus(programStatus);

        return programInstance;
    }

    @Override
    @Transactional
    public ProgramInstance prepareProgramInstanceWithActivity(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        ProgramStatus programStatus,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit orgUnit,
        String uid) {

        ProgramInstance programInstance = prepareProgramInstance(trackedEntityInstance,
            program, programStatus, enrollmentDate, incidentDate, orgUnit, uid);
        programInstance.setActivity(activity);

        return programInstance;
    }

    @Override
    @Transactional
    public ProgramInstance enrollTrackedEntityInstance(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit organisationUnit
    ) {
        return enrollTrackedEntityInstance(
            activity,
            trackedEntityInstance,
            program,
            enrollmentDate,
            incidentDate,
            organisationUnit,
            CodeGenerator.generateUid()
        );
    }

    @Override
    @Transactional
    public ProgramInstance enrollTrackedEntityInstance(
        Activity activity,
        TrackedEntityInstance trackedEntityInstance,
        Program program,
        LocalDateTime enrollmentDate,
        LocalDateTime incidentDate,
        OrganisationUnit organisationUnit,
        String uid
    ) {
        // ---------------------------------------------------------------------
        // Add program instance
        // ---------------------------------------------------------------------

        ProgramInstance programInstance = prepareProgramInstanceWithActivity(
            activity,
            trackedEntityInstance,
            program,
            ProgramStatus.ACTIVE,
            enrollmentDate,
            incidentDate,
            organisationUnit,
            uid
        );
        addProgramInstance(programInstance);

        // ---------------------------------------------------------------------
        // Add program owner and overwrite if already exists.
        // ---------------------------------------------------------------------

        trackerOwnershipAccessManager.assignOwnership(trackedEntityInstance, program, organisationUnit, true, true);

        // -----------------------------------------------------------------
        // Send enrollment notifications (if any)
        // -----------------------------------------------------------------

        eventPublisher.publishEvent(new ProgramEnrollmentNotificationEvent(this, programInstance.getId()));

        eventPublisher.publishEvent(new EnrollmentEvaluationEvent(this, programInstance.getId()));

        // -----------------------------------------------------------------
        // Update ProgramInstance and TEI
        // -----------------------------------------------------------------

        updateProgramInstance(programInstance);
        trackedEntityInstanceServiceExt.updateTrackedEntityInstance(trackedEntityInstance);

        return programInstance;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAutoCompleteProgramInstanceStatus(ProgramInstance programInstance) {
        Set<ProgramStageInstance> programStageInstances = new HashSet<>(programInstance.getProgramStageInstances());
        Set<ProgramStage> programStages = new HashSet<>();

        for (ProgramStageInstance programStageInstance : programStageInstances) {
            if (
                (!programStageInstance.isCompleted() && programStageInstance.getStatus() != EventStatus.SKIPPED) ||
                    programStageInstance.getProgramStage().getRepeatable()
            ) {
                return false;
            }

            programStages.add(programStageInstance.getProgramStage());
        }

        return programStages.size() == programInstance.getProgram().getProgramStages().size();
    }

    @Override
    @Transactional
    public void completeProgramInstanceStatus(ProgramInstance programInstance) {
        // -----------------------------------------------------------------
        // Update program-instance
        // -----------------------------------------------------------------

        programInstance.setStatus(ProgramStatus.COMPLETED);
        updateProgramInstance(programInstance);

        // ---------------------------------------------------------------------
        // Send sms-message after program completion
        // ---------------------------------------------------------------------

        eventPublisher.publishEvent(new ProgramEnrollmentCompletionNotificationEvent(this, programInstance.getId()));

        eventPublisher.publishEvent(new EnrollmentEvaluationEvent(this, programInstance.getId()));
    }

    @Override
    @Transactional
    public void cancelProgramInstanceStatus(ProgramInstance programInstance) {
        // ---------------------------------------------------------------------
        // Set status of the program-instance
        // ---------------------------------------------------------------------
        programInstance.setStatus(ProgramStatus.CANCELLED);
        updateProgramInstance(programInstance);

        // ---------------------------------------------------------------------
        // Set statuses of the program-stage-instances
        // ---------------------------------------------------------------------

        for (ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances()) {
            if (programStageInstance.getExecutionDate() == null) {
                // -------------------------------------------------------------
                // Set status as skipped for overdue events, or delete
                // -------------------------------------------------------------

                if (programStageInstance.getDueDate().isBefore(programInstance.getEndDate())) {
                    programStageInstance.setStatus(EventStatus.SKIPPED);
                    programStageInstanceRepositoryExt.update(programStageInstance);
                } else {
                    programStageInstanceRepositoryExt.deleteObject(programStageInstance);
                }
            }
        }
    }

    @Override
    @Transactional
    public void incompleteProgramInstanceStatus(ProgramInstance programInstance) {
        Program program = programInstance.getProgram();

        TrackedEntityInstance tei = programInstance.getEntityInstance();

        if (getProgramInstances(tei, program, ProgramStatus.ACTIVE).size() > 0) {
            log.warn("Program has another active enrollment going on. Not possible to incomplete");

            throw new IllegalQueryException("Program has another active enrollment going on. Not possible to incomplete");
        }

        // -----------------------------------------------------------------
        // Update program-instance
        // -----------------------------------------------------------------

        programInstance.setStatus(ProgramStatus.ACTIVE);
        programInstance.setEndDate(null);

        updateProgramInstance(programInstance);
    }
}
