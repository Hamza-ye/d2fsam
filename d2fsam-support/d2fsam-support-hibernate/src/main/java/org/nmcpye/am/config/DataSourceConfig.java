package org.nmcpye.am.config;

import com.google.common.base.MoreObjects;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.commons.util.DebugUtils;
import org.nmcpye.am.datasource.DatabasePoolUtils;
import org.nmcpye.am.datasource.DefaultReadOnlyDataSourceManager;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.external.conf.ConfigurationKey;
import org.nmcpye.am.hibernate.DefaultHibernateConfigurationProvider;
import org.nmcpye.am.hibernate.HibernateConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Spring DataSource auto configuration will back off when we create a customized
 * DataSource in this configuration
 *
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Slf4j
@Configuration
public class DataSourceConfig {
    @Autowired
    private AmConfigurationProvider amConfig;

    @Bean
    @DependsOn("dataSource")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("jdbcTemplate")
    @DependsOn("dataSource")
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(1000);
        return jdbcTemplate;
    }

    @Bean("executionPlanJdbcTemplate")
    @DependsOn("dataSource")
    public JdbcTemplate executionPlanJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(1000);
        jdbcTemplate.setQueryTimeout(10);
        return jdbcTemplate;
    }

    @Bean("readOnlyJdbcTemplate")
    @DependsOn("dataSource")
    public JdbcTemplate readOnlyJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        DefaultReadOnlyDataSourceManager manager = new DefaultReadOnlyDataSourceManager(amConfig);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            MoreObjects.firstNonNull(manager.getReadOnlyDataSource(), dataSource));
        jdbcTemplate.setFetchSize(1000);

        return jdbcTemplate;
    }

    @Bean("actualDataSource")
    public DataSource actualDataSource(HibernateConfigurationProvider hibernateConfigurationProvider) {
        String jdbcUrl = amConfig.getProperty(ConfigurationKey.CONNECTION_URL);
        String username = amConfig.getProperty(ConfigurationKey.CONNECTION_USERNAME);
        String dbPoolType = amConfig.getProperty(ConfigurationKey.DB_POOL_TYPE);

        DatabasePoolUtils.PoolConfig.PoolConfigBuilder builder = DatabasePoolUtils.PoolConfig.builder();
        builder.amConfig(amConfig);
        builder.hibernateConfig(hibernateConfigurationProvider);
        builder.dbPoolType(dbPoolType);

        try {
            return DatabasePoolUtils.createDbPool(builder.build());
        } catch (SQLException | PropertyVetoException e) {
            String message = String.format("Connection test failed for main database pool, " +
                "jdbcUrl: '%s', user: '%s'", jdbcUrl, username);

            log.error(message);
            log.error(DebugUtils.getStackTrace(e));

            throw new IllegalStateException(message, e);
        }
    }

    @Bean("dataSource")
    @DependsOn("actualDataSource")
    @Primary
    public DataSource dataSource(@Qualifier("actualDataSource") DataSource actualDataSource) {
        boolean enableQueryLogging = amConfig.isEnabled(
            ConfigurationKey.ENABLE_QUERY_LOGGING);

        if (!enableQueryLogging) {
            return actualDataSource;
        }

        PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);

        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        listener.setLogger("org.nmcpye.am.datasource.query");
        listener.setLogLevel(SLF4JLogLevel.INFO);
        listener.setQueryLogEntryCreator(creator);

        ProxyDataSourceBuilder b = ProxyDataSourceBuilder

            .create(actualDataSource)
            .name("ProxyDS_AM_" + amConfig.getProperty(
                ConfigurationKey.DB_POOL_TYPE) +
                "_" + CodeGenerator.generateCode(5))

            .logSlowQueryBySlf4j(
                Integer.parseInt(amConfig.getProperty(
                    ConfigurationKey.SLOW_QUERY_LOGGING_THRESHOLD_TIME_MS)),
                TimeUnit.MILLISECONDS, SLF4JLogLevel.WARN)

            .listener(listener)
            .proxyResultSet();

        boolean elapsedTimeLogging = amConfig.isEnabled(
            ConfigurationKey.ELAPSED_TIME_QUERY_LOGGING_ENABLED);
        boolean methodLoggingEnabled = amConfig.isEnabled(
            ConfigurationKey.METHOD_QUERY_LOGGING_ENABLED);

        if (methodLoggingEnabled) {
            b.afterMethod(DataSourceConfig::executeAfterMethod);
        }

        if (elapsedTimeLogging) {
            b.afterQuery(
                (execInfo, queryInfoList) -> log.info("Query took " +
                    execInfo.getElapsedTime() + "msec"));
        }

        return b.build();
    }

    private static void executeAfterMethod(MethodExecutionContext executionContext) {
        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();

        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            String methodName = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            int pos = className.lastIndexOf('.');
            String packageName = className.substring(0, pos);

            if (className.contains("org.nmcpye.am.cacheinvalidation.KnownTransactionsService") || methodName.equals(
                "getSingleResult") || methodName.equals("doFilterInternal")) {
                break;
            }

            if (packageName.startsWith("org.nmcpye.am") && !methodName.equals("executeAfterMethod")) {
                StackTraceElement nextElement = stackTrace[i - 1];
                String methodName1 = nextElement.getMethodName();
                String className1 = nextElement.getClassName();

                log.info(
                    "---- JDBC: " + className + "#" + methodName + " ---- \n ----" + className1 + "#" + methodName1);
                break;
            }

        }

    }

    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        // use hibernate to format queries
        private final Formatter formatter = FormatStyle.HIGHLIGHT.getFormatter();

        @Override
        protected String formatQuery(String query) {
            try {
                Objects.requireNonNull(query);
                return this.formatter.format(query) + "\n";
            } catch (Exception e) {
                log.error("Query formatter failed!", e);
            }
            return "FORMATTER ERROR!";
        }
    }
}
