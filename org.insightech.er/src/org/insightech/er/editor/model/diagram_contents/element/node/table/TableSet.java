package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class TableSet extends AbstractModel implements ObjectListModel,
		Iterable<ERTable> {

	private static final long serialVersionUID = 5264397678674390103L;

	private List<ERTable> tableList;

	public TableSet() {
		this.tableList = new ArrayList<ERTable>();
	}

	public void sort() {
		Collections.sort(this.tableList);

		for (ERTable table : this.tableList) {
			Collections.sort(table.getOutgoings());
			Collections.sort(table.getIncomings());
		}
	}

	public void add(ERTable table) {
		this.tableList.add(table);
	}

	public int remove(ERTable table) {
		int index = this.tableList.indexOf(table);
		this.tableList.remove(index);

		return index;
	}

	public void setDirty() {
	}

	public List<ERTable> getList() {
		return this.tableList;
	}

	public Iterator<ERTable> iterator() {
		return this.tableList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableSet clone() {
		TableSet tableSet = (TableSet) super.clone();
		List<ERTable> newTableList = new ArrayList<ERTable>();

		for (ERTable table : this.tableList) {
			ERTable newTable = (ERTable) table.clone();
			newTableList.add(newTable);
		}

		tableSet.tableList = newTableList;

		return tableSet;
	}

	public List<String> getAutoSequenceNames(String database) {
		List<String> autoSequenceNames = new ArrayList<String>();

		for (ERTable table : this.tableList) {
			String prefix = table.getNameWithSchema(database) + "_";

			for (NormalColumn column : table.getNormalColumns()) {
				SqlType sqlType = column.getType();

				if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(
						sqlType)
						|| SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL)
								.equals(sqlType)) {
					autoSequenceNames
							.add((prefix + column.getPhysicalName() + "_seq")
									.toUpperCase());
				}
			}
		}

		return autoSequenceNames;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.table_list");
	}

	public String getObjectType() {
		return "list";
	}

}
