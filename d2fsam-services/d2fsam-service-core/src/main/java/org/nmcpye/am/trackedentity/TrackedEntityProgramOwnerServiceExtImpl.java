package org.nmcpye.am.trackedentity;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Service Implementation for managing {@link TrackedEntityProgramOwner}.
 */
@Service("org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerServiceExt")
@Slf4j
public class TrackedEntityProgramOwnerServiceExtImpl implements TrackedEntityProgramOwnerServiceExt {

    private final TrackedEntityProgramOwnerRepositoryExt trackedEntityProgramOwnerStore;

    private final TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    private final ProgramServiceExt programService;

    private final OrganisationUnitServiceExt orgUnitService;

    private final CurrentUserService currentUserService;

    public TrackedEntityProgramOwnerServiceExtImpl(
        TrackedEntityProgramOwnerRepositoryExt trackedEntityProgramOwnerStore,
        TrackedEntityInstanceServiceExt trackedEntityInstanceService,
        ProgramServiceExt programService,
        OrganisationUnitServiceExt orgUnitService,
        CurrentUserService currentUserService) {
        this.trackedEntityProgramOwnerStore = trackedEntityProgramOwnerStore;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.programService = programService;
        this.orgUnitService = orgUnitService;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public void createTrackedEntityProgramOwner(String teiUid, String programUid, String orgUnitUid) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiUid);
        if (entityInstance == null) {
            return;
        }
        Program program = programService.getProgram(programUid);
        if (program == null) {
            return;
        }
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitUid);
        if (ou == null) {
            return;
        }
        trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
    }

    @Override
    @Transactional
    public void createTrackedEntityProgramOwner(TrackedEntityInstance entityInstance, Program program, OrganisationUnit ou) {
        if (entityInstance == null || program == null || ou == null) {
            return;
        }
        trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
    }

    private TrackedEntityProgramOwner buildTrackedEntityProgramOwner(
        TrackedEntityInstance entityInstance,
        Program program,
        OrganisationUnit ou
    ) {
        TrackedEntityProgramOwner teiProgramOwner = new TrackedEntityProgramOwner(entityInstance, program, ou);
        teiProgramOwner.updateDates();
        User user = currentUserService.getCurrentUser();
        if (user != null) {
            teiProgramOwner.setCreatedBy(user.getUsername());
        }
        return teiProgramOwner;
    }

    @Override
    @Transactional
    public void createOrUpdateTrackedEntityProgramOwner(String teiUid, String programUid, String orgUnitUid) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiUid);
        Program program = programService.getProgram(programUid);
        if (entityInstance == null) {
            return;
        }
        TrackedEntityProgramOwner teiProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(
            entityInstance.getId(),
            program.getId()
        );
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitUid);
        if (ou == null) {
            return;
        }

        if (teiProgramOwner == null) {
            trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
        } else {
            teiProgramOwner = updateTrackedEntityProgramOwner(teiProgramOwner, ou);
            trackedEntityProgramOwnerStore.update(teiProgramOwner);
        }
    }

    @Override
    @Transactional
    public void createOrUpdateTrackedEntityProgramOwner(Long teiUid, Long programUid, Long orgUnitUid) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiUid);
        Program program = programService.getProgram(programUid);
        if (entityInstance == null) {
            return;
        }
        TrackedEntityProgramOwner teiProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(
            entityInstance.getId(),
            program.getId()
        );
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitUid);
        if (ou == null) {
            return;
        }

        if (teiProgramOwner == null) {
            trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
        } else {
            teiProgramOwner = updateTrackedEntityProgramOwner(teiProgramOwner, ou);
            trackedEntityProgramOwnerStore.update(teiProgramOwner);
        }
    }

    @Override
    @Transactional
    public void createOrUpdateTrackedEntityProgramOwner(TrackedEntityInstance entityInstance, Program program, OrganisationUnit ou) {
        if (entityInstance == null || program == null || ou == null) {
            return;
        }
        TrackedEntityProgramOwner teiProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(
            entityInstance.getId(),
            program.getId()
        );
        if (teiProgramOwner == null) {
            trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
        } else {
            teiProgramOwner = updateTrackedEntityProgramOwner(teiProgramOwner, ou);
            trackedEntityProgramOwnerStore.update(teiProgramOwner);
        }
    }

    @Override
    @Transactional
    public void updateTrackedEntityProgramOwner(TrackedEntityInstance entityInstance, Program program, OrganisationUnit ou) {
        if (entityInstance == null || program == null || ou == null) {
            return;
        }
        TrackedEntityProgramOwner teiProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(
            entityInstance.getId(),
            program.getId()
        );
        if (teiProgramOwner == null) {
            return;
        }
        teiProgramOwner = updateTrackedEntityProgramOwner(teiProgramOwner, ou);
        trackedEntityProgramOwnerStore.update(teiProgramOwner);
    }

    private TrackedEntityProgramOwner updateTrackedEntityProgramOwner(TrackedEntityProgramOwner teiProgramOwner, OrganisationUnit ou) {
        teiProgramOwner.setOrganisationUnit(ou);
        teiProgramOwner.updateDates();
        User user = currentUserService.getCurrentUser();
        if (user != null) {
            teiProgramOwner.setCreatedBy(user.getUsername());
        }
        return teiProgramOwner;
    }

    @Override
    @Transactional
    public void updateTrackedEntityProgramOwner(String teiUid, String programUid, String orgUnitUid) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiUid);
        if (entityInstance == null) {
            return;
        }
        Program program = programService.getProgram(programUid);
        if (program == null) {
            return;
        }

        TrackedEntityProgramOwner teProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(
            entityInstance.getId(),
            program.getId()
        );
        if (teProgramOwner == null) {
            return;
        }
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitUid);
        if (ou == null) {
            return;
        }
        teProgramOwner = updateTrackedEntityProgramOwner(teProgramOwner, ou);
        trackedEntityProgramOwnerStore.update(teProgramOwner);
    }

    @Override
    @Transactional
    public void createTrackedEntityProgramOwner(Long teiId, Long programId, Long orgUnitId) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiId);
        if (entityInstance == null) {
            return;
        }
        Program program = programService.getProgram(programId);
        if (program == null) {
            return;
        }
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitId);
        if (ou == null) {
            return;
        }
        trackedEntityProgramOwnerStore.saveObject(buildTrackedEntityProgramOwner(entityInstance, program, ou));
    }

    @Override
    @Transactional
    public void updateTrackedEntityProgramOwner(Long teiId, Long programId, Long orgUnitId) {
        TrackedEntityProgramOwner teProgramOwner = trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(teiId, programId);
        if (teProgramOwner == null) {
            return;
        }
        OrganisationUnit ou = orgUnitService.getOrganisationUnit(orgUnitId);
        if (ou == null) {
            return;
        }
        trackedEntityProgramOwnerStore.update(updateTrackedEntityProgramOwner(teProgramOwner, ou));
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityProgramOwner getTrackedEntityProgramOwner(Long teiId, Long programId) {
        return trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(teiId, programId);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityProgramOwner getTrackedEntityProgramOwner(String teiUid, String programUid) {
        TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(teiUid);
        Program program = programService.getProgram(programUid);
        if (entityInstance == null || program == null) {
            return null;
        }
        return trackedEntityProgramOwnerStore.getTrackedEntityProgramOwner(entityInstance.getId(), program.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityProgramOwner> getTrackedEntityProgramOwnersUsingId(List<Long> teiIds) {
        return trackedEntityProgramOwnerStore.getTrackedEntityProgramOwners(teiIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityProgramOwner> getTrackedEntityProgramOwnersUsingId(List<Long> teiIds, Program program) {
        return trackedEntityProgramOwnerStore.getTrackedEntityProgramOwners(teiIds, program.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityProgramOwnerIds> getTrackedEntityProgramOwnersUidsUsingId(List<Long> teiIds, Program program) {
        if (teiIds.isEmpty()) {
            return Collections.emptyList();
        }
        return trackedEntityProgramOwnerStore.getTrackedEntityProgramOwnersUids(teiIds, program.getId());
    }
}
