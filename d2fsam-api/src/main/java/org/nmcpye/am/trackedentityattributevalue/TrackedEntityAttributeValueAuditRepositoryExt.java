package org.nmcpye.am.trackedentityattributevalue;

import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import java.util.List;

/**
 * Spring Data JPA repository for the TrackedEntityAttributeValueAudit entity.
 */
//@Repository
//public interface TrackedEntityAttributeValueAuditRepositoryExt
//    extends TrackedEntityAttributeValueAuditRepositoryExtCustom,
//    JpaRepository<TrackedEntityAttributeValueAudit, Long> {
//}

public interface TrackedEntityAttributeValueAuditRepositoryExt {
    void addTrackedEntityAttributeValueAudit(TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit);

    List<TrackedEntityAttributeValueAudit> getTrackedEntityAttributeValueAudits(
        TrackedEntityAttributeValueAuditQueryParams params);

    int countTrackedEntityAttributeValueAudits(TrackedEntityAttributeValueAuditQueryParams params);

    void deleteTrackedEntityAttributeValueAudits(TrackedEntityInstance trackedEntityInstance);
}
