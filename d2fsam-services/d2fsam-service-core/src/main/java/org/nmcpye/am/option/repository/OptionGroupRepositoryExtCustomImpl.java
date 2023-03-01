package org.nmcpye.am.option.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.DataDimensionType;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.option.OptionGroup;
import org.nmcpye.am.option.OptionGroupRepositoryExt;
import org.nmcpye.am.option.OptionGroupSet;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository("org.nmcpye.am.option.OptionGroupRepositoryExt")
public class OptionGroupRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<OptionGroup>
    implements OptionGroupRepositoryExt {
    public OptionGroupRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                              ApplicationEventPublisher publisher,
                                              CurrentUserService currentUserService, AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, OptionGroup.class, currentUserService, aclService, true);
    }

    @Override
    public List<OptionGroup> getOptionGroups(OptionGroupSet groupSet) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicates(getSharingPredicates(builder))
            .addPredicate(root -> builder.equal(root.get("groupSet"), groupSet)));
    }

    @Override
    public List<OptionGroup> getOptionGroupsByOptionId(String optionId) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicates(getSharingPredicates(builder))
            .addPredicate(root -> builder.equal(root.join("members").get("uid"), optionId)));
    }

    @Override
    public List<OptionGroup> getOptionGroupsNoAcl(DataDimensionType dataDimensionType, boolean dataDimension) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("dataDimensionType"), dataDimension))
            .addPredicate(root -> builder.equal(root.get("dataDimension"), dataDimension)));
    }
}
