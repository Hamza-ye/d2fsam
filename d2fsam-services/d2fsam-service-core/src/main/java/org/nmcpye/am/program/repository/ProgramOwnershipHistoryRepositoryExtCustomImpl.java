package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.program.ProgramOwnershipHistory;
import org.nmcpye.am.program.ProgramOwnershipHistoryRepositoryExt;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.program.ProgramOwnershipHistoryRepositoryExt")
public class ProgramOwnershipHistoryRepositoryExtCustomImpl
    implements ProgramOwnershipHistoryRepositoryExt {

    private final SessionFactory sessionFactory;

    public ProgramOwnershipHistoryRepositoryExtCustomImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void addProgramOwnershipHistory(ProgramOwnershipHistory programOwnershipHistory) {
        sessionFactory.getCurrentSession().save(programOwnershipHistory);
    }
}
