package org.nmcpye.am.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.version.MetadataVersion;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

@Repository("org.nmcpye.am.repository.MetadataVersionRepositoryExt")
public class MetadataVersionRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<MetadataVersion>
    implements MetadataVersionRepositoryExt {

    public MetadataVersionRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                  ApplicationEventPublisher publisher,
                                                  CurrentUserService currentUserService,
                                                  AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, MetadataVersion.class, currentUserService, aclService, false);
    }

    @Override
    public MetadataVersion getVersionByKey(long key) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters().addPredicate(root -> builder.equal(root.get("id"), key)));
    }

    @Override
    public MetadataVersion getVersionByName(String versionName) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters().addPredicate(root -> builder.equal(root.get("name"), versionName)));
    }

    @Override
    public MetadataVersion getCurrentVersion() {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(
            builder,
            newJpaParameters().addOrder(root -> builder.desc(root.get("created"))).setMaxResults(1).setCacheable(false)
        );
    }

    @Override
    public List<MetadataVersion> getAllVersionsInBetween(Date startDate, Date endDate) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters().addPredicate(root -> builder.between(root.get("created"), startDate, endDate)));
    }

    @Override
    public MetadataVersion getInitialVersion() {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(
            builder,
            newJpaParameters().addOrder(root -> builder.asc(root.get("created"))).setMaxResults(1).setCacheable(false)
        );
    }
}
