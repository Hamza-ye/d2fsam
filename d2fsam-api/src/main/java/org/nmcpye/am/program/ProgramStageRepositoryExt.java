package org.nmcpye.am.program;

import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;

/**
 * Spring Data JPA repository for the ProgramStage entity.
 * <p>
 * When extending this class, extend ProgramStageRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface ProgramStageRepositoryExt
//    extends ProgramStageRepositoryExtCustom, JpaRepository<ProgramStage, Long> {
//}
public interface ProgramStageRepositoryExt extends IdentifiableObjectStore<ProgramStage> {

    /**
     * Retrieve a program stage by name and a program
     *
     * @param name    Name of program stage
     * @param program Specify a {@link Program} for retrieving a program stage.
     *                The system allows the name of program stages are duplicated on
     *                different programs
     * @return ProgramStage
     */
    ProgramStage getByNameAndProgram(String name, Program program);

    //    /**
    //     * Get all ProgramStages associated with the given DataEntryForm.
    //     *
    //     * @param dataEntryForm the DataEntryForm.
    //     * @return a list of ProgramStages.
    //     */
    //    List<ProgramStage> getByDataEntryForm(DataEntryForm dataEntryForm );

    List<ProgramStage> getByProgram(Program program);
}

