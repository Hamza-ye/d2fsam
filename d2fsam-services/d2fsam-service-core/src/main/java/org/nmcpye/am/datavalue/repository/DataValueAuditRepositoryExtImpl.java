package org.nmcpye.am.datavalue.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.datavalue.DataValueAudit;
import org.nmcpye.am.datavalue.DataValueAuditQueryParams;
import org.nmcpye.am.datavalue.DataValueAuditRepositoryExt;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.hibernate.JpaQueryParameters;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Repository("org.nmcpye.am.datavalue.DataValueAuditStore")
public class DataValueAuditRepositoryExtImpl
    extends HibernateGenericStore<DataValueAudit>
    implements DataValueAuditRepositoryExt {

    private final PeriodRepositoryExt periodStore;

    public DataValueAuditRepositoryExtImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                           ApplicationEventPublisher publisher,
                                           PeriodRepositoryExt periodStore) {
        super(sessionFactory, jdbcTemplate, publisher, DataValueAudit.class, false);

        checkNotNull(periodStore);

        this.periodStore = periodStore;
    }


    @Override
    @Transactional
    public void updateDataValueAudit(DataValueAudit dataValueAudit) {
        getSession().update(dataValueAudit);
    }

    @Override
    public void addDataValueAudit(DataValueAudit dataValueAudit) {
        getSession().save(dataValueAudit);
    }

    @Override
    public void deleteDataValueAudits(OrganisationUnit organisationUnit) {
        String hql = "delete from DataValueAudit d where d.organisationUnit = :unit";

        getSession().createQuery(hql).setParameter("unit", organisationUnit).executeUpdate();
    }

    @Override
    public void deleteDataValueAudits(DataElement dataElement) {
        String hql = "delete from DataValueAudit d where d.dataElement = :dataElement";

        getSession().createQuery(hql).setParameter("dataElement", dataElement).executeUpdate();
    }

    @Override
    public List<DataValueAudit> getDataValueAudits(DataValueAuditQueryParams params) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();

        JpaQueryParameters<DataValueAudit> queryParams = newJpaParameters()
            .addPredicates(getDataValueAuditPredicates(builder, params))
            .addOrder(root -> builder.desc(root.get("created")));

        if (params.hasPaging()) {
            queryParams
                .setFirstResult(params.getPager().getOffset())
                .setMaxResults(params.getPager().getPageSize());
        }

        return getList(builder, queryParams);
    }

    @Override
    public int countDataValueAudits(DataValueAuditQueryParams params) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();

        List<Function<Root<DataValueAudit>, Predicate>> predicates = getDataValueAuditPredicates(builder, params);

        return getCount(builder, newJpaParameters()
            .addPredicates(predicates)
            .count(root -> builder.countDistinct(root.get("id")))).intValue();
    }

    /**
     * Returns a list of Predicates generated from given parameters. Returns an
     * empty list if given Period does not exist in database.
     *
     * @param builder the {@link CriteriaBuilder}.
     * @param params  the {@link DataValueAuditQueryParams}.
     */
    private List<Function<Root<DataValueAudit>, Predicate>> getDataValueAuditPredicates(CriteriaBuilder builder,
                                                                                        DataValueAuditQueryParams params) {
        List<Period> storedPeriods = new ArrayList<>();

        if (!params.getPeriods().isEmpty()) {
            for (Period period : params.getPeriods()) {
                Period storedPeriod = periodStore.reloadPeriod(period);

                if (storedPeriod != null) {
                    storedPeriods.add(storedPeriod);
                }
            }
        }

        List<Function<Root<DataValueAudit>, Predicate>> predicates = new ArrayList<>();

        if (!storedPeriods.isEmpty()) {
            predicates.add(root -> root.get("period").in(storedPeriods));
        } else if (!params.getPeriods().isEmpty()) {
            return predicates;
        }

        if (!params.getDataElements().isEmpty()) {
            predicates.add(root -> root.get("dataElement").in(params.getDataElements()));
        }

        if (!params.getOrgUnits().isEmpty()) {
            predicates.add(root -> root.get("organisationUnit").in(params.getOrgUnits()));
        }

//        if (params.getCategoryOptionCombo() != null) {
//            predicates
//                .add(root -> builder.equal(root.get("categoryOptionCombo"), params.getCategoryOptionCombo()));
//        }
//
//        if (params.getAttributeOptionCombo() != null) {
//            predicates
//                .add(root -> builder.equal(root.get("attributeOptionCombo"), params.getAttributeOptionCombo()));
//        }

        if (!params.getAuditTypes().isEmpty()) {
            predicates.add(root -> root.get("auditType").in(params.getAuditTypes()));
        }

        return predicates;
    }
}
