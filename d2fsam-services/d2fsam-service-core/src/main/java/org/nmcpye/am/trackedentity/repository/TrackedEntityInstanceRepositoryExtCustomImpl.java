package org.nmcpye.am.trackedentity.repository;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.QueryHints;
import org.hibernate.query.Query;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.hibernate.SoftDeleteHibernateObjectStore;
import org.nmcpye.am.commons.util.SqlHelper;
import org.nmcpye.am.commons.util.TextUtils;
import org.nmcpye.am.dxf2.events.event.EventContext;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams.OrderColumn;

/**
 *
 */
@Repository("org.nmcpye.am.trackedentity.TrackedEntityInstanceRepositoryExt")
public class TrackedEntityInstanceRepositoryExtCustomImpl
    extends SoftDeleteHibernateObjectStore<TrackedEntityInstance>
    implements TrackedEntityInstanceRepositoryExt {

    private static final String TEI_HQL_BY_UIDS = "from TrackedEntityInstance as tei where tei.uid in (:uids)";

    private static final String TEI_HQL_BY_IDS = "from TrackedEntityInstance as tei where tei.id in (:ids)";

    private static final String AND_PSI_STATUS_EQUALS_SINGLE_QUOTE = "and psi.status = '";

    private static final String OFFSET = "OFFSET";

    private static final String LIMIT = "LIMIT";

    private static final String PSI_EXECUTIONDATE = "PSI.executiondate";

    private static final String PSI_DUEDATE = "PSI.duedate";

    private static final String IS_NULL = "IS NULL";

    private static final String IS_NOT_NULL = "IS NOT NULL";

    private static final String SPACE = " ";

    private static final String SINGLE_QUOTE = "'";

    private static final String EQUALS = " = ";

    private static final String PSI_STATUS = "PSI.status";

    private static final String TEI_LASTUPDATED = " tei.updated";

    private static final String GT_EQUAL = " >= ";

    private static final String UID_VALUE_SEPARATOR = ":";

    private static final String UID_VALUE_PAIR_SEPARATOR = ";@//@;";

    private final Logger log = LoggerFactory.getLogger(TrackedEntityInstanceRepositoryExt.class);

    private final OrganisationUnitRepositoryExt organisationUnitRepositoryExt;

    private final StatementBuilder statementBuilder;

    private final SystemSettingManager systemSettingManager;

    public TrackedEntityInstanceRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                        ApplicationEventPublisher publisher,
                                                        CurrentUserService currentUserService,
                                                        AclService aclService,
                                                        OrganisationUnitRepositoryExt organisationUnitRepositoryExt,
                                                        StatementBuilder statementBuilder,
                                                        SystemSettingManager systemSettingManager) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityInstance.class, currentUserService, aclService, true);
        this.organisationUnitRepositoryExt = organisationUnitRepositoryExt;
        this.statementBuilder = statementBuilder;
        this.systemSettingManager = systemSettingManager;
    }

    @Override
    public List<TrackedEntityInstance> getTrackedEntityInstances(TrackedEntityInstanceQueryParams params) {
        List<Long> teiIds = getTrackedEntityInstanceIds(params);
        List<TrackedEntityInstance> sortedTeis = new ArrayList<>();
        List<List<Long>> idsPartitions = Lists.partition(Lists.newArrayList(teiIds), 20000);

        for (List<Long> idsPartition : idsPartitions) {
            if (!idsPartition.isEmpty()) {
                List<TrackedEntityInstance> teis = getSession()
                    .createQuery(TEI_HQL_BY_IDS, TrackedEntityInstance.class)
                    .setParameter("ids", idsPartition)
                    .getResultList();

                teis.sort(comparing(tei -> idsPartition.indexOf(tei.getId())));

                sortedTeis.addAll(teis);
            }
        }

        return sortedTeis;
    }

    @Override
    public List<Long> getTrackedEntityInstanceIds(TrackedEntityInstanceQueryParams params) {
        String sql = getQuery(params, false);
        log.debug("Tracked entity instance query SQL: " + sql);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        checkMaxTeiCountReached(params, rowSet);

        List<Long> ids = new ArrayList<>();

        while (rowSet.next()) {
            ids.add(rowSet.getLong("teiid"));
        }

        return ids;
    }

    @Override
    public List<Map<String, String>> getTrackedEntityInstancesGrid(TrackedEntityInstanceQueryParams params) {
        String sql = getQuery(params, true);
        log.debug("Tracked entity instance query SQL: " + sql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        checkMaxTeiCountReached(params, rowSet);

        List<Map<String, String>> list = new ArrayList<>();

        while (rowSet.next()) {
            final Map<String, String> map = new HashMap<>();

            map.put(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID, rowSet.getString(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID));
            map.put(TrackedEntityInstanceQueryParams.CREATED_ID, rowSet.getString(TrackedEntityInstanceQueryParams.CREATED_ID));
            map.put(TrackedEntityInstanceQueryParams.LAST_UPDATED_ID, rowSet.getString(TrackedEntityInstanceQueryParams.LAST_UPDATED_ID));
            map.put(TrackedEntityInstanceQueryParams.ORG_UNIT_ID, rowSet.getString(TrackedEntityInstanceQueryParams.ORG_UNIT_ID));
            map.put(TrackedEntityInstanceQueryParams.ORG_UNIT_NAME, rowSet.getString(TrackedEntityInstanceQueryParams.ORG_UNIT_NAME));
            map.put(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_ID, rowSet.getString(TrackedEntityInstanceQueryParams.TRACKED_ENTITY_ID));
            map.put(TrackedEntityInstanceQueryParams.INACTIVE_ID, rowSet.getString(TrackedEntityInstanceQueryParams.INACTIVE_ID));
            map.put(TrackedEntityInstanceQueryParams.POTENTIAL_DUPLICATE, rowSet.getString(TrackedEntityInstanceQueryParams.POTENTIAL_DUPLICATE));

            if (params.isIncludeDeleted()) {
                map.put(TrackedEntityInstanceQueryParams.DELETED, rowSet.getString(TrackedEntityInstanceQueryParams.DELETED));
            }

            if (!params.getAttributesAndFilters().isEmpty()) {
                HashMap<String, String> attributeValues = new HashMap<>();
                String teavString = rowSet.getString("tea_values");

                extractFromTeavAggregatedString(attributeValues, teavString);

                for (QueryItem item : params.getAttributes()) {
                    map.put(
                        item.getItemId(),
                        isOrgUnit(item) && attributeValues.containsKey(item.getItemId())
                            ? getOrgUnitNameByUid(attributeValues.get(item.getItemId()))
                            : attributeValues.get(item.getItemId())
                    );
                }
            }

            list.add(map);
        }

        return list;
    }

    @Override
    public int getTrackedEntityInstanceCountForGrid(TrackedEntityInstanceQueryParams params) {
        // ---------------------------------------------------------------------
        // Select clause
        // ---------------------------------------------------------------------

        String sql = getCountQuery(params);

        // ---------------------------------------------------------------------
        // Query
        // ---------------------------------------------------------------------

        log.debug("Tracked entity instance count SQL: " + sql);

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int getTrackedEntityInstanceCountForGridWithMaxTeiLimit(TrackedEntityInstanceQueryParams params) {
        String sql = getCountQueryWithMaxTeiLimit(params);

        log.debug("Tracked entity instance count SQL: " + sql);

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * Generates SQL based on "params". The purpose of the SQL is to retrieve a
     * list of tracked entity instances, and additionally any requested
     * attributes (If defined in params).
     * <p>
     * The params are validated before we generate the SQL, so the only
     * access-related SQL is the inner join o organisation units.
     * <p>
     * The general structure of the query is as follows:
     * <p>
     * select (main_projection) from (constraint_subquery) left join
     * (additional_information) group by (main_groupby) order by (order)
     * <p>
     * The constraint_subquery looks as follows:
     * <p>
     * select (subquery_projection) from (tracked entity instances) inner join
     * (attribute_constraints) [inner join (program_owner)] inner join
     * (organisation units) left join (attribute_orderby) where
     * exist(program_constraint) order by (order) limit (limit_offset)
     * <p>
     * main_projection: Will have an aggregate string of attributevalues
     * (uid:value) as well as basic tei-info. constraint_subquery: Includes all
     * SQL related to narrowing down the number of tei's we are looking for. We
     * use inner join primarily for this, as well as exists for program
     * instances. Do make sure we get the right selection, we also use left join
     * on attributes, when we are sorting by attributes, before we sort and
     * finally limit the selection. subquery_projection: Has all the required
     * information for knowing what teis to return and how to order them
     * attribute_constraints: We inner join the attributes, and add 3
     * conditions: tei id, tea id and value. This uses a (tei, tea,
     * lower(value)) index. For each attribute constraints, we add subsequent
     * inner joins. program_owner: Only included when a program is specified. If
     * included, it will join on 3 columns: tei, program and ou. We have an
     * index for this (program, ou, tei) which allows a scan only lookup
     * attribute_orderby: When a user specified an attribute in the order param,
     * we need to join that attribute (We do left join, in case the value is not
     * there. This join is not for removing resulting records). After joining it
     * and projecting it, we can order by it. program_constraint: If a program
     * is specified, it indicates the tei must be enrolled in that program.
     * Since the relation between tei and enrollments are not 1:1, but 1:many,
     * we use exists to avoid duplicate rows of tei, allowing us to avoid
     * grouping the result before we order and limit. This saves a lot of time.
     * NOTE: Within the program_constraint, we also have a subquery to deal with
     * any event-related constraints. These can either be constraints on any
     * static properties, or user assignment. For user assignment, we also join
     * with the userinfo table. For events, we have an index (status,
     * executiondate) which speeds up the lookup significantly order: Order is
     * used both in the subquery and the main query. The sort depends on the
     * params (see more info on the related method). We order the subquery to
     * make sure we get correct results before we limit. We order the main query
     * since the aggregation mixes up the order, so to return a consistent
     * order, we order again. limit_offset: The limit and offset is set based on
     * a combination of params: program and tet can have a maxtei limit, which
     * only applies during a search outside the users capture scope. If applied,
     * it will throw an error if the number of results exceeds the limit.
     * Otherwise we use paging. If no paging is set, there is no limit.
     * additional_information: Here we do a left join with any relevant
     * information needed for the result: tet name, any attributes to project,
     * etc. We left join, since we don't want to reduce the results, just add
     * information. main_groupby: The purpose of this group by, is to aggregate
     * any attributes added in additional_information
     *
     * @param params params defining the query
     * @return SQL string
     */
    private String getQuery(TrackedEntityInstanceQueryParams params, boolean isGridQuery) {
        // Master
        if (params.isOrQuery() && params.getAttributesAndFilters().isEmpty()) {
            throw new IllegalArgumentException(
                "A query parameter is used in the request but there aren't filterable attributes");
        }

        String query = new StringBuilder()
            .append(getQuerySelect(params, isGridQuery))
            .append("FROM ")
            .append(getFromSubQuery(params, false, isGridQuery))
            .append(getQueryRelatedTables(params))
            .append(getQueryGroupBy(params))
            .append(getQueryOrderBy(false, params, isGridQuery))
            .toString();

        return query;
    }

    /**
     * Uses the same basis as the getQuery method, but replaces the projection
     * with a count and ignores order and limit
     *
     * @param params params defining the query
     * @return a count SQL query
     */
    private String getCountQuery(TrackedEntityInstanceQueryParams params) {
        return new StringBuilder()
            .append(getQueryCountSelect(params))
            .append(getQuerySelect(params, true))
            .append("FROM ")
            .append(getFromSubQuery(params, true, true))
            .append(getQueryRelatedTables(params))
            .append(getQueryGroupBy(params))
            .append(" ) teicount")
            .toString();
    }

    /**
     * Uses the same basis as the getQuery method, but replaces the projection
     * with a count, ignores order but uses the TEI limit set on the program if
     * higher than 0
     *
     * @param params params defining the query
     * @return a count SQL query
     */
    private String getCountQueryWithMaxTeiLimit(TrackedEntityInstanceQueryParams params) {
        return new StringBuilder()
            .append(getQueryCountSelect(params))
            .append(getQuerySelect(params, true))
            .append("FROM ")
            .append(getFromSubQuery(params, true, true))
            .append(getQueryRelatedTables(params))
            .append(getQueryGroupBy(params))
            .append(params.getProgram().getMaxTeiCountToReturn() > 0
                ? getLimitClause(params.getProgram().getMaxTeiCountToReturn() + 1)
                : "")
            .append(" ) teicount")
            .toString();
    }

    /**
     * Generates the projection of the main query. Includes two optional
     * columns, deleted and tea_values
     *
     * @param params
     * @return an SQL projection
     */
    private String getQuerySelect(TrackedEntityInstanceQueryParams params, boolean isGridQuery) {
        StringBuilder select = new StringBuilder()
            .append("SELECT TEI.uid AS " + TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID + ", ")
            .append("TEI.created AS " + TrackedEntityInstanceQueryParams.CREATED_ID + ", ")
            .append("TEI.updated AS " + TrackedEntityInstanceQueryParams.LAST_UPDATED_ID + ", ")
            .append("TEI.ou AS " + TrackedEntityInstanceQueryParams.ORG_UNIT_ID + ", ")
            .append("TEI.ouname AS " + TrackedEntityInstanceQueryParams.ORG_UNIT_NAME + ", ")
            .append("TET.uid AS " + TrackedEntityInstanceQueryParams.TRACKED_ENTITY_ID + ", ")
            .append("TEI.inactive AS " + TrackedEntityInstanceQueryParams.INACTIVE_ID + ", ")
            .append("TEI.potentialduplicate AS " + TrackedEntityInstanceQueryParams.POTENTIAL_DUPLICATE)
            .append(params.isIncludeDeleted() ? ", TEI.deleted AS " + TrackedEntityInstanceQueryParams.DELETED : "")
            .append(
                params.hasAttributes()
                    ? ", string_agg(TEA.uid || '" +
                    UID_VALUE_SEPARATOR +
                    "' || TEAV.value, '" +
                    UID_VALUE_PAIR_SEPARATOR +
                    "') AS tea_values"
                    : ""
            );

        if (!isGridQuery) {
            select.append(", TEI.trackedentityinstanceid AS teiid");
        }
        select.append(SPACE);

        return select.toString();
    }

    /**
     * Generates the projection of the main query when doing a count query.
     *
     * @param params
     * @return an SQL projection
     */
    private String getQueryCountSelect(TrackedEntityInstanceQueryParams params) {
        return "SELECT count(instance) FROM ( ";
    }

    /**
     * Generates the SQL of the subquery, used to find the correct subset of
     * tracked entity instances to return. Orchestrates all the different
     * segments of the SQL into a complete subquery.
     *
     * @param params
     * @param isCountQuery indicates if the query is a count query. In that case
     *                     we skip order and limit.
     * @param isGridQuery  indicates if the query is a grid query.
     * @return an SQL subquery
     */
    private String getFromSubQuery(TrackedEntityInstanceQueryParams params, boolean isCountQuery, boolean isGridQuery) {
        SqlHelper whereAnd = new SqlHelper(true);
        StringBuilder fromSubQuery = new StringBuilder()
            .append("(")
            .append(getFromSubQuerySelect(params))
            .append(" FROM tracked_entity_instance TEI ")
            // INNER JOIN on constraints
            .append(getFromSubQueryJoinAttributeConditions(params))
            .append(getFromSubQueryJoinProgramOwnerConditions(params))
            .append(getFromSubQueryJoinOrgUnitConditions(params))
            // LEFT JOIN attributes we need to sort on.
            .append(getFromSubQueryJoinOrderByAttributes(params))
            // WHERE
            .append(getFromSubQueryTrackedEntityConditions(whereAnd, params))
            .append(getFromSubQueryProgramInstanceConditions(whereAnd, params));

        if (!isCountQuery) {
            // SORT
            fromSubQuery
                .append(getQueryOrderBy(true, params, isGridQuery))
                // LIMIT, OFFSET
                .append(getFromSubQueryLimitAndOffset(params));
        }

        return fromSubQuery.append(") TEI ").toString();
    }

    /**
     * The subquery projection. If we are sorting by attribute, we need to
     * include the value in the subquery projection.
     *
     * @param params
     * @return a SQL projection
     */
    private String getFromSubQuerySelect(TrackedEntityInstanceQueryParams params) {
        return new StringBuilder()
            .append("SELECT ")
            .append("TEI.trackedentityinstanceid, ")
            .append("TEI.uid, ")
            .append("TEI.created, ")
            .append("TEI.updated, ")
            .append("TEI.inactive, ")
            .append("TEI.trackedentitytypeid, ")
            .append("TEI.potentialduplicate, ")
            .append("TEI.deleted, ")
            .append("OU.uid as ou, ")
            .append("OU.name as ouname ")
            .append(getFromSubQueryOrderAttributes(params))
            .toString();
    }

    /**
     * Generates a list of columns for projections if we are ordering by
     * attributes.
     *
     * @param params
     * @return a list of columns to be used in the subquery SQL projection, or
     * empty string if no attributes are used for ordering.
     */
    private String getFromSubQueryOrderAttributes(TrackedEntityInstanceQueryParams params) {
        StringBuilder orderAttributes = new StringBuilder();

        for (QueryItem orderAttribute : getOrderAttributes(params)) {
            orderAttributes
                .append(", ")
                .append(statementBuilder.columnQuote(orderAttribute.getItemId()))
                .append(".value AS ")
                .append(statementBuilder.columnQuote(orderAttribute.getItemId()));
        }

        return orderAttributes.toString();
    }

    /**
     * Generates the WHERE-clause of the subquery SQL related to tracked entity
     * instances.
     *
     * @param whereAnd tracking if where has been invoked or not
     * @param params
     * @return a SQL segment for the WHERE clause used in the subquery
     */
    private String getFromSubQueryTrackedEntityConditions(SqlHelper whereAnd, TrackedEntityInstanceQueryParams params) {
        StringBuilder trackedEntity = new StringBuilder();

        if (params.hasTrackedEntityType()) {
            trackedEntity
                .append(whereAnd.whereAnd())
                .append("TEI.trackedentitytypeid = ")
                .append(params.getTrackedEntityType().getId())
                .append(SPACE);
        } else if (!CollectionUtils.isEmpty(params.getTrackedEntityTypes())) {
            trackedEntity
                .append(whereAnd.whereAnd())
                .append("TEI.trackedentitytypeid IN (")
                .append(TextUtils.getCommaDelimitedString(IdentifiableObjectUtils.getIdentifiers(params.getTrackedEntityTypes())))
                .append(") ");
        }

        if (params.hasTrackedEntityInstances()) {
            trackedEntity
                .append(whereAnd.whereAnd())
                .append("TEI.uid IN (")
                .append(encodeAndQuote(params.getTrackedEntityInstanceUids()))
                .append(") ");
        }

        if (params.hasLastUpdatedDuration()) {
            trackedEntity
                .append(whereAnd.whereAnd())
                .append(" TEI.updated >= '")
                .append(DateUtils.getLongGmtDateString(DateUtils.nowMinusDuration(params.getLastUpdatedDuration())))
                .append(SINGLE_QUOTE);
        } else {
            if (params.hasLastUpdatedStartDate()) {
                trackedEntity
                    .append(whereAnd.whereAnd())
                    .append(" TEI.updated >= '")
                    .append(DateUtils.getMediumDateString(params.getLastUpdatedStartDate()))
                    .append(SINGLE_QUOTE);
            }
            if (params.hasLastUpdatedEndDate()) {
                trackedEntity
                    .append(whereAnd.whereAnd())
                    .append(" TEI.updated < '")
                    .append(DateUtils.getMediumDateString(addDays(params.getLastUpdatedEndDate(), 1)))
                    .append(SINGLE_QUOTE);
            }
        }

        if (!params.isIncludeDeleted()) {
            trackedEntity.append(whereAnd.whereAnd()).append("TEI.deleted IS FALSE ");
        }

        if (params.hasPotentialDuplicateFilter()) {
            trackedEntity
                .append(whereAnd.whereAnd()).append("TEI.potentialduplicate=")
                .append(params.getPotentialDuplicate()).append(SPACE);
        }

        return trackedEntity.toString();
    }

    /**
     * Generates SQL for INNER JOINing attribute values. One INNER JOIN for each
     * attribute to search for.
     *
     * @param params
     * @return a series of 1 or more SQL INNER JOINs, or empty string if no
     * query or attribute filters exists.
     */
    private String getFromSubQueryJoinAttributeConditions(TrackedEntityInstanceQueryParams params) {
        if (!params.isOrQuery()) {
            return joinAttributeValueWithoutQueryParameter(params);
        } else {
            return joinAttributeValueWithQueryParameter(params);
        }
    }

    /**
     * Generates a single INNER JOIN for searching for an attribute by query
     * strings. Searches are done using lower() expression, since attribute
     * values are case insensitive. The query search is extremely slow compared
     * to alternatives. A query string (Can be multiple) has to match at least 1
     * attribute value for each attribute we have access to. We use Regex to
     * search, allowing both exact match and with wildcards (EQ or LIKE).
     *
     * @param params
     */
    private String joinAttributeValueWithQueryParameter(TrackedEntityInstanceQueryParams params) {
        StringBuilder attributes = new StringBuilder();

        final String regexp = statementBuilder.getRegexpMatch();
        final String wordStart = statementBuilder.getRegexpWordStart();
        final String wordEnd = statementBuilder.getRegexpWordEnd();
        final String anyChar = "\\.*?";
        final String start = params.getQuery().isOperator(QueryOperator.LIKE) ? anyChar : wordStart;
        final String end = params.getQuery().isOperator(QueryOperator.LIKE) ? anyChar : wordEnd;
        SqlHelper orHlp = new SqlHelper(true);

        List<Long> itemIds = params
            .getAttributesAndFilters()
            .stream()
            .map(QueryItem::getItem)
            .map(DimensionalItemObject::getId)
            .collect(Collectors.toList());

        attributes
            .append("INNER JOIN tracked_entity_attribute_value Q ")
            .append("ON Q.trackedentityinstanceid = TEI.trackedentityinstanceid ")
            .append("AND Q.trackedentityattributeid IN (")
            .append(TextUtils.getCommaDelimitedString(itemIds))
            .append(") AND (");

        for (String queryToken : TextUtils.getTokens(params.getQuery().getFilter())) {
            final String query = statementBuilder.encode(queryToken, false);

            attributes
                .append(orHlp.or())
                .append("lower(Q.value) ")
                .append(regexp)
                .append(" '")
                .append(start)
                .append(StringUtils.lowerCase(query))
                .append(end)
                .append(SINGLE_QUOTE);
        }

        return attributes.append(")").toString();
    }

    /**
     * Generates a single INNER JOIN for each attribute we are searching on. We
     * can search by a range of operators. All searching is using lower() since
     * attribute values are case insensitive.
     *
     * @param params
     */
    private String joinAttributeValueWithoutQueryParameter(TrackedEntityInstanceQueryParams params) {
        StringBuilder attributes = new StringBuilder();

        List<QueryItem> filterItems = params.getAttributesAndFilters().stream()
            .filter(QueryItem::hasFilter)
            .collect(Collectors.toList());

        for (QueryItem queryItem : filterItems) {
            String col = statementBuilder.columnQuote(queryItem.getItemId());
            String teaId = col + ".trackedentityattributeid";
            String teav = "lower(" + col + ".value)";
            String teiid = col + ".trackedentityinstanceid";

            attributes
                .append(" INNER JOIN tracked_entity_attribute_value ")
                .append(col)
                .append(" ON ")
                .append(teaId)
                .append(EQUALS)
                .append(queryItem.getItem().getId())
                .append(" AND ")
                .append(teiid)
                .append(" = TEI.trackedentityinstanceid ");

            for (QueryFilter filter : queryItem.getFilters()) {
                String encodedFilter = statementBuilder.encode(filter.getFilter(),
                    false);
                attributes
                    .append("AND ")
                    .append(teav)
                    .append(SPACE)
                    .append(filter.getSqlOperator())
                    .append(SPACE)
                    .append(StringUtils
                        .lowerCase(filter.getSqlFilter(encodedFilter)));
            }
        }

        return attributes.toString();
    }

    /**
     * Generates the LEFT JOINs used for attributes we are ordering by (If any).
     * We use LEFT JOIN to avoid removing any rows if there is no value for a
     * given attribute and tei. The result of this LEFT JOIN is used in the
     * subquery projection, and ordering in the subquery and main query.
     *
     * @param params
     * @return a SQL LEFT JOIN for attributes used for ordering, or empty string
     * if not attributes is used in order.
     */
    private String getFromSubQueryJoinOrderByAttributes(TrackedEntityInstanceQueryParams params) {
        StringBuilder joinOrderAttributes = new StringBuilder();

        for (QueryItem orderAttribute : getOrderAttributes(params)) {
            if (orderAttribute.hasFilter()) { // We already joined this if it is a filter.
                continue;
            }

            joinOrderAttributes
                .append(" LEFT JOIN tracked_entity_attribute_value AS ")
                .append(statementBuilder.columnQuote(orderAttribute.getItemId()))
                .append(" ON ")
                .append(statementBuilder.columnQuote(orderAttribute.getItemId()))
                .append(".trackedentityinstanceid = TEI.trackedentityinstanceid ")
                .append("AND ")
                .append(statementBuilder.columnQuote(orderAttribute.getItemId()))
                .append(".trackedentityattributeid = ")
                .append(orderAttribute.getItem().getId())
                .append(SPACE);
        }

        return joinOrderAttributes.toString();
    }

    /**
     * Generates an INNER JOIN for program owner. This segment is only included
     * if program is specified.
     *
     * @param params
     * @return a SQL INNER JOIN for program owner, or empty string if no program
     * is specified.
     */
    private String getFromSubQueryJoinProgramOwnerConditions(TrackedEntityInstanceQueryParams params) {
        if (!params.hasProgram()) {
            return "";
        }

        return new StringBuilder()
            .append(" INNER JOIN tracked_entity_program_owner PO ")
            .append("ON PO.programid = ")
            .append(params.getProgram().getId())
            .append(" AND PO.trackedentityinstanceid = TEI.trackedentityinstanceid ")
            .toString();
    }

    /**
     * Generates an INNER JOIN for organisation units. If a program is
     * specified, we join on program ownership (PO), if not we join by tracked
     * entity instance (TEI). Based on the ouMode, they will boil down to either
     * DESCENDANTS (requiring matching on PATH), ALL (No constraints) or not
     * DESCENDANTS or ALL (SELECTED) which will match against a collection of
     * ids.
     *
     * @param params
     * @return a SQL INNER JOIN for organisation units
     */
    private String getFromSubQueryJoinOrgUnitConditions(TrackedEntityInstanceQueryParams params) {
        StringBuilder orgUnits = new StringBuilder();

        params.handleOrganisationUnits();

        orgUnits
            .append(" INNER JOIN organisation_unit OU ")
            .append("ON OU.organisationunitid = ")
            .append(params.hasProgram() ? "PO.organisationunitid " : "TEI.organisationunitid ");

        if (!params.hasOrganisationUnits()) {
            return orgUnits.toString();
        }

        if (params.isOrganisationUnitMode(OrganisationUnitSelectionMode.DESCENDANTS)) {
            SqlHelper orHlp = new SqlHelper(true);

            orgUnits.append("AND (");

            for (OrganisationUnit organisationUnit : params.getOrganisationUnits()) {

                OrganisationUnit ou = organisationUnitRepositoryExt
                    .getByUid(organisationUnit.getUid());
                if (ou != null) {
                    orgUnits
                        .append(orHlp.or())
                        .append("OU.path LIKE '")
                        .append(ou.getPath())
                        .append("%'");
                }
            }

            orgUnits.append(") ");
        } else if (!params.isOrganisationUnitMode(OrganisationUnitSelectionMode.ALL)) {
            orgUnits.append("AND OU.organisationunitid IN (").append(TextUtils.getCommaDelimitedString(
                IdentifiableObjectUtils.getIdentifiers(params.getOrganisationUnits()))).append(") ");
        }

        return orgUnits.toString();
    }

    /**
     * Generates an EXISTS condition for program instance (and program stage
     * instance if specified). The EXIST will allow us to filter by enrollments
     * with a low overhead. This condition only applies when a program is
     * specified.
     *
     * @param whereAnd indicator tracking whether WHERE has been invoked or not
     * @param params
     * @return an SQL EXISTS clause for programinstance, or empty string if not
     * program is specified.
     */
    private String getFromSubQueryProgramInstanceConditions(SqlHelper whereAnd, TrackedEntityInstanceQueryParams params) {
        StringBuilder program = new StringBuilder();

        if (!params.hasProgram()) {
            return "";
        }

        program
            .append(whereAnd.whereAnd())
            .append("EXISTS (")
            .append("SELECT PI.trackedentityinstanceid ")
            .append("FROM program_instance PI ")
            .append("LEFT JOIN activity ACT on PI.activityid = ACT.activityid ")
            .append("LEFT JOIN project PRO on ACT.projectid = PRO.projectid ");

        if (params.hasFilterForEvents()) {
            program.append(getFromSubQueryProgramStageInstance(params));
        }

        program.append("WHERE PI.trackedentityinstanceid = TEI.trackedentityinstanceid ");

        if (params.hasProgram()) {
            program.append("AND PI.programid = ").append(params.getProgram().getId()).append(SPACE);
        }

        if (params.hasProject()) {
            program.append("AND PRO.projectid = ").append(params.getProject().getId()).append(SPACE);
        }

        if (params.hasActivities()) {
            program.append("AND (PI.activityid is not null and ACT.uid in (").append(
                TextUtils.getQuotedCommaDelimitedString(
                    IdentifiableObjectUtils.getUids(params.getActivities()))).append(")) ").append(SPACE);
        }

        if (params.hasProgramStatus()) {
            program.append("AND PI.status = '").append(params.getProgramStatus()).append("' ");
        }

        if (params.hasFollowUp()) {
            program.append("AND PI.followup IS ").append(params.getFollowUp()).append(SPACE);
        }

        if (params.hasProgramEnrollmentStartDate()) {
            program
                .append("AND PI.enrollmentdate >= '")
                .append(DateUtils.getMediumDateString(params.getProgramEnrollmentStartDate()))
                .append("' ");
        }

        if (params.hasProgramEnrollmentEndDate()) {
            program
                .append("AND PI.enrollmentdate <= '")
                .append(DateUtils.getMediumDateString(params.getProgramEnrollmentEndDate()))
                .append("' ");
        }

        if (params.hasProgramIncidentStartDate()) {
            program
                .append("AND PI.incidentdate >= '")
                .append(DateUtils.getMediumDateString(params.getProgramIncidentStartDate()))
                .append("' ");
        }

        if (params.hasProgramIncidentEndDate()) {
            program
                .append("AND PI.incidentdate <= '")
                .append(DateUtils.getMediumDateString(params.getProgramIncidentEndDate()))
                .append("' ");
        }

        if (!params.isIncludeDeleted()) {
            program.append("AND PI.deleted is false ");
        }

        program.append(") ");

        return program.toString();
    }

    /**
     * Generates an INNER JOIN with the program instances if event-filters are
     * specified. In the case of user assignment is part of the filter, we join
     * with the app_user table as well.
     *
     * @param params
     * @return an SQL INNER JOIN for filtering on events.
     */
    private String getFromSubQueryProgramStageInstance(TrackedEntityInstanceQueryParams params) {
        StringBuilder events = new StringBuilder();
        SqlHelper whereHlp = new SqlHelper(true);

        events.append("INNER JOIN (")
            .append("SELECT PSI.programinstanceid ")
            .append("FROM program_stage_instance PSI ");

        if (params.getAssignedUserQueryParam().hasAssignedUsers()) {
            events
                .append("INNER JOIN (")
                .append("SELECT userid AS userid ")
                .append("FROM app_user ")
                .append("WHERE uid IN (")
                .append(encodeAndQuote(params.getAssignedUserQueryParam().getAssignedUsers()))
                .append(") ")
                .append(") AU ON AU.userid = PSI.assigneduserid");
        }

        if (params.hasEventStatus()) {
            String start = DateUtils.getMediumDateString(params.getEventStartDate());
            String end = DateUtils.getMediumDateString(params.getEventEndDate());

            if (params.isEventStatus(EventStatus.COMPLETED)) {
                events
                    .append(getQueryDateConditionBetween(whereHlp, PSI_EXECUTIONDATE, start, end))
                    .append(whereHlp.whereAnd())
                    .append(PSI_STATUS)
                    .append(EQUALS)
                    .append(SINGLE_QUOTE)
                    .append(EventStatus.COMPLETED.name())
                    .append(SINGLE_QUOTE)
                    .append(SPACE);
            } else if (params.isEventStatus(EventStatus.VISITED) || params.isEventStatus(EventStatus.ACTIVE)) {
                events
                    .append(getQueryDateConditionBetween(whereHlp, PSI_EXECUTIONDATE, start, end))
                    .append(whereHlp.whereAnd())
                    .append(PSI_STATUS)
                    .append(EQUALS)
                    .append(SINGLE_QUOTE)
                    .append(EventStatus.ACTIVE.name())
                    .append(SINGLE_QUOTE)
                    .append(SPACE);
            } else if (params.isEventStatus(EventStatus.SCHEDULE)) {
                events
                    .append(getQueryDateConditionBetween(whereHlp, PSI_DUEDATE, start, end))
                    .append(whereHlp.whereAnd())
                    .append(PSI_STATUS)
                    .append(SPACE)
                    .append(IS_NOT_NULL)
                    .append(whereHlp.whereAnd())
                    .append(PSI_EXECUTIONDATE)
                    .append(SPACE)
                    .append(IS_NULL)
                    .append(whereHlp.whereAnd())
                    .append("date(now()) <= date(PSI.duedate) ");
            } else if (params.isEventStatus(EventStatus.OVERDUE)) {
                events
                    .append(getQueryDateConditionBetween(whereHlp, PSI_DUEDATE, start, end))
                    .append(whereHlp.whereAnd())
                    .append(PSI_STATUS)
                    .append(SPACE)
                    .append(IS_NOT_NULL)
                    .append(whereHlp.whereAnd())
                    .append(PSI_EXECUTIONDATE)
                    .append(SPACE)
                    .append(IS_NULL)
                    .append(whereHlp.whereAnd())
                    .append("date(now()) > date(PSI.duedate) ");
            } else if (params.isEventStatus(EventStatus.SKIPPED)) {
                events
                    .append(getQueryDateConditionBetween(whereHlp, PSI_DUEDATE, start, end))
                    .append(whereHlp.whereAnd())
                    .append(PSI_STATUS)
                    .append(EQUALS)
                    .append(SINGLE_QUOTE)
                    .append(EventStatus.SKIPPED.name())
                    .append(SINGLE_QUOTE)
                    .append(SPACE);
            }
        }

        if (params.hasProgramStage()) {
            events.append(whereHlp.whereAnd()).append("PSI.programstageid = ").append(params.getProgramStage().getId()).append(SPACE);
        }

        if (AssignedUserSelectionMode.NONE == params.getAssignedUserQueryParam().getMode()) {
            events
                .append(whereHlp.whereAnd())
                .append("PSI.assigneduserid IS NULL ");
        }

        if (AssignedUserSelectionMode.ANY == params.getAssignedUserQueryParam().getMode()) {
            events
                .append(whereHlp.whereAnd())
                .append("PSI.assigneduserid IS NOT NULL ");
        }

        if (!params.isIncludeDeleted()) {
            events.append(whereHlp.whereAnd()).append("PSI.deleted IS FALSE");
        }

        events.append(") PSI ON PSI.programinstanceid = PI.programinstanceid ");

        return events.toString();
    }

    /**
     * Helper method for making a date condition. The format is "[WHERE|AND]
     * date >= start AND date <= end".
     *
     * @param whereHelper tracking whether WHERE has been invoked or not
     * @param column      the column for filter on
     * @param start       the start date
     * @param end         the end date
     * @return a SQL filter for finding dates between two dates.
     */
    private String getQueryDateConditionBetween(SqlHelper whereHelper, String column, String start, String end) {
        StringBuilder dateBetween = new StringBuilder();

        dateBetween
            .append(whereHelper.whereAnd())
            .append(column)
            .append(" >= '")
            .append(start)
            .append(SINGLE_QUOTE)
            .append(whereHelper.whereAnd())
            .append(column)
            .append(" <= '")
            .append(end)
            .append("' ");

        return dateBetween.toString();
    }

    /**
     * Generates SQL for LEFT JOINing relevant tables with the teis we have in
     * our result. After the subquery, we know which teis we are returning, but
     * these LEFT JOINs will add any extra information we need. For example
     * attribute values, tet uid, tea uid, etc.
     *
     * @param params
     * @return a SQL with several LEFT JOINS, one for each relevant table to
     * retrieve information from.
     */
    private String getQueryRelatedTables(TrackedEntityInstanceQueryParams params) {
        List<QueryItem> attributes = params.getAttributes();
        StringBuilder relatedTables = new StringBuilder();

        relatedTables.append("LEFT JOIN tracked_entity_type TET ON TET.trackedentitytypeid = TEI.trackedentitytypeid ");

        if (!attributes.isEmpty()) {
            String attributeString = TextUtils.getCommaDelimitedString(
                attributes.stream().map(QueryItem::getItem).map(IdentifiableObject::getId).collect(Collectors.toList())
            );

            relatedTables
                .append("LEFT JOIN tracked_entity_attribute_value TEAV ")
                .append("ON TEAV.trackedentityinstanceid = TEI.trackedentityinstanceid ")
                .append("AND TEAV.trackedentityattributeid IN (")
                .append(attributeString)
                .append(") ");

            relatedTables.append("LEFT JOIN tracked_entity_attribute TEA ").append("ON TEA.trackedentityattributeid = TEAV.trackedentityattributeid ");
        }

        return relatedTables.toString();
    }

    /**
     * Generates the GROUP BY clause of the query. This is only needed when we
     * are projecting any attributes. If any attributes are present we are
     * aggregating them into a string. In case we are ordering by an attribute,
     * we also need to include that column in the group by.
     *
     * @param params
     * @return a SQL GROUP BY clause, or empty string if no attributes are
     * specified.
     */
    private String getQueryGroupBy(TrackedEntityInstanceQueryParams params) {
        if (params.getAttributes().isEmpty()) {
            return "";
        }

        StringBuilder groupBy = new StringBuilder()
            .append("GROUP BY TEI.trackedentityinstanceid, ")
            .append("TEI.uid, ")
            .append("TEI.created, ")
            .append("TEI.updated, ")
            .append("TEI.ou, ")
            .append("TEI.ouname, ")
            .append("TET.uid, ")
            .append("TEI.potentialduplicate, ")
            .append("TEI.inactive ")
            .append(params.isIncludeDeleted() ? ", TEI.deleted " : "");

        if (!getOrderAttributes(params).isEmpty()) {
            for (QueryItem orderAttribute : getOrderAttributes(params)) {
                groupBy.append(", TEI.").append(statementBuilder.columnQuote(orderAttribute.getItemId())).append(SPACE);
            }
        }

        return groupBy.toString();
    }

    /**
     * Generates the ORDER BY clause. This clause is used both in the subquery
     * and main query. When using it in the subquery, we want to make sure we
     * get the right teis. When we order in the main query, it's to make sure we
     * return the results in the correct order, since order might be mixed after
     * GROUP BY.
     *
     * @param innerOrder  indicates whether this is the subquery order by or main
     *                    query order by
     * @param params
     * @param isGridQuery indicates whether this is used for grid query or not.
     * @return a SQL ORDER BY clause.
     */
    private String getQueryOrderBy(boolean innerOrder, TrackedEntityInstanceQueryParams params, boolean isGridQuery) {
        if (params.getOrders() != null && (!isGridQuery || (params.getAttributes() != null && !params.getAttributes().isEmpty()))) {
            ArrayList<String> orderFields = new ArrayList<>();

            for (OrderParam order : params.getOrders()) {
                if (OrderColumn.isStaticColumn(order.getField())) {
                    String columnName = TrackedEntityInstanceQueryParams.OrderColumn.getColumn(order.getField());
                    orderFields.add(columnName + " " + order.getDirection());
                } else {
                    extractDynamicOrderField(innerOrder, params, orderFields, order);
                }
            }

            if (!orderFields.isEmpty()) {
                return "ORDER BY " + StringUtils.join(orderFields, ',') + SPACE;
            }
        }

        if (params.getAttributesAndFilters().stream().noneMatch(qi -> qi.hasFilter() && qi.isUnique())) {
            return "ORDER BY TEI.trackedentityinstanceid ASC ";
        } else {
            return "";
        }
    }

    private String getLimitClause(int limit) {
        return "LIMIT " + limit;
    }

    private String getFromSubQueryLimitAndOffset(TrackedEntityInstanceQueryParams params) {
        StringBuilder limitOffset = new StringBuilder();
        int limit = params.getMaxTeiLimit();
        int teiQueryLimit = systemSettingManager.getIntSetting(SettingKey.TRACKED_ENTITY_MAX_LIMIT);

        if (limit == 0 && !params.isPaging()) {
            if (teiQueryLimit > 0) {
                return limitOffset.append(LIMIT).append(SPACE).append(teiQueryLimit).append(SPACE).toString();
            }

            return limitOffset.toString();
        } else if (limit == 0 && params.isPaging()) {
            return limitOffset
                .append(LIMIT)
                .append(SPACE)
                .append(params.getPageSizeWithDefault())
                .append(SPACE)
                .append(OFFSET)
                .append(SPACE)
                .append(params.getOffset())
                .append(SPACE)
                .toString();
        } else if (params.isPaging()) {
            return limitOffset
                .append(LIMIT)
                .append(SPACE)
                .append(Math.min(limit + 1, params.getPageSizeWithDefault()))
                .append(SPACE)
                .append(OFFSET)
                .append(SPACE)
                .append(params.getOffset())
                .append(SPACE)
                .toString();
        } else {
            return limitOffset
                .append(LIMIT)
                .append(SPACE)
                .append(limit + 1) // We add +1, since we use this limit to
                // restrict a user to search to wide.
                .append(SPACE)
                .toString();
        }
    }

    /**
     * Helper method for getting all attributes with a filter.
     *
     * @param params
     * @return a list of QueryItem, each representing an attribute with 1 or
     * more filters.
     */
    private List<QueryItem> getOrderAttributes(TrackedEntityInstanceQueryParams params) {
        if (params.getOrders() != null) {
            List<String> ordersIdentifier = params.getOrders().stream().map(OrderParam::getField).collect(Collectors.toList());

            return params
                .getAttributesAndFilters()
                .stream()
                .filter(queryItem -> ordersIdentifier.contains(queryItem.getItemId()))
                .collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    private String encodeAndQuote(Collection<String> elements) {
        return TextUtils.getQuotedCommaDelimitedString(
            elements.stream().map(element -> statementBuilder.encode(element, false)).collect(Collectors.toList())
        );
    }

    private void extractDynamicOrderField(
        boolean innerOrder,
        TrackedEntityInstanceQueryParams params,
        ArrayList<String> orderFields,
        OrderParam order
    ) {
        if (isAttributeOrder(params, order.getField()) || isAttributeFilterOrder(params, order.getField())) {
            if (innerOrder) {
                orderFields.add(statementBuilder.columnQuote(order.getField()) + ".value " + order.getDirection());
            } else {
                orderFields.add("TEI." + statementBuilder.columnQuote(order.getField()) + SPACE + order.getDirection());
            }
        }
    }

    private boolean isAttributeOrder(TrackedEntityInstanceQueryParams params, String attributeId) {
        if (params.hasAttributes()) {
            for (QueryItem item : params.getAttributes()) {
                if (attributeId.equals(item.getItemId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAttributeFilterOrder(TrackedEntityInstanceQueryParams params, String attributeId) {
        if (params.hasFilters()) {
            for (QueryItem item : params.getFilters()) {
                if (attributeId.equals(item.getItemId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkMaxTeiCountReached(TrackedEntityInstanceQueryParams params, SqlRowSet rowSet) {
        if (params.getMaxTeiLimit() > 0 && rowSet.last()) {
            if (rowSet.getRow() > params.getMaxTeiLimit()) {
                throw new IllegalQueryException("maxteicountreached");
            }
            rowSet.beforeFirst();
        }
    }

    private void extractFromTeavAggregatedString(HashMap<String, String> attributeValues, String teavString) {
        if (teavString != null) {
            String[] pairs = teavString.split(UID_VALUE_PAIR_SEPARATOR);

            for (String pair : pairs) {
                String[] teav = pair.split(UID_VALUE_SEPARATOR, 2);

                if (teav.length == 2) {
                    attributeValues.put(teav[0], teav[1]);
                }
            }
        }
    }

    @Override
    public void updateTrackedEntityInstancesSyncTimestamp(List<String> trackedEntityInstanceUIDs, Instant lastSynchronized) {
        final String hql = "update TrackedEntityInstance set lastSynchronized = :lastSynchronized WHERE uid in :trackedEntityInstances";

        getQuery(hql)
            .setParameter("lastSynchronized", lastSynchronized)
            .setParameter("trackedEntityInstances", trackedEntityInstanceUIDs)
            .executeUpdate();
    }

    @Override
    public void updateTrackedEntityInstancesLastUpdated(Set<String> trackedEntityInstanceUIDs, Instant lastUpdated) {
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(trackedEntityInstanceUIDs), 20000);

        uidsPartitions.stream().filter(teis -> !teis.isEmpty())
            .forEach(
                teis -> getSession().getNamedQuery("updateTeisLastUpdated")
                    .setParameter("trackedEntityInstances", teis)
                    .setParameter("lastUpdated", lastUpdated)
                    .executeUpdate());
    }

    @Override
    public List<TrackedEntityInstance> getTrackedEntityInstancesByUid(List<String> uids, User user) {
        List<List<String>> uidPartitions = Lists.partition(uids, 20000);

        List<TrackedEntityInstance> instances = new ArrayList<>();

        for (List<String> partition : uidPartitions) {
            instances.addAll(getList(getCriteriaBuilder(),
                newJpaParameters().addPredicate(root -> root.get("uid").in(partition))));
        }

        return instances;
    }

    @Override
    public List<EventContext.TrackedEntityOuInfo> getTrackedEntityOuInfoByUid(List<String> uids, User user) {
        List<List<String>> uidPartitions = Lists.partition(uids, 20000);

        List<EventContext.TrackedEntityOuInfo> instances = new ArrayList<>();

        String hql = "select tei.id, tei.uid, tei.organisationUnit.id " + "from TrackedEntityInstance tei where tei.uid in (:uids)";

        for (List<String> partition : uidPartitions) {
            List<Object[]> resultList = getSession()
                .createQuery(hql, Object[].class)
                .setParameter("uids", partition)
                .setCacheable(cacheable)
                .setHint(QueryHints.CACHEABLE, cacheable)
                .getResultList();

            instances.addAll(resultList.stream().map(this::toTrackedEntityOuInfo).collect(Collectors.toList()));
        }

        return instances;
    }

    @Override
    public boolean exists(String uid) {
        Query<?> query = getSession()
            .createNativeQuery("select count(*) from tracked_entity_instance where uid=:uid and deleted is false");
        query.setParameter("uid", uid);
        int count = ((Number) query.getSingleResult()).intValue();

        return count > 0;
    }

    @Override
    public boolean existsIncludingDeleted(String uid) {
        Query<?> query = getSession().createNativeQuery("select count(*) from tracked_entity_instance where uid=:uid");
        query.setParameter("uid", uid);
        int count = ((Number) query.getSingleResult()).intValue();

        return count > 0;
    }

    @Override
    public List<String> getUidsIncludingDeleted(List<String> uids) {
        String hql = "select tei.uid " + TEI_HQL_BY_UIDS;
        List<String> resultUids = new ArrayList<>();
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(uids), 20000);

        for (List<String> uidsPartition : uidsPartitions) {
            if (!uidsPartition.isEmpty()) {
                resultUids.addAll(
                    getSession().createQuery(hql, String.class).setParameter("uids", uidsPartition).list());
            }
        }

        return resultUids;
    }

    @Override
    public List<TrackedEntityInstance> getIncludingDeleted(List<String> uids) {
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        List<List<String>> uidsPartitions = Lists.partition(Lists.newArrayList(uids), 20000);

        for (List<String> uidsPartition : uidsPartitions) {
            if (!uidsPartition.isEmpty()) {
                trackedEntityInstances.addAll(getSession().createQuery(TEI_HQL_BY_UIDS, TrackedEntityInstance.class)
                    .setParameter("uids", uidsPartition).list());
            }
        }

        return trackedEntityInstances;
    }

    private EventContext.TrackedEntityOuInfo toTrackedEntityOuInfo(Object[] objects) {
        return new EventContext.TrackedEntityOuInfo((Long) objects[0], (String) objects[1], (Long) objects[2]);
    }

    private boolean isOrgUnit(QueryItem item) {
        return item.getValueType().isOrganisationUnit();
    }

    private String getOrgUnitNameByUid(String uid) {
        if (uid != null) {
            return Optional.ofNullable(organisationUnitRepositoryExt.getByUid(uid)).orElseGet(() -> new OrganisationUnit("")).getName();
        }

        return StringUtils.EMPTY;
    }

    @Override
    protected TrackedEntityInstance postProcessObject(TrackedEntityInstance trackedEntityInstance) {
        return (trackedEntityInstance == null || trackedEntityInstance.isDeleted()) ? null : trackedEntityInstance;
    }

    @Override
    protected void preProcessPredicates(CriteriaBuilder builder,
                                        List<Function<Root<TrackedEntityInstance>, Predicate>> predicates) {
        predicates.add(root -> builder.equal(root.get("deleted"), false));
    }
}
