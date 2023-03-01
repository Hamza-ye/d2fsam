package org.nmcpye.am.trackedentity.repository;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.QueryFilter;
import org.nmcpye.am.common.QueryItem;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.commons.util.SqlHelper;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramTrackedEntityAttribute;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Repository("org.nmcpye.am.trackedentity.TrackedEntityAttributeRepositoryExt")
public class TrackedEntityAttributeRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<TrackedEntityAttribute>
    implements TrackedEntityAttributeRepositoryExt {

    private final StatementBuilder statementBuilder;

    public TrackedEntityAttributeRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                         ApplicationEventPublisher publisher,
                                                         CurrentUserService currentUserService,
                                                         AclService aclService,
                                                         StatementBuilder statementBuilder) {
        super(sessionFactory, jdbcTemplate, publisher,
            TrackedEntityAttribute.class, currentUserService, aclService, true);
        checkNotNull(statementBuilder);
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public List<TrackedEntityAttribute> getByDisplayOnVisitSchedule(boolean displayOnVisitSchedule) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("displayOnVisitSchedule"), displayOnVisitSchedule)));
    }

    @Override
    public List<TrackedEntityAttribute> getDisplayInListNoProgram() {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("displayInListNoProgram"), true)));
    }

    @Override
    public Optional<String> getTrackedEntityInstanceUidWithUniqueAttributeValue(
        TrackedEntityInstanceQueryParams params) {
        // ---------------------------------------------------------------------
        // Select clause
        // ---------------------------------------------------------------------

        SqlHelper hlp = new SqlHelper(true);

        String hql = "select tei.uid from TrackedEntityInstance tei ";

        if (params.hasOrganisationUnits()) {
            String orgUnitUids = params.getOrganisationUnits().stream()
                .map(OrganisationUnit::getUid)
                .collect(Collectors.joining(", ", "'", "'"));

            hql += "inner join tei.organisationUnit as ou ";
            hql += hlp.whereAnd() + " ou.uid in (" + orgUnitUids + ") ";
        }

        for (QueryItem item : params.getAttributes()) {
            for (QueryFilter filter : item.getFilters()) {
                final String encodedFilter = filter
                    .getSqlFilter(statementBuilder.encode(StringUtils.lowerCase(filter.getFilter()), false));

                hql += hlp.whereAnd() + " exists (from TrackedEntityAttributeValue teav where teav.entityInstance=tei";
                hql += " and teav.attribute.uid='" + item.getItemId() + "'";

                if (item.isNumeric()) {
                    hql += " and teav.plainValue " + filter.getSqlOperator() + encodedFilter + ")";
                } else {
                    hql += " and lower(teav.plainValue) " + filter.getSqlOperator() + encodedFilter + ")";
                }
            }
        }

        if (!params.isIncludeDeleted()) {
            hql += hlp.whereAnd() + " tei.deleted is false";
        }

        Query<String> query = getTypedQuery(hql);

        Iterator<String> it = query.iterate();

        if (it.hasNext()) {
            return Optional.of(it.next());
        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<TrackedEntityAttribute> getTrackedEntityAttributesByTrackedEntityTypes() {
        Query query = getSession()
            .createQuery("select trackedEntityTypeAttributes from TrackedEntityType");

        Set<TrackedEntityTypeAttribute> trackedEntityTypeAttributes = new HashSet<>(query.list());

        return trackedEntityTypeAttributes.stream()
            .map(TrackedEntityTypeAttribute::getTrackedEntityAttribute)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<TrackedEntityAttribute> getAllSearchableAndUniqueTrackedEntityAttributes() {
        Set<TrackedEntityAttribute> result = new HashSet<>();

        Query programTeaQuery = getSession()
            .createQuery(
                "select attribute from ProgramTrackedEntityAttribute ptea where ptea.searchable=true and ptea.attribute.valueType in ('TEXT','LONG_TEXT','PHONE_NUMBER','EMAIL','USERNAME','URL')");
        Query tetypeAttributeQuery = getSession()
            .createQuery(
                "select trackedEntityAttribute from TrackedEntityTypeAttribute teta where teta.searchable=true and teta.trackedEntityAttribute.valueType in ('TEXT','LONG_TEXT','PHONE_NUMBER','EMAIL','USERNAME','URL')");
        Query uniqueAttributeQuery = getSession()
            .createQuery("from TrackedEntityAttribute tea where tea.uniqueAttribute=true");

        List<TrackedEntityAttribute> programSearchableTrackedEntityAttributes = programTeaQuery.list();
        List<TrackedEntityAttribute> trackedEntityTypeSearchableAttributes = tetypeAttributeQuery.list();
        List<TrackedEntityAttribute> uniqueAttributes = uniqueAttributeQuery.list();

        result.addAll(programSearchableTrackedEntityAttributes);
        result.addAll(trackedEntityTypeSearchableAttributes);
        result.addAll(uniqueAttributes);

        return result;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<Program, Set<TrackedEntityAttribute>> getTrackedEntityAttributesByProgram() {
        Map<Program, Set<TrackedEntityAttribute>> result = new HashMap<>();

        Query query = getSession().createQuery("select p.programAttributes from Program p");

        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = query.list();

        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
            if (!result.containsKey(programTrackedEntityAttribute.getProgram())) {
                result.put(programTrackedEntityAttribute.getProgram(),
                    Sets.newHashSet(programTrackedEntityAttribute.getAttribute()));
            } else {
                result.get(programTrackedEntityAttribute.getProgram())
                    .add(programTrackedEntityAttribute.getAttribute());
            }
        }
        return result;
    }
}
