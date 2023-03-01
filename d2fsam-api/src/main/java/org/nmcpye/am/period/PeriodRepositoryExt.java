package org.nmcpye.am.period;

import org.nmcpye.am.common.GenericStore;

import java.util.Date;
import java.util.List;

public interface PeriodRepositoryExt extends GenericStore<Period> {
    /**
     * Adds a Period.
     *
     * @param period the Period to add.
     */
    void addPeriod(Period period);

    /**
     * Returns a Period.
     *
     * @param startDate  the start date of the Period.
     * @param endDate    the end date of the Period.
     * @param periodType the PeriodType of the Period
     * @return the Period matching the dates and periodtype, or null if no
     * match.
     */
    Period getPeriod(Date startDate, Date endDate, PeriodType periodType);

    /**
     * Returns a Period.
     *
     * @param startDate  the start date of the Period.
     * @param endDate    the end date of the Period.
     * @param periodType the PeriodType of the Period
     * @return the Period matching the dates and periodtype, or null if no
     * match.
     */
    Period getPeriodFromDates(Date startDate, Date endDate, PeriodType periodType);

    /**
     * Returns all Periods with start date after or equal the specified start
     * date and end date before or equal the specified end date.
     *
     * @param startDate the ultimate start date.
     * @param endDate   the ultimate end date.
     * @return a list of all Periods with start date after or equal the
     * specified start date and end date before or equal the specified
     * end date, or an empty collection if no Periods match.
     */
    List<Period> getPeriodsBetweenDates(Date startDate, Date endDate);

    /**
     * Returns all Periods of the specified PeriodType with start date after or
     * equal the specified start date and end date before or equal the specified
     * end date.
     *
     * @param periodType the PeriodType.
     * @param startDate  the ultimate start date.
     * @param endDate    the ultimate end date.
     * @return a list of all Periods with start date after or equal the
     * specified start date and end date before or equal the specified
     * end date, or an empty collection if no Periods match.
     */
    List<Period> getPeriodsBetweenDates(PeriodType periodType, Date startDate, Date endDate);

    List<Period> getPeriodsBetweenOrSpanningDates(Date startDate, Date endDate);

    /**
     * Returns all intersecting Periods between the startDate and endDate based
     * on PeriodType For example if the startDate is 2007-05-01 and endDate is
     * 2007-08-01 and periodType is Quarterly then it returns the periods for
     * Q2,Q3
     *
     * @param periodType is the ultimate period type
     * @param startDate  is intercepting startDate
     * @param endDate    is intercepting endDate
     * @return a list of periods.
     */
    List<Period> getIntersectingPeriodsByPeriodType(PeriodType periodType, Date startDate, Date endDate);

    /**
     * Returns Periods where at least one its days is between the given start
     * date and end date.
     *
     * @param startDate the start date.
     * @param endDate   the end date.
     * @return Periods where at least one its days is between the given start
     * date and end date.
     */
    List<Period> getIntersectingPeriods(Date startDate, Date endDate);

    /**
     * Returns all Periods with a given PeriodType.
     *
     * @param periodType the PeriodType of the Periods to return.
     * @return all Periods with the given PeriodType, or an empty list if no
     * Periods match.
     */
    List<Period> getPeriodsByPeriodType(PeriodType periodType);

    /**
     * Checks if the given period is associated with the current session and
     * loads it if not. Null is returned if the period does not exist.
     *
     * @param period the Period.
     * @return the Period.
     */
    Period reloadPeriod(Period period);

    /**
     * Checks if the given period is associated with the current session and
     * loads it if not. The period is persisted if it does not exist. The
     * persisted Period is returned.
     *
     * @param period the Period.
     * @return the persisted Period.
     */
    Period reloadForceAddPeriod(Period period);

    // -------------------------------------------------------------------------
    // PeriodType
    // -------------------------------------------------------------------------

    /**
     * Adds a PeriodType.
     *
     * @param periodType the PeriodType to add.
     * @return a generated unique id of the added PeriodType.
     */
    int addPeriodType(PeriodType periodType);

    /**
     * Deletes a PeriodType.
     *
     * @param periodType the PeriodType to delete.
     */
    void deletePeriodType(PeriodType periodType);

    /**
     * Returns a PeriodType.
     *
     * @param id the id of the PeriodType to return.
     * @return the PeriodType with the given id, or null if no match.
     */
    PeriodType getPeriodType(int id);

    /**
     * Returns the persisted instance of a given PeriodType.
     *
     * @param periodType the PeriodType class of the instance to return.
     * @return the period type.
     */
    PeriodType getPeriodType(Class<? extends PeriodType> periodType);

    /**
     * Returns all PeriodTypes.
     *
     * @return a list of all PeriodTypes, or an empty list if there are no
     * PeriodTypes.
     */
    List<PeriodType> getAllPeriodTypes();

    /**
     * Checks if the given periodType is associated with the current session and
     * loads it if not. Null is returned if the period does not exist.
     *
     * @param periodType the PeriodType.
     * @return the Period.
     */
    PeriodType reloadPeriodType(PeriodType periodType);

    // -------------------------------------------------------------------------
    // RelativePeriods
    // -------------------------------------------------------------------------

    Period insertIsoPeriodInStatelessSession(Period period);

    /**
     * Deletes a RelativePeriods instance.
     *
     * @param relativePeriods the RelativePeriods instance.
     */
    void deleteRelativePeriods(RelativePeriods relativePeriods);
}
