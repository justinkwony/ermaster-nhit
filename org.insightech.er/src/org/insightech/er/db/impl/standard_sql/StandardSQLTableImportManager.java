package org.insightech.er.db.impl.standard_sql;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;

public class StandardSQLTableImportManager extends ImportFromDBManagerEclipseBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}
}
