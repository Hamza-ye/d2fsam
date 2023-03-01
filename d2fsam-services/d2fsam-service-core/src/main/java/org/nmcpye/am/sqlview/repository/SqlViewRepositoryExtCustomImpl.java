package org.nmcpye.am.sqlview.repository;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.nmcpye.am.common.Grid;
import org.nmcpye.am.sqlview.SqlViewType;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.sqlview.SqlView;
import org.nmcpye.am.sqlview.SqlViewRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Repository("org.nmcpye.am.sqlview.SqlViewRepositoryExt")
@Slf4j
public class SqlViewRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<SqlView>
    implements SqlViewRepositoryExt {

    private static final Map<SqlViewType, String> TYPE_CREATE_PREFIX_MAP = ImmutableMap.of(SqlViewType.VIEW,
        "CREATE VIEW ", SqlViewType.MATERIALIZED_VIEW, "CREATE MATERIALIZED VIEW ");

    private static final Map<SqlViewType, String> TYPE_DROP_PREFIX_MAP = ImmutableMap.of(SqlViewType.VIEW,
        "DROP VIEW ", SqlViewType.MATERIALIZED_VIEW, "DROP MATERIALIZED VIEW ");

    private final StatementBuilder statementBuilder;

    private final JdbcTemplate readOnlyJdbcTemplate;

    private final SystemSettingManager systemSettingManager;

    public SqlViewRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                          ApplicationEventPublisher publisher, CurrentUserService currentUserService,
                                          AclService aclService, StatementBuilder statementBuilder,
                                          @Qualifier("readOnlyJdbcTemplate") JdbcTemplate readOnlyJdbcTemplate,
                                          SystemSettingManager systemSettingManager) {
        super(sessionFactory, jdbcTemplate, publisher, SqlView.class, currentUserService, aclService, false);
        checkNotNull(statementBuilder);
        checkNotNull(readOnlyJdbcTemplate);
        checkNotNull(systemSettingManager);

        this.statementBuilder = statementBuilder;
        this.readOnlyJdbcTemplate = readOnlyJdbcTemplate;
        this.systemSettingManager = systemSettingManager;
    }

    @Override
    public boolean viewTableExists(String viewTableName) {
        try {
            jdbcTemplate
                .queryForRowSet("select * from " + statementBuilder.columnQuote(viewTableName) + " limit 1");

            return true;
        } catch (BadSqlGrammarException ex) {
            return false; // View does not exist
        }
    }

    @Override
    public String createViewTable(SqlView sqlView) {
        dropViewTable(sqlView);

        final String sql = TYPE_CREATE_PREFIX_MAP.get(sqlView.getType())
            + statementBuilder.columnQuote(sqlView.getViewName()) + " AS " + sqlView.getSqlQuery();

        log.debug("Create view SQL: " + sql);

        try {
            jdbcTemplate.execute(sql);

            return null;
        } catch (BadSqlGrammarException ex) {
            return ex.getCause().getMessage();
        }
    }

    @Override
    public void populateSqlViewGrid(Grid grid, String sql) {
        SqlRowSet rs = readOnlyJdbcTemplate.queryForRowSet(sql);

        int maxLimit = systemSettingManager.getIntSetting(SettingKey.SQL_VIEW_MAX_LIMIT);

        log.debug("Get view SQL: " + sql + ", max limit: " + maxLimit);

        grid.addHeaders(rs);
        grid.addRows(rs, maxLimit);
    }

    @Override
    public String testSqlGrammar(String sql) {
        String viewName = SqlView.PREFIX_VIEWNAME + System.currentTimeMillis();

        sql = "CREATE VIEW " + viewName + " AS " + sql;

        log.debug("Test view SQL: " + sql);

        try {
            jdbcTemplate.execute(sql);

            jdbcTemplate.execute("DROP VIEW IF EXISTS " + viewName);
        } catch (BadSqlGrammarException | UncategorizedSQLException ex) {
            return ex.getCause().getMessage();
        }

        return null;
    }

    @Override
    public void dropViewTable(SqlView sqlView) {
        String viewName = sqlView.getViewName();

        try {
            final String sql = TYPE_DROP_PREFIX_MAP.get(sqlView.getType()) + " IF EXISTS "
                + statementBuilder.columnQuote(viewName);

            log.debug("Drop view SQL: " + sql);

            jdbcTemplate.update(sql);
        } catch (Exception ex) {
            log.warn("Could not drop view: " + viewName, ex);
        }
    }

    @Override
    public boolean refreshMaterializedView(SqlView sqlView) {
        final String sql = "REFRESH MATERIALIZED VIEW " + sqlView.getViewName();

        log.debug("Refresh materialized view: " + sql);

        try {
            jdbcTemplate.update(sql);

            return true;
        } catch (Exception ex) {
            log.warn("Could not refresh materialized view: " + sqlView.getViewName(), ex);

            return false;
        }
    }
}
