package org.nmcpye.am.datavalue;

import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service("org.nmcpye.am.datavalue.DataValueAuditService")
public class DataValueAuditServiceImpl implements DataValueAuditService {

    private final DataValueAuditRepositoryExt dataValueAuditStore;

    private final DataValueRepositoryExt dataValueStore;


    public DataValueAuditServiceImpl(DataValueAuditRepositoryExt dataValueAuditStore,
                                     DataValueRepositoryExt dataValueStore) {
        checkNotNull(dataValueAuditStore);
        checkNotNull(dataValueStore);

        this.dataValueAuditStore = dataValueAuditStore;
        this.dataValueStore = dataValueStore;
    }

    // -------------------------------------------------------------------------
    // DataValueAuditService implementation
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void addDataValueAudit(DataValueAudit dataValueAudit) {
        dataValueAuditStore.addDataValueAudit(dataValueAudit);
    }

    @Override
    @Transactional
    public void deleteDataValueAudits(OrganisationUnit organisationUnit) {
        dataValueAuditStore.deleteDataValueAudits(organisationUnit);
    }

    @Override
    @Transactional
    public void deleteDataValueAudits(DataElement dataElement) {
        dataValueAuditStore.deleteDataValueAudits(dataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataValueAudit> getDataValueAudits(DataValue dataValue) {
        DataValueAuditQueryParams params = new DataValueAuditQueryParams()
            .setDataElements(List.of(dataValue.getDataElement()))
            .setPeriods(List.of(dataValue.getPeriod()))
            .setOrgUnits(List.of(dataValue.getSource()));
//            .setCategoryOptionCombo(dataValue.getCategoryOptionCombo())
//            .setAttributeOptionCombo(dataValue.getAttributeOptionCombo());

        return dataValueAuditStore.getDataValueAudits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataValueAudit> getDataValueAudits(DataElement dataElement, Period period,
                                                   OrganisationUnit organisationUnit) {

        DataValue dataValue = dataValueStore.getDataValue(dataElement, period, organisationUnit, true);

        if (dataValue == null) {
            return List.of();
        }

        DataValueAuditQueryParams params = new DataValueAuditQueryParams()
            .setDataElements(List.of(dataElement))
            .setPeriods(List.of(period))
            .setOrgUnits(List.of(organisationUnit));
//            .setCategoryOptionCombo(coc)
//            .setAttributeOptionCombo(aoc);

        List<DataValueAudit> dataValueAudits = dataValueAuditStore.getDataValueAudits(params).stream()
            .map(x -> DataValueAudit.from(x, x.getCreated()))
            .collect(Collectors.toList());

        if (dataValueAudits.isEmpty()) {
            dataValueAudits.add(createDataValueAudit(dataValue));
            return dataValueAudits;
        }

        // case if the audit trail started out with DELETE
        if (dataValueAudits.get(dataValueAudits.size() - 1).getAuditType() == AuditType.DELETE) {
            DataValueAudit valueAudit = createDataValueAudit(dataValue);
            valueAudit.setValue(dataValueAudits.get(dataValueAudits.size() - 1).getValue());
            dataValueAudits.add(valueAudit);
        }

        // unless top is CREATE, inject current DV as audit on top
        if (!dataValue.getDeleted()
            && dataValueAudits.get(0).getAuditType() != AuditType.CREATE) {
            DataValueAudit dataValueAudit = createDataValueAudit(dataValue);
            dataValueAudit.setAuditType(AuditType.UPDATE);
            dataValueAudit.setCreated(dataValue.getUpdated());
            dataValueAudits.add(0, dataValueAudit);
        }

        dataValueAudits.get(dataValueAudits.size() - 1).setAuditType(AuditType.CREATE);

        return dataValueAudits;
    }

    private static DataValueAudit createDataValueAudit(DataValue dataValue) {
        DataValueAudit dataValueAudit = new DataValueAudit(dataValue, dataValue.getValue(),
            dataValue.getStoredBy(), AuditType.CREATE);
        dataValueAudit.setCreated(dataValue.getCreated());

        return dataValueAudit;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataValueAudit> getDataValueAudits(DataValueAuditQueryParams params) {
        return dataValueAuditStore.getDataValueAudits(params);
    }

    @Override
    @Transactional(readOnly = true)
    public int countDataValueAudits(DataValueAuditQueryParams params) {
        return dataValueAuditStore.countDataValueAudits(params);
    }
}
