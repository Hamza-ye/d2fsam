package org.nmcpye.am.dataelement;

import org.nmcpye.am.common.GenericDimensionalObjectStore;

/**
 * Spring Data JPA repository for the DataElement entity.
 */
//@Repository
//public interface DataElementGroupSetRepositoryExt
//    extends DataElementGroupSetRepositoryExtCustom, JpaRepository<DataElementGroupSet, Long> {
//}

public interface DataElementGroupSetRepositoryExt
    extends GenericDimensionalObjectStore<DataElementGroupSet> {
}
