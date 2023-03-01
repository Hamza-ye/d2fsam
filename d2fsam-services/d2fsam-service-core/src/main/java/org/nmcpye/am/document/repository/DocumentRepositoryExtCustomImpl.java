package org.nmcpye.am.document.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.adapter.BaseIdentifiableObject_;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.document.Document;
import org.nmcpye.am.document.DocumentRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository("org.nmcpye.am.document.DocumentRepositoryExt")
public class DocumentRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Document>
    implements DocumentRepositoryExt {

    public DocumentRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                           ApplicationEventPublisher publisher,
                                           CurrentUserService currentUserService,
                                           AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Document.class, currentUserService, aclService, true);
    }

    @Override
    public long getCountByUser(User user) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Document> root = query.from(Document.class);
        query.select(builder.count(root));
        query.where(builder.equal(root.get(BaseIdentifiableObject_.CREATED_BY), user));

        return getSession().createQuery(query).getSingleResult();
    }
}
