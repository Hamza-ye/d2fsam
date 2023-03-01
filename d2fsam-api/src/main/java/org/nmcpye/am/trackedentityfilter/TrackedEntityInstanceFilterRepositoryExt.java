package org.nmcpye.am.trackedentityfilter;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.program.Program;

import java.util.List;

public interface TrackedEntityInstanceFilterRepositoryExt
    extends IdentifiableObjectStore<TrackedEntityInstanceFilter> {
    /**
     * Gets trackedEntityInstanceFilters
     *
     * @param program program of trackedEntityInstanceFilter to be fetched
     * @return list of trackedEntityInstanceFilters
     */
    List<TrackedEntityInstanceFilter> get(Program program);
}
