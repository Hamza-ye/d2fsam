package org.nmcpye.am.program;

import org.nmcpye.am.dataelement.DataElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("org.nmcpye.am.program.ProgramStageDataElementServiceExt")
public class ProgramStageDataElementServiceExtImpl
    implements ProgramStageDataElementServiceExt {

    private final ProgramStageDataElementRepositoryExt programStageDataElementRepositoryExt;

    public ProgramStageDataElementServiceExtImpl(
        ProgramStageDataElementRepositoryExt programStageDataElementRepositoryExt) {
        this.programStageDataElementRepositoryExt = programStageDataElementRepositoryExt;
    }

    @Override
    @Transactional
    public void addProgramStageDataElement(ProgramStageDataElement programStageDataElement) {
        programStageDataElementRepositoryExt.saveObject(programStageDataElement);
    }

    @Override
    @Transactional
    public void deleteProgramStageDataElement(ProgramStageDataElement programStageDataElement) {
        programStageDataElementRepositoryExt.deleteObject(programStageDataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramStageDataElement> getAllProgramStageDataElements() {
        return programStageDataElementRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramStageDataElement> getProgramStageDataElements(DataElement dataElement) {
        return programStageDataElementRepositoryExt.getProgramStageDataElements(dataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStageDataElement get(ProgramStage programStage, DataElement dataElement) {
        return programStageDataElementRepositoryExt.get(programStage, dataElement);
    }

    @Override
    @Transactional
    public void updateProgramStageDataElement(ProgramStageDataElement programStageDataElement) {
        programStageDataElementRepositoryExt.update(programStageDataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<String>> getProgramStageDataElementsWithSkipSynchronizationSetToTrue() {
        return programStageDataElementRepositoryExt.getProgramStageDataElementsWithSkipSynchronizationSetToTrue();
    }
}
