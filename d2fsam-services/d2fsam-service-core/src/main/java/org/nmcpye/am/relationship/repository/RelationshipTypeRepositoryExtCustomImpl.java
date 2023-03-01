package org.nmcpye.am.relationship.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.relationship.RelationshipTypeRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository("org.nmcpye.am.relationship.RelationshipTypeRepositoryExt")
public class RelationshipTypeRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<RelationshipType>
    implements RelationshipTypeRepositoryExt {
    public RelationshipTypeRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                   ApplicationEventPublisher publisher,
                                                   CurrentUserService currentUserService,
                                                   AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, RelationshipType.class,
            currentUserService, aclService, true);
    }

    @Override
    public RelationshipType getRelationshipType(String aIsToB, String bIsToA) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("aIsToB"), aIsToB))
            .addPredicate(root -> builder.equal(root.get("bIsToA"), bIsToA)));
    }
}
