package org.nmcpye.am.programstagefilter;

import java.util.List;

/**
 * Service Interface for managing {@link ProgramStageInstanceFilter}.
 */
public interface ProgramStageInstanceFilterServiceExt {
    String ID = ProgramStageInstanceFilter.class.getName();

    List<String> validate(ProgramStageInstanceFilter programStageInstanceFilter);
}
