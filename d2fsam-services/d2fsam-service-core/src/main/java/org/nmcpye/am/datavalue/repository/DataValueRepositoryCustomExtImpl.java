package org.nmcpye.am.datavalue.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.datavalue.DataValue;
import org.nmcpye.am.datavalue.DataValueRepositoryCustomExt;
import org.nmcpye.am.datavalue.DataValueRepositoryExt;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("org.nmcpye.am.datavalue.DataValueRepositoryExt")
public class DataValueRepositoryCustomExtImpl
    extends HibernateGenericStore<DataValue> implements DataValueRepositoryExt {

    private final PeriodRepositoryExt periodStore;

    private final StatementBuilder statementBuilder;

    public DataValueRepositoryCustomExtImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher,
                                            PeriodRepositoryExt periodStore,
                                            StatementBuilder statementBuilder) {
        super(sessionFactory, jdbcTemplate, publisher, DataValue.class, false);
        this.periodStore = periodStore;
        this.statementBuilder = statementBuilder;
    }

    @Override
    public void addDataValue(DataValue dataValue) {
        dataValue.setPeriod(periodStore.reloadForceAddPeriod(dataValue.getPeriod()));

        getSession().save(dataValue);
    }

    @Override
    public void updateDataValue(DataValue dataValue) {
        dataValue.setPeriod(periodStore.reloadForceAddPeriod(dataValue.getPeriod()));

        getSession().update(dataValue);
    }

    @Override
    public void deleteDataValues(OrganisationUnit organisationUnit) {
        String hql = "delete from DataValue d where d.source = :source";

        getSession().createQuery(hql).setParameter("source", organisationUnit).executeUpdate();
    }

    @Override
    public void deleteDataValues(DataElement dataElement) {
        String hql = "delete from DataValue d where d.dataElement = :dataElement";

        getSession().createQuery(hql)
            .setParameter("dataElement", dataElement).executeUpdate();
    }

    @Override
    public DataValue getDataValue(DataElement dataElement, Period period, OrganisationUnit source) {
        return getDataValue(dataElement, period, source, false);
    }

    @Override
    public DataValue getDataValue(DataElement dataElement, Period period, OrganisationUnit source, boolean includeDeleted) {
        Period storedPeriod = periodStore.reloadPeriod(period);

        if (storedPeriod == null) {
            return null;
        }

        String includeDeletedSql = includeDeleted ? "" : "and dv.deleted = false ";

        String hql = "select dv from DataValue dv  where dv.dataElement =:dataElement and dv.period =:period "
            + includeDeletedSql
            + "and dv.source =:source ";

        return getSingleResult(getQuery(hql)
                .setParameter("dataElement", dataElement)
                .setParameter("period", storedPeriod)
                .setParameter("source", source)
            /*.setParameter("attributeOptionCombo", attributeOptionCombo)
            .setParameter("categoryOptionCombo", categoryOptionCombo)*/);
    }

    @Override
    public DataValue getSoftDeletedDataValue(DataValue dataValue) {
        Period storedPeriod = periodStore.reloadPeriod(dataValue.getPeriod());

        if (storedPeriod == null) {
            return null;
        }

        dataValue.setPeriod(storedPeriod);

        CriteriaBuilder builder = getCriteriaBuilder();

//        return getSingleResult(builder, newJpaParameters()
//            .addPredicate(root -> builder.equal(root, dataValue))
//            .addPredicate(root -> builder.equal(root.get("deleted"), true)));
        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("dataElement"), dataValue.getDataElement()))
            .addPredicate(root -> builder.equal(root.get("period"), dataValue.getPeriod()))
            .addPredicate(root -> builder.equal(root.get("source"), dataValue.getSource()))
            .addPredicate(root -> builder.equal(root.get("deleted"), true)));
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @Override
    public List<DataValue> getAllDataValues() {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("deleted"), false)));
    }

    @Override
    public int getDataValueCountLastUpdatedBetween(Instant startDate, Instant endDate, boolean includeDeleted) {
        if (startDate == null && endDate == null) {
            throw new IllegalArgumentException("Start date or end date must be specified");
        }

        CriteriaBuilder builder = getCriteriaBuilder();

        List<Function<Root<DataValue>, Predicate>> predicateList = new ArrayList<>();

        if (!includeDeleted) {
            predicateList.add(root -> builder.equal(root.get("deleted"), false));
        }

        if (startDate != null) {
            predicateList.add(root -> builder.greaterThanOrEqualTo(root.get("updated"), startDate));
        }

        if (endDate != null) {
            predicateList.add(root -> builder.lessThanOrEqualTo(root.get("updated"), endDate));
        }

        return getCount(builder, newJpaParameters()
            .addPredicates(predicateList)
            .count(root -> builder.countDistinct(root)))
            .intValue();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Reloads the periods in the given collection, and filters out periods
     * which do not exist in the database.
     *
     * @param periods the collection of {@link Period}.
     * @return a set of reloaded {@link Period}.
     */
    private Set<Period> reloadAndFilterPeriods(Collection<Period> periods) {
        return periods != null ? periods.stream()
            .map(periodStore::reloadPeriod)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()) : new HashSet<>();
    }
}
