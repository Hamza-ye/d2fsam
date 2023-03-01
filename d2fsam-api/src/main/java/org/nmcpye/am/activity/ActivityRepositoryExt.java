package org.nmcpye.am.activity;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the Activity entity.
 * <p>
 * When extending this class, extend ActivityRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface ActivityRepositoryExt
//    extends ActivityRepositoryExtCustom, JpaRepository<Activity, Long> {
//}
public interface ActivityRepositoryExt extends IdentifiableObjectStore<Activity> {

}
