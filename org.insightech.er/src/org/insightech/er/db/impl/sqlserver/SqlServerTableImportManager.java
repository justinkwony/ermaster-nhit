package org.insightech.er.db.impl.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.util.Check;

public class SqlServerTableImportManager extends ImportFromDBManagerEclipseBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("time")) {
			columnData.size = columnData.decimalDegits;

		} else if (type.startsWith("datetime2")) {
			columnData.size = columnData.decimalDegits;

		}

		return columnData;
	}

	@Override
	protected void cacheOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {

		if (columnData.type.equals("uniqueidentifier")) {

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = con.prepareStatement("SELECT IS_ROWGUIDCOL "
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
					if (rs.getInt("IS_ROWGUIDCOL") == 1) {
						columnData.type += " rowguidcol";

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

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		if (!Check.isEmpty(schema)) {
			schema = "[" + schema + "]";
		}

		tableName = "[" + tableName + "]";

		return super.getTableNameWithSchema(schema, tableName);
	}

}
