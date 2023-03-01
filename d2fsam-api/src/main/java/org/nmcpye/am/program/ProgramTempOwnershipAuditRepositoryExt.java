package org.nmcpye.am.program;

import java.util.List;

/**
 * Spring Data JPA repository for the ProgramTempOwnershipAudit entity.
 */
//@Repository
//public interface ProgramTempOwnershipAuditRepositoryExt
//    extends ProgramTempOwnershipAuditRepositoryExtCustom,
//    JpaRepository<ProgramTempOwnershipAudit, Long> {
//}

public interface ProgramTempOwnershipAuditRepositoryExt {
    String ID = ProgramTempOwnershipAuditRepositoryExt.class.getName();

    /**
     * Adds program temp ownership audit
     *
     * @param programTempOwnershipAudit the audit to add
     */
    void addProgramTempOwnershipAudit(ProgramTempOwnershipAudit programTempOwnershipAudit);

    /**
     * Deletes audit for the given program
     *
     * @param program the program instance
     */
    void deleteProgramTempOwnershipAudit(Program program);

    /**
     * Returns program temp ownership audits matching query params
     *
     * @param params program temp ownership audit query params
     * @return matching ProgramTempOwnershipAudit
     */
    List<ProgramTempOwnershipAudit> getProgramTempOwnershipAudits(ProgramTempOwnershipAuditQueryParams params);

    /**
     * Returns count of program temp ownership audits matching query params
     *
     * @param params program temp ownership audit query params
     * @return count of ProgramTempOwnershipAudit
     */
    int getProgramTempOwnershipAuditsCount(ProgramTempOwnershipAuditQueryParams params);
}

