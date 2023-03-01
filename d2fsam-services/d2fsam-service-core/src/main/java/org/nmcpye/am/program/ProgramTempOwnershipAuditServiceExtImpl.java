package org.nmcpye.am.program;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link ProgramTempOwnershipAudit}.
 */
@Service("org.nmcpye.am.program.ProgramTempOwnershipAuditServiceExt")
@Slf4j
public class ProgramTempOwnershipAuditServiceExtImpl implements ProgramTempOwnershipAuditServiceExt {

    private final ProgramTempOwnershipAuditRepositoryExt programTempOwnershipAuditRepositoryExt;

    public ProgramTempOwnershipAuditServiceExtImpl(
        ProgramTempOwnershipAuditRepositoryExt programTempOwnershipAuditRepositoryExt) {
        this.programTempOwnershipAuditRepositoryExt = programTempOwnershipAuditRepositoryExt;
    }

    @Override
    @Transactional
    public void addProgramTempOwnershipAudit(ProgramTempOwnershipAudit programTempOwnershipAudit) {
        programTempOwnershipAuditRepositoryExt.addProgramTempOwnershipAudit(programTempOwnershipAudit);
    }

    @Override
    @Transactional
    public void deleteProgramTempOwnershipAudit(Program program) {
        programTempOwnershipAuditRepositoryExt.deleteProgramTempOwnershipAudit(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramTempOwnershipAudit> getProgramTempOwnershipAudits(ProgramTempOwnershipAuditQueryParams params) {
        return programTempOwnershipAuditRepositoryExt.getProgramTempOwnershipAudits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public int getProgramTempOwnershipAuditsCount(ProgramTempOwnershipAuditQueryParams params) {
        return programTempOwnershipAuditRepositoryExt.getProgramTempOwnershipAuditsCount(params);
    }
}
