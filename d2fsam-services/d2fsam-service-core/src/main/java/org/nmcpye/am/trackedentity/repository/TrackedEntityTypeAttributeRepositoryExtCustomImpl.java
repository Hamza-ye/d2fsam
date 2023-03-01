package org.nmcpye.am.trackedentity.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityTypeAttributeRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository("org.nmcpye.am.trackedentity.TrackedEntityTypeAttributeRepositoryExt")
public class TrackedEntityTypeAttributeRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<TrackedEntityTypeAttribute>
    implements TrackedEntityTypeAttributeRepositoryExt {
    public TrackedEntityTypeAttributeRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                             ApplicationEventPublisher publisher,
                                                             CurrentUserService currentUserService,
                                                             AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityTypeAttribute.class,
            currentUserService, aclService, true);
    }

    @Override
    public List<TrackedEntityAttribute> getAttributes(List<TrackedEntityType> trackedEntityTypes) {
        CriteriaBuilder builder = getCriteriaBuilder();

        CriteriaQuery<TrackedEntityAttribute> query = builder.createQuery(TrackedEntityAttribute.class);
        Root<TrackedEntityTypeAttribute> root = query.from(TrackedEntityTypeAttribute.class);
        query.select(root.get("trackedEntityAttribute"));
        query.where(root.get("trackedEntityType").in(trackedEntityTypes));
        query.distinct(true);

        return getSession().createQuery(query).getResultList();
    }
}
