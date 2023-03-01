package org.nmcpye.am.trackedentityattributevalue;

import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service Implementation for managing {@link TrackedEntityAttributeValueAudit}.
 */
@Service("org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditServiceExt")
public class TrackedEntityAttributeValueAuditServiceExtImpl
    implements TrackedEntityAttributeValueAuditServiceExt {

    private final TrackedEntityAttributeValueAuditRepositoryExt trackedEntityAttributeValueAuditRepositoryExt;

    private final TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    private final CurrentUserService currentUserService;

    public TrackedEntityAttributeValueAuditServiceExtImpl(
        TrackedEntityAttributeValueAuditRepositoryExt trackedEntityAttributeValueAuditRepositoryExt,
        TrackedEntityAttributeServiceExt trackedEntityAttributeService, CurrentUserService currentUserService) {
        checkNotNull(trackedEntityAttributeValueAuditRepositoryExt);
        checkNotNull(trackedEntityAttributeService);
        checkNotNull(currentUserService);

        this.trackedEntityAttributeValueAuditRepositoryExt = trackedEntityAttributeValueAuditRepositoryExt;
        this.trackedEntityAttributeService = trackedEntityAttributeService;
        this.currentUserService = currentUserService;
    }

    @Override
    public void addTrackedEntityAttributeValueAudit(TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit) {
        trackedEntityAttributeValueAuditRepositoryExt.addTrackedEntityAttributeValueAudit(trackedEntityAttributeValueAudit);
    }

    @Override
    public List<TrackedEntityAttributeValueAudit> getTrackedEntityAttributeValueAudits(
        TrackedEntityAttributeValueAuditQueryParams params) {
        return aclFilter(trackedEntityAttributeValueAuditRepositoryExt
            .getTrackedEntityAttributeValueAudits(params));
    }

    private List<TrackedEntityAttributeValueAudit> aclFilter(
        List<TrackedEntityAttributeValueAudit> trackedEntityAttributeValueAudits) {
        // Fetch all the Tracked Entity Instance Attributes this user has access
        // to (only store UIDs). Not a very efficient solution, but at the
        // moment
        // we do not have ACL API to check TEI attributes.

        Set<String> allUserReadableTrackedEntityAttributes = trackedEntityAttributeService
            .getAllUserReadableTrackedEntityAttributes(currentUserService.getCurrentUser()).stream()
            .map(BaseIdentifiableObject::getUid).collect(Collectors.toSet());

        return trackedEntityAttributeValueAudits.stream()
            .filter(audit -> allUserReadableTrackedEntityAttributes.contains(audit.getAttribute().getUid()))
            .collect(Collectors.toList());
    }

    @Override
    public int countTrackedEntityAttributeValueAudits(TrackedEntityAttributeValueAuditQueryParams params) {
        return trackedEntityAttributeValueAuditRepositoryExt.countTrackedEntityAttributeValueAudits(params);
    }

    @Override
    public void deleteTrackedEntityAttributeValueAudits(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityAttributeValueAuditRepositoryExt.deleteTrackedEntityAttributeValueAudits(trackedEntityInstance);
    }
}
