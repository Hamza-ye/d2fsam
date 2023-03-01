package org.nmcpye.am.program.repository;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository("org.nmcpye.am.program.ProgramStageRepositoryExt")
public class ProgramStageRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<ProgramStage>
    implements ProgramStageRepositoryExt {

    public ProgramStageRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                               ApplicationEventPublisher publisher,
                                               CurrentUserService currentUserService,
                                               AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramStage.class, currentUserService, aclService, true);
    }

    @Override
    public ProgramStage getByNameAndProgram(String name, Program program) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(
            builder,
            newJpaParameters()
                .addPredicate(root -> builder.equal(root.get("name"), name))
                .addPredicate(root -> builder.equal(root.get("program"), program))
        );
    }

    @Override
    public List<ProgramStage> getByProgram(Program program) {
        if (program == null) {
            return Lists.newArrayList();
        }

        final String hql = "from ProgramStage p where p.program = :program";

        return getQuery(hql).setParameter("program", program).list();
    }
}
