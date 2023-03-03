package org.nmcpye.am.setting.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.setting.SystemSetting;
import org.nmcpye.am.setting.SystemSettingRepositoryExt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository("org.nmcpye.am.setting.SystemSettingRepositoryExt")
public class SystemSettingRepositoryExtCustomImpl
    extends HibernateGenericStore<SystemSetting>
    implements SystemSettingRepositoryExt {

    public SystemSettingRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                ApplicationEventPublisher publisher) {
        super(sessionFactory, jdbcTemplate, publisher, SystemSetting.class, true);
    }

    @Override
    public SystemSetting getByName(String name) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("name"), name)));
    }
}
