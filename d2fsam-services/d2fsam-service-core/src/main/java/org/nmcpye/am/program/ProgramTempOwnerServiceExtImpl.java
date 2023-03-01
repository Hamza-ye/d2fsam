package org.nmcpye.am.program;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProgramTempOwner}.
 */
@Service("org.nmcpye.am.program.ProgramTempOwnerServiceExt")
@Slf4j
public class ProgramTempOwnerServiceExtImpl implements ProgramTempOwnerServiceExt {

    private final ProgramTempOwnerRepositoryExt programTempOwnerRepositoryExt;

    public ProgramTempOwnerServiceExtImpl(
        ProgramTempOwnerRepositoryExt programTempOwnerRepositoryExt) {
        this.programTempOwnerRepositoryExt = programTempOwnerRepositoryExt;
    }

    @Override
    @Transactional
    public void addProgramTempOwner(ProgramTempOwner programTempOwner) {
        programTempOwnerRepositoryExt.addProgramTempOwner(programTempOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public int getValidTempOwnerRecordCount(Program program, TrackedEntityInstance entityInstance, User user) {
        return programTempOwnerRepositoryExt.getValidTempOwnerCount(program, entityInstance, user);
    }
}
