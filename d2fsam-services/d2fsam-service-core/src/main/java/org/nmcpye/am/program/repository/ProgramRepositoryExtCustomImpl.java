package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository("org.nmcpye.am.program.ProgramRepositoryExt")
public class ProgramRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Program>
    implements ProgramRepositoryExt {

    public ProgramRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                          ApplicationEventPublisher publisher,
                                          CurrentUserService currentUserService,
                                          AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Program.class, currentUserService, aclService, true);
    }

    @Override
    public List<Program> getByType(ProgramType type) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters().addPredicate(root -> builder.equal(root.get("programType"), type)));
    }

    @Override
    public List<Program> get(OrganisationUnit organisationUnit) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(
            builder,
            newJpaParameters().addPredicate(root -> builder.equal(root.join("organisationUnits").get("id"), organisationUnit.getId()))
        );
    }

    @Override
    public List<Program> getByTrackedEntityType(TrackedEntityType trackedEntityType) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters().addPredicate(root -> builder.equal(root.get("trackedEntityType"), trackedEntityType)));
    }

    //    @Override
    //    public List<Program> getByDataEntryForm(DataEntryForm dataEntryForm) {
    //        if (dataEntryForm == null) {
    //            return Lists.newArrayList();
    //        }
    //
    //        final String hql = "from Program p where p.dataEntryForm = :dataEntryForm";
    //
    //        return getQuery(hql).setParameter("dataEntryForm", dataEntryForm).list();
    //    }

    public Boolean hasOrgUnit(Program program, OrganisationUnit organisationUnit) {
        NativeQuery<Long> query = getSession()
            .createNativeQuery(
                "select programid from program__organisation_units where programid = :pid and organisationunitid = :ouid"
            );
        query.setParameter("pid", program.getId());
        query.setParameter("ouid", organisationUnit.getId());

        return !query.getResultList().isEmpty();
    }
}
