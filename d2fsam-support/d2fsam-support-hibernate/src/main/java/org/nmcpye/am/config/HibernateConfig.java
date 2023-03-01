package org.nmcpye.am.config;

import org.hibernate.SessionFactory;
import org.nmcpye.am.cache.DefaultHibernateCacheManager;
import org.nmcpye.am.dbms.DbmsManager;
import org.nmcpye.am.dbms.HibernateDbmsManager;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.hibernate.DefaultHibernateConfigurationProvider;
import org.nmcpye.am.hibernate.HibernateConfigurationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Hamza on 09/11/2021.
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean("hibernateConfigurationProvider")
    public HibernateConfigurationProvider hibernateConfigurationProvider(AmConfigurationProvider amonfig) {
        DefaultHibernateConfigurationProvider hibernateConfigurationProvider = new DefaultHibernateConfigurationProvider();
        hibernateConfigurationProvider.setConfigProvider(amonfig);
        return hibernateConfigurationProvider;
    }

    @Bean
    @DependsOn("liquibase")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource,
                                                  @Qualifier("hibernateConfigurationProvider") HibernateConfigurationProvider hibernateConfigurationProvider) {
        Objects.requireNonNull(dataSource);
        Objects.requireNonNull(hibernateConfigurationProvider);

        Properties hibernateProperties = hibernateConfigurationProvider.getConfiguration().getProperties();
        Objects.requireNonNull(hibernateProperties);

        List<Resource> jarResources = hibernateConfigurationProvider.getJarResources();
        List<Resource> directoryResources = hibernateConfigurationProvider.getDirectoryResources();

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // NMCP /////////
        sessionFactory.setPackagesToScan("org.nmcpye.am");
        sessionFactory.setMappingJarLocations(jarResources.toArray(new Resource[0]));
        sessionFactory.setMappingDirectoryLocations(directoryResources.toArray(new Resource[0]));
//        sessionFactory.setAnnotatedClasses(DeletedObject.class);
        //////////////

        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager hibernateTransactionManager(DataSource dataSource,
                                                                   SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        transactionManager.setDataSource(dataSource);

        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(HibernateTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public DefaultHibernateCacheManager cacheManager(SessionFactory sessionFactory) {
        DefaultHibernateCacheManager cacheManager = new DefaultHibernateCacheManager();
        cacheManager.setSessionFactory(sessionFactory);
        return cacheManager;
    }

    @Bean
    public DbmsManager dbmsManager(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory,
                                   DefaultHibernateCacheManager cacheManager) {
        HibernateDbmsManager hibernateDbmsManager = new HibernateDbmsManager();
        hibernateDbmsManager.setCacheManager(cacheManager);
        hibernateDbmsManager.setSessionFactory(sessionFactory);
        hibernateDbmsManager.setJdbcTemplate(jdbcTemplate);
        return hibernateDbmsManager;
    }
}
