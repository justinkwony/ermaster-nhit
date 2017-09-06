package org.insightech.er.db.impl.sqlserver;

import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.util.Check;

public class SqlServerPreTableImportManager extends PreImportFromDBManager {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		if (!Check.isEmpty(schema)) {
			schema = "[" + schema + "]";
		}

		tableName = "[" + tableName + "]";

		return super.getTableNameWithSchema(schema, tableName);
	}

}
