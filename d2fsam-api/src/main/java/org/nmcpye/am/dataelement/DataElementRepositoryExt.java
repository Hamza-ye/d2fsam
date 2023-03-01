package org.nmcpye.am.dataelement;

import org.nmcpye.am.common.GenericDimensionalObjectStore;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.user.User;

import java.util.List;

/**
 * Spring Data JPA repository for the DataElement entity.
 */
//@Repository
//public interface DataElementRepositoryExt
//    extends DataElementRepositoryExtCustom,
//    JpaRepository<DataElement, Long> {
//}

public interface DataElementRepositoryExt
    extends GenericDimensionalObjectStore<DataElement> {
    String ID = DataElementRepositoryExt.class.getName();

    /**
     * Returns all DataElement which zeroIsSignificant property is true or false
     *
     * @param zeroIsSignificant is zeroIsSignificant property
     * @return a collection of all DataElement
     */
    List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant);

    /**
     * Returns all DataElements of the given domain type.
     *
     * @param domainType the domain type.
     * @return all DataElements of the given domain type.
     */
    List<DataElement> getDataElementsByDomainType(DataElementDomain domainType);

    /**
     * Returns all DataElements of the given value type.
     *
     * @param valueType the value type.
     * @return all DataElements of the given value type.
     */
    List<DataElement> getDataElementsByValueType(ValueType valueType);

    /**
     * Returns all DataElements which are not member of any DataElementGroups.
     *
     * @return all DataElements which are not member of any DataElementGroups.
     */
    List<DataElement> getDataElementsWithoutGroups();

    /**
     * Returns all DataElements which have the given aggregation level assigned.
     *
     * @param aggregationLevel the aggregation level.
     * @return all DataElements which have the given aggregation level assigned.
     */
    List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel);

    DataElement getDataElement(String uid, User user);
}
