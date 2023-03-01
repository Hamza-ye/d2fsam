package org.nmcpye.am.trackedentity;

import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;

import java.util.List;

public interface TrackedEntityInstanceAuditRepositoryExt {

    String ID = TrackedEntityInstanceAuditRepositoryExt.class.getName();

    /**
     * Adds the given tracked entity instance audit.
     *
     * @param trackedEntityInstanceAudit the {@link TrackedEntityInstanceAudit}
     *                                   to add.
     */
    void addTrackedEntityInstanceAudit(TrackedEntityInstanceAudit trackedEntityInstanceAudit);

    /**
     * Adds the given {@link TrackedEntityInstanceAudit} instances.
     *
     * @param trackedEntityInstanceAudit the list of
     *                                   {@link TrackedEntityInstanceAudit}.
     */
    void addTrackedEntityInstanceAudit(List<TrackedEntityInstanceAudit> trackedEntityInstanceAudit);

    /**
     * Deletes tracked entity instance audit for the given tracked entity
     * instance.
     *
     * @param trackedEntityInstance the {@link TrackedEntityInstance}.
     */
    void deleteTrackedEntityInstanceAudit(TrackedEntityInstance trackedEntityInstance);

    /**
     * Returns tracked entity instance audits matching query params
     *
     * @param params tracked entity instance audit query params
     * @return a list of {@link TrackedEntityInstanceAudit}.
     */
    List<TrackedEntityInstanceAudit> getTrackedEntityInstanceAudits(TrackedEntityInstanceAuditQueryParams params);

    /**
     * Returns count of tracked entity instance audits matching query params
     *
     * @param params tracked entity instance audit query params
     * @return count of audits.
     */
    int getTrackedEntityInstanceAuditsCount(TrackedEntityInstanceAuditQueryParams params);
}
