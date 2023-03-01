package org.nmcpye.am.trackedentitydatavalue.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAudit;
import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditQueryParams;
import org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditRepositoryExt;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.nmcpye.am.common.OrganisationUnitSelectionMode.DESCENDANTS;
import static org.nmcpye.am.common.OrganisationUnitSelectionMode.SELECTED;

@Repository("org.nmcpye.am.trackedentitydatavalue.TrackedEntityDataValueAuditRepositoryExt")
public class TrackedEntityDataValueAuditRepositoryExtCustomImpl
    implements TrackedEntityDataValueAuditRepositoryExt {
    private static final String PROP_PSI = "programStageInstance";

    private static final String PROP_ORGANISATION_UNIT = "organisationUnit";

    private static final String PROP_CREATED = "created";

    private SessionFactory sessionFactory;

    public TrackedEntityDataValueAuditRepositoryExtCustomImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public void addTrackedEntityDataValueAudit(TrackedEntityDataValueAudit trackedEntityDataValueAudit) {
        Session session = sessionFactory.getCurrentSession();
        session.save(trackedEntityDataValueAudit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TrackedEntityDataValueAudit> getTrackedEntityDataValueAudits(
        TrackedEntityDataValueAuditQueryParams params) {
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TrackedEntityDataValueAudit> criteria = builder.createQuery(TrackedEntityDataValueAudit.class);
        Root<TrackedEntityDataValueAudit> tedva = criteria.from(TrackedEntityDataValueAudit.class);
        Join<TrackedEntityDataValueAudit, ProgramStageInstance> psi = tedva.join(PROP_PSI);
        Join<ProgramStageInstance, OrganisationUnit> ou = psi.join(PROP_ORGANISATION_UNIT);
        criteria.select(tedva);

        List<Predicate> predicates = getTrackedEntityDataValueAuditCriteria(params, builder, tedva, psi, ou);
        criteria.where(predicates.toArray(Predicate[]::new));
        criteria.orderBy(builder.desc(tedva.get(PROP_CREATED)));

        Query query = sessionFactory.getCurrentSession().createQuery(criteria);

        if (params.hasPaging()) {
            query
                .setFirstResult(params.getPager().getOffset())
                .setMaxResults(params.getPager().getPageSize());
        }

        return query.getResultList();
    }

    @Override
    public int countTrackedEntityDataValueAudits(TrackedEntityDataValueAuditQueryParams params) {
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<TrackedEntityDataValueAudit> tedva = criteria.from(TrackedEntityDataValueAudit.class);
        Join<TrackedEntityDataValueAudit, ProgramStageInstance> psi = tedva.join(PROP_PSI);
        Join<ProgramStageInstance, OrganisationUnit> ou = psi.join(PROP_ORGANISATION_UNIT);
        criteria.select(builder.countDistinct(tedva.get("id")));

        List<Predicate> predicates = getTrackedEntityDataValueAuditCriteria(params, builder, tedva, psi, ou);
        criteria.where(predicates.toArray(Predicate[]::new));

        return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult().intValue();
    }

    @Override
    public void deleteTrackedEntityDataValueAudit(DataElement dataElement) {
        String hql = "delete from TrackedEntityDataValueAudit d where d.dataElement = :de";

        sessionFactory.getCurrentSession().createQuery(hql).setParameter("de", dataElement).executeUpdate();
    }

    @Override
    public void deleteTrackedEntityDataValueAudit(ProgramStageInstance psi) {
        String hql = "delete from TrackedEntityDataValueAudit d where d.programStageInstance = :psi";

        sessionFactory.getCurrentSession().createQuery(hql).setParameter("psi", psi).executeUpdate();
    }

    private List<Predicate> getTrackedEntityDataValueAuditCriteria(TrackedEntityDataValueAuditQueryParams params,
                                                                   CriteriaBuilder builder,
                                                                   Root<TrackedEntityDataValueAudit> tedva,
                                                                   Join<TrackedEntityDataValueAudit, ProgramStageInstance> psi,
                                                                   Join<ProgramStageInstance, OrganisationUnit> ou) {
        List<Predicate> predicates = new ArrayList<>();

        if (!params.getDataElements().isEmpty()) {
            predicates.add(tedva.get("dataElement").in(params.getDataElements()));
        }

        if (!params.getOrgUnits().isEmpty()) {
            if (DESCENDANTS == params.getOuMode()) {
                List<Predicate> orgUnitPredicates = new ArrayList<>();

                for (OrganisationUnit orgUnit : params.getOrgUnits()) {
                    orgUnitPredicates.add(builder.like(ou.get("path"), (orgUnit.getPath() + "%")));
                }

                predicates.add(builder.or(orgUnitPredicates.toArray(Predicate[]::new)));
            } else if (SELECTED == params.getOuMode() || !params.hasOuMode()) {
                predicates.add(psi.get("organisationUnit").in(params.getOrgUnits()));
            }
        }

        if (!params.getProgramStageInstances().isEmpty()) {
            predicates.add(tedva.get(PROP_PSI).in(params.getProgramStageInstances()));
        }

        if (!params.getProgramStages().isEmpty()) {
            predicates.add(psi.get("programStage").in(params.getProgramStages()));
        }

        if (params.getStartDate() != null) {
            predicates.add(builder.greaterThanOrEqualTo(tedva.get(PROP_CREATED), params.getStartDate().toInstant()));
        }

        if (params.getEndDate() != null) {
            predicates.add(builder.lessThanOrEqualTo(tedva.get(PROP_CREATED), params.getEndDate().toInstant()));
        }

        if (!params.getAuditTypes().isEmpty()) {
            predicates.add(tedva.get("auditType").in(params.getAuditTypes()));
        }

        return predicates;
    }
}
