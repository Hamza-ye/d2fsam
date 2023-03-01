package org.nmcpye.am.program;

/**
 * Spring Data JPA repository for the ProgramOwnershipHistory entity.
 */
//@Repository
//public interface ProgramOwnershipHistoryRepositoryExt
//    extends ProgramOwnershipHistoryRepositoryExtCustom, JpaRepository<ProgramOwnershipHistory, Long> {
//}

public interface ProgramOwnershipHistoryRepositoryExt {
    String ID = ProgramOwnershipHistoryRepositoryExt.class.getName();

    /**
     * Adds program ownership history record
     *
     * @param programOwnershipHistory the ownership history to add
     */
    void addProgramOwnershipHistory(ProgramOwnershipHistory programOwnershipHistory);
}
