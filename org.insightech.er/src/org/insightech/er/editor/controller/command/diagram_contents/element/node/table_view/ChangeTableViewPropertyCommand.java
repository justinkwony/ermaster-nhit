package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeTableViewPropertyCommand extends AbstractCommand {

	private TableView oldCopyTableView;

	private TableView tableView;

	private TableView newCopyTableView;

	public ChangeTableViewPropertyCommand(TableView tableView,
			TableView newCopyTableView) {
		this.tableView = tableView;
		this.oldCopyTableView = tableView.copyData();
		this.newCopyTableView = newCopyTableView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.newCopyTableView.restructureData(tableView);

		this.tableView.getDiagram().refreshVisuals();
		this.tableView.getDiagram().getDiagramContents().getIndexSet()
				.refresh();

		for (Relation relation : this.tableView.getIncomingRelations()) {
			relation.refreshVisuals();
		}

		this.tableView.getDiagram().getEditor().refreshPropertySheet();
		this.tableView.getDiagram().refreshCategories();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.oldCopyTableView.restructureData(tableView);

		this.tableView.getDiagram().refreshVisuals();
		this.tableView.getDiagram().getDiagramContents().getIndexSet()
				.refresh();

		this.tableView.getDiagram().getEditor().refreshPropertySheet();

		this.tableView.getDiagram().refresh();
	}

}
