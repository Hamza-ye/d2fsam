package org.nmcpye.am.relationship.repository;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.hibernate.SoftDeleteHibernateObjectStore;
import org.nmcpye.am.hibernate.JpaQueryParameters;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.relationship.Relationship;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.relationship.RelationshipRepositoryExt;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
import org.nmcpye.am.webapi.controller.event.webrequest.PagingAndSortingCriteriaAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("org.nmcpye.am.relationship.RelationshipRepositoryExt")
public class RelationshipRepositoryExtCustomImpl
    extends SoftDeleteHibernateObjectStore<Relationship>
    implements RelationshipRepositoryExt {
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

    private static final String PROGRAM_INSTANCE = "programInstance";

    private static final String PROGRAM_STAGE_INSTANCE = "programStageInstance";

    public RelationshipRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                               ApplicationEventPublisher publisher,
                                               CurrentUserService currentUserService,
                                               AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Relationship.class, currentUserService,
            aclService, true);
    }

    @Override
    public List<Relationship> getByTrackedEntityInstance(TrackedEntityInstance tei,
                                                         PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter) {
        TypedQuery<Relationship> relationshipTypedQuery = getRelationshipTypedQuery(tei,
            pagingAndSortingCriteriaAdapter);

        return getList(relationshipTypedQuery);
    }

    @Override
    public List<Relationship> getByProgramInstance(ProgramInstance pi,
                                                   PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter) {
        TypedQuery<Relationship> relationshipTypedQuery = getRelationshipTypedQuery(pi,
            pagingAndSortingCriteriaAdapter);

        return getList(relationshipTypedQuery);
    }

    @Override
    public List<Relationship> getByProgramStageInstance(ProgramStageInstance psi,
                                                        PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter) {
        TypedQuery<Relationship> relationshipTypedQuery = getRelationshipTypedQuery(psi,
            pagingAndSortingCriteriaAdapter);

        return getList(relationshipTypedQuery);
    }

    private <T extends IdentifiableObject> TypedQuery<Relationship> getRelationshipTypedQuery(T entity,
                                                                                              PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter) {
        CriteriaBuilder builder = getCriteriaBuilder();

        CriteriaQuery<Relationship> relationshipItemCriteriaQuery = builder.createQuery(Relationship.class);
        Root<Relationship> root = relationshipItemCriteriaQuery.from(Relationship.class);

        setRelationshipItemCriteriaQueryExistsCondition(entity, builder, relationshipItemCriteriaQuery, root);

        return getRelationshipTypedQuery(pagingAndSortingCriteriaAdapter, builder, relationshipItemCriteriaQuery,
            root);
    }

    private <T extends IdentifiableObject> void setRelationshipItemCriteriaQueryExistsCondition(T entity,
                                                                                                CriteriaBuilder builder, CriteriaQuery<Relationship> relationshipItemCriteriaQuery, Root<Relationship> root) {
        Subquery<RelationshipItem> fromSubQuery = relationshipItemCriteriaQuery.subquery(RelationshipItem.class);
        Root<RelationshipItem> fromRoot = fromSubQuery.from(RelationshipItem.class);

        String relationshipEntityType = getRelationshipEntityType(entity);

        fromSubQuery.where(builder.equal(root.get("from"), fromRoot.get("id")),
            builder.equal(fromRoot.get(relationshipEntityType),
                entity.getId()));

        fromSubQuery.select(fromRoot.get("id"));

        Subquery<RelationshipItem> toSubQuery = relationshipItemCriteriaQuery.subquery(RelationshipItem.class);
        Root<RelationshipItem> toRoot = toSubQuery.from(RelationshipItem.class);

        toSubQuery.where(builder.equal(root.get("to"), toRoot.get("id")),
            builder.equal(toRoot.get(relationshipEntityType),
                entity.getId()));

        toSubQuery.select(toRoot.get("id"));

        relationshipItemCriteriaQuery
            .where(builder.or(builder.exists(fromSubQuery), builder.exists(toSubQuery)));

        relationshipItemCriteriaQuery.select(root);
    }

    private <T extends IdentifiableObject> String getRelationshipEntityType(T entity) {
        if (entity instanceof TrackedEntityInstance)
            return TRACKED_ENTITY_INSTANCE;
        else if (entity instanceof ProgramInstance)
            return PROGRAM_INSTANCE;
        else if (entity instanceof ProgramStageInstance)
            return PROGRAM_STAGE_INSTANCE;
        else
            throw new IllegalArgumentException(entity.getClass()
                .getSimpleName() + " not supported in relationship");
    }

    private TypedQuery<Relationship> getRelationshipTypedQuery(
        PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter, CriteriaBuilder builder,
        CriteriaQuery<Relationship> relationshipItemCriteriaQuery, Root<Relationship> root) {
        JpaQueryParameters<Relationship> jpaQueryParameters = newJpaParameters(pagingAndSortingCriteriaAdapter,
            builder);

        relationshipItemCriteriaQuery.orderBy(jpaQueryParameters.getOrders()
            .stream()
            .map(o -> o.apply(root))
            .collect(Collectors.toList()));

        TypedQuery<Relationship> relationshipTypedQuery = getSession().createQuery(relationshipItemCriteriaQuery);

        if (jpaQueryParameters.hasFirstResult()) {
            relationshipTypedQuery.setFirstResult(jpaQueryParameters.getFirstResult());
        }

        if (jpaQueryParameters.hasMaxResult()) {
            relationshipTypedQuery.setMaxResults(jpaQueryParameters.getMaxResults());
        }

        return relationshipTypedQuery;
    }

    @Override
    public List<Relationship> getByRelationshipType(RelationshipType relationshipType) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.join("relationshipType"), relationshipType)));

    }

    @Override
    public boolean existsIncludingDeleted(String uid) {
        Query<String> query = getSession().createNativeQuery("select uid from relationship where uid=:uid limit 1;");
        query.setParameter("uid", uid);

        return !query.list().isEmpty();
    }

    private JpaQueryParameters<Relationship> newJpaParameters(
        PagingAndSortingCriteriaAdapter pagingAndSortingCriteriaAdapter, CriteriaBuilder criteriaBuilder) {

        JpaQueryParameters<Relationship> jpaQueryParameters = newJpaParameters();

        if (Objects.nonNull(pagingAndSortingCriteriaAdapter)) {
            if (pagingAndSortingCriteriaAdapter.isSortingRequest()) {
                pagingAndSortingCriteriaAdapter.getOrder()
                    .forEach(orderCriteria -> addOrder(jpaQueryParameters, orderCriteria, criteriaBuilder));
            }

            if (pagingAndSortingCriteriaAdapter.isPagingRequest()) {
                jpaQueryParameters.setFirstResult(pagingAndSortingCriteriaAdapter.getFirstResult());
                jpaQueryParameters.setMaxResults(pagingAndSortingCriteriaAdapter.getPageSize());
            }
        }

        return jpaQueryParameters;
    }

    private void addOrder(JpaQueryParameters<Relationship> jpaQueryParameters, OrderCriteria orderCriteria,
                          CriteriaBuilder builder) {
        jpaQueryParameters.addOrder(relationshipRoot -> orderCriteria.getDirection()
            .isAscending() ? builder.asc(relationshipRoot.get(orderCriteria.getField()))
            : builder.desc(relationshipRoot.get(orderCriteria.getField())));
    }

    @Override
    public Relationship getByRelationship(Relationship relationship) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Relationship> criteriaQuery = builder.createQuery(Relationship.class);

        Root<Relationship> root = criteriaQuery.from(Relationship.class);

        criteriaQuery.where(builder.and(getFromOrToPredicate("from", builder, root, relationship),
            getFromOrToPredicate("to", builder, root, relationship),
            builder.equal(root.join("relationshipType"), relationship.getRelationshipType())));

        try {
            return getSession().createQuery(criteriaQuery)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public List<String> getUidsByRelationshipKeys(List<String> relationshipKeyList) {
        if (CollectionUtils.isEmpty(relationshipKeyList)) {
            return Collections.emptyList();
        }

        List<Object> c = getSession().createNativeQuery(new StringBuilder().append("SELECT R.uid ")
            .append("FROM relationship R ")
            .append("INNER JOIN relationship_type RT ON RT.relationshiptypeid = R.relationshiptypeid ")
            .append("WHERE R.deleted = false AND (R.key IN (:keys) ")
            .append("OR (R.invertedkey IN (:keys) AND RT.bidirectional = TRUE))")
            .toString())
            .setParameter("keys", relationshipKeyList)
            .getResultList();

        return c.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());

    }

    @Override
    public List<Relationship> getByUidsIncludeDeleted(List<String> uids) {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder();

        CriteriaQuery<Relationship> query = criteriaBuilder.createQuery(Relationship.class);

        Root<Relationship> root = query.from(Relationship.class);

        query.where(criteriaBuilder.in(root.get("uid"))
            .value(uids));

        try {
            return getSession().createQuery(query)
                .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    protected void preProcessPredicates(CriteriaBuilder builder,
                                        List<Function<Root<Relationship>, Predicate>> predicates) {
        predicates.add(root -> builder.equal(root.get("deleted"), false));
    }

    @Override
    protected Relationship postProcessObject(Relationship relationship) {
        return (relationship == null || relationship.isDeleted()) ? null : relationship;
    }

    private Predicate bidirectionalCriteria(CriteriaBuilder criteriaBuilder, Root<Relationship> root,
                                            Pair<String, String> fromFieldValuePair, Pair<String, String> toFieldValuePair) {
        return criteriaBuilder.and(criteriaBuilder.equal(root.join("relationshipType")
                .get("bidirectional"), true),
            criteriaBuilder.or(
                criteriaBuilder.and(getRelatedEntityCriteria(criteriaBuilder, root, fromFieldValuePair, "from"),
                    getRelatedEntityCriteria(criteriaBuilder, root, toFieldValuePair, "to")),
                criteriaBuilder.and(getRelatedEntityCriteria(criteriaBuilder, root, fromFieldValuePair, "to"),
                    getRelatedEntityCriteria(criteriaBuilder, root, toFieldValuePair, "from"))));
    }

    private Predicate getRelatedEntityCriteria(CriteriaBuilder criteriaBuilder, Root<Relationship> root,
                                               Pair<String, String> fromFieldValuePair, String from) {
        return criteriaBuilder.equal(root.join(from)
            .join(fromFieldValuePair.getKey())
            .get("uid"), fromFieldValuePair.getValue());
    }

    private Predicate nonBidirectionalCriteria(CriteriaBuilder criteriaBuilder, Root<Relationship> root,
                                               Pair<String, String> fromFieldValuePair, Pair<String, String> toFieldValuePair) {
        return criteriaBuilder.and(criteriaBuilder.equal(root.join("relationshipType")
                .get("bidirectional"), false),
            criteriaBuilder.and(getRelatedEntityCriteria(criteriaBuilder, root, fromFieldValuePair, "from"),
                getRelatedEntityCriteria(criteriaBuilder, root, toFieldValuePair, "to")));
    }

    private Predicate getFromOrToPredicate(String direction, CriteriaBuilder builder, Root<Relationship> root,
                                           Relationship relationship) {

        RelationshipItem relationshipItemDirection = getItem(direction, relationship);

        if (relationshipItemDirection.getTrackedEntityInstance() != null) {
            return builder.equal(root.join(direction)
                .get(TRACKED_ENTITY_INSTANCE), getItem(direction, relationship).getTrackedEntityInstance());
        } else if (relationshipItemDirection.getProgramInstance() != null) {
            return builder.equal(root.join(direction)
                .get(PROGRAM_INSTANCE), getItem(direction, relationship).getProgramInstance());
        } else if (relationshipItemDirection.getProgramStageInstance() != null) {
            return builder.equal(root.join(direction)
                .get(PROGRAM_STAGE_INSTANCE), getItem(direction, relationship).getProgramStageInstance());
        } else {
            return null;
        }
    }

    private RelationshipItem getItem(String direction, Relationship relationship) {
        return (direction.equalsIgnoreCase("from") ? relationship.getFrom() : relationship.getTo());
    }
}
