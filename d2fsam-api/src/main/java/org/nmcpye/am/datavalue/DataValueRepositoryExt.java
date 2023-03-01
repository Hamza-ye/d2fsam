package org.nmcpye.am.datavalue;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spring Data JPA repository for the DataValue entity.
 */
//@Repository
//public interface DataValueRepositoryExt
//    extends DataValueRepositoryCustomExt, JpaRepository<DataValue, Long> {
//}

public interface DataValueRepositoryExt {
    String ID = DataValueRepositoryExt.class.getName();

    /**
     * Special {@see DeflatedDataValue} to signal "End of file" for queued DDVs.
     */
    public static final DeflatedDataValue END_OF_DDV_DATA = new DeflatedDataValue();

    /**
     * Timeout value for {@see DeflatedDataValue} queue, to prevent waiting
     * forever if the other thread has aborted.
     */
    public static final int DDV_QUEUE_TIMEOUT_VALUE = 10;

    /**
     * Timeout unit for {@see DeflatedDataValue} queue, to prevent waiting
     * forever if the other thread has aborted.
     */
    public static final TimeUnit DDV_QUEUE_TIMEOUT_UNIT = TimeUnit.MINUTES;

    /**
     * Adds a DataValue.
     *
     * @param dataValue the DataValue to add.
     */
    void addDataValue(DataValue dataValue);

    /**
     * Updates a DataValue.
     *
     * @param dataValue the DataValue to update.
     */
    void updateDataValue(DataValue dataValue);

    /**
     * Deletes all data values for the given organisation unit.
     *
     * @param organisationUnit the organisation unit.
     */
    void deleteDataValues(OrganisationUnit organisationUnit);

    /**
     * Deletes all data values for the given data element.
     *
     * @param dataElement the data element.
     */
    void deleteDataValues(DataElement dataElement);

    /**
     * Returns a DataValue.
     *
     * @param dataElement the DataElement of the DataValue.
     * @param period      the Period of the DataValue.
     * @param source      the Source of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     * if no match.
     */
    DataValue getDataValue(DataElement dataElement, Period period, OrganisationUnit source/*,
                           CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo*/);

    /**
     * Returns a DataValue.
     *
     * @param dataElement    the DataElement of the DataValue.
     * @param period         the Period of the DataValue.
     * @param source         the Source of the DataValue.
     * @param includeDeleted Include deleted data values
     * @return the DataValue which corresponds to the given parameters, or null
     * if no match.
     */
    DataValue getDataValue(DataElement dataElement, Period period, OrganisationUnit source,
        /*CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo,*/ boolean includeDeleted);

    /**
     * Returns a soft deleted DataValue.
     *
     * @param dataValue the DataValue to use as parameters.
     * @return the DataValue which corresponds to the given parameters, or null
     * if no match.
     */
    DataValue getSoftDeletedDataValue(DataValue dataValue);

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

//    /**
//     * Returns data values for the given data export parameters.
//     *
//     * @param params the data export parameters.
//     * @return a list of data values.
//     */
//    List<DataValue> getDataValues(DataExportParams params);

    /**
     * Returns all DataValues.
     *
     * @return a list of all DataValues.
     */
    List<DataValue> getAllDataValues();

//    /**
//     * Returns deflated data values for the given data export parameters.
//     *
//     * @param params the data export parameters.
//     * @return a list of deflated data values.
//     */
//    List<DeflatedDataValue> getDeflatedDataValues(DataExportParams params);

    /**
     * Gets the number of DataValues which have been updated between the given
     * start and end date. Either the start or end date can be null, but they
     * cannot both be null.
     *
     * @param startDate      the start date to compare against data value last
     *                       updated.
     * @param endDate        the end date to compare against data value last updated.
     * @param includeDeleted whether to include deleted data values.
     * @return the number of DataValues.
     */
    int getDataValueCountLastUpdatedBetween(Instant startDate, Instant endDate, boolean includeDeleted);
//
//    /**
//     * Checks if any data values exist for the provided {@link CategoryCombo}.
//     *
//     * @param combo the combo to check
//     * @return true, if any value exist, otherwise false
//     */
//    boolean dataValueExists(CategoryCombo combo);
}
