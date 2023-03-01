package org.nmcpye.am.option;

import org.nmcpye.am.common.DataDimensionType;
import org.nmcpye.am.common.GenericDimensionalObjectStore;

import java.util.List;

/**
 * Spring Data JPA repository for the OptionGroup entity.
 * <p>
 * When extending this class, extend OptionGroupRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
//@Repository
//public interface OptionGroupRepositoryExt
//    extends OptionGroupRepositoryExtCustom, JpaRepository<OptionGroup, Long> {
//}

public interface OptionGroupRepositoryExt
    extends GenericDimensionalObjectStore<OptionGroup> {
    List<OptionGroup> getOptionGroups(OptionGroupSet groupSet);

    List<OptionGroup> getOptionGroupsByOptionId(String optionId);

    List<OptionGroup> getOptionGroupsNoAcl(DataDimensionType dataDimensionType, boolean dataDimension);
}

