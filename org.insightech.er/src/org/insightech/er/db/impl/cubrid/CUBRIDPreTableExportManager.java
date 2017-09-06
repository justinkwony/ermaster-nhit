/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import java.sql.SQLException;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class CUBRIDPreTableExportManager extends PreTableExportManager {
	
	@Override
	protected String dropForeignKeys() throws SQLException {
		StringBuilder ddl = new StringBuilder();

		return ddl.toString();
	}
}
