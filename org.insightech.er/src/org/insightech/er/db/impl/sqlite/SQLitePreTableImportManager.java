package org.insightech.er.db.impl.sqlite;

import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class SQLitePreTableImportManager extends PreImportFromDBManager {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		return "[" + super.getTableNameWithSchema(schema, tableName) + "]";
	}
}
