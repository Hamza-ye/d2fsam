package org.nmcpye.am.trackedentity;

import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;

import java.util.List;

/**
 * Service Interface for managing {@link TrackedEntityInstanceAudit}.
 */
public interface TrackedEntityInstanceAuditServiceExt {

    String ID = TrackedEntityInstanceAuditServiceExt.class.getName();

    /**
     * Adds tracked entity instance audit
     *
     * @param trackedEntityInstanceAudit the audit to add
     */
    void addTrackedEntityInstanceAudit(TrackedEntityInstanceAudit trackedEntityInstanceAudit);

    /**
     * Adds multipe tracked entity instance audit
     */
    void addTrackedEntityInstanceAudit(List<TrackedEntityInstanceAudit> trackedEntityInstanceAudits);

    /**
     * Deletes tracked entity instance audit for the given tracked entity
     * instance
     *
     * @param trackedEntityInstance the tracked entity instance
     */
    void deleteTrackedEntityInstanceAudit(TrackedEntityInstance trackedEntityInstance);
}
