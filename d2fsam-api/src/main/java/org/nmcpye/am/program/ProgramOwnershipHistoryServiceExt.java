package org.nmcpye.am.program;

/**
 * Service Interface for managing {@link ProgramOwnershipHistory}.
 */
public interface ProgramOwnershipHistoryServiceExt {
    String ID = ProgramOwnershipHistoryServiceExt.class.getName();

    /**
     * Adds program ownership history
     *
     * @param programOwnershipHistory the history to add
     */
    void addProgramOwnershipHistory(ProgramOwnershipHistory programOwnershipHistory);
}
