package org.nmcpye.am.dataelement;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorMessage;
import org.nmcpye.am.option.Option;
import org.nmcpye.am.option.OptionSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Service Implementation for managing {@link DataElement}.
 */
@Service("org.nmcpye.am.dataelement.DataElementServiceExt")
@Slf4j
public class DataElementServiceExtImpl implements DataElementServiceExt {

    private static final int VALUE_MAX_LENGTH = 50000;

    private static final Set<String> VALID_IMAGE_FORMATS = ImmutableSet.<String>builder().add(ImageIO.getReaderFormatNames()).build();

    private final DataElementRepositoryExt dataElementStore;

    private final IdentifiableObjectStore<OptionSet> optionSetStore;

    private final DataElementGroupRepositoryExt dataElementGroupStore;

    private final DataElementGroupSetRepositoryExt dataElementGroupSetStore;

    public DataElementServiceExtImpl(
        DataElementRepositoryExt dataElementStore,
        IdentifiableObjectStore<OptionSet> optionSetStore,
        DataElementGroupRepositoryExt dataElementGroupStore,
        DataElementGroupSetRepositoryExt dataElementGroupSetStore) {
        this.dataElementStore = dataElementStore;
        this.optionSetStore = optionSetStore;
        this.dataElementGroupStore = dataElementGroupStore;
        this.dataElementGroupSetStore = dataElementGroupSetStore;
    }

    @Override
    @Transactional
    public Long addDataElement(DataElement dataElement) {
        validateDateElement(dataElement);
        dataElementStore.saveObject(dataElement);
        return dataElement.getId();
    }

    @Override
    @Transactional
    public void updateDataElement(DataElement dataElement) {
        validateDateElement(dataElement);
        dataElementStore.update(dataElement);
    }

    @Override
    public void validateDateElement(DataElement dataElement) {
        if (dataElement.getOptionSet() == null) {
            if (dataElement.getValueType() == ValueType.MULTI_TEXT) {
                throw new IllegalQueryException(new ErrorMessage(ErrorCode.E1116, dataElement.getUid()));
            }
            return;
        }
        // need to reload the options in case this is a create and the options
        // is a shallow reference object
        OptionSet options = optionSetStore.getByUid(dataElement.getOptionSet().getUid());
        if (options.getValueType() != dataElement.getValueType()) {
            throw new IllegalQueryException(new ErrorMessage(ErrorCode.E1115, options.getValueType()));
        }
        if (dataElement.getValueType() == ValueType.MULTI_TEXT && options.getOptions().stream().anyMatch(
            option -> option.getCode().contains(ValueType.MULTI_TEXT_SEPARATOR))) {
            throw new IllegalQueryException(new ErrorMessage(ErrorCode.E1117, dataElement.getUid(), options.getUid(),
                options.getOptions().stream().map(Option::getCode)
                    .filter(code -> code.contains(ValueType.MULTI_TEXT_SEPARATOR))
                    .findFirst().orElse("")));
        }
    }

    @Override
    @Transactional
    public void deleteDataElement(DataElement dataElement) {
        dataElementStore.deleteObject(dataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElement getDataElement(Long id) {
        return dataElementStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElement getDataElement(String uid) {
        return dataElementStore.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElement getDataElementByCode(String code) {
        return dataElementStore.getByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getAllDataElements() {
        return dataElementStore.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getAllDataElementsByValueType(ValueType valueType) {
        return dataElementStore.getDataElementsByValueType(valueType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant) {
        return dataElementStore.getDataElementsByZeroIsSignificant(zeroIsSignificant);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<DataElement> getDataElementsByPeriodType(final PeriodType periodType) {
//        return getAllDataElements().stream()
//            .filter(p -> p.getPeriodType() != null && p.getPeriodType().equals(periodType))
//            .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getDataElementsByDomainType(DataElementDomain domainType) {
        return dataElementStore.getDataElementsByDomainType(domainType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getDataElementsWithoutGroups() {
        return dataElementStore.getDataElementsWithoutGroups();
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<DataElement> getDataElementsWithoutDataSets() {
//        return dataElementStore.getDataElementsWithoutDataSets();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<DataElement> getDataElementsWithDataSets() {
//        return dataElementStore.getDataElementsWithDataSets();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel) {
        return dataElementStore.getDataElementsByAggregationLevel(aggregationLevel);
    }

    // -------------------------------------------------------------------------
    // DataElementGroup
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long addDataElementGroup(DataElementGroup dataElementGroup) {
        dataElementGroupStore.saveObject(dataElementGroup);

        return dataElementGroup.getId();
    }

    @Override
    @Transactional
    public void updateDataElementGroup(DataElementGroup dataElementGroup) {
        dataElementGroupStore.update(dataElementGroup);
    }

    @Override
    @Transactional
    public void deleteDataElementGroup(DataElementGroup dataElementGroup) {
        dataElementGroupStore.deleteObject(dataElementGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroup getDataElementGroup(Long id) {
        return dataElementGroupStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElementGroup> getDataElementGroupsByUid(Collection<String> uids) {
        return dataElementGroupStore.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroup getDataElementGroup(String uid) {
        return dataElementGroupStore.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElementGroup> getAllDataElementGroups() {
        return dataElementGroupStore.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroup getDataElementGroupByName(String name) {
        List<DataElementGroup> dataElementGroups = dataElementGroupStore.getAllEqName(name);

        return !dataElementGroups.isEmpty() ? dataElementGroups.get(0) : null;
    }

    // -------------------------------------------------------------------------
    // DataElementGroupSet
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long addDataElementGroupSet(DataElementGroupSet groupSet) {
        dataElementGroupSetStore.saveObject(groupSet);

        return groupSet.getId();
    }

    @Override
    @Transactional
    public void updateDataElementGroupSet(DataElementGroupSet groupSet) {
        dataElementGroupSetStore.update(groupSet);
    }

    @Override
    @Transactional
    public void deleteDataElementGroupSet(DataElementGroupSet groupSet) {
        dataElementGroupSetStore.deleteObject(groupSet);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroupSet getDataElementGroupSet(Long id) {
        return dataElementGroupSetStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroupSet getDataElementGroupSet(String uid) {
        return dataElementGroupSetStore.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElementGroupSet getDataElementGroupSetByName(String name) {
        List<DataElementGroupSet> dataElementGroupSets = dataElementGroupSetStore.getAllEqName(name);

        return !dataElementGroupSets.isEmpty() ? dataElementGroupSets.get(0) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElementGroupSet> getAllDataElementGroupSets() {
        return dataElementGroupSetStore.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getByAttributeAndValue(Attribute attribute, String value) {
        return dataElementStore.getByAttributeAndValue(attribute, value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataElement> getByAttribute(Attribute attribute) {
        return dataElementStore.getByAttribute(attribute);
    }

    @Override
    @Transactional(readOnly = true)
    public DataElement getByUniqueAttributeValue(Attribute attribute, String value) {
        return dataElementStore.getByUniqueAttributeValue(attribute, value);
    }
}
