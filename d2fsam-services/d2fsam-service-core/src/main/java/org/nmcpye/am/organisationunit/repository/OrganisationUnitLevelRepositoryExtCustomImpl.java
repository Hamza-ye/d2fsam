package org.nmcpye.am.organisationunit.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.organisationunit.OrganisationUnitLevel;
import org.nmcpye.am.organisationunit.OrganisationUnitLevelRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository("org.nmcpye.am.organisationunit.OrganisationUnitLevelRepositoryExt")
public class OrganisationUnitLevelRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OrganisationUnitLevel>
    implements OrganisationUnitLevelRepositoryExt {

    public OrganisationUnitLevelRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                        ApplicationEventPublisher publisher,
                                                        CurrentUserService userServiceExt,
                                                        AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OrganisationUnitLevel.class, userServiceExt,
            aclService, true);
    }

    @Override
    public void deleteAll() {
        String hql = "delete from OrganisationUnitLevel";

        getQuery(hql).executeUpdate();
    }

    @Override
    public OrganisationUnitLevel getByLevel(int level) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("level"), level)));
    }
}
