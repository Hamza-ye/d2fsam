package org.nmcpye.am.trackedentitydatavalue;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.ProgramStageInstance;

import java.util.List;

/**
 * Service Interface for managing {@link TrackedEntityDataValueAudit}.
 */
public interface TrackedEntityDataValueAuditServiceExt {
    void addTrackedEntityDataValueAudit(TrackedEntityDataValueAudit trackedEntityDataValueAudit);

    List<TrackedEntityDataValueAudit> getTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params);

    int countTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params);

    void deleteTrackedEntityDataValueAudit(DataElement dataElement);

    void deleteTrackedEntityDataValueAudit(ProgramStageInstance programStageInstance);
}
