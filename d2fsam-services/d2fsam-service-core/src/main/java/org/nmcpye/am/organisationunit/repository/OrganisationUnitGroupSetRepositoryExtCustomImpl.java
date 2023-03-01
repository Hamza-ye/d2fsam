package org.nmcpye.am.organisationunit.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupSetRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.organisationunit.OrganisationUnitGroupSetRepositoryExt")
public class OrganisationUnitGroupSetRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OrganisationUnitGroupSet>
    implements OrganisationUnitGroupSetRepositoryExt {

    public OrganisationUnitGroupSetRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                           ApplicationEventPublisher publisher,
                                                           CurrentUserService currentUserService,
                                                           AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OrganisationUnitGroupSet.class, currentUserService,
            aclService, true);
    }
}
