package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ChangeRelationPropertyCommand extends AbstractCommand {

	private Relation oldCopyRelation;

	private Relation newCopyRelation;

	private Relation relation;

	private TableView oldTargetTable;

	private boolean isChildNotNull;

	private Map<NormalColumn, Boolean> foreignKeyNotNullMap;

	public ChangeRelationPropertyCommand(Relation relation,
			Relation newCopyRelation) {
		this.relation = relation;
		this.oldCopyRelation = relation.copy();
		this.newCopyRelation = newCopyRelation;

		this.oldTargetTable = relation.getTargetTableView().copyData();

		this.foreignKeyNotNullMap = new HashMap<NormalColumn, Boolean>();

		if (Relation.PARENT_CARDINALITY_1.equals(newCopyRelation
				.getParentCardinality())) {
			this.isChildNotNull = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.newCopyRelation.restructureRelationData(this.relation);

		if (this.newCopyRelation.isReferenceForPK()) {
			this.relation.setForeignKeyColumnForPK();

		} else if (this.newCopyRelation.getReferencedComplexUniqueKey() != null) {
			this.relation.setForeignKeyForComplexUniqueKey(this.newCopyRelation
					.getReferencedComplexUniqueKey());

		} else {
			this.relation.setForeignKeyColumn(this.newCopyRelation
					.getReferencedColumn());
		}

		for (NormalColumn foreignKeyColumn : this.relation
				.getForeignKeyColumns()) {
			this.foreignKeyNotNullMap.put(foreignKeyColumn,
					foreignKeyColumn.isNotNull());

			foreignKeyColumn.setNotNull(this.isChildNotNull);
		}
		
		this.relation.getTarget().refresh();
		this.relation.getSource().refresh();
		this.relation.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.oldCopyRelation.restructureRelationData(this.relation);

		this.relation
				.setReferenceForPK(this.oldCopyRelation.isReferenceForPK());
		this.relation.setReferencedComplexUniqueKey(this.oldCopyRelation
				.getReferencedComplexUniqueKey());
		this.relation.setReferencedColumn(this.oldCopyRelation
				.getReferencedColumn());

		this.oldTargetTable.restructureData(this.relation.getTargetTableView());

		for (Entry<NormalColumn, Boolean> foreignKeyEntry : this.foreignKeyNotNullMap
				.entrySet()) {
			foreignKeyEntry.getKey().setNotNull(foreignKeyEntry.getValue());
		}
		
		this.relation.getTargetTableView().setDirty();
		
		this.relation.getTarget().refresh();
		this.relation.getSource().refresh();
		this.relation.refreshVisuals();
	}
}
