package org.nmcpye.am.organisationunit;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the OrganisationUnitLevel entity.
 */
//@SuppressWarnings("unused")
//@Repository
//public interface OrganisationUnitLevelRepositoryExt
//    extends OrganisationUnitLevelRepositoryExtCustom, JpaRepository<OrganisationUnitLevel, Long> {
//}

public interface OrganisationUnitLevelRepositoryExt
    extends IdentifiableObjectStore<OrganisationUnitLevel> {

    String ID = OrganisationUnitLevelRepositoryExt.class.getName();

    /**
     * Deletes all OrganisationUnitLevels.
     */
    void deleteAll();

    /**
     * Gets the OrganisationUnitLevel at the given level.
     *
     * @param level the level.
     * @return the OrganisationUnitLevel at the given level.
     */
    OrganisationUnitLevel getByLevel(int level);
}
