package org.insightech.er.db.impl.sqlite;

import java.sql.SQLException;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class SQLitePreTableExportManager extends PreTableExportManager {

	@Override
	protected String dropForeignKeys() throws SQLException {
		return "";
	}

	
}
