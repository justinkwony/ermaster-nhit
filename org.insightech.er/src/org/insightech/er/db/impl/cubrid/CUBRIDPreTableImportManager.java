/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class CUBRIDPreTableImportManager extends PreImportFromDBManager {

	@Override
	protected List<DBObject> importTables() throws SQLException, InterruptedException {
		List<DBObject> list = new ArrayList<DBObject>();

		ResultSet resultSet = null;
		PreparedStatement stmt = null;

		if (this.schemaList.isEmpty()) {
			this.schemaList.add(null);
		}
		
		for (String schemaPattern : this.schemaList) {
			try {
				if (schemaPattern == null) {
					stmt = con.prepareStatement("SELECT NULL AS TABLE_SCHEM, CLASS_NAME AS TABLE_NAME FROM DB_CLASS"
							+ " WHERE CLASS_TYPE = 'CLASS' AND IS_SYSTEM_CLASS = 'NO'"
							+ " AND CLASS_NAME <> '_cub_schema_comments' ORDER BY CLASS_NAME");
				}

				resultSet = stmt.executeQuery();

				while (resultSet.next()) {
					String schema = resultSet.getString("TABLE_SCHEM");
					String name = resultSet.getString("TABLE_NAME");

					DBObject dbObject = new DBObject(schema, name, DBObject.TYPE_TABLE);
					list.add(dbObject);
				}

			} finally {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		}

		return list;
	}
	
	@Override
	protected List<DBObject> importSequences() throws SQLException, InterruptedException {
		List<DBObject> list = new ArrayList<DBObject>();

		ResultSet resultSet = null;
		PreparedStatement stmt = null;

		if (this.schemaList.isEmpty()) {
			this.schemaList.add(null);
		}
		
		for (String schemaPattern : this.schemaList) {
			try {
				if (schemaPattern == null) {
					stmt = con.prepareStatement("SELECT NULL AS TABLE_SCHEM, NAME AS TABLE_NAME FROM DB_SERIAL WHERE CLASS_NAME IS NULL");
				}

				resultSet = stmt.executeQuery();

				while (resultSet.next()) {
					String schema = resultSet.getString("TABLE_SCHEM");
					String name = resultSet.getString("TABLE_NAME");

					DBObject dbObject = new DBObject(schema, name, DBObject.TYPE_SEQUENCE);
					list.add(dbObject);
				}

			} finally {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		}

		return list;
	}
}
