package org.nmcpye.am.trackedentityattributevalue;

import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import java.util.List;

/**
 * Service Interface for managing {@link TrackedEntityAttributeValueAudit}.
 */
public interface TrackedEntityAttributeValueAuditServiceExt {

    void addTrackedEntityAttributeValueAudit(TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit);

    List<TrackedEntityAttributeValueAudit> getTrackedEntityAttributeValueAudits(
        TrackedEntityAttributeValueAuditQueryParams params);

    int countTrackedEntityAttributeValueAudits(TrackedEntityAttributeValueAuditQueryParams params);

    void deleteTrackedEntityAttributeValueAudits(TrackedEntityInstance trackedEntityInstance);
}
