package org.nmcpye.am.sqlview;

import org.nmcpye.am.common.Grid;
import org.nmcpye.am.common.IllegalQueryException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service Interface for managing {@link SqlView}.
 */
public interface SqlViewServiceExt {
    String ID = SqlViewServiceExt.class.getName();

    String SELECT_EXPRESSION = "^(?i)\\s*(select|with)\\s+.+";

    Pattern SELECT_PATTERN = Pattern.compile(SELECT_EXPRESSION, Pattern.DOTALL);

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    long saveSqlView(SqlView sqlView);

    void deleteSqlView(SqlView sqlView);

    void updateSqlView(SqlView sqlView);

    int getSqlViewCount();

    SqlView getSqlView(long viewId);

    SqlView getSqlViewByUid(String uid);

    SqlView getSqlView(String viewName);

    List<SqlView> getAllSqlViews();

    List<SqlView> getAllSqlViewsNoAcl();

    // -------------------------------------------------------------------------
    // SQL view
    // -------------------------------------------------------------------------

    boolean viewTableExists(String viewTableName);

    /**
     * Creates the SQL view in the database. Checks if the SQL query is valid.
     *
     * @param sqlView the SQL view.
     * @return null if the view was created successfully, a non-null error
     * message if the operation failed.
     * @throws {@link IllegalQueryException} if the SQL query is invalid.
     */
    String createViewTable(SqlView sqlView);

    void dropViewTable(SqlView sqlView);

    /**
     * Returns the SQL view as a grid. Checks if the SQL query is valid.
     *
     * @param sqlView   the SQL view to render.
     * @param criteria  the criteria on the format key:value, will be applied as
     *                  criteria on the SQL result set.
     * @param variables the variables on the format key:value, will be
     *                  substituted with variables inside the SQL view.
     * @return a grid.
     * @throws {@link IllegalQueryException} if the SQL query is invalid.
     */
    Grid getSqlViewGrid(SqlView sqlView, Map<String, String> criteria, Map<String, String> variables,
                        List<String> filters, List<String> fields);

    /**
     * Validates the given SQL view. Checks include:
     *
     * <ul>
     * <li>All necessary variables are supplied.</li>
     * <li>Variable keys and values do not contain null values.</li>
     * <li>Invalid tables are not present in SQL query.</li>
     * </ul>
     *
     * @param sqlView   the SQL view.
     * @param criteria  the criteria.
     * @param variables the variables.
     * @throws IllegalQueryException if SQL view is invalid.
     */
    void validateSqlView(SqlView sqlView, Map<String, String> criteria, Map<String, String> variables)
        throws IllegalQueryException;

    /**
     * Tests whether the given SQL view syntax is valid.
     *
     * @param sql the SQL view.
     * @return null if valid, a non-null descriptive string if invalid.
     */
    String testSqlGrammar(String sql);

    /**
     * Refreshes the materialized view.
     *
     * @param sqlView the SQL view.
     * @return true if the materialized view was refreshed, false if not.
     */
    boolean refreshMaterializedView(SqlView sqlView);
}
