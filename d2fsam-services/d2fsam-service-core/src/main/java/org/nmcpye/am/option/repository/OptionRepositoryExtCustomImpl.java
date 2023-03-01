package org.nmcpye.am.option.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.option.Option;
import org.nmcpye.am.option.OptionRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("org.nmcpye.am.option.OptionRepositoryExt")
public class OptionRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<Option>
    implements OptionRepositoryExt {

    public OptionRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                         ApplicationEventPublisher publisher,
                                         CurrentUserService currentUserService,
                                         AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, Option.class, currentUserService, aclService, true);
    }

    @Override
    public List<Option> getOptions(Long optionSetId, String key, Integer max) {
        String hql = "select option from OptionSet as optionset " +
            "join optionset.options as option where optionset.id = :optionSetId ";

        if (key != null) {
            hql += "and lower(option.name) like lower('%" + key + "%') ";
        }

        hql += "order by index(option)";

        Query<Option> query = getQuery(hql);
        query.setParameter("optionSetId", optionSetId);

        if (max != null) {
            query.setMaxResults(max);
        }

        return query.list();
    }
}
