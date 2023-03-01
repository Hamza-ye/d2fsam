package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.dataelement.DataElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring Data JPA repository for the ProgramStageDataElement entity.
 */
//@Repository
//public interface ProgramStageDataElementRepositoryExt
//    extends ProgramStageDataElementRepositoryExtCustom,
//    JpaRepository<ProgramStageDataElement, Long> {
//}

public interface ProgramStageDataElementRepositoryExt
    extends IdentifiableObjectStore<ProgramStageDataElement> {
    String ID = ProgramStageDataElementRepositoryExt.class.getName();

    /**
     * Retrieve ProgramStageDataElement list on a program stage and a data
     * element
     *
     * @param programStage ProgramStage
     * @param dataElement  DataElement
     * @return ProgramStageDataElement
     */
    ProgramStageDataElement get(ProgramStage programStage, DataElement dataElement);

    /**
     * Returns Map of ProgramStages containing Set of DataElements (together
     * ProgramStageDataElements) that have skipSynchronization flag set to true
     *
     * @return Map<String, Set < String>>
     */
    Map<String, Set<String>> getProgramStageDataElementsWithSkipSynchronizationSetToTrue();

    List<ProgramStageDataElement> getProgramStageDataElements(DataElement dataElement);
}

