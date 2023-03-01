package org.nmcpye.am.datavalue;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static org.nmcpye.am.external.conf.ConfigurationKey.CHANGELOG_AGGREGATE;
import static org.nmcpye.am.system.util.ValidationUtils.dataValueIsValid;
import static org.nmcpye.am.system.util.ValidationUtils.dataValueIsZeroAndInsignificant;


@Service("org.nmcpye.am.datavalue.DataValueServiceExt")
@Slf4j
public class DataValueServiceExtImpl implements DataValueServiceExt {

    private final DataValueRepositoryExt dataValueStore;

    private final DataValueAuditService dataValueAuditService;

    private final CurrentUserService currentUserService;

    private final AmConfigurationProvider config;

    public DataValueServiceExtImpl(DataValueRepositoryExt dataValueRepositoryExt,
                                   DataValueAuditService dataValueAuditService,
                                   CurrentUserService currentUserService,
                                   AmConfigurationProvider config) {
        this.dataValueStore = dataValueRepositoryExt;
        this.dataValueAuditService = dataValueAuditService;
        this.currentUserService = currentUserService;
        this.config = config;
    }

    @Override
    @Transactional
    public boolean addDataValue(DataValue dataValue) {
        // ---------------------------------------------------------------------
        // Validation
        // ---------------------------------------------------------------------

        if (dataValue == null || dataValue.isNullValue()) {
            log.info("Data value is null");
            return false;
        }

        String result = dataValueIsValid(dataValue.getValue(), dataValue.getDataElement());

        if (result != null) {
            log.info("Data value is not valid: " + result);
            return false;
        }

        boolean zeroInsignificant = dataValueIsZeroAndInsignificant(dataValue.getValue(), dataValue.getDataElement());

        if (zeroInsignificant) {
            log.info("Data value is zero and insignificant");
            return false;
        }

        // ---------------------------------------------------------------------
        // Set default category option combo if null
        // ---------------------------------------------------------------------

//        if (dataValue.getCategoryOptionCombo() == null) {
//            dataValue.setCategoryOptionCombo(categoryService.getDefaultCategoryOptionCombo());
//        }
//
//        if (dataValue.getAttributeOptionCombo() == null) {
//            dataValue.setAttributeOptionCombo(categoryService.getDefaultCategoryOptionCombo());
//        }

        // ---------------------------------------------------------------------
        // Check and restore soft deleted value
        // ---------------------------------------------------------------------

        DataValue softDelete = dataValueStore.getSoftDeletedDataValue(dataValue);

        Instant currentDate = Instant.now();

        if (softDelete == null) {
            dataValue.setCreated(currentDate);
            dataValue.setUpdated(currentDate);
            dataValueStore.addDataValue(dataValue);
        } else {
            // don't let original created date be overwritten
            Instant created = softDelete.getCreated();
            softDelete.mergeWith(dataValue);
            softDelete.setDeleted(false);
            softDelete.setCreated(created);
            softDelete.setUpdated(currentDate);

            dataValueStore.updateDataValue(softDelete);

            if (config.isEnabled(CHANGELOG_AGGREGATE)) {
                DataValueAudit dataValueAudit = new DataValueAudit(dataValue, dataValue.getValue(),
                    dataValue.getStoredBy(), AuditType.CREATE);

                dataValueAuditService.addDataValueAudit(dataValueAudit);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public void updateDataValue(DataValue dataValue) {
        if (dataValue.isNullValue() ||
            dataValueIsZeroAndInsignificant(dataValue.getValue(), dataValue.getDataElement())) {
            deleteDataValue(dataValue);
        } else if (dataValueIsValid(dataValue.getValue(), dataValue.getDataElement()) == null) {
            dataValue.setUpdated(Instant.now());

            if (config.isEnabled(CHANGELOG_AGGREGATE)
                && !Objects.equals(dataValue.getAuditValue(), dataValue.getValue())) {
                DataValueAudit dataValueAudit = new DataValueAudit(dataValue, dataValue.getAuditValue(),
                    dataValue.getStoredBy(), AuditType.UPDATE);

                dataValueAuditService.addDataValueAudit(dataValueAudit);
            }

            dataValueStore.updateDataValue(dataValue);
        }
    }

    @Override
    @Transactional
    public void updateDataValues(List<DataValue> dataValues) {
        if (dataValues != null && !dataValues.isEmpty()) {
            for (DataValue dataValue : dataValues) {
                updateDataValue(dataValue);
            }
        }
    }

    @Override
    @Transactional
    public void deleteDataValue(DataValue dataValue) {
        if (config.isEnabled(CHANGELOG_AGGREGATE)) {
            DataValueAudit dataValueAudit = new DataValueAudit(dataValue, dataValue.getAuditValue(),
                currentUserService.getCurrentUsername(), AuditType.DELETE);

            dataValueAuditService.addDataValueAudit(dataValueAudit);
        }

        dataValue.setUpdated(Instant.now());
        dataValue.setDeleted(true);

        dataValueStore.updateDataValue(dataValue);
    }

    @Override
    @Transactional
    public void deleteDataValues(OrganisationUnit organisationUnit) {
        dataValueStore.deleteDataValues(organisationUnit);
    }

    @Override
    @Transactional
    public void deleteDataValues(DataElement dataElement) {
        dataValueStore.deleteDataValues(dataElement);
    }

    @Override
    @Transactional(readOnly = true)
    public DataValue getDataValue(DataElement dataElement, Period period, OrganisationUnit source) {

        return dataValueStore.getDataValue(dataElement, period, source);
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<DataValue> getAllDataValues() {
        return dataValueStore.getAllDataValues();
    }

    @Override
    @Transactional(readOnly = true)
    public int getDataValueCount(int days) {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add(Calendar.DAY_OF_YEAR, (days * -1));

        return dataValueStore.getDataValueCountLastUpdatedBetween(DateUtils.instantFromDate(cal.getTime()),
            null, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getDataValueCountLastUpdatedAfter(Instant date, boolean includeDeleted) {
        return dataValueStore.getDataValueCountLastUpdatedBetween(date, null, includeDeleted);
    }

    @Override
    @Transactional(readOnly = true)
    public int getDataValueCountLastUpdatedBetween(Instant startDate, Instant endDate, boolean includeDeleted) {
        return dataValueStore.getDataValueCountLastUpdatedBetween(startDate, endDate, includeDeleted);
    }
}
