package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramTempOwner;
import org.nmcpye.am.program.ProgramTempOwnerRepositoryExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.program.ProgramTempOwnerRepositoryExt")
public class ProgramTempOwnerRepositoryExtCustomImpl
    extends HibernateGenericStore<ProgramTempOwner>
    implements ProgramTempOwnerRepositoryExt {

    public ProgramTempOwnerRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                   ApplicationEventPublisher publisher) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramTempOwner.class, false);
    }

    @Override
    public void addProgramTempOwner(ProgramTempOwner programTempOwner) {
        getSession().save(programTempOwner);
    }

    @Override
    public int getValidTempOwnerCount(Program program, TrackedEntityInstance entityInstance, User user) {
//        final String sql =
//            "select count(*) from program_temp_owner " +
//                "where programid =" + program.getId() +
//                " and trackedentityinstanceid=" + entityInstance.getId() +
//                " and userid=" + user.getId() +
//                " and extract(epoch from validtill)-extract (epoch from now()::timestamp) > 0";
//
//        var count = jdbcTemplate.queryForObject(sql, Integer.class);
//
//        return jdbcTemplate.queryForObject(sql, Integer.class);

        Query<?> query = getSession().createNativeQuery("select count(*) from program_temp_owner " +
            "where programid=:pid and trackedentityinstanceid=:eid and userid=:uid " +
            "and extract(epoch from validtill)-extract (epoch from now()\\:\\:timestamp) > 0 ");
        query.setParameter("pid", program.getId());
        query.setParameter("eid", entityInstance.getId());
        query.setParameter("uid", user.getId());
        int count = ((Number) query.getSingleResult()).intValue();
        return count;
    }
}
