package org.nmcpye.am.program.repository;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.hibernate.SoftDeleteHibernateObjectStore;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.program.ProgramStageInstanceRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Repository
public class ProgramStageInstanceRepositoryExtCustomImpl
    extends SoftDeleteHibernateObjectStore<ProgramStageInstance>
    implements ProgramStageInstanceRepositoryExt {

    private static final String PSI_HQL_BY_UIDS = "from ProgramStageInstance as psi where psi.uid in (:uids)";


    public ProgramStageInstanceRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                       ApplicationEventPublisher publisher,
                                                       CurrentUserService currentUserService,
                                                       AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramStageInstance.class, currentUserService, aclService, true);
    }

    @Override
    public ProgramStageInstance get(ProgramInstance programInstance, ProgramStage programStage) {
        CriteriaBuilder builder = getCriteriaBuilder();

        List<ProgramStageInstance> list = getList(
            builder,
            newJpaParameters()
                .addPredicate(root -> builder.equal(root.get("programInstance"), programInstance))
                .addPredicate(root -> builder.equal(root.get("programStage"), programStage))
                .addOrder(root -> builder.asc(root.get("id")))
                .setMaxResults(1)
        );

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean exists(String uid) {
        if (uid == null) {
            return false;
        }

        Query<?> query = getSession().createNativeQuery(
            "select exists(select 1 from program_stage_instance where uid=:uid and deleted is false)");
        query.setParameter("uid", uid);

        return ((Boolean) query.getSingleResult()).booleanValue();
    }

    @Override
    public boolean existsIncludingDeleted(String uid) {
        if (uid == null) {
            return false;
        }

        Query<?> query = getSession().createNativeQuery(
            "select exists(select 1 from program_stage_instance where uid=:uid)");
        query.setParameter("uid", uid);

        return ((Boolean) query.getSingleResult()).booleanValue();
    }

    @Override
    public List<String> getUidsIncludingDeleted(List<String> uids) {
        final String hql = "select psi.uid " + PSI_HQL_BY_UIDS;
        List<String> resultUids = new ArrayList<>();
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(uids), 20000);

        for (List<String> uidsPartition : uidsPartitions) {
            if (!uidsPartition.isEmpty()) {
                resultUids.addAll(getSession().createQuery(hql, String.class).setParameter("uids", uidsPartition).list());
            }
        }

        return resultUids;
    }

    @Override
    public List<ProgramStageInstance> getIncludingDeleted(List<String> uids) {
        List<ProgramStageInstance> programStageInstances = new ArrayList<>();
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(uids), 20000);

        for (List<String> uidsPartition : uidsPartitions) {
            if (!uidsPartition.isEmpty()) {
                programStageInstances.addAll(
                    getSession().createQuery(PSI_HQL_BY_UIDS, ProgramStageInstance.class).setParameter("uids", uidsPartition).list()
                );
            }
        }

        return programStageInstances;
    }

    @Override
    public List<ProgramStageInstance> get(Collection<ProgramInstance> programInstances, EventStatus status) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("status"), status))
            .addPredicate(root -> root.get("programInstance").in(programInstances)));
    }

    @Override
    public long getProgramStageInstanceCountLastUpdatedAfter(Date time) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getCount(builder, newJpaParameters()
            .addPredicate(root -> builder.greaterThanOrEqualTo(root.get("updated"), time))
            .count(builder::countDistinct));
    }

    @Override
    public void updateProgramStageInstancesSyncTimestamp(List<String> programStageInstanceUIDs, Instant lastSynchronized) {
        String hql = "update ProgramStageInstance set lastSynchronized = :lastSynchronized WHERE uid in :programStageInstances";

        getQuery(hql)
            .setParameter("lastSynchronized", lastSynchronized)
            .setParameter("programStageInstances", programStageInstanceUIDs)
            .executeUpdate();
    }

    @Override
    protected ProgramStageInstance postProcessObject(ProgramStageInstance programStageInstance) {
        return (programStageInstance == null || programStageInstance.isDeleted()) ? null : programStageInstance;
    }

    @Override
    protected void preProcessPredicates(CriteriaBuilder builder,
                                        List<Function<Root<ProgramStageInstance>, Predicate>> predicates) {
        predicates.add(root -> builder.equal(root.get("deleted"), false));
    }
}
