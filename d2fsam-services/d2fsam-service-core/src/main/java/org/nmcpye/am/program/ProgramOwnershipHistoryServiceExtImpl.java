package org.nmcpye.am.program;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProgramOwnershipHistory}.
 */
@Service("org.nmcpye.am.program.ProgramOwnershipHistoryServiceExt")
@Slf4j
public class ProgramOwnershipHistoryServiceExtImpl implements ProgramOwnershipHistoryServiceExt {

    private final ProgramOwnershipHistoryRepositoryExt programOwnershipHistoryRepositoryExt;

    public ProgramOwnershipHistoryServiceExtImpl(
        ProgramOwnershipHistoryRepositoryExt programOwnershipHistoryRepositoryExt) {
        this.programOwnershipHistoryRepositoryExt = programOwnershipHistoryRepositoryExt;
    }

    @Transactional
    @Override
    public void addProgramOwnershipHistory(ProgramOwnershipHistory programOwnershipHistory) {
        programOwnershipHistoryRepositoryExt.addProgramOwnershipHistory(programOwnershipHistory);
    }
}
