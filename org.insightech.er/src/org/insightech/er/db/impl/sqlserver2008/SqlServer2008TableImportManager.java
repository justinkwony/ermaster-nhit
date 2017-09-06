package org.insightech.er.db.impl.sqlserver2008;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.db.impl.sqlserver.SqlServerTableImportManager;

public class SqlServer2008TableImportManager extends
		SqlServerTableImportManager {

	@Override
	protected void cacheOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {

		super.cacheOtherColumnData(tableName, schema, columnData);

		if (columnData.type.equals("varbinary")) {

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = con.prepareStatement("SELECT IS_FILESTREAM "
						+ "FROM SYS.COLUMNS C " + "INNER JOIN SYS.TABLES T "
						+ "ON T.OBJECT_ID = C.OBJECT_ID "
						+ "INNER JOIN SYS.SCHEMAS S "
						+ "ON S.SCHEMA_ID = T.SCHEMA_ID " + "WHERE T.NAME = ? "
						+ "AND S.NAME = ? " + "AND C.NAME = ? ");

				ps.setString(1, tableName);
				ps.setString(2, schema);
				ps.setString(3, columnData.columnName);
				rs = ps.executeQuery();

				if (rs.next()) {
					if (rs.getInt("IS_FILESTREAM") == 1) {
						columnData.type += " filestream";

					}
				}

			} finally {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			}
		}
	}

}
