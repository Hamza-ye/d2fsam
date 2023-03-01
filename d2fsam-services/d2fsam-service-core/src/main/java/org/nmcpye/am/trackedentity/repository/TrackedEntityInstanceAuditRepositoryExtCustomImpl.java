package org.nmcpye.am.trackedentity.repository;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.hibernate.JpaQueryParameters;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditQueryParams;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.nmcpye.am.system.util.SqlUtils.singleQuote;

@Repository("org.nmcpye.am.trackedentity.TrackedEntityInstanceAuditRepositoryExt")
public class TrackedEntityInstanceAuditRepositoryExtCustomImpl
    extends HibernateGenericStore<TrackedEntityInstanceAudit>
    implements TrackedEntityInstanceAuditRepositoryExt {

    private final StatementBuilder statementBuilder;

    public TrackedEntityInstanceAuditRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                             ApplicationEventPublisher publisher,
                                                             StatementBuilder statementBuilder) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityInstanceAudit.class, false);
        checkNotNull(statementBuilder);
        this.statementBuilder = statementBuilder;
    }

    @Override
    public void addTrackedEntityInstanceAudit(TrackedEntityInstanceAudit trackedEntityInstanceAudit) {
        getSession().save(trackedEntityInstanceAudit);
    }

    @Override
    public void addTrackedEntityInstanceAudit(List<TrackedEntityInstanceAudit> trackedEntityInstanceAudit) {
        final String sql = "INSERT INTO tracked_entity_instance_audit (" +
            "trackedentityinstanceauditid, " +
            "trackedentityinstance, " +
            "created, " +
            "accessedby, " +
            "audittype, " +
            "comment ) VALUES ";

        Function<TrackedEntityInstanceAudit, String> mapToString = audit -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append("nextval('trackedentityinstanceaudit_sequence'), ");
            sb.append(singleQuote(audit.getTrackedEntityInstance())).append(",");
            sb.append("now()").append(",");
            sb.append(singleQuote(audit.getAccessedBy())).append(",");
            sb.append(singleQuote(audit.getAuditType().getValue())).append(",");
            sb.append(
                StringUtils.isNotEmpty(audit.getComment()) ? statementBuilder.encode(audit.getComment()) : "''");
            sb.append(")");
            return sb.toString();
        };

        final String values = trackedEntityInstanceAudit.stream().map(mapToString)
            .collect(Collectors.joining(","));

        getSession().createNativeQuery(sql + values).executeUpdate();
    }

    @Override
    public void deleteTrackedEntityInstanceAudit(TrackedEntityInstance trackedEntityInstance) {
        String hql = "delete TrackedEntityInstanceAudit where trackedEntityInstance = :trackedEntityInstance";
        getSession().createQuery(hql).setParameter("trackedEntityInstance", trackedEntityInstance).executeUpdate();
    }

    @Override
    public List<TrackedEntityInstanceAudit> getTrackedEntityInstanceAudits(
        TrackedEntityInstanceAuditQueryParams params) {
        CriteriaBuilder builder = getCriteriaBuilder();

        JpaQueryParameters<TrackedEntityInstanceAudit> jpaParameters = newJpaParameters()
            .addPredicates(getTrackedEntityInstanceAuditPredicates(params, builder))
            .addOrder(root -> builder.desc(root.get("created")));

        if (params.hasPaging()) {
            jpaParameters
                .setFirstResult(params.getPager().getOffset())
                .setMaxResults(params.getPager().getPageSize());
        }

        return getList(builder, jpaParameters);
    }

    @Override
    public int getTrackedEntityInstanceAuditsCount(TrackedEntityInstanceAuditQueryParams params) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getCount(builder, newJpaParameters()
            .addPredicates(getTrackedEntityInstanceAuditPredicates(params, builder))
            .count(root -> builder.countDistinct(root.get("id")))).intValue();
    }

    private List<Function<Root<TrackedEntityInstanceAudit>, Predicate>> getTrackedEntityInstanceAuditPredicates(
        TrackedEntityInstanceAuditQueryParams params, CriteriaBuilder builder) {
        List<Function<Root<TrackedEntityInstanceAudit>, Predicate>> predicates = new ArrayList<>();

        if (params.hasTrackedEntityInstances()) {
            predicates.add(root -> root.get("trackedEntityInstance").in(params.getTrackedEntityInstances()));
        }

        if (params.hasUsers()) {
            predicates.add(root -> root.get("accessedBy").in(params.getUsers()));
        }

        if (params.hasAuditTypes()) {
            predicates.add(root -> root.get("auditType").in(params.getAuditTypes()));
        }

        if (params.hasStartDate()) {
            predicates.add(root -> builder.greaterThanOrEqualTo(root.get("created"), params.getStartDate()));
        }

        if (params.hasEndDate()) {
            predicates.add(root -> builder.lessThanOrEqualTo(root.get("created"), params.getEndDate()));
        }

        return predicates;
    }
}
