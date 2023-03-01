package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramTrackedEntityAttribute;
import org.nmcpye.am.program.ProgramTrackedEntityAttributeRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository("org.nmcpye.am.program.ProgramTrackedEntityAttributeRepositoryExt")
public class ProgramTrackedEntityAttributeRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<ProgramTrackedEntityAttribute>
    implements ProgramTrackedEntityAttributeRepositoryExt {

    public ProgramTrackedEntityAttributeRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                                ApplicationEventPublisher publisher,
                                                                CurrentUserService currentUserService,
                                                                AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramTrackedEntityAttribute.class,
            currentUserService, aclService, true);
    }

    @Override
    public ProgramTrackedEntityAttribute get(Program program, TrackedEntityAttribute attribute) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("program"), program))
            .addPredicate(root -> builder.equal(root.get("attribute"), attribute)));
    }

    @Override
    public List<TrackedEntityAttribute> getAttributes(List<Program> programs) {
        CriteriaBuilder builder = getCriteriaBuilder();

        CriteriaQuery<TrackedEntityAttribute> query = builder.createQuery(TrackedEntityAttribute.class);
        Root<ProgramTrackedEntityAttribute> root = query.from(ProgramTrackedEntityAttribute.class);
        query.select(root.get("attribute"));
        query.where(root.get("program").in(programs));
        query.distinct(true);

        return getSession().createQuery(query).getResultList();
    }
}
