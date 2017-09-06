/**
 * 201700903 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.tibero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.tibero.tablespace.TiberoTablespaceProperties;
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

public class TiberoDBManager extends DBManagerBase {

	public static final String ID = "Tibero";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.tmax.tibero.jdbc.TbDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:tibero:thin:@<SERVER NAME>:<PORT>:<DB NAME>";
	}

	public int getDefaultPort() {
		return 8629;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new TiberoSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof TiberoTableProperties) {
			return tableProperties;
		}

		return new TiberoTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		return new TiberoDDLCreator(diagram, targetCategory, semicolon);
	}

	public List<String> getIndexTypeList(ERTable table) {
		List<String> list = new ArrayList<String>();

		list.add("BTREE");

		return list;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA,
				SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE };
	}

	public ImportFromDBManager getTableImportManager() {
		return new TiberoTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new TiberoPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new TiberoPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new TiberoTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof TiberoTablespaceProperties)) {
			return new TiberoTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "SYSDATE" };
	}

	@Override
	public List<String> getSystemSchemaList() {
		List<String> list = new ArrayList<String>();

		list.add("outln");
		list.add("public");
		list.add("sys");
		list.add("syscat");
		list.add("sysgis");
		list.add("tibero");
		list.add("tibero1");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return new BigDecimal("9999999999999999999999999999");
	}
}
