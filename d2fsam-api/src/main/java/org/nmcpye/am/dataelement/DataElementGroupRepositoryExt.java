package org.nmcpye.am.dataelement;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the DataElement entity.
 */
//@Repository
//public interface DataElementGroupRepositoryExt
//    extends DataElementGroupRepositoryExtCustom, JpaRepository<DataElementGroup, Long> {
//}

public interface DataElementGroupRepositoryExt
    extends IdentifiableObjectStore<DataElementGroup> {
}
