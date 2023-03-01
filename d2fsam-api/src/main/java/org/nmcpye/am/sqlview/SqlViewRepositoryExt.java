package org.nmcpye.am.sqlview;

import org.nmcpye.am.common.Grid;
import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the SqlView entity.
 */
//@Repository
//public interface SqlViewRepositoryExt
//    extends SqlViewRepositoryExtCustom,
//    JpaRepository<SqlView, Long> {
//}

public interface SqlViewRepositoryExt
    extends IdentifiableObjectStore<SqlView> {
    String ID = SqlViewRepositoryExt.class.getName();

    boolean viewTableExists(String viewTableName);

    String createViewTable(SqlView sqlView);

    void dropViewTable(SqlView sqlView);

    void populateSqlViewGrid(Grid grid, String sql);

    /**
     * Tests the given SQL for validity.
     *
     * @param sql the SQL string.
     * @return a non-null description if invalid, and null if valid.
     */
    String testSqlGrammar(String sql);

    boolean refreshMaterializedView(SqlView sqlView);
}
