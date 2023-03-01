package org.nmcpye.am.assignment.repository;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.assignment.AssignmentRepositoryExt;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.commons.util.SqlHelper;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("org.nmcpye.am.assignment.AssignmentRepositoryExt")
public class AssignmentRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Assignment>
    implements AssignmentRepositoryExt {

    private final Cache<Boolean> assignedToUserOrgUnitCache;

    public AssignmentRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                             ApplicationEventPublisher publisher,
                                             CurrentUserService currentUserService,
                                             AclService aclService,
                                             CacheProvider cacheProvider) {
        super(sessionFactory, jdbcTemplate, publisher, Assignment.class,
            currentUserService, aclService, true);
        this.assignedToUserOrgUnitCache = cacheProvider.createAssignedToUserOrgUnitCache();
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
    public List<Assignment> getAssignments(User user, Activity activity) {
        if (user == null) {
            return new ArrayList<>();
        }
        StringBuilder query = new StringBuilder()
            .append(
                "SELECT assi.* " +
                    "FROM assignment assi ")
            .append("JOIN team__members mem ON assi.assignedteamid = mem.teamid ")
            .append("WHERE mem.member_id = :userId ");

        if (activity != null) {
            query.append("AND assi.activityid = :actId ");
        }

        NativeQuery<Assignment> nativeQuery = getSession()
            .createNativeQuery(query.toString());

        nativeQuery.setParameter("userId", user.getId());

        if (activity != null) {
            nativeQuery.setParameter("actId", activity.getId());
        }

        return nativeQuery.getResultList();
    }

    @Override
    public List<Assignment> getAssignments(User user, Activity activity
        , boolean withManagedAssignments, boolean includeInactiveActivities) {
        SqlHelper hlp = new SqlHelper(true);
        if (user == null) {
            return new ArrayList<>();
        }

        StringBuilder query = new StringBuilder()
            .append(
                "SELECT assi.* " +
                    "FROM assignment assi ")
            .append("LEFT JOIN organisation_unit ou ON ou.organisationunitid = assi.organisationunitid ")
            .append("JOIN team__members mem ON mem.teamid = assi.assignedteamid ")
            .append("JOIN app_user au ON au.userid = mem.member_id ");

        if (withManagedAssignments) {
            query
                .append("JOIN team__managed_teams tmt ON tmt.managedteamid = assi.assignedteamid ")
                .append("JOIN team__members mgmem ON tmt.teamid = mgmem.teamid ")
                .append("JOIN app_user au2 ON au2.userid = mgmem.member_id ");
        }

        if (activity != null) {
            query.append("JOIN activity act ON act.activityid = assi.activityid ");
            query.append(hlp.whereAnd())
                .append("assi.activityid = ")
                .append(activity.getId());
            if (!includeInactiveActivities) {
                query
                    .append(hlp.whereAnd())
                    .append("act.inactive = false");
            }
        }

        query.append(hlp.whereAnd())
            .append("(au.userid = :userId ");
        if (withManagedAssignments) {
            query.append(" or au2.userid = :userId");
        }
        query.append(")");

        NativeQuery<Assignment> nativeQuery = getSession()
            .createNativeQuery(query.toString(), Assignment.class);

        nativeQuery.setParameter("userId", user.getId());

        List<Assignment> assignments = nativeQuery.getResultList();

        return assignments;
    }

//    private QueryWithOrderBy buildAssignmentHql(AssignmentQueryParams params) {
//
//        SqlHelper hlp = new SqlHelper(true);
//        String hql = "select distinct assi from Assignment assi";
//
//        boolean includeInactiveActivities = params.isIncludeInactiveActivities();
//
//        if (params.hasUser()) {
//            hql += "join assi.team.members mem";
//            hql += "join assi.team.members mem";
//        }
//
//        if (params.hasUser() && params.isIncludeManagedTeams()) {
//            hql += "join assi.team.members mem";
//        }
//
//        if (params.hasUser()) {
//            hql += hlp.whereAnd() + "mem.uid = '" + params.getUser().getUid() + "'";
//        }
//
//        if (params.hasTeams()) {
//            hql += hlp.whereAnd() + "assi.team.uid in ("
//                + getQuotedCommaDelimitedString(getUids(params.getTeams())) + ")";
//        }
//
//        if (params.hasActivity()) {
//            hql +=
//                hlp.whereAnd() +
//                    "assi.activity.uid = '" + params.getActivity().getUid() + "'";
//        }
//
//        if (params.hasStartDate()) {
//            hql += hlp.whereAnd() + "assi.startDate >= '" + DateUtils.getMediumDateString(params.getStartDate()) + "'";
//        }
//
//        if (params.hasLastUpdatedDuration()) {
//            includeInactiveActivities = true;
//            hql += hlp.whereAnd() + "assi.updated >= '" + DateUtils.getLongGmtDateString(DateUtils.nowMinusDuration(params.getLastUpdatedDuration())) + "'";
//        } else if (params.hasLastUpdated()) {
//            includeInactiveActivities = true;
//            hql += hlp.whereAnd() + "assi.updated >= '" + DateUtils.getMediumDateString(DateUtils.fromInstant(params.getLastUpdated())) + "'";
//        }
//
//        if (params.hasTrackedEntityInstance()) {
//            hql += hlp.whereAnd() + "assi.entityInstance.uid = '" + params.getTrackedEntityInstanceUid() + "'";
//        }
//
//        if (params.hasProject()) {
//            hql += hlp.whereAnd() + "assi.activity is not null and assi.activity.project.uid = '" + params.getProject().getUid() + "'";
//        }
//
//        if (params.hasActivities()) {
//            includeInactiveActivities = true;
//            hql +=
//                hlp.whereAnd() +
//                    "assi.activity.uid in (" +
//                    getQuotedCommaDelimitedString(getUids(params.getActivities())) +
//                    ")";
//        }
//
//        if (params.hasTrackedEntityType()) {
//            hql += hlp.whereAnd() + "assi.entityInstance.trackedEntityType.uid = '"
//                + params.getTrackedEntityType().getUid() + "'";
//        }
//
//        if (params.hasOrganisationUnits()) {
//            if (params.isOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS)) {
//                String ouClause = "(";
//                SqlHelper orHlp = new SqlHelper(true);
//
//                for (OrganisationUnit organisationUnit : params.getOrganisationUnits()) {
//                    ouClause += orHlp.or() + "assi.organisationUnit.path LIKE '" + organisationUnit.getPath() + "%'";
//                }
//
//                ouClause += ")";
//
//                hql += hlp.whereAnd() + ouClause;
//            } else {
//                hql += hlp.whereAnd() + "assi.organisationUnit.uid in ("
//                    + getQuotedCommaDelimitedString(getUids(params.getOrganisationUnits())) + ")";
//            }
//        }
//
//        if (params.hasProgram()) {
//            hql += hlp.whereAnd() + "assi.program.uid = '" + params.getProgram().getUid() + "'";
//        }
//
//        if (params.hasProgramStatus()) {
//            hql += hlp.whereAnd() + "assi." + STATUS + " = '" + params.getProgramStatus() + "'";
//        }
//
//        if (params.hasFollowUp()) {
//            hql += hlp.whereAnd() + "assi.followup = " + params.getFollowUp();
//        }
//
//        if (params.hasProgramStartDate()) {
//            includeInactiveActivities = true;
//            hql += hlp.whereAnd() + "assi.enrollmentDate >= '" + DateUtils.getMediumDateString(params.getProgramStartDate()) + "'";
//        }
//
//        if (params.hasProgramEndDate()) {
//            includeInactiveActivities = true;
//            hql += hlp.whereAnd() + "assi.enrollmentDate <= '" + DateUtils.getMediumDateString(params.getProgramEndDate()) + "'";
//        }
//
//        if (!params.isIncludeDeleted()) {
//            hql += hlp.whereAnd() + " assi.deleted is false ";
//        }
//
//        if (!includeInactiveActivities) {
//            hql += hlp.whereAnd() + " (assi.activity is null or assi.activity.inactive is false) ";
//        }
//
//
//        ProgramInstanceRepositoryExtCustomImpl.QueryWithOrderBy query = ProgramInstanceRepositoryExtCustomImpl.QueryWithOrderBy.builder().query(hql).build();
//
//        if (params.isSorting()) {
//            query =
//                query
//                    .toBuilder()
//                    .orderBy(
//                        " order by " +
//                            params
//                                .getOrder()
//                                .stream()
//                                .map(orderParam -> orderParam.getField() + " " + (orderParam.getDirection().isAscending() ? "asc" : "desc"))
//                                .collect(Collectors.joining(", "))
//                    )
//                    .build();
//        }
//
//        return query;
//    }
}
