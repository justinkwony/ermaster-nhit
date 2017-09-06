package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class CreateRelatedTableCommand extends AbstractCreateRelationCommand {

	private Relation relation1;

	private Relation relation2;

	private ERTable relatedTable;

	private ERDiagram diagram;

	private int sourceX;

	private int sourceY;

	private int targetX;

	private int targetY;

	private Category category;

	protected Location newCategoryLocation;

	protected Location oldCategoryLocation;

	public CreateRelatedTableCommand(ERDiagram diagram) {
		super();

		this.relatedTable = new ERTable();

		this.diagram = diagram;
		this.category = this.diagram.getCurrentCategory();
		if (this.category != null) {
			this.oldCategoryLocation = this.category.getLocation();
		}
	}

	public void setSourcePoint(int x, int y) {
		this.sourceX = x;
		this.sourceY = y;
	}

	private void setTargetPoint(int x, int y) {
		this.targetX = x;
		this.targetY = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTarget(EditPart target) {
		super.setTarget(target);

		if (target != null) {
			if (target instanceof TableViewEditPart) {
				TableViewEditPart tableEditPart = (TableViewEditPart) target;

				Point point = tableEditPart.getFigure().getBounds().getCenter();
				this.setTargetPoint(point.x, point.y);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		// ERDiagramEditPart.setUpdateable(false);

		this.init();

		this.diagram.addNewContent(this.relatedTable);
		this.addToCategory(this.relatedTable);

		this.relation1.setSource((ERTable) this.source.getModel());
		this.relation1.setTargetTableView(this.relatedTable);

		this.relation2.setSource((ERTable) this.target.getModel());
		this.relation2.setTargetTableView(this.relatedTable);

		this.diagram.refreshChildren();
		this.getTargetModel().refresh();
		this.getSourceModel().refresh();

		if (this.category != null) {
			this.category.refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.diagram.removeContent(this.relatedTable);
		this.removeFromCategory(this.category);

		this.relation1.setSource(null);
		this.relation1.setTargetTableView(null);

		this.relation2.setSource(null);
		this.relation2.setTargetTableView(null);

		this.diagram.refreshChildren();
		this.getTargetModel().refresh();
		this.getSourceModel().refresh();

		if (this.category != null) {
			this.category.refresh();
		}
	}

	private void init() {
		ERTable sourceTable = (ERTable) this.getSourceModel();

		this.relation1 = sourceTable.createRelation();

		ERTable targetTable = (ERTable) this.getTargetModel();
		this.relation2 = targetTable.createRelation();

		this.relatedTable.setLocation(new Location(
				(this.sourceX + this.targetX - ERTable.DEFAULT_WIDTH) / 2,
				(this.sourceY + this.targetY - ERTable.DEFAULT_HEIGHT) / 2,
				ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));

		this.relatedTable.setLogicalName(ERTable.NEW_LOGICAL_NAME);
		this.relatedTable.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		if (!(this.getSourceModel() instanceof ERTable)
				|| !(this.getTargetModel() instanceof ERTable)) {
			return false;
		}

		return true;
	}

	protected void addToCategory(NodeElement nodeElement) {
		if (this.category != null) {
			this.category.add(nodeElement);
			Location newLocation = category.getNewCategoryLocation(nodeElement);

			if (newLocation != null) {
				this.newCategoryLocation = newLocation;
				this.category.setLocation(this.newCategoryLocation);
			}
		}
	}

	protected void removeFromCategory(NodeElement nodeElement) {
		if (this.category != null) {
			this.category.remove(nodeElement);

			if (this.newCategoryLocation != null) {
				this.category.setLocation(this.oldCategoryLocation);
			}
		}
	}

}
