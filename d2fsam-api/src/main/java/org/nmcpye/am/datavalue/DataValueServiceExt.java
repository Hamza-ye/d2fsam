package org.nmcpye.am.datavalue;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;

import java.time.Instant;
import java.util.List;

/**
 * Service Interface for managing {@link DataValue}.
 */
public interface DataValueServiceExt {

    /**
     * Adds a DataValue. If both the value and the comment properties of the
     * specified DataValue object are null, then the object should not be
     * persisted. The value will be validated and not be saved if not passing
     * validation.
     *
     * @param dataValue the DataValue to add.
     * @return false whether the data value is null or invalid, true if value is
     * valid and attempted to be saved.
     */
    boolean addDataValue(DataValue dataValue);

    /**
     * Updates a DataValue. If both the value and the comment properties of the
     * specified DataValue object are null, then the object should be deleted
     * from the underlying storage.
     *
     * @param dataValue the DataValue to update.
     */
    void updateDataValue(DataValue dataValue);

    /**
     * Updates multiple DataValues. If both the value and the comment properties
     * of the specified DataValue object are null, then the object should be
     * deleted from the underlying storage.
     *
     * @param dataValues list of DataValues to update.
     */
    void updateDataValues(List<DataValue> dataValues);

    /**
     * Deletes a DataValue.
     *
     * @param dataValue the DataValue to delete.
     */
    void deleteDataValue(DataValue dataValue);

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
                           CategoryOptionCombo optionCombo*/);

    // -------------------------------------------------------------------------
    // Lists of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DataValues.
     *
     * @return a collection of all DataValues.
     */
    List<DataValue> getAllDataValues();

    /**
     * Gets the number of DataValues persisted since the given number of days.
     *
     * @param days the number of days since now to include in the count.
     * @return the number of DataValues.
     */
    int getDataValueCount(int days);

    /**
     * Gets the number of DataValues which have been updated after the given
     * date time.
     *
     * @param date           the date time.
     * @param includeDeleted whether to include deleted data values.
     * @return the number of DataValues.
     */
    int getDataValueCountLastUpdatedAfter(Instant date, boolean includeDeleted);

    /**
     * Gets the number of DataValues which have been updated between the given
     * start and end date. The {@code startDate} and {@code endDate} parameters
     * can both be null but one must be defined.
     *
     * @param startDate      the start date to compare against data value last
     *                       updated.
     * @param endDate        the end date to compare against data value last updated.
     * @param includeDeleted whether to include deleted data values.
     * @return the number of DataValues.
     */
    int getDataValueCountLastUpdatedBetween(Instant startDate, Instant endDate, boolean includeDeleted);
}
