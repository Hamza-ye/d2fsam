package org.nmcpye.am.dataelement.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementDomain;
import org.nmcpye.am.dataelement.DataElementRepositoryExt;
import org.nmcpye.am.hibernate.JpaQueryParameters;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository("org.nmcpye.am.dataelement.DataElementRepositoryExt")
public class DataElementRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<DataElement>
    implements DataElementRepositoryExt {

    private final StatementBuilder statementBuilder;

    public DataElementRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                              ApplicationEventPublisher publisher,
                                              CurrentUserService currentUserService,
                                              StatementBuilder statementBuilder,
                                              AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, DataElement.class, currentUserService, aclService, false);
        this.statementBuilder = statementBuilder;
    }

    @Override
    public List<DataElement> getDataElementsByValueType(ValueType valueType) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder,
            newJpaParameters().addPredicate(root -> builder.equal(root.get("valueType"), valueType)));
    }

    @Override
    public List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("zeroIsSignificant"), zeroIsSignificant))
            .addPredicate(root -> root.get("valueType").in(ValueType.NUMERIC_TYPES)));
    }

    @Override
    public List<DataElement> getDataElementsWithoutGroups() {
        String hql = "from DataElement d where size(d.groups) = 0";

        return getQuery(hql).setCacheable(true).list();
    }

    @Override
    public List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel) {
        String hql = "from DataElement de join de.aggregationLevels al where al = :aggregationLevel";

        return getQuery(hql).setParameter("aggregationLevel", aggregationLevel).list();
    }

    @Override
    public DataElement getDataElement(String uid, User user) {
        CriteriaBuilder builder = getCriteriaBuilder();

        JpaQueryParameters<DataElement> param = new JpaQueryParameters<DataElement>()
            .addPredicates(getSharingPredicates(builder, user, AclService.LIKE_READ_METADATA))
            .addPredicate(root -> builder.equal(root.get("uid"), uid));

        return getSingleResult(builder, param);
    }

    @Override
    public List<DataElement> getDataElementsByDomainType(DataElementDomain domainType) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder,
            newJpaParameters().addPredicate(root -> builder.equal(root.get("domainType"), domainType)));
    }
}
