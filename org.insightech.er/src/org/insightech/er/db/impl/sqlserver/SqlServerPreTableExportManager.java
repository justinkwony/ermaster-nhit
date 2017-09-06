package org.insightech.er.db.impl.sqlserver;

import java.sql.SQLException;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class SqlServerPreTableExportManager extends PreTableExportManager {

	@Override
	protected String dropForeignKeys() throws SQLException {
		return "";
	}

	@Override
	protected String dropTables() throws SQLException, InterruptedException {
		// TODO Auto-generated method stub
		return super.dropTables();
	}

	
}
