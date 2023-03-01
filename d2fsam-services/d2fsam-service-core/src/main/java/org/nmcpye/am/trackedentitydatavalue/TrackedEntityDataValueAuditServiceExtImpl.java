package org.nmcpye.am.trackedentitydatavalue;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentity.TrackerAccessManager;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service Implementation for managing {@link TrackedEntityDataValueAudit}.
 */
@Service("org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditServiceExt")
public class TrackedEntityDataValueAuditServiceExtImpl
    implements TrackedEntityDataValueAuditServiceExt {

    private final TrackedEntityDataValueAuditRepositoryExt trackedEntityDataValueAuditRepositoryExt;

    private final Predicate<TrackedEntityDataValueAudit> aclFilter;

    public TrackedEntityDataValueAuditServiceExtImpl(
        TrackedEntityDataValueAuditRepositoryExt trackedEntityDataValueAuditRepositoryExt,
        TrackerAccessManager trackerAccessManager, CurrentUserService currentUserService) {
        checkNotNull(trackedEntityDataValueAuditRepositoryExt);
        checkNotNull(trackerAccessManager);
        checkNotNull(currentUserService);

        this.trackedEntityDataValueAuditRepositoryExt = trackedEntityDataValueAuditRepositoryExt;
        aclFilter = (audit) -> trackerAccessManager.canRead(currentUserService.getCurrentUser(),
            audit.getProgramStageInstance(), audit.getDataElement(), false).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void addTrackedEntityDataValueAudit(TrackedEntityDataValueAudit trackedEntityDataValueAudit) {
        trackedEntityDataValueAuditRepositoryExt.addTrackedEntityDataValueAudit(trackedEntityDataValueAudit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityDataValueAudit> getTrackedEntityDataValueAudits(
        TrackedEntityDataValueAuditQueryParams params) {
        return trackedEntityDataValueAuditRepositoryExt.getTrackedEntityDataValueAudits(params).stream()
            .filter(aclFilter).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int countTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params) {
        return trackedEntityDataValueAuditRepositoryExt.countTrackedEntityDataValueAudits(params);
    }

    @Override
    @Transactional
    public void deleteTrackedEntityDataValueAudit(DataElement dataElement) {
        trackedEntityDataValueAuditRepositoryExt.deleteTrackedEntityDataValueAudit(dataElement);
    }

    @Override
    @Transactional
    public void deleteTrackedEntityDataValueAudit(ProgramStageInstance programStageInstance) {
        trackedEntityDataValueAuditRepositoryExt.deleteTrackedEntityDataValueAudit(programStageInstance);
    }
}
