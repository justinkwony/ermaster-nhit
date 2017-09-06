package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CreateRelationCommand extends AbstractCreateRelationCommand {

	private Relation relation;

	private List<NormalColumn> foreignKeyColumnList;

	public CreateRelationCommand(Relation relation) {
		this(relation, null);
	}

	public CreateRelationCommand(Relation relation,
			List<NormalColumn> foreignKeyColumnList) {
		super();
		this.relation = relation;
		this.foreignKeyColumnList = foreignKeyColumnList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		// ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource((TableView) source.getModel());

		// ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView((TableView) target.getModel(),
				this.foreignKeyColumnList);

		this.getTargetModel().refresh();
		this.getSourceModel().refreshSourceConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		// ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource(null);

		// ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView(null);

		this.getTargetModel().refresh();
		this.getSourceModel().refreshSourceConnections();
	}
}
