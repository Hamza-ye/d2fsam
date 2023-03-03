package org.nmcpye.am.trackedentity.repository;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwner;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerIds;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerOrgUnit;
import org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository("org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerRepositoryExt")
public class TrackedEntityProgramOwnerRepositoryExtCustomImpl
    extends HibernateGenericStore<TrackedEntityProgramOwner>
    implements TrackedEntityProgramOwnerRepositoryExt {

    public TrackedEntityProgramOwnerRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                            ApplicationEventPublisher publisher) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityProgramOwner.class, false);
    }

    @Override
    public TrackedEntityProgramOwner getTrackedEntityProgramOwner(Long teiId, Long programId) {
        Query<TrackedEntityProgramOwner> query = getQuery(
            "from TrackedEntityProgramOwner tepo where " + "tepo.entityInstance.id= :teiId and " + "tepo.program.id= :programId"
        );

        query.setParameter("teiId", teiId);
        query.setParameter("programId", programId);
        return query.uniqueResult();
    }

    @Override
    public List<TrackedEntityProgramOwner> getTrackedEntityProgramOwners(List<Long> teiIds) {
        String hql = "from TrackedEntityProgramOwner tepo where tepo.entityInstance.id in (:teiIds)";
        Query<TrackedEntityProgramOwner> q = getQuery(hql);
        q.setParameterList("teiIds", teiIds);
        return q.list();
    }

    @Override
    public List<TrackedEntityProgramOwner> getTrackedEntityProgramOwners(List<Long> teiIds, Long programId) {
        String hql = "from TrackedEntityProgramOwner tepo where tepo.entityInstance.id in (:teiIds) and tepo.program.id=(:programId) ";
        Query<TrackedEntityProgramOwner> q = getQuery(hql);
        q.setParameterList("teiIds", teiIds);
        q.setParameter("programId", programId);
        return q.list();
    }

    @Override
    public List<TrackedEntityProgramOwnerIds> getTrackedEntityProgramOwnersUids(List<Long> teiIds, Long programId) {
        List<List<Long>> teiIdsPartitions = Lists.partition(teiIds, 20000);
        String hql =
            "select new org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerIds(tepo.entityInstance.uid, tepo.program.uid, tepo.organisationUnit.uid) from TrackedEntityProgramOwner tepo where tepo.entityInstance.id in (:teiIds) and tepo.program.id=(:programId) ";
        Query<TrackedEntityProgramOwnerIds> q = getQuery(hql, TrackedEntityProgramOwnerIds.class);

        List<TrackedEntityProgramOwnerIds> trackedEntityProgramOwnerIds = new ArrayList<>();

        q.setParameter("programId", programId);
        teiIdsPartitions.forEach(partition -> {
            q.setParameterList("teiIds", partition);
            trackedEntityProgramOwnerIds.addAll(q.list());
        });
        return trackedEntityProgramOwnerIds;
    }

    @Override
    public List<TrackedEntityProgramOwnerOrgUnit> getTrackedEntityProgramOwnerOrgUnits(Set<Long> teiIds) {
        List<TrackedEntityProgramOwnerOrgUnit> trackedEntityProgramOwnerOrgUnits = new ArrayList<>();

        if (teiIds == null || teiIds.size() == 0) {
            return trackedEntityProgramOwnerOrgUnits;
        }

        Iterable<List<Long>> teiIdsPartitions = Iterables.partition(teiIds, 20000);

        String hql =
            "select new org.nmcpye.am.trackedentity.TrackedEntityProgramOwnerOrgUnit( tepo.entityInstance.uid, tepo.program.uid, tepo.organisationUnit) from TrackedEntityProgramOwner tepo where tepo.entityInstance.id in (:teiIds)";

        Query<TrackedEntityProgramOwnerOrgUnit> q = getQuery(hql, TrackedEntityProgramOwnerOrgUnit.class);

        teiIdsPartitions.forEach(partition -> {
            q.setParameterList("teiIds", partition);
            trackedEntityProgramOwnerOrgUnits.addAll(q.list());
        });

        return trackedEntityProgramOwnerOrgUnits;
    }
}
