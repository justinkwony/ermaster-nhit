package org.insightech.er.db.impl.sqlserver2008;

import org.insightech.er.db.impl.sqlserver.SqlServerDDLCreator;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class SqlServer2008DDLCreator extends SqlServerDDLCreator {

	public SqlServer2008DDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		super(diagram, targetCategory, semicolon);
	}

	@Override
	public String getDropDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP INDEX ");
		ddl.append(this.getIfExistsOption());
		ddl.append(filterName(index.getName()));
		ddl.append(" ON ");
		ddl.append(filterName(table.getNameWithSchema(this.getDiagram()
				.getDatabase())));

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}
}
