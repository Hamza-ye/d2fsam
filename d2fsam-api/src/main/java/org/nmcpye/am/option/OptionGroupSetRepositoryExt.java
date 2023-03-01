package org.nmcpye.am.option;

import org.nmcpye.am.common.GenericDimensionalObjectStore;

/**
 * Spring Data JPA repository for the OptionGroupSet entity.
 * <p>
 * When extending this class, extend OptionGroupSetRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface OptionGroupSetRepositoryExt
//    extends OptionGroupSetRepositoryExtCustom, JpaRepository<OptionGroupSet, Long> {
//}

public interface OptionGroupSetRepositoryExt
    extends GenericDimensionalObjectStore<OptionGroupSet> {
}
