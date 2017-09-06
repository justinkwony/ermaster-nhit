/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;

public class CUBRIDDDLCreator extends DDLCreator {

	public CUBRIDDDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		super(diagram, targetCategory, semicolon);
	}

	@Override
	public List<String> getCommentDDL(ERTable table) {
		List<String> ddlList = new ArrayList<String>();

		String tableComment = this.filterComment(table.getLogicalName(), table.getDescription(), false);

		if (!Check.isEmpty(tableComment)) {
			StringBuilder ddl = new StringBuilder();

			ddl.append("DELETE _CUB_SCHEMA_COMMENTS WHERE TABLE_NAME = '");
			ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
			ddl.append("' AND COLUMN_NAME = '*'");                                        //COLUMN_NAME
			if (this.semicolon) {
				ddl.append(";");
			}
			ddl.append("\r\n");
			ddl.append("INSERT INTO _CUB_SCHEMA_COMMENTS(TABLE_NAME, COLUMN_NAME, DESCRIPTION, LAST_UPDATED) VALUES('");
			ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
			ddl.append("', '*', '");                                                      //COLUMN_NAME
			ddl.append(tableComment.replaceAll("'", "''"));                               //DESCRIPTION
			ddl.append("', CURRENT_TIMESTAMP)");
			if (this.semicolon) {
				ddl.append(";");
			}

			ddlList.add(ddl.toString());
		}

		for (Column column : table.getColumns()) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				String comment = this.filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

				if (!Check.isEmpty(comment)) {
					StringBuilder ddl = new StringBuilder();

					ddl.append("DELETE _CUB_SCHEMA_COMMENTS WHERE TABLE_NAME = '");
					ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
					ddl.append("' AND COLUMN_NAME = '");
					ddl.append(filter(normalColumn.getPhysicalName()));                           //COLUMN_NAME
					ddl.append("'");
					if (this.semicolon) {
						ddl.append(";");
					}
					ddl.append("\r\n");
					ddl.append("INSERT INTO _CUB_SCHEMA_COMMENTS(TABLE_NAME, COLUMN_NAME, DESCRIPTION, LAST_UPDATED) VALUES('");
					ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
					ddl.append("', '");
					ddl.append(filter(normalColumn.getPhysicalName()));                           //COLUMN_NAME
					ddl.append("', '");
					ddl.append(comment.replaceAll("'", "''"));                                    //DESCRIPTION
					ddl.append("', CURRENT_TIMESTAMP)");
					if (this.semicolon) {
						ddl.append(";");
					}

					ddlList.add(ddl.toString());
				}

			} else {
				ColumnGroup columnGroup = (ColumnGroup) column;

				for (NormalColumn normalColumn : columnGroup.getColumns()) {
					String comment = this.filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

					if (!Check.isEmpty(comment)) {
						StringBuilder ddl = new StringBuilder();

						ddl.append("DELETE _CUB_SCHEMA_COMMENTS WHERE TABLE_NAME = '");
						ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
						ddl.append("' AND COLUMN_NAME = '");
						ddl.append(filter(normalColumn.getPhysicalName()));                           //COLUMN_NAME
						ddl.append("'");
						if (this.semicolon) {
							ddl.append(";");
						}
						ddl.append("\r\n");
						ddl.append("INSERT INTO _CUB_SCHEMA_COMMENTS(TABLE_NAME, COLUMN_NAME, DESCRIPTION, LAST_UPDATED) VALUES('");
						ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase()))); //TABLE_NAME
						ddl.append("', '");
						ddl.append(filter(normalColumn.getPhysicalName()));                           //COLUMN_NAME
						ddl.append("', '");
						ddl.append(comment.replaceAll("'", "''"));                                    //DESCRIPTION
						ddl.append("', CURRENT_TIMESTAMP)");
						if (this.semicolon) {
							ddl.append(";");
						}

						ddlList.add(ddl.toString());
					}
				}
			}
		}

		return ddlList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" AUTO_INCREMENT");

			Sequence sequence = normalColumn.getAutoIncrementSetting();

			if (sequence.getIncrement() != null || sequence.getStart() != null) {
				ddl.append(" (");
				if (sequence.getStart() != null) {
					ddl.append(sequence.getStart());

				} else {
					ddl.append("1");
				}

				if (sequence.getIncrement() != null) {
					ddl.append(", ");
					ddl.append(sequence.getIncrement());
				}

				ddl.append(")");
			}
		}

		return ddl.toString();
	}

	@Override
	public String getPostDDL(ERTable table) {
		CUBRIDTableProperties commonTableProperties = (CUBRIDTableProperties) this
				.getDiagram().getDiagramContents().getSettings()
				.getTableViewProperties();

		CUBRIDTableProperties tableProperties = (CUBRIDTableProperties) table
				.getTableViewProperties();

		String collation = tableProperties.getCollation();
		if (Check.isEmpty(collation)) {
			collation = commonTableProperties.getCollation();
		}

		boolean isReuseOID = tableProperties.isReuseOID();
		if (!isReuseOID) {
			isReuseOID = commonTableProperties.isReuseOID();
		}

		StringBuilder postDDL = new StringBuilder();

		if (!Check.isEmpty(collation)) {
			postDDL.append(" COLLATE ");
			postDDL.append(collation);
		}
		
		if (isReuseOID) {
			postDDL.append(" REUSE_OID");
		}
		
		postDDL.append(super.getPostDDL(table));

		return postDDL.toString();
	}

	@Override
	protected String getDDL(Tablespace object) {
		return null;
	}

	@Override
	public String getDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		String description = sequence.getDescription();
		if (this.semicolon && !Check.isEmpty(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		ddl.append("SERIAL ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(),
				sequence.getName())));
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT BY ");
			ddl.append(sequence.getIncrement());
		}
		if (sequence.getMinValue() != null) {
			ddl.append(" MINVALUE ");
			ddl.append(sequence.getMinValue());
		}
		if (sequence.getMaxValue() != null) {
			if(sequence.getMaxValue().equals(new BigDecimal("10000000000000000000000000000000000000")))
				ddl.append(" NOMAXVALUE");
			else {
				ddl.append(" MAXVALUE ");
				ddl.append(sequence.getMaxValue());
			}
		}
		if (sequence.getStart() != null) {
			ddl.append(" START WITH ");
			ddl.append(sequence.getStart());
		}
		if (!sequence.isNocache() && sequence.getCache() != null) {
			ddl.append(" CACHE ");
			ddl.append(sequence.getCache());
		}
		if (sequence.isCycle()) {
			ddl.append(" CYCLE");
		} else
			ddl.append(" NOCYCLE");
		if (sequence.isNocache()) {
			ddl.append(" NOCACHE");
		}

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	public String getDropDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP ");
		ddl.append("SERIAL ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(),
				sequence.getName())));
		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	
	}
}
