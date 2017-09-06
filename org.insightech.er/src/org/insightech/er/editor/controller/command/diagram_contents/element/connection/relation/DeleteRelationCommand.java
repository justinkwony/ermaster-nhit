package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public class DeleteRelationCommand extends DeleteConnectionCommand {

	private TableView oldTargetCopyTable;

	private TableView oldTargetTable;

	private TableView oldSourceTable;

	private Relation relation;

	private Boolean removeForeignKey;

	private Map<NormalColumn, NormalColumn> foreignKeyReferencedColumnMap;

	public DeleteRelationCommand(Relation relation, Boolean removeForeignKey) {
		super(relation);

		this.relation = relation;
		this.oldTargetTable = relation.getTargetTableView();
		this.oldSourceTable = relation.getSourceTableView();

		this.removeForeignKey = removeForeignKey;

		this.foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (this.oldTargetCopyTable == null) {
			for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
				NormalColumn referencedColumn = foreignKey
						.getReferencedColumn(relation);

				this.foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
			}

			this.oldTargetCopyTable = this.oldTargetTable.copyData();
		}

		Dictionary dictionary = this.oldTargetTable.getDiagram()
				.getDiagramContents().getDictionary();

		this.relation.delete(this.removeForeignKey, dictionary);
		
		this.oldTargetTable.refresh();
		this.oldSourceTable.refreshSourceConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		super.doUndo();

		for (NormalColumn foreignKey : this.foreignKeyReferencedColumnMap.keySet()) {
			if (!this.removeForeignKey) {
				Dictionary dictionary = this.oldTargetTable.getDiagram()
						.getDiagramContents().getDictionary();
				dictionary.remove(foreignKey);
			}

			foreignKey.addReference(this.foreignKeyReferencedColumnMap.get(foreignKey),
					this.relation);
		}

		this.oldTargetCopyTable.restructureData(this.oldTargetTable);
		
		if (this.oldTargetTable == this.oldSourceTable) {
			//this.oldTargetTable.update();
			this.oldTargetTable.refresh();
			
		} else {
			this.oldTargetTable.refresh();
			this.oldSourceTable.refreshSourceConnections();
		}
	}

	@Override
	public boolean canExecute() {
		if (this.removeForeignKey == null) {
			if (this.relation.isReferedStrictly()) {
				if (this.isReferencedByMultiRelations()) {
					ERDiagramActivator
							.showErrorDialog("dialog.message.referenced.by.multi.foreign.key");
					return false;
				}

				this.removeForeignKey = false;

				this.foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

				for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
					NormalColumn referencedColumn = foreignKey
							.getReferencedColumn(relation);

					this.foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
				}

				return true;
			}

			if (ERDiagramActivator.showConfirmDialog(
					"dialog.message.confirm.remove.foreign.key", SWT.YES,
					SWT.NO)) {
				this.removeForeignKey = true;

			} else {
				this.removeForeignKey = false;

				this.foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

				for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
					NormalColumn referencedColumn = foreignKey
							.getReferencedColumn(relation);

					this.foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
				}
			}
		}

		return true;
	}

	private boolean isReferencedByMultiRelations() {
		for (NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
			for (NormalColumn childForeignKeyColumn : foreignKeyColumn
					.getForeignKeyList()) {
				if (childForeignKeyColumn.getRelationList().size() >= 2) {
					Set<TableView> referencedTables = new HashSet<TableView>();

					for (Relation relation : childForeignKeyColumn
							.getRelationList()) {
						referencedTables.add(relation.getSourceTableView());
					}

					if (referencedTables.size() >= 2) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
