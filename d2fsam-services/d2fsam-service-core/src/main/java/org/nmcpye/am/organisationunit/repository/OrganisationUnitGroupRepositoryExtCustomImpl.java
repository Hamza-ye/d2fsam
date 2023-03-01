package org.nmcpye.am.organisationunit.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.organisationunit.OrganisationUnitGroup;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupRepositoryExt;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository("org.nmcpye.am.organisationunit.OrganisationUnitGroupRepositoryExt")
public class OrganisationUnitGroupRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OrganisationUnitGroup>
    implements OrganisationUnitGroupRepositoryExt {

    public OrganisationUnitGroupRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                        ApplicationEventPublisher publisher,
                                                        CurrentUserService currentUserService,
                                                        AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OrganisationUnitGroup.class, currentUserService,
            aclService, true);
    }

    /////////////////////////////
    ////////////////////////////
    @Override
    public List<OrganisationUnitGroup> getOrganisationUnitGroupsWithGroupSets() {
        return getQuery("from OrganisationUnitGroup o where size(o.groupSets) > 0").list();
    }

    @Override
    public List<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets() {
        return getQuery("from OrganisationUnitGroup g where size(g.groupSets) = 0").list();
    }

    @Override
    public OrganisationUnitGroup getOrgUnitGroupInGroupSet(Set<OrganisationUnitGroup> groups, OrganisationUnitGroupSet groupSet) {
        return getQuery("select g from OrganisationUnitGroup g inner join g.groupSets gs where gs = :groupSet and g in :groups")
            .setParameter("groupSet", groupSet)
            .setParameter("groups", groups)
            .setMaxResults(1)
            .uniqueResult();
    }
}
