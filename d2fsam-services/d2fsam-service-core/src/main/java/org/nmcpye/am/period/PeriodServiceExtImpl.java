package org.nmcpye.am.period;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.common.IdentifiableObjectUtils;
import org.nmcpye.am.i18n.I18nFormat;
import org.nmcpye.am.util.DateUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service Implementation for managing {@link Period}.
 */
@Service("org.nmcpye.am.period.PeriodServiceExt")
@Slf4j
public class PeriodServiceExtImpl implements PeriodServiceExt {

    private final PeriodRepositoryExt periodRepositoryExt;

    public PeriodServiceExtImpl(PeriodRepositoryExt periodRepositoryExt) {
        this.periodRepositoryExt = periodRepositoryExt;
    }

    @Override
    @Transactional
    public Long addPeriod(Period period) {
        periodRepositoryExt.addPeriod(period);
        return period.getId();
    }

    @Override
    @Transactional
    public void deletePeriod(Period period) {
        periodRepositoryExt.deleteObject(period);
    }

    @Override
    @Transactional(readOnly = true)
    public Period getPeriod(Long id) {
        return periodRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Period getPeriod(String isoPeriod) {
        Period period = PeriodType.getPeriodFromIsoString(isoPeriod);

        if (period != null) {
            period = periodRepositoryExt.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
        }

        return period;
    }

    @Override
    @Transactional(readOnly = true)
    public Period getPeriod(Date startDate, Date endDate, PeriodType periodType) {
        return periodRepositoryExt.getPeriod(startDate, endDate, periodType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getAllPeriods() {
        return periodRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getPeriodsByPeriodType(PeriodType periodType) {
        return periodRepositoryExt.getPeriodsByPeriodType(periodType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getPeriodsBetweenDates(Date startDate, Date endDate) {
        return periodRepositoryExt.getPeriodsBetweenDates(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getPeriodsBetweenDates(PeriodType periodType, Date startDate, Date endDate) {
        return periodRepositoryExt.getPeriodsBetweenDates(periodType, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getPeriodsBetweenOrSpanningDates(Date startDate, Date endDate) {
        return periodRepositoryExt.getPeriodsBetweenOrSpanningDates(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getIntersectingPeriodsByPeriodType(PeriodType periodType, Date startDate, Date endDate) {
        return periodRepositoryExt.getIntersectingPeriodsByPeriodType(periodType, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getIntersectingPeriods(Date startDate, Date endDate) {
        return periodRepositoryExt.getIntersectingPeriods(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getIntersectionPeriods(Collection<Period> periods) {
        Set<Period> intersecting = new HashSet<>();

        for (Period period : periods) {
            intersecting.addAll(getIntersectingPeriods(period.getStartDate(), period.getEndDate()));
        }

        return new ArrayList<>(intersecting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getBoundaryPeriods(Period period, Collection<Period> periods) {
        List<Period> immutablePeriods = new ArrayList<>(periods);

        Iterator<Period> iterator = immutablePeriods.iterator();

        while (iterator.hasNext()) {
            Period iterated = iterator.next();

            if (!DateUtils.strictlyBetween(
                period.getStartDate(),
                iterated.getStartDate(),
                iterated.getEndDate()) &&
                !DateUtils.strictlyBetween(
                    period.getEndDate(),
                    iterated.getStartDate(),
                    iterated.getEndDate())) {
                iterator.remove();
            }
        }

        return immutablePeriods;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getInclusivePeriods(Period period, Collection<Period> periods) {
        List<Period> immutablePeriods = new ArrayList<>(periods);

        Iterator<Period> iterator = immutablePeriods.iterator();

        while (iterator.hasNext()) {
            Period iterated = iterator.next();

            if (
                !DateUtils.between(
                    iterated.getStartDate(),
                    period.getStartDate(),
                    period.getEndDate()) ||
                    !DateUtils.between(
                        iterated.getEndDate(),
                        period.getStartDate(),
                        period.getEndDate())) {
                iterator.remove();
            }
        }

        return immutablePeriods;
    }

    @Override
    @Transactional
    public List<Period> reloadPeriods(List<Period> periods) {
        List<Period> reloaded = new ArrayList<>();

        for (Period period : periods) {
            reloaded.add(periodRepositoryExt.reloadForceAddPeriod(period));
        }

        return reloaded;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> getPeriods(Period lastPeriod, int historyLength) {
        List<Period> periods = new ArrayList<>(historyLength);

        lastPeriod = periodRepositoryExt.reloadForceAddPeriod(lastPeriod);

        PeriodType periodType = lastPeriod.getPeriodType();

        for (int i = 0; i < historyLength; ++i) {
            Period pe = getPeriodFromDates(lastPeriod.getStartDate(), lastPeriod.getEndDate(), periodType);

            periods.add(pe != null ? pe : lastPeriod);

            lastPeriod = periodType.getPreviousPeriod(lastPeriod);
        }

        Collections.reverse(periods);

        return periods;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Period> namePeriods(Collection<Period> periods, I18nFormat format) {
        for (Period period : periods) {
            period.setName(format.formatPeriod(period));
        }

        return new ArrayList<>(periods);
    }

    @Override
    @Transactional(readOnly = true)
    public Period getPeriodFromDates(Date startDate, Date endDate, PeriodType periodType) {
        return periodRepositoryExt.getPeriodFromDates(startDate, endDate, periodType);
    }

    @Override
    @Transactional
    public Period reloadPeriod(Period period) {
        return periodRepositoryExt.reloadForceAddPeriod(period);
    }

    /**
     * Fix issue DHIS2-7539 If period doesn't exist in cache and database. Need
     * to add and sync with database right away in a separate
     * session/transaction. Otherwise will get foreign key constraint error in
     * subsequence calls of batch.flush()
     **/
    @Override
    @Transactional(readOnly = true)
    public Period reloadIsoPeriodInStatelessSession(String isoPeriod) {
        Period period = PeriodType.getPeriodFromIsoString(isoPeriod);

        if (period == null) {
            return null;
        }

        Period reloadedPeriod = periodRepositoryExt.reloadPeriod(period);

        if (reloadedPeriod != null) {
            return reloadedPeriod;
        }

        period.setPeriodType(reloadPeriodType(period.getPeriodType()));

        return periodRepositoryExt.insertIsoPeriodInStatelessSession(period);
    }

    @Override
    @Transactional
    public Period reloadIsoPeriod(String isoPeriod) {
        Period period = PeriodType.getPeriodFromIsoString(isoPeriod);

        return period != null ? reloadPeriod(period) : null;
    }

    @Override
    @Transactional
    public List<Period> reloadIsoPeriods(List<String> isoPeriods) {
        List<Period> periods = new ArrayList<>();

        for (String iso : isoPeriods) {
            Period period = reloadIsoPeriod(iso);

            if (period != null) {
                periods.add(period);
            }
        }

        return periods;
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodHierarchy getPeriodHierarchy(Collection<Period> periods) {
        PeriodHierarchy hierarchy = new PeriodHierarchy();

        for (Period period : periods) {
            hierarchy
                .getIntersectingPeriods()
                .put(period.getId(), new HashSet<>(IdentifiableObjectUtils.getIdentifiers(getIntersectingPeriods(period.getStartDate(), period.getEndDate()))));
            hierarchy
                .getPeriodsBetween()
                .put(period.getId(), new HashSet<>(IdentifiableObjectUtils.getIdentifiers(getPeriodsBetweenDates(period.getStartDate(), period.getEndDate()))));
        }

        return hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public int getDayInPeriod(Period period, Date date) {
        int days = (int) TimeUnit.DAYS.convert(
            date.getTime() - period.getStartDate().getTime(),
            TimeUnit.MILLISECONDS
        );

        return Math.min(Math.max(0, days), period.getDaysInPeriod());
    }

    // -------------------------------------------------------------------------
    // PeriodType
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PeriodType getPeriodType(int id) {
        return periodRepositoryExt.getPeriodType(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodType> getAllPeriodTypes() {
        return PeriodType.getAvailablePeriodTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodType getPeriodTypeByName(String name) {
        return PeriodType.getPeriodTypeByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodType getPeriodTypeByClass(Class<? extends PeriodType> periodType) {
        return periodRepositoryExt.getPeriodType(periodType);
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodType reloadPeriodType(PeriodType periodType) {
        return periodRepositoryExt.reloadPeriodType(periodType);
    }

    // -------------------------------------------------------------------------
    // PeriodType
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteRelativePeriods(RelativePeriods relativePeriods) {
        periodRepositoryExt.deleteRelativePeriods(relativePeriods);
    }
}
