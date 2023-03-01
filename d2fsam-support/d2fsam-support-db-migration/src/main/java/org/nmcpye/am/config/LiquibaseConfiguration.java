package org.nmcpye.am.config;

import liquibase.integration.spring.SpringLiquibase;
import org.nmcpye.am.commons.util.SystemUtils;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.external.conf.ConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.task.AsyncListenableTaskExecutor;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(LiquibaseConfiguration.class);

    private final static String LIQUIBASE_CHANGE_LOG_FILE = "classpath:config/liquibase/master.xml";

    private final static String DATABASE_CHANGE_LOG_TABLE = "database_change_log";

    private final static String DATABASE_CHANGE_LOG_LOCK_TABLE = "database_change_log_lock";

    private final Environment env;

    public LiquibaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean(value = "liquibase")
    @Profile("!test-h2")
    @DependsOn("dataSource")
    public SpringLiquibase liquibase(
        AmConfigurationProvider amConfig,
        @Qualifier("taskScheduler") AsyncListenableTaskExecutor executor,
        ObjectProvider<DataSource> dataSource) {

        SpringLiquibase liquibase;

        if (amConfig.isEnabled(ConfigurationKey.LIQUIBASE_ASYNC)) {
            liquibase = new AsyncSpringLiquibase(executor, env);
            liquibase.setDataSource(dataSource.getIfUnique());
        } else {
            liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource.getIfUnique());
        }

        if (isTestRun()) {
            liquibase.setContexts("test");
        }

        liquibase.setChangeLog(LIQUIBASE_CHANGE_LOG_FILE);
        liquibase.setDatabaseChangeLogLockTable(DATABASE_CHANGE_LOG_LOCK_TABLE);
        liquibase.setDatabaseChangeLogTable(DATABASE_CHANGE_LOG_TABLE);
        liquibase.setDropFirst(amConfig.isEnabled(ConfigurationKey.LIQUIBASE_DROP_FIRST));
        liquibase.setTestRollbackOnUpdate(amConfig.isEnabled(ConfigurationKey.LIQUIBASE_TEST_ROLLBACK_ON_UPDATE));
        if (env.acceptsProfiles(Profiles.of("no-liquibase", "test-h2"))) {
            liquibase.setShouldRun(false);
        } else {
            liquibase.setShouldRun(amConfig.isEnabled(ConfigurationKey.LIQUIBASE_ENABLED));
            log.debug("Configuring Liquibase");
        }
        return liquibase;
    }

    protected boolean isTestRun() {
        return SystemUtils.isTestRun(env.getActiveProfiles());
    }
}
