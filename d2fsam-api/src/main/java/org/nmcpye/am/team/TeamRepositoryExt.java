package org.nmcpye.am.team;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the Team entity.
 * <p>
 * When extending this class, extend TeamRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface TeamRepositoryExt
//    extends TeamRepositoryExtCustom, JpaRepository<Team, Long> {
//}

public interface TeamRepositoryExt extends IdentifiableObjectStore<Team> {
    final String USER_TEAM_MEMBERSHIP_RELATION_NAME = "team__members";
    final String TEAM_MANAGED_TEAM_RELATION_NAME = "team__managed_teams";
}
