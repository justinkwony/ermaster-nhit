package org.insightech.er.editor.controller.command.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class DeleteGroupCommand extends AbstractCommand {

	private ERDiagram diagram;

	private GroupSet groupSet;

	private ColumnGroup columnGroup;

	private Map<TableView, List<Column>> oldColumnListMap;

	public DeleteGroupCommand(ERDiagram diagram, ColumnGroup columnGroup) {
		this.groupSet = diagram.getDiagramContents().getGroups();
		this.columnGroup = columnGroup;
		this.diagram = diagram;

		this.oldColumnListMap = new HashMap<TableView, List<Column>>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		for (NormalColumn column : columnGroup.getColumns()) {
			this.diagram.getDiagramContents().getDictionary().remove(column);
		}

		for (TableView tableView : this.diagram.getDiagramContents()
				.getContents().getTableViewList()) {
			List<Column> columns = tableView.getColumns();
			List<Column> oldColumns = new ArrayList<Column>(columns);

			this.oldColumnListMap.put(tableView, oldColumns);

			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				Column column = iter.next();

				if (column instanceof ColumnGroup) {
					if (column == this.columnGroup) {
						iter.remove();
					}
				}
			}

			tableView.setColumns(columns);
		}

		this.groupSet.remove(this.columnGroup);

		this.diagram.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		for (NormalColumn column : this.columnGroup.getColumns()) {
			this.diagram.getDiagramContents().getDictionary().add(column);
		}

		for (TableView tableView : this.oldColumnListMap.keySet()) {
			List<Column> oldColumns = this.oldColumnListMap.get(tableView);
			tableView.setColumns(oldColumns);
		}

		this.groupSet.add(this.columnGroup);

		this.diagram.refreshVisuals();
	}
}
