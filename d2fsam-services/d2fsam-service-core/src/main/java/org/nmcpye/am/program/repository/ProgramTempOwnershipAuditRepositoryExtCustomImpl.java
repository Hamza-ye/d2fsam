package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.hibernate.JpaQueryParameters;
import org.nmcpye.am.program.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository("org.nmcpye.am.program.ProgramTempOwnershipAuditRepositoryExt")
public class ProgramTempOwnershipAuditRepositoryExtCustomImpl
    extends HibernateGenericStore<ProgramTempOwnershipAudit>
    implements ProgramTempOwnershipAuditRepositoryExt {

    public ProgramTempOwnershipAuditRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                            ApplicationEventPublisher publisher) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramTempOwnershipAudit.class, false);
    }

    @Override
    public void addProgramTempOwnershipAudit(ProgramTempOwnershipAudit programTempOwnershipAudit) {
        getSession().save(programTempOwnershipAudit);
    }

    @Override
    public void deleteProgramTempOwnershipAudit(Program program) {
        String hql = "delete ProgramTempOwnershipAudit where program = :program";
        getSession().createQuery(hql).setParameter("program", program).executeUpdate();
    }

    @Override
    public List<ProgramTempOwnershipAudit> getProgramTempOwnershipAudits(ProgramTempOwnershipAuditQueryParams params) {
        CriteriaBuilder builder = getCriteriaBuilder();

        JpaQueryParameters<ProgramTempOwnershipAudit> jpaParameters = newJpaParameters()
            .addPredicates(getProgramTempOwnershipAuditPredicates(params, builder))
            .addOrder(root -> builder.desc(root.get("created")));

        if (!params.isSkipPaging()) {
            jpaParameters.setFirstResult(params.getFirst()).setMaxResults(params.getMax());
        }

        return getList(builder, jpaParameters);
    }

    @Override
    public int getProgramTempOwnershipAuditsCount(ProgramTempOwnershipAuditQueryParams params) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getCount(builder, newJpaParameters()
            .addPredicates(getProgramTempOwnershipAuditPredicates(params, builder))
            .count(root -> builder.countDistinct(root.get("id")))).intValue();
    }

    private List<Function<Root<ProgramTempOwnershipAudit>, Predicate>> getProgramTempOwnershipAuditPredicates(
        ProgramTempOwnershipAuditQueryParams params, CriteriaBuilder builder) {
        List<Function<Root<ProgramTempOwnershipAudit>, Predicate>> predicates = new ArrayList<>();

        if (params.hasUsers()) {
            predicates.add(root -> root.get("accessedBy").in(params.getUsers()));
        }

        if (params.hasStartDate()) {
            predicates.add(root -> builder.greaterThanOrEqualTo(root.get("created"), params.getStartDate()));
        }

        if (params.hasEndDate()) {
            predicates.add(root -> builder.lessThanOrEqualTo(root.get("created"), params.getEndDate()));
        }

        if (params.hasPrograms()) {
            predicates.add(root -> root.get("program").in(params.getPrograms()));
        }

        return predicates;
    }
}
