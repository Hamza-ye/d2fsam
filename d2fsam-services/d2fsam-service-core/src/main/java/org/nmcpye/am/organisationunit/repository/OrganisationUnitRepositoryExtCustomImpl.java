package org.nmcpye.am.organisationunit.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.IdentifiableObjectUtils;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.commons.util.SqlHelper;
import org.nmcpye.am.commons.util.TextUtils;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitHierarchy;
import org.nmcpye.am.organisationunit.OrganisationUnitQueryParams;
import org.nmcpye.am.organisationunit.OrganisationUnitRepositoryExt;
import org.nmcpye.am.organisationunit.objectmapper.OrganisationUnitRelationshipRowMapper;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.system.util.SqlUtils;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Repository
public class OrganisationUnitRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OrganisationUnit>
    implements OrganisationUnitRepositoryExt {

    public OrganisationUnitRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                   ApplicationEventPublisher publisher,
                                                   CurrentUserService currentUserService,
                                                   AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OrganisationUnit.class,
            currentUserService, aclService, true);
    }

    @Override
    public List<OrganisationUnit> getRootOrganisationUnits() {
        return getQuery("from OrganisationUnit o where o.parent is null").list();
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsWithoutGroups() {
        return getQuery("from OrganisationUnit o where size(o.groups) = 0").list();
    }

    @Override
    public Long getOrganisationUnitHierarchyMemberCount(OrganisationUnit parent, Object member, String collectionName) {
        final String hql =
            "select count(*) from OrganisationUnit o " + "where o.path like :path " + "and :object in elements(o." + collectionName + ")";

        Query<Long> query = getTypedQuery(hql);
        query.setParameter("path", parent.getPath() + "%").setParameter("object", member);

        return query.getSingleResult();
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnits(OrganisationUnitQueryParams params) {
        SqlHelper hlp = new SqlHelper();

        String hql = "select distinct o from OrganisationUnit o ";

        if (params.isFetchChildren()) {
            hql += "left join fetch o.children c ";
        }

        if (params.hasGroups()) {
            hql += "join o.groups og ";
        }

        if (params.hasQuery()) {
            hql += hlp.whereAnd() + " (lower(o.name) like :queryLower or o.code = :query or o.uid = :query) ";
        }

        if (params.hasParents()) {
            hql += hlp.whereAnd() + " (";

            for (OrganisationUnit parent : params.getParents()) {
                hql += "o.path like :" + parent.getUid() + " or ";
            }

            hql = TextUtils.removeLastOr(hql) + ") ";
        }

        if (params.hasGroups()) {
            hql += hlp.whereAnd() + " og.id in (:groupIds) ";
        }

        if (params.hasLevels()) {
            hql += hlp.whereAnd() + " o.hierarchyLevel in (:levels) ";
        }

        if (params.getMaxLevels() != null) {
            hql += hlp.whereAnd() + " o.hierarchyLevel <= :maxLevels ";
        }

        hql += "order by o." + params.getOrderBy().getName();

        Query<OrganisationUnit> query = getQuery(hql);

        if (params.hasQuery()) {
            query.setParameter("queryLower", "%" + params.getQuery().toLowerCase() + "%");
            query.setParameter("query", params.getQuery());
        }

        if (params.hasParents()) {
            for (OrganisationUnit parent : params.getParents()) {
                query.setParameter(parent.getUid(), parent.getPath() + "%");
            }
        }

        if (params.hasGroups()) {
            query.setParameterList("groupIds", IdentifiableObjectUtils.getIdentifiers(params.getGroups()));
        }

        if (params.hasLevels()) {
            query.setParameterList("levels", params.getLevels());
        }

        if (params.getMaxLevels() != null) {
            query.setParameter("maxLevels", params.getMaxLevels());
        }

        if (params.getFirst() != null) {
            query.setFirstResult(params.getFirst());
        }

        if (params.getMax() != null) {
            query.setMaxResults(params.getMax()).list();
        }

        return query.list();
    }

    @Override
    public List<OrganisationUnit> getWithinCoordinateArea(double[] box) {
        // can't use hibernate-spatial 'makeenvelope' function, because not available in
        // current hibernate version
        // see: https://hibernate.atlassian.net/browse/HHH-13083

        if (box != null && box.length == 4) {
            return getSession()
                .createQuery(
                    "from OrganisationUnit ou " + "where within(ou.geometry, " + doMakeEnvelopeSql(box) + ") = true",
                    OrganisationUnit.class
                )
                .getResultList();
        }
        return new ArrayList<>();
    }

    private String doMakeEnvelopeSql(double[] box) {
        // equivalent to: postgis 'ST_MakeEnvelope' (https://postgis.net/docs/ST_MakeEnvelope.html)
        return "ST_MakeEnvelope(" + box[1] + "," + box[0] + "," + box[3] + "," + box[2] + ", 4326)";
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    @Override
    public OrganisationUnitHierarchy getOrganisationUnitHierarchy() {
        final String sql = "select organisationunitid, parentid from organisation_unit";

        return new OrganisationUnitHierarchy(jdbcTemplate.query(sql, new OrganisationUnitRelationshipRowMapper()));
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsWithProgram(Program program) {
        final String jpql = "select distinct o from OrganisationUnit o " + "join o.programs p where p.id = :programId";

        return getQuery(jpql).setParameter("programId", program.getId()).list();
    }

    @Override
    public void updateOrganisationUnitParent(Long organisationUnitId, Long parentId) {
        Timestamp now = new Timestamp(new Date().getTime());

        final String sql =
            "update organisationunit " + "set parentid=" + parentId + ", updated='" + now + "' " + "where organisationunitid=" + organisationUnitId;

        jdbcTemplate.execute(sql);
    }

    @Override
    public void updatePaths() {
        List<OrganisationUnit> organisationUnits = new ArrayList<>(
            getQuery("from OrganisationUnit ou where ou.path is null or ou.hierarchyLevel is null").list()
        );
        updatePaths(organisationUnits);
    }

    @Override
    public void forceUpdatePaths() {
        List<OrganisationUnit> organisationUnits = new ArrayList<>(getQuery("from OrganisationUnit").list());
        updatePaths(organisationUnits);
    }

    @Override
    public int getMaxLevel() {
        String hql = "select max(ou.hierarchyLevel) from OrganisationUnit ou";

        Query<Integer> query = getTypedQuery(hql);
        Integer maxLength = query.getSingleResult();

        return maxLength != null ? maxLength : 0;
    }

    private void updatePaths(List<OrganisationUnit> organisationUnits) {
        Session session = getSession();
        int counter = 0;

        for (OrganisationUnit organisationUnit : organisationUnits) {
            // TODO Ext
//            organisationUnit.getPath();
//            organisationUnit.getHierarchyLevel();
            organisationUnit.setPath(organisationUnit.getPath());
            organisationUnit.setHierarchyLevel(organisationUnit.getHierarchyLevel());

            session.update(organisationUnit);

            if ((counter % 400) == 0) {
                session.flush();
            }

            counter++;
        }
    }

    @Override
    public List<OrganisationUnit> getOrphanedOrganisationUnits() {
        return getQuery(
            "from OrganisationUnit o where o.parent is null and not exists " + "(select 1 from OrganisationUnit io where io.parent = o.id)"
        )
            .list();
    }

    @Override
    public Set<OrganisationUnit> getOrganisationUnitsWithCyclicReferences() {
        return getQuery(
            "from OrganisationUnit o where exists (select 1 from OrganisationUnit i " +
                "where i.id <> o.id " +
                "and i.path like concat('%', o.uid, '%') " +
                "and o.path like concat('%', i.uid, '%'))"
        )
            .stream()
            .collect(toSet());
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets() {
        // OBS: size(o.groups) > 1 is just to narrow search right away
        return getQuery(
            "from OrganisationUnit o where size(o.groups) > 1 and exists " +
                "(select 1 from OrganisationUnitGroupSet s where " +
                "(select count(*) from OrganisationUnitGroup g where o in elements(g.members) and s in elements(g.groupSets)) > 1)"
        )
            .list();
    }

    @Override
    public List<String> getOrganisationUnitUids(OrganisationUnitQueryParams params) {
        String sql = buildOrganisationUnitDistinctUidsSql(params);
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private String buildOrganisationUnitDistinctUidsSql(OrganisationUnitQueryParams params) {
        SqlHelper hlp = new SqlHelper();

        String sql = "select distinct o.uid from organisation_unit o ";

        if (params.isFetchChildren()) {
            sql += " left outer join organisation_unit c ON o.organisationunitid = c.parentid ";
        }

        if (params.hasParents()) {
            sql += hlp.whereAnd() + " (";

            for (OrganisationUnit parent : params.getParents()) {
                sql += "o.path like '" + SqlUtils.escapeSql(parent.getPath()) + "%'" + " or ";
            }

            sql = TextUtils.removeLastOr(sql) + ") ";
        }

        // TODO: Support Groups + Query + Hierarchy + MaxLevels in this sql

        return sql;
    }

    @Override
    public boolean isOrgUnitCountAboveThreshold(OrganisationUnitQueryParams params, int threshold) {
        String sql = buildOrganisationUnitDistinctUidsSql(params);

        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from (");
        sb.append(sql);
        sb.append(" limit ");
        sb.append(threshold + 1);
        sb.append(") as douid");

        return (jdbcTemplate.queryForObject(sb.toString(), Integer.class) > threshold);
    }

    @Override
    public List<OrganisationUnit> getAllOrganisationUnitsByLastUpdated(Instant lastUpdated) {
        return getAllGeLastUpdated(lastUpdated);
    }
}
