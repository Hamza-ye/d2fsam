package org.nmcpye.am.option.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.option.OptionGroupSet;
import org.nmcpye.am.option.OptionGroupSetRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("org.nmcpye.am.option.OptionGroupSetRepositoryExt")
public class OptionGroupSetRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OptionGroupSet>
    implements OptionGroupSetRepositoryExt {
    public OptionGroupSetRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                 ApplicationEventPublisher publisher,
                                                 CurrentUserService currentUserService, AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OptionGroupSet.class, currentUserService, aclService, true);
    }
}
