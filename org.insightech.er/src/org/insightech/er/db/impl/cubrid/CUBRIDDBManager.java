/**
 * 20170903 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.access.AccessDDLCreator;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class CUBRIDDBManager extends DBManagerBase {

	public static final String ID = "CUBRID";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "cubrid.jdbc.driver.CUBRIDDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:cubrid:<SERVER NAME>:<PORT>:<DB NAME>:::";
	}

	public int getDefaultPort() {
		return 33000;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new CUBRIDSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof CUBRIDTableProperties) {
			return tableProperties;
		}

		return new CUBRIDTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		return new AccessDDLCreator(diagram, targetCategory, semicolon);
	}

	public List<String> getIndexTypeList(ERTable table) {
		List<String> list = new ArrayList<String>();

		list.add("BTREE");

		return list;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] {
				  SUPPORT_AUTO_INCREMENT
				, SUPPORT_AUTO_INCREMENT_SETTING
				, SUPPORT_DESC_INDEX
//				, SUPPORT_FULLTEXT_INDEX
//				, SUPPORT_SCHEMA
				, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new CUBRIDTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new CUBRIDPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new CUBRIDPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		return null;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "SYSDATETIME" };
	}

	public BigDecimal getSequenceMaxValue() {
		return new BigDecimal("10000000000000000000000000000000000000");
	}

	public static List<String> getCollationList() {
		List<String> list = new ArrayList<String>();
		
		list.add("iso88591_bin");
		list.add("utf8_bin");
		list.add("iso88591_en_cs");
		list.add("iso88591_en_ci");
		list.add("utf8_en_cs");
		list.add("utf8_en_ci");
		list.add("utf8_tr_cs");
		list.add("utf8_ko_cs");
		list.add("euckr_bin");

		return list;
	}
}
