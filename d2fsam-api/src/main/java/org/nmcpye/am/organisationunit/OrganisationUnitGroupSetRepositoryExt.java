package org.nmcpye.am.organisationunit;

import org.nmcpye.am.common.GenericDimensionalObjectStore;

/**
 * Spring Data JPA repository for the OrganisationUnitGroupSet entity.
 * <p>
 * When extending this class, extend OrganisationUnitGroupSetRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface OrganisationUnitGroupSetRepositoryExt
//    extends OrganisationUnitGroupSetRepositoryExtCustom, JpaRepository<OrganisationUnitGroupSet, Long> {
//}

public interface OrganisationUnitGroupSetRepositoryExt
    extends GenericDimensionalObjectStore<OrganisationUnitGroupSet> {
}
