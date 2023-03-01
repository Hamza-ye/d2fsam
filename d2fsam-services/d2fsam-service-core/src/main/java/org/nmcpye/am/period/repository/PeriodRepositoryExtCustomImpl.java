package org.nmcpye.am.period.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.common.exception.InvalidIdentifierReferenceException;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.commons.util.DebugUtils;
import org.nmcpye.am.dbms.DbmsUtils;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodRepositoryExt;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.period.RelativePeriods;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Repository("org.nmcpye.am.period.PeriodRepositoryExt")
@Slf4j
public class PeriodRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Period>
    implements PeriodRepositoryExt {

    private final Cache<Long> periodIdCache;

    public PeriodRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                         ApplicationEventPublisher publisher,
                                         CurrentUserService currentUserService,
                                         AclService aclService,
                                         CacheProvider cacheProvider) {
        super(sessionFactory, jdbcTemplate, publisher, Period.class,
            currentUserService, aclService, true);
        transientIdentifiableProperties = true;
        this.periodIdCache = cacheProvider.createPeriodIdCache();
    }

    @Override
    public void addPeriod(Period period) {
        period.setPeriodType(reloadPeriodType(period.getPeriodType()));

        saveObject(period);
    }

    @Override
    public Period getPeriod(Date startDate, Date endDate, PeriodType periodType) {
        String query = "from Period p where p.startDate =:startDate and p.endDate =:endDate and p.periodType =:periodType";

        return getSingleResult(
            getQuery(query)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("periodType", reloadPeriodType(periodType))
        );
    }

    @Override
    public List<Period> getPeriodsBetweenDates(Date startDate, Date endDate) {
        String query = "from Period p where p.startDate >=:startDate and p.endDate <=:endDate";

        Query<Period> typedQuery = getQuery(query).setParameter("startDate", startDate).setParameter("endDate", endDate);
        return getList(typedQuery);
    }

    @Override
    public List<Period> getPeriodsBetweenDates(PeriodType periodType, Date startDate, Date endDate) {
        String query = "from Period p where p.startDate >=:startDate and p.endDate <=:endDate and p.periodType.id =:periodType";

        Query<Period> typedQuery = getQuery(query)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("periodType", reloadPeriodType(periodType).getId());
        return getList(typedQuery);
    }

    @Override
    public List<Period> getPeriodsBetweenOrSpanningDates(Date startDate, Date endDate) {
        String hql =
            "from Period p where ( p.startDate >= :startDate and p.endDate <= :endDate ) or ( p.startDate <= :startDate and p.endDate >= :endDate )";

        return getQuery(hql).setParameter("startDate", startDate).setParameter("endDate", endDate).list();
    }

    @Override
    public List<Period> getIntersectingPeriodsByPeriodType(PeriodType periodType, Date startDate, Date endDate) {
        String query = "from Period p where p.startDate <=:endDate and p.endDate >=:startDate and p.periodType.id =:periodType";

        Query<Period> typedQuery = getQuery(query)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("periodType", reloadPeriodType(periodType).getId());
        return getList(typedQuery);
    }

    @Override
    public List<Period> getIntersectingPeriods(Date startDate, Date endDate) {
        String query = "from Period p where p.startDate <=:endDate and p.endDate >=:startDate";

        Query<Period> typedQuery = getQuery(query).setParameter("startDate", startDate).setParameter("endDate", endDate);
        return getList(typedQuery);
    }

    @Override
    public List<Period> getPeriodsByPeriodType(PeriodType periodType) {
        String query = "from Period p where p.periodType.id =:periodType";

        Query<Period> typedQuery = getQuery(query).setParameter("periodType", reloadPeriodType(periodType).getId());
        return getList(typedQuery);
    }

    @Override
    public Period getPeriodFromDates(Date startDate, Date endDate, PeriodType periodType) {
        String query = "from Period p where p.startDate =:startDate and p.endDate =:endDate and p.periodType.id =:periodType";

        Query<Period> typedQuery = getQuery(query)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("periodType", reloadPeriodType(periodType).getId());
        return getSingleResult(typedQuery);
    }

    @Override
    public Period reloadPeriod(Period period) {
        Session session = getSession();

        if (session.contains(period)) {
            return period; // Already in session, no reload needed
        }

        Long id = periodIdCache.get(
            period.getCacheKey(),
            key -> getPeriodId(period.getStartDate(), period.getEndDate(), period.getPeriodType())
        );

        Period storedPeriod = id != null ? getSession().get(Period.class, id) : null;

        return storedPeriod != null ? storedPeriod.copyTransientProperties(period) : null;
    }

    private Long getPeriodId(Date startDate, Date endDate, PeriodType periodType) {
        Period period = getPeriod(startDate, endDate, periodType);

        return period != null ? period.getId() : null;
    }

    @Override
    public Period reloadForceAddPeriod(Period period) {
        Period storedPeriod = reloadPeriod(period);

        if (storedPeriod == null) {
            addPeriod(period);

            return period;
        }

        return storedPeriod;
    }

    // -------------------------------------------------------------------------
    // PeriodType (do not use generic store which is linked to Period)
    // -------------------------------------------------------------------------

    @Override
    public int addPeriodType(PeriodType periodType) {
        Session session = getSession();

        return (Integer) session.save(periodType);
    }

    @Override
    public void deletePeriodType(PeriodType periodType) {
        Session session = getSession();

        session.delete(periodType);
    }

    @Override
    public PeriodType getPeriodType(int id) {
        Session session = getSession();

        return session.get(PeriodType.class, id);
    }

    @Override
    public PeriodType getPeriodType(Class<? extends PeriodType> periodType) {
        CriteriaBuilder builder = getCriteriaBuilder();

        CriteriaQuery<PeriodType> query = builder.createQuery(PeriodType.class);
        query.select(query.from(periodType));

        return getSession().createQuery(query).setCacheable(true).uniqueResult();
    }

    @Override
    public List<PeriodType> getAllPeriodTypes() {
        CriteriaBuilder builder = getCriteriaBuilder();

        CriteriaQuery<PeriodType> query = builder.createQuery(PeriodType.class);
        query.select(query.from(PeriodType.class));

        return getSession().createQuery(query).setCacheable(true).getResultList();
    }

    @Override
    public PeriodType reloadPeriodType(PeriodType periodType) {
        Session session = getSession();

        if (periodType == null || session.contains(periodType)) {
            return periodType;
        }

        PeriodType reloadedPeriodType = getPeriodType(periodType.getClass());

        if (reloadedPeriodType == null) {
            throw new InvalidIdentifierReferenceException(
                "The PeriodType referenced by the Period is not in database: " + periodType.getName()
            );
        }

        return reloadedPeriodType;
    }

    @Override
    public Period insertIsoPeriodInStatelessSession(Period period) {
        StatelessSession session = getSession().getSessionFactory().openStatelessSession();
        session.beginTransaction();
        try {
            Serializable id = session.insert(period);
            periodIdCache.put(period.getCacheKey(), (Long) id);

            return period;
        } catch (Exception exception) {
            log.error(DebugUtils.getStackTrace(exception));
        } finally {
            DbmsUtils.closeStatelessSession(session);
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // RelativePeriods (do not use generic store which is linked to Period)
    // -------------------------------------------------------------------------

    @Override
    public void deleteRelativePeriods(RelativePeriods relativePeriods) {
        getSession().delete(relativePeriods);
    }
}
