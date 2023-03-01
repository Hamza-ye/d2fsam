package org.nmcpye.am.trackedentityattributevalue.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAudit;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditQueryParams;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditRepositoryExt;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository("org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditRepositoryExt")
public class TrackedEntityAttributeValueAuditRepositoryExtCustomImpl
    implements TrackedEntityAttributeValueAuditRepositoryExt {

    private SessionFactory sessionFactory;

    public TrackedEntityAttributeValueAuditRepositoryExtCustomImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addTrackedEntityAttributeValueAudit(TrackedEntityAttributeValueAudit trackedEntityAttributeValueAudit) {
        Session session = sessionFactory.getCurrentSession();
        session.save(trackedEntityAttributeValueAudit);
    }

    @Override
    public List<TrackedEntityAttributeValueAudit> getTrackedEntityAttributeValueAudits(
        TrackedEntityAttributeValueAuditQueryParams params) {
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<TrackedEntityAttributeValueAudit> criteria = builder
            .createQuery(TrackedEntityAttributeValueAudit.class);

        Root<TrackedEntityAttributeValueAudit> root = criteria.from(TrackedEntityAttributeValueAudit.class);

        List<Predicate> predicates = getTrackedEntityAttributeValueAuditCriteria(root, params);

        criteria.where(predicates.toArray(new Predicate[0]))
            .orderBy(builder.desc(root.get("created")));

        Query<TrackedEntityAttributeValueAudit> query = sessionFactory.getCurrentSession()
            .createQuery(criteria);

        if (params.hasPager()) {
            query
                .setFirstResult(params.getPager().getOffset())
                .setMaxResults(params.getPager().getPageSize());
        }

        return query.getResultList();
    }

    @Override
    public int countTrackedEntityAttributeValueAudits(TrackedEntityAttributeValueAuditQueryParams params) {
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<TrackedEntityAttributeValueAudit> root = query.from(TrackedEntityAttributeValueAudit.class);

        List<Predicate> predicates = getTrackedEntityAttributeValueAuditCriteria(root, params);

        query.select(builder.countDistinct(root.get("id")))
            .where(predicates.toArray(new Predicate[0]));

        return (sessionFactory.getCurrentSession()
            .createQuery(query)
            .uniqueResult()).intValue();
    }

    @Override
    public void deleteTrackedEntityAttributeValueAudits(TrackedEntityInstance entityInstance) {
        Session session = sessionFactory.getCurrentSession();
        Query<?> query = session.createQuery(
            "delete TrackedEntityAttributeValueAudit where entityInstance = :entityInstance");
        query.setParameter("entityInstance", entityInstance);
        query.executeUpdate();
    }

    private List<Predicate> getTrackedEntityAttributeValueAuditCriteria(
        Root<TrackedEntityAttributeValueAudit> root, TrackedEntityAttributeValueAuditQueryParams params) {
        List<Predicate> predicates = new ArrayList<>();

        if (!params.getTrackedEntityAttributes().isEmpty()) {
            predicates.add(root.get("attribute").in(params.getTrackedEntityAttributes()));
        }

        if (!params.getTrackedEntityInstances().isEmpty()) {
            predicates.add(root.get("entityInstance").in(params.getTrackedEntityInstances()));
        }

        if (!params.getAuditTypes().isEmpty()) {
            predicates.add(root.get("auditType").in(params.getAuditTypes()));
        }

        return predicates;
    }
}
