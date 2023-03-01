package org.nmcpye.am.organisationunit.objectmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public interface RowMapper<T> {
    T mapRow(ResultSet var1) throws SQLException;
}
