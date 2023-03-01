package org.nmcpye.am.trackedentitydatavalue;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.ProgramStageInstance;

import java.util.List;

public interface TrackedEntityDataValueAuditRepositoryExt {
    void addTrackedEntityDataValueAudit(TrackedEntityDataValueAudit trackedEntityDataValueAudit);

    List<TrackedEntityDataValueAudit> getTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params);

    int countTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params);

    void deleteTrackedEntityDataValueAudit(DataElement dataElement);

    void deleteTrackedEntityDataValueAudit(ProgramStageInstance programStageInstance);
}
