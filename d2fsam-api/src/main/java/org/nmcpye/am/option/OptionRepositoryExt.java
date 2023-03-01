package org.nmcpye.am.option;

import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;

/**
 * Spring Data JPA repository for the Option entity.
 */
//@Repository
//public interface OptionRepositoryExt
//    extends OptionRepositoryExtCustom, JpaRepository<Option, Long> {
//}

public interface OptionRepositoryExt
    extends IdentifiableObjectStore<Option> {
    List<Option> getOptions(Long optionSetId, String key, Integer max);
}
