package org.nmcpye.am.trackedentity;

import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link TrackedEntityInstanceAudit}.
 */
@Service("org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditServiceExt")
public class TrackedEntityInstanceAuditServiceExtImpl
    implements TrackedEntityInstanceAuditServiceExt {

    private final TrackedEntityInstanceAuditRepositoryExt trackedEntityInstanceAuditRepositoryExt;

    public TrackedEntityInstanceAuditServiceExtImpl(
        TrackedEntityInstanceAuditRepositoryExt trackedEntityInstanceAuditRepositoryExt) {
        this.trackedEntityInstanceAuditRepositoryExt = trackedEntityInstanceAuditRepositoryExt;
    }

    @Override
    @Async
    @Transactional
    public void addTrackedEntityInstanceAudit(TrackedEntityInstanceAudit trackedEntityInstanceAudit) {
        trackedEntityInstanceAuditRepositoryExt.addTrackedEntityInstanceAudit(trackedEntityInstanceAudit);
    }

    @Override
    @Async
    @Transactional
    public void addTrackedEntityInstanceAudit(List<TrackedEntityInstanceAudit> trackedEntityInstanceAudits) {
        trackedEntityInstanceAuditRepositoryExt.addTrackedEntityInstanceAudit(trackedEntityInstanceAudits);
    }

    @Override
    @Transactional
    public void deleteTrackedEntityInstanceAudit(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityInstanceAuditRepositoryExt.deleteTrackedEntityInstanceAudit(trackedEntityInstance);
    }
}
