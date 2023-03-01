package org.nmcpye.am.organisationunit;

import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the OrganisationUnitGroup entity.
 * <p>
 * When extending this class, extend OrganisationUnitGroupRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface OrganisationUnitGroupRepositoryExt
//    extends OrganisationUnitGroupRepositoryExtCustom,
//    JpaRepository<OrganisationUnitGroup, Long> {
//}

public interface OrganisationUnitGroupRepositoryExt
    extends IdentifiableObjectStore<OrganisationUnitGroup> {
    List<OrganisationUnitGroup> getOrganisationUnitGroupsWithGroupSets();

    OrganisationUnitGroup getOrgUnitGroupInGroupSet(Set<OrganisationUnitGroup> groups, OrganisationUnitGroupSet groupSet);

    /**
     * Gets all organisation unit groups which are not assigned to any group
     * set.
     */
    List<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets();
}
