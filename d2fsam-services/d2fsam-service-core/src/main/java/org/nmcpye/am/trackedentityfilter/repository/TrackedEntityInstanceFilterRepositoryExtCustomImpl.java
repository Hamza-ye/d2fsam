package org.nmcpye.am.trackedentityfilter.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilter;
import org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilterRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository("org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilterRepositoryExt")
public class TrackedEntityInstanceFilterRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<TrackedEntityInstanceFilter>
    implements TrackedEntityInstanceFilterRepositoryExt {
    public TrackedEntityInstanceFilterRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                              ApplicationEventPublisher publisher,
                                                              CurrentUserService currentUserService,
                                                              AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, TrackedEntityInstanceFilter.class, currentUserService,
            aclService, true);
    }

    @Override
    public List<TrackedEntityInstanceFilter> get(Program program) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("program"), program)));
    }
}
