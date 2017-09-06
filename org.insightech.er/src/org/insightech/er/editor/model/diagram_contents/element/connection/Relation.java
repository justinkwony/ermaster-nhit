package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public class Relation extends ConnectionElement {

	private static final long serialVersionUID = 4456694342537711599L;

	public static final String PARENT_CARDINALITY_0_OR_1 = "0..1";

	public static final String PARENT_CARDINALITY_1 = "1";

	public static final String CHILD_CARDINALITY_1 = "1";

	private String name;

	private String onUpdateAction;

	private String onDeleteAction;

	private String parentCardinality;

	private String childCardinality;

	private boolean referenceForPK;

	private ComplexUniqueKey referencedComplexUniqueKey;

	private NormalColumn referencedColumn;

	private Relation original;

	public Relation() {
		this(false, null, null, true, false);
	}

	public Relation(boolean referenceForPK,
			ComplexUniqueKey referencedComplexUniqueKey,
			NormalColumn referencedColumn, boolean notnullParent, boolean uniqueChild) {
		this.onUpdateAction = "RESTRICT";
		this.onDeleteAction = "RESTRICT";

		this.referenceForPK = referenceForPK;
		this.referencedComplexUniqueKey = referencedComplexUniqueKey;
		this.referencedColumn = referencedColumn;

		if (notnullParent) {
			this.parentCardinality = PARENT_CARDINALITY_1;
		} else {
			this.parentCardinality = PARENT_CARDINALITY_0_OR_1;
		}
		if (uniqueChild) {
			this.childCardinality = "1";
		} else {
			this.childCardinality = "1..n";
		}
	}

	private Relation getOriginal() {
		if (this.original != null) {
			return this.original.getOriginal();
		}

		return this;
	}

	public TableView getSourceTableView() {
		return (TableView) this.getSource();
	}

	public TableView getTargetTableView() {
		return (TableView) this.getTarget();
	}

	public void setTargetTableView(TableView target) {
		this.setTargetTableView(target, null);
	}

	public void setTargetTableView(TableView target,
			List<NormalColumn> foreignKeyColumnList) {

		if (this.getTargetTableView() != null) {
			removeAllForeignKey();
		}

		super.setTarget(target);

		if (target != null) {
			TableView sourceTable = (TableView) this.getSource();

			int i = 0;

			if (this.isReferenceForPK()) {
				for (NormalColumn sourceColumn : ((ERTable) sourceTable)
						.getPrimaryKeys()) {
					NormalColumn foreignKeyColumn = this.createForeiKeyColumn(
							sourceColumn, foreignKeyColumnList, i++);

					target.addColumn(foreignKeyColumn);
				}

			} else if (this.referencedComplexUniqueKey != null) {
				for (NormalColumn sourceColumn : referencedComplexUniqueKey
						.getColumnList()) {
					NormalColumn foreignKeyColumn = this.createForeiKeyColumn(
							sourceColumn, foreignKeyColumnList, i++);

					target.addColumn(foreignKeyColumn);
				}

			} else {
				for (NormalColumn sourceColumn : sourceTable.getNormalColumns()) {
					if (sourceColumn == this.referencedColumn) {
						NormalColumn foreignKeyColumn = this
								.createForeiKeyColumn(sourceColumn,
										foreignKeyColumnList, i++);

						target.addColumn(foreignKeyColumn);
						break;
					}
				}
			}
		}
	}

	private NormalColumn createForeiKeyColumn(NormalColumn referencedColumn,
			List<NormalColumn> foreignKeyColumnList, int index) {
		NormalColumn foreignKeyColumn = referencedColumn.createForeignKey(this,
				false);

		if (foreignKeyColumnList != null) {
			NormalColumn data = foreignKeyColumnList.get(index);
			data.copyForeikeyData(foreignKeyColumn);
		}

		return foreignKeyColumn;
	}

	public void setTargetWithoutForeignKey(TableView target) {
		super.setTarget(target);
	}

	public void setTargetTableWithExistingColumns(ERTable target,
			List<NormalColumn> referencedColumnList,
			List<NormalColumn> foreignKeyColumnList) {

		super.setTarget(target);
	}

	public void delete(boolean removeForeignKey, Dictionary dictionary) {
		super.delete();

		for (NormalColumn foreignKeyColumn : this.getForeignKeyColumns()) {
			foreignKeyColumn.removeReference(this);

			if (removeForeignKey) {
				if (foreignKeyColumn.getRelationList().isEmpty()) {
					this.getTargetTableView().removeColumn(foreignKeyColumn);
				}

			} else {
				dictionary.add(foreignKeyColumn);
			}
		}
	}

	public List<NormalColumn> getForeignKeyColumns() {
		List<NormalColumn> list = new ArrayList<NormalColumn>();

		if (this.getTargetTableView() != null) {
			for (NormalColumn column : this.getTargetTableView()
					.getNormalColumns()) {
				if (column.isForeignKey()) {
					NormalColumn foreignKeyColumn = (NormalColumn) column;
					for (Relation relation : foreignKeyColumn.getRelationList()) {
						if (relation == this.getOriginal()) {
							list.add(column);
							break;
						}
					}
				}
			}
		}

		return list;
	}

	public String getName() {
		return name;
	}

	public String getOnDeleteAction() {
		return onDeleteAction;
	}

	public void setOnDeleteAction(String onDeleteAction) {
		this.onDeleteAction = onDeleteAction;
	}

	public String getOnUpdateAction() {
		return onUpdateAction;
	}

	public void setOnUpdateAction(String onUpdateAction) {
		this.onUpdateAction = onUpdateAction;
	}

	public String getChildCardinality() {
		return this.childCardinality;
	}

	public void setChildCardinality(String childCardinality) {
		this.childCardinality = childCardinality;
	}

	public String getParentCardinality() {
		return parentCardinality;
	}

	public void setParentCardinality(String parentCardinality) {
		this.parentCardinality = parentCardinality;
	}

	public void setNotNullOfForeignKey(boolean notnull) {
		if (notnull) {
			this.parentCardinality = PARENT_CARDINALITY_1;
		} else {
			this.parentCardinality = PARENT_CARDINALITY_0_OR_1;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public Relation copy() {
		Relation to = new Relation(this.isReferenceForPK(),
				this.getReferencedComplexUniqueKey(),
				this.getReferencedColumn(), true, true);

		to.setName(this.getName());
		to.setOnDeleteAction(this.getOnDeleteAction());
		to.setOnUpdateAction(this.getOnUpdateAction());
		to.setChildCardinality(this.getChildCardinality());
		to.setParentCardinality(this.getParentCardinality());

		to.source = this.getSourceTableView();
		to.target = this.getTargetTableView();

		to.original = this;
		
		return to;
	}

	public Relation restructureRelationData(Relation to) {
		to.setName(this.getName());
		to.setOnDeleteAction(this.getOnDeleteAction());
		to.setOnUpdateAction(this.getOnUpdateAction());
		to.setChildCardinality(this.getChildCardinality());
		to.setParentCardinality(this.getParentCardinality());

		return to;
	}

	public boolean isReferenceForPK() {
		return this.referenceForPK;
	}

	public void setReferenceForPK(boolean referenceForPK) {
		this.referenceForPK = referenceForPK;
	}

	public void setForeignKeyColumn(NormalColumn sourceColumn) {
		if (this.referencedColumn == sourceColumn) {
			return;
		}

		this.removeAllForeignKey();

		NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this,
				false);

		this.getTargetTableView().addColumn(foreignKeyColumn);

		this.referenceForPK = false;
		this.referencedColumn = sourceColumn;
		this.referencedComplexUniqueKey = null;
	}

	public void setForeignKeyForComplexUniqueKey(
			ComplexUniqueKey complexUniqueKey) {
		if (this.referencedComplexUniqueKey == complexUniqueKey) {
			return;
		}

		this.removeAllForeignKey();

		for (NormalColumn sourceColumn : complexUniqueKey.getColumnList()) {
			NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this,
					false);

			this.getTargetTableView().addColumn(foreignKeyColumn);
		}

		this.referenceForPK = false;
		this.referencedColumn = null;
		this.referencedComplexUniqueKey = complexUniqueKey;
	}

	public void setForeignKeyColumnForPK() {
		if (this.referenceForPK) {
			return;
		}

		this.removeAllForeignKey();

		for (NormalColumn sourceColumn : ((ERTable) this.getSourceTableView())
				.getPrimaryKeys()) {
			NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this,
					false);

			this.getTargetTableView().addColumn(foreignKeyColumn);
		}

		this.referenceForPK = true;
		this.referencedColumn = null;
		this.referencedComplexUniqueKey = null;
	}

	private void removeAllForeignKey() {
		for (Iterator iter = this.getTargetTableView().getColumns().iterator(); iter
				.hasNext();) {
			Column column = (Column) iter.next();

			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (normalColumn.isForeignKey()) {
					if (normalColumn.getRelationList().size() == 1
							&& normalColumn.getRelationList().get(0) == this) {
						iter.remove();
					}
				}
			}
		}

		this.getTargetTableView().setDirty();
	}

	public void setReferencedColumn(NormalColumn referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	public NormalColumn getReferencedColumn() {
		return this.referencedColumn;
	}

	public void setReferencedComplexUniqueKey(
			ComplexUniqueKey referencedComplexUniqueKey) {
		this.referencedComplexUniqueKey = referencedComplexUniqueKey;
	}

	public ComplexUniqueKey getReferencedComplexUniqueKey() {
		return this.referencedComplexUniqueKey;
	}

	public boolean isReferedStrictly() {
		for (NormalColumn column : this.getForeignKeyColumns()) {
			if (column.isReferedStrictly()) {
				return true;
			}
		}

		return false;
	}

	public boolean isSelfRelation() {
		if (this.source == this.target) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Relation clone() {
		Relation clone = (Relation) super.clone();

		return clone;
	}
}
