package org.nmcpye.am.program;

import org.nmcpye.am.dataelement.DataElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service Interface for managing {@link ProgramStageDataElement}.
 */
public interface ProgramStageDataElementServiceExt {
    String ID = ProgramStageDataElementServiceExt.class.getName();

    /**
     * Adds an {@link ProgramStageDataElement}
     *
     * @param programStageDataElement The to ProgramStageDataElement add.
     */
    void addProgramStageDataElement(ProgramStageDataElement programStageDataElement);

    /**
     * Updates an {@link ProgramStageDataElement}.
     *
     * @param programStageDataElement the ProgramStageDataElement to update.
     */
    void updateProgramStageDataElement(ProgramStageDataElement programStageDataElement);

    /**
     * Deletes a {@link ProgramStageDataElement}.
     *
     * @param programStageDataElement the ProgramStageDataElement to delete.
     */
    void deleteProgramStageDataElement(ProgramStageDataElement programStageDataElement);

    /**
     * Retrieve ProgramStageDataElement on a program stage and a data element
     *
     * @param programStage ProgramStage
     * @param dataElement  DataElement
     * @return ProgramStageDataElement
     */
    ProgramStageDataElement get(ProgramStage programStage, DataElement dataElement);

    /**
     * Returns all {@link ProgramStageDataElement}
     *
     * @return a collection of all ProgramStageDataElement, or an empty
     * collection if there are no ProgramStageDataElements.
     */
    List<ProgramStageDataElement> getAllProgramStageDataElements();

    /**
     * Returns all {@link ProgramStageDataElement} for the given
     * {@link DataElement}.
     *
     * @param dataElement filter, not null
     * @return a collection of {@link ProgramStageDataElement} associated with
     * the provided {@link DataElement}
     */
    List<ProgramStageDataElement> getProgramStageDataElements(DataElement dataElement);

    /**
     * Returns Map of ProgramStages containing Set of DataElements (together
     * ProgramStageDataElements) that have skipSynchronization flag set to true
     *
     * @return Map<String, Set < String>>
     */
    Map<String, Set<String>> getProgramStageDataElementsWithSkipSynchronizationSetToTrue();
}
