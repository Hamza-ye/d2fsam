package org.nmcpye.am.config;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.constant.Constant;
import org.nmcpye.am.hibernate.HibernateGenericStore;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.option.OptionSet;
import org.nmcpye.am.program.ProgramIndicatorGroup;
import org.nmcpye.am.scheduling.JobConfiguration;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Hamza on 14/10/2021.
 */
@Configuration("coreStoreConfig")
public class StoreConfig {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private AclService aclService;

//    @Autowired
//    public StoreConfig(
//        JdbcTemplate jdbcTemplate,
//        CurrentUserService currentUserService,
//        ApplicationEventPublisher publisher,
//        AclService aclService
//    ) {
//        this.jdbcTemplate = jdbcTemplate;
//        this.currentUserService = currentUserService;
//        this.aclService = aclService;
//        this.publisher = publisher;
//    }

    @Bean("org.nmcpye.am.configuration.ConfigurationStore")
    public HibernateGenericStore<org.nmcpye.am.configuration.Configuration> configurationStore() {
        return new HibernateGenericStore<>(sessionFactory, jdbcTemplate, publisher, org.nmcpye.am.configuration.Configuration.class, true);
    }

    @Bean("org.nmcpye.am.scheduling.JobConfigurationStore")
    public HibernateIdentifiableObjectStore<JobConfiguration> jobConfigurationStore() {
        return new HibernateIdentifiableObjectStore<>(sessionFactory, jdbcTemplate, publisher, JobConfiguration.class, currentUserService, aclService, true);
    }

    @Bean("org.nmcpye.am.option.OptionSetStore")
    public HibernateIdentifiableObjectStore<OptionSet> optionSetStore() {
        return new HibernateIdentifiableObjectStore<>(sessionFactory, jdbcTemplate, publisher, OptionSet.class, currentUserService, aclService, true);
    }

    @Bean("org.nmcpye.am.constant.ConstantStore")
    public HibernateIdentifiableObjectStore<Constant> constantStore() {
        return new HibernateIdentifiableObjectStore<>(sessionFactory, jdbcTemplate, publisher,
            Constant.class, currentUserService, aclService, true);
    }

    @Bean("org.nmcpye.am.legend.LegendSetStore")
    public HibernateIdentifiableObjectStore<LegendSet> legendSetStore() {
        return new HibernateIdentifiableObjectStore<>(sessionFactory, jdbcTemplate, publisher,
            LegendSet.class, currentUserService, aclService, true);
    }

    @Bean("org.nmcpye.am.program.ProgramIndicatorGroupStore")
    public HibernateIdentifiableObjectStore<ProgramIndicatorGroup> programIndicatorGroupStore() {
        return new HibernateIdentifiableObjectStore<>(sessionFactory, jdbcTemplate, publisher,
            ProgramIndicatorGroup.class, currentUserService, aclService, true);
    }
}
