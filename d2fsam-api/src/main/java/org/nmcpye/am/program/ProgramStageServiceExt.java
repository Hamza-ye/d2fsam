package org.nmcpye.am.program;

import java.util.List;

/**
 * Service Interface for managing {@link ProgramStage}.
 */
public interface ProgramStageServiceExt {
    String ID = ProgramStageServiceExt.class.getName();

    // -------------------------------------------------------------------------
    // ProgramStage
    // -------------------------------------------------------------------------

    /**
     * Adds an {@link ProgramStage}
     *
     * @param programStage The to ProgramStage add.
     * @return A generated unique id of the added {@link ProgramStage}.
     */
    Long saveProgramStage(ProgramStage programStage);

    /**
     * Deletes a {@link ProgramStage}.
     *
     * @param programStage the ProgramStage to delete.
     */
    void deleteProgramStage(ProgramStage programStage);

    /**
     * Updates an {@link ProgramStage}.
     *
     * @param programStage the ProgramStage to update.
     */
    void updateProgramStage(ProgramStage programStage);

    /**
     * Returns a {@link ProgramStage}.
     *
     * @param id the id of the ProgramStage to return.
     * @return the ProgramStage with the given id
     */
    ProgramStage getProgramStage(Long id);

    /**
     * Returns the {@link ProgramStage} with the given UID.
     *
     * @param uid the UID.
     * @return the ProgramStage with the given UID, or null if no match.
     */
    ProgramStage getProgramStage(String uid);

    //    /**
    //     * Retrieve all ProgramStages associated with the given DataEntryForm.
    //     *
    //     * @param dataEntryForm the DataEntryForm.
    //     * @return a list og ProgramStages.
    //     */
    //    List<ProgramStage> getProgramStagesByDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Retrieve all ProgramStages associated with the given Program.
     *
     * @param program the Program.
     * @return a list og ProgramStages.
     */
    List<ProgramStage> getProgramStagesByProgram(Program program);
}
