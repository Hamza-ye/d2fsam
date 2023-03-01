package org.nmcpye.am.jdbc.config;

import com.google.common.collect.Lists;
import org.hisp.quick.StatementDialect;
import org.hisp.quick.StatementInterceptor;
import org.hisp.quick.configuration.JdbcConfigurationFactoryBean;
import org.hisp.quick.factory.DefaultBatchHandlerFactory;
import org.hisp.quick.statement.JdbcStatementManager;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.external.conf.ConfigurationKey;
import org.nmcpye.am.jdbc.dialect.StatementDialectFactoryBean;
import org.nmcpye.am.jdbc.statementbuilder.StatementBuilderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private AmConfigurationProvider amConfigurationProvider;

    @Bean
    public JdbcStatementManager statementManager()
        throws Exception {
        JdbcStatementManager jdbcStatementManager = new JdbcStatementManager();
        jdbcStatementManager.setJdbcConfiguration(jdbcConfiguration().getObject());
        return jdbcStatementManager;
    }

    @Bean(initMethod = "init")
    public StatementDialectFactoryBean statementDialect() {
        return new StatementDialectFactoryBean(
            amConfigurationProvider.getProperty(ConfigurationKey.CONNECTION_DIALECT));
    }

    @Bean(initMethod = "init")
    public JdbcConfigurationFactoryBean jdbcConfiguration() {
        JdbcConfigurationFactoryBean jdbcConf = new JdbcConfigurationFactoryBean();
        StatementDialect statementDialect = statementDialect().getObject();
        jdbcConf.setDialect(statementDialect);
        jdbcConf.setDataSource(dataSource);

        return jdbcConf;
    }

    @Bean(initMethod = "init")
    public StatementBuilderFactoryBean statementBuilder() {
        return new StatementBuilderFactoryBean(statementDialect().getObject());
    }

    @Bean
    public DefaultBatchHandlerFactory batchHandlerFactory()
        throws Exception {
        DefaultBatchHandlerFactory defaultBatchHandlerFactory = new DefaultBatchHandlerFactory();
        defaultBatchHandlerFactory.setJdbcConfiguration(jdbcConfiguration().getObject());
        return defaultBatchHandlerFactory;
    }

    @Bean
    public StatementInterceptor statementInterceptor()
        throws Exception {
        StatementInterceptor statementInterceptor = new StatementInterceptor();
        statementInterceptor.setStatementManagers(Lists.newArrayList(statementManager()));
        return statementInterceptor;
    }
}
