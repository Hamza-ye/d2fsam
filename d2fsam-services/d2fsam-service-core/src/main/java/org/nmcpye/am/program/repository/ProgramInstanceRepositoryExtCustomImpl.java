package org.nmcpye.am.program.repository;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.ObjectDeletionRequestedEvent;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.common.hibernate.SoftDeleteHibernateObjectStore;
import org.nmcpye.am.commons.util.SqlHelper;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.*;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.util.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.nmcpye.am.common.IdentifiableObjectUtils.getUids;
import static org.nmcpye.am.commons.util.TextUtils.getQuotedCommaDelimitedString;

@Repository
public class ProgramInstanceRepositoryExtCustomImpl
    extends SoftDeleteHibernateObjectStore<ProgramInstance>
    implements ProgramInstanceRepositoryExt {

    private static final String PI_HQL_BY_UIDS = "from ProgramInstance as pi where pi.uid in (:uids)";

    private static final String STATUS = "status";

    public ProgramInstanceRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                  ApplicationEventPublisher publisher,
                                                  CurrentUserService currentUserService,
                                                  AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramInstance.class, currentUserService, aclService, true);
    }

    @Override
    public int countProgramInstances(ProgramInstanceQueryParams params) {
        String hql = buildCountProgramInstanceHql(params);

        Query<Long> query = getTypedQuery(hql);

        return query.getSingleResult().intValue();
    }

    private String buildCountProgramInstanceHql(ProgramInstanceQueryParams params) {
        return buildProgramInstanceHql(params)
            .getQuery()
            .replaceFirst("from ProgramInstance pi", "select count(distinct uid) from ProgramInstance pi");
    }

    @Override
    public List<ProgramInstance> getProgramInstances(ProgramInstanceQueryParams params) {
        String hql = buildProgramInstanceHql(params).getFullQuery();

        Query<ProgramInstance> query = getQuery(hql);

        if (!params.isSkipPaging()) {
            query.setFirstResult(params.getOffset());
            query.setMaxResults(params.getPageSizeWithDefault());
        }

        // When the clients choose to not show the total of pages.
        if (!params.isTotalPages()) {
            // Get pageSize + 1, so we are able to know if there is another
            // page available. It adds one additional element into the list,
            // as consequence. The caller needs to remove the last element.
            query.setMaxResults(params.getPageSizeWithDefault() + 1);
        }

        return query.list();
    }

    private QueryWithOrderBy buildProgramInstanceHql(ProgramInstanceQueryParams params) {
        String hql = "from ProgramInstance pi";
        SqlHelper hlp = new SqlHelper(true);

        boolean includeInactiveActivities = params.isIncludeInactiveActivities();

        if (params.hasLastUpdatedDuration()) {
            includeInactiveActivities = true;
            hql += hlp.whereAnd() + "pi.updated >= '" + DateUtils.getLongGmtDateString(DateUtils.nowMinusDuration(params.getLastUpdatedDuration())) + "'";
        } else if (params.hasLastUpdated()) {
            includeInactiveActivities = true;
            hql += hlp.whereAnd() + "pi.updated >= '" + DateUtils.getMediumDateString(DateUtils.fromInstant(params.getLastUpdated())) + "'";
        }

        if (params.hasTrackedEntityInstance()) {
            hql += hlp.whereAnd() + "pi.entityInstance.uid = '" + params.getTrackedEntityInstanceUid() + "'";
        }

        if (params.hasProject()) {
            hql += hlp.whereAnd() + "pi.activity is not null and pi.activity.project.uid = '" + params.getProject().getUid() + "'";
        }

        if (params.hasActivities()) {
            includeInactiveActivities = true;
            hql +=
                hlp.whereAnd() +
                    "pi.activity.uid in (" +
                    getQuotedCommaDelimitedString(getUids(params.getActivities())) +
                    ")";
        }

        if (params.hasTrackedEntityType()) {
            hql += hlp.whereAnd() + "pi.entityInstance.trackedEntityType.uid = '"
                + params.getTrackedEntityType().getUid() + "'";
        }

        if (params.hasOrganisationUnits()) {
            if (params.isOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS)) {
                String ouClause = "(";
                SqlHelper orHlp = new SqlHelper(true);

                for (OrganisationUnit organisationUnit : params.getOrganisationUnits()) {
                    ouClause += orHlp.or() + "pi.organisationUnit.path LIKE '" + organisationUnit.getPath() + "%'";
                }

                ouClause += ")";

                hql += hlp.whereAnd() + ouClause;
            } else {
                hql += hlp.whereAnd() + "pi.organisationUnit.uid in ("
                    + getQuotedCommaDelimitedString(getUids(params.getOrganisationUnits())) + ")";
            }
        }

        if (params.hasProgram()) {
            hql += hlp.whereAnd() + "pi.program.uid = '" + params.getProgram().getUid() + "'";
        }

        if (params.hasProgramStatus()) {
            hql += hlp.whereAnd() + "pi." + STATUS + " = '" + params.getProgramStatus() + "'";
        }

        if (params.hasFollowUp()) {
            hql += hlp.whereAnd() + "pi.followup = " + params.getFollowUp();
        }

        if (params.hasProgramStartDate()) {
            includeInactiveActivities = true;
            hql += hlp.whereAnd() + "pi.enrollmentDate >= '" + DateUtils.getMediumDateString(params.getProgramStartDate()) + "'";
        }

        if (params.hasProgramEndDate()) {
            includeInactiveActivities = true;
            hql += hlp.whereAnd() + "pi.enrollmentDate <= '" + DateUtils.getMediumDateString(params.getProgramEndDate()) + "'";
        }

        if (!params.isIncludeDeleted()) {
            hql += hlp.whereAnd() + " pi.deleted is false ";
        }

        if (!includeInactiveActivities) {
            hql += hlp.whereAnd() + " (pi.activity is null or pi.activity.inactive is false) ";
        }


        QueryWithOrderBy query = QueryWithOrderBy.builder().query(hql).build();

        if (params.isSorting()) {
            query =
                query
                    .toBuilder()
                    .orderBy(
                        " order by " +
                            params
                                .getOrder()
                                .stream()
                                .map(orderParam -> orderParam.getField() + " " + (orderParam.getDirection().isAscending() ? "asc" : "desc"))
                                .collect(Collectors.joining(", "))
                    )
                    .build();
        }

        return query;
    }

    @Override
    public List<ProgramInstance> get(Program program) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder,
            newJpaParameters().addPredicate(root -> builder.equal(root.get("program"), program)));
    }

    @Override
    public List<ProgramInstance> get(Program program, ProgramStatus status) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("program"), program))
            .addPredicate(root -> builder.equal(root.get(STATUS), status)));
    }

    @Override
    public List<ProgramInstance> get(TrackedEntityInstance entityInstance, Program program, ProgramStatus status) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("entityInstance"), entityInstance))
            .addPredicate(root -> builder.equal(root.get("program"), program))
            .addPredicate(root -> builder.equal(root.get(STATUS), status)));
    }

    @Override
    public List<String> getUidsIncludingDeleted(List<String> uids) {
        String hql = "select pi.uid " + PI_HQL_BY_UIDS;
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
    public List<ProgramInstance> getIncludingDeleted(List<String> uids) {
        List<ProgramInstance> programInstances = new ArrayList<>();
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(uids), 20000);

        for (List<String> uidsPartition : uidsPartitions) {
            if (!uidsPartition.isEmpty()) {
                programInstances.addAll(
                    getSession().createQuery(PI_HQL_BY_UIDS, ProgramInstance.class).setParameter("uids", uidsPartition).list()
                );
            }
        }

        return programInstances;
    }

    @Override
    public boolean exists(String uid) {
        if (uid == null) {
            return false;
        }

        Query<?> query = getSession().createNativeQuery(
            "select exists(select 1 from program_instance where uid=:uid and deleted is false)");
        query.setParameter("uid", uid);

        return ((Boolean) query.getSingleResult()).booleanValue();
    }

    @Override
    public boolean existsIncludingDeleted(String uid) {
        if (uid == null) {
            return false;
        }

        Query<?> query = getSession().createNativeQuery(
            "select exists(select 1 from program_instance where uid=:uid)");
        query.setParameter("uid", uid);

        return ((Boolean) query.getSingleResult()).booleanValue();
    }

    @Override
    public List<ProgramInstance> getByPrograms(List<Program> programs) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder,
            newJpaParameters().addPredicate(root -> builder.in(root.get("program")).value(programs)));
    }

    @Override
    public void hardDelete(ProgramInstance programInstance) {
        publisher.publishEvent(new ObjectDeletionRequestedEvent(programInstance));
        getSession().delete(programInstance);
    }

    @Override
    public List<ProgramInstance> getByProgramAndTrackedEntityInstance(
        List<Pair<Program, TrackedEntityInstance>> programTeiPair,
        ProgramStatus programStatus
    ) {
        checkNotNull(programTeiPair);

        if (programTeiPair.isEmpty()) {
            return new ArrayList<>();
        }

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<ProgramInstance> cr = cb.createQuery(ProgramInstance.class);
        Root<ProgramInstance> programInstance = cr.from(ProgramInstance.class);

        // Constructing list of parameters
        List<Predicate> predicates = new ArrayList<>();

        // TODO we may have potentially thousands of events here, so, it's
        // better to
        // partition the list
        for (Pair<Program, TrackedEntityInstance> pair : programTeiPair) {
            predicates.add(
                cb.and(
                    cb.equal(programInstance.get("program"), pair.getLeft()),
                    cb.equal(programInstance.get("entityInstance"), pair.getRight()),
                    cb.equal(programInstance.get(STATUS), programStatus)
                )
            );
        }

        cr.select(programInstance).where(cb.or(predicates.toArray(new Predicate[]{})));

        return getSession().createQuery(cr).getResultList();
    }

    @Getter
    @Builder(toBuilder = true)
    static class QueryWithOrderBy {

        private final String query;

        private final String orderBy;

        String getFullQuery() {
            return Stream.of(query, orderBy)
                .map(StringUtils::trimToEmpty)
                .filter(Objects::nonNull).collect(Collectors.joining(" "));
        }
    }

    @Override
    protected ProgramInstance postProcessObject(ProgramInstance programInstance) {
        return (programInstance == null || programInstance.isDeleted()) ? null : programInstance;
    }

    @Override
    protected void preProcessPredicates(CriteriaBuilder builder,
                                        List<Function<Root<ProgramInstance>, Predicate>> predicates) {
        predicates.add(root -> builder.equal(root.get("deleted"), false));
    }
}
