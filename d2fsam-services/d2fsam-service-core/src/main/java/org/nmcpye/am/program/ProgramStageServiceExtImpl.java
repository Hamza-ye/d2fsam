package org.nmcpye.am.program;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link ProgramStage}.
 */
@Service("org.nmcpye.am.program.ProgramStageServiceExt")
@Slf4j
public class ProgramStageServiceExtImpl implements ProgramStageServiceExt {

    private final ProgramStageRepositoryExt programStageRepositoryExt;

    public ProgramStageServiceExtImpl(ProgramStageRepositoryExt programStageRepositoryExt) {
        this.programStageRepositoryExt = programStageRepositoryExt;
    }

    @Override
    @Transactional
    public Long saveProgramStage(ProgramStage programStage) {
        programStageRepositoryExt.saveObject(programStage);
        return programStage.getId();
    }

    @Override
    @Transactional
    public void deleteProgramStage(ProgramStage programStage) {
        programStageRepositoryExt.deleteObject(programStage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStage getProgramStage(Long id) {
        return programStageRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStage getProgramStage(String uid) {
        return programStageRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional
    public void updateProgramStage(ProgramStage programStage) {
        programStageRepositoryExt.update(programStage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramStage> getProgramStagesByProgram(Program program) {
        return programStageRepositoryExt.getByProgram(program);
    }
}
