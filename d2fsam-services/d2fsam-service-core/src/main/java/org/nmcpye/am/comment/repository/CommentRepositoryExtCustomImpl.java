package org.nmcpye.am.comment.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.comment.CommentRepositoryExt;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.comment.CommentRepositoryExt")
public class CommentRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Comment> implements CommentRepositoryExt {

    public CommentRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                          ApplicationEventPublisher publisher,
                                          CurrentUserService currentUserService,
                                          AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Comment.class, currentUserService, aclService, false);
    }

    @Override
    public boolean exists(String uid) {
        return (boolean) getSession()
            .createNativeQuery("select exists(select 1 from comment where uid=:uid)")
            .setParameter("uid", uid)
            .getSingleResult();
    }
}
