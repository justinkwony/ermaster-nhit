package org.insightech.er.editor.controller.command.edit;

import java.util.ArrayList;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class PasteCommand extends AbstractCommand {

	private ERDiagram diagram;

	private GraphicalViewer viewer;

	// 貼り付け対象の一覧
	private NodeSet nodeElements;

	// 貼り付け時に追加するグループ列の一覧
	private GroupSet columnGroups;

	private Category category;

	/**
	 * 貼り付けコマンドを作成します。
	 * 
	 * @param editor
	 * @param nodeElements
	 */
	public PasteCommand(ERDiagramEditor editor, NodeSet nodeElements, int x,
			int y) {
		this.viewer = editor.getGraphicalViewer();
		this.diagram = (ERDiagram) viewer.getContents().getModel();
		this.category = this.diagram.getCurrentCategory();

		this.nodeElements = nodeElements;

		this.columnGroups = new GroupSet();

		GroupSet groupSet = diagram.getDiagramContents().getGroups();

		// 貼り付け対象に対して処理を繰り返します
		for (NodeElement nodeElement : nodeElements) {
			nodeElement.setLocation(new Location(nodeElement.getX() + x,
					nodeElement.getY() + y, nodeElement.getWidth(), nodeElement
							.getHeight()));

			for (ConnectionElement connection : nodeElement.getIncomings()) {
				for (Bendpoint bendpoint : connection.getBendpoints()) {
					bendpoint.transform(x, y);
				}
			}

			// 貼り付け対象がテーブルの場合
			if (nodeElement instanceof ERTable) {

				ERTable table = (ERTable) nodeElement;

				// 列に対して処理を繰り返します
				for (Column column : new ArrayList<Column>(table.getColumns())) {

					// 列がグループ列の場合
					if (column instanceof ColumnGroup) {
						ColumnGroup group = (ColumnGroup) column;

						// この図のグループ列でない場合
						if (!groupSet.contains(group)) {
							// 対象のグループ列に追加します。
							columnGroups.add(group);

						} else {
							if (groupSet.findSame(group) == null) {
								ColumnGroup equalColumnGroup = groupSet
										.find(group);

								table.replaceColumnGroup(group,
										equalColumnGroup);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 貼り付け処理を実行する
	 */
	@Override
	protected void doExecute() {
		GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

		// 図にノードを追加します。
		for (NodeElement nodeElement : this.nodeElements) {
			if (this.category != null) {
				this.category.add(nodeElement);
			}
			this.diagram.addContent(nodeElement);
		}

		// グループ列を追加します。
		for (ColumnGroup columnGroup : this.columnGroups) {
			columnGroupSet.add(columnGroup);

			for (NormalColumn normalColumn : columnGroup.getColumns()) {
				this.diagram.getDiagramContents().getDictionary()
						.add(normalColumn);
			}
		}

		this.diagram.refreshChildren();

		// 貼り付けられたテーブルを選択状態にします。
		this.setFocus();
	}

	/**
	 * 貼り付け処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

		// 図からノードを削除します。
		for (NodeElement nodeElement : this.nodeElements) {
			if (this.category != null) {
				this.category.remove(nodeElement);
			}
			this.diagram.removeContent(nodeElement);
		}

		// グループ列を削除します。
		for (ColumnGroup columnGroup : this.columnGroups) {
			columnGroupSet.remove(columnGroup);

			for (NormalColumn normalColumn : columnGroup.getColumns()) {
				this.diagram.getDiagramContents().getDictionary()
						.remove(normalColumn);
			}
		}

		this.diagram.refreshChildren();
	}

	/**
	 * 貼り付けられたテーブルを選択状態にします。
	 */
	private void setFocus() {
		// 貼り付けられたテーブルを選択状態にします。
		for (NodeElement nodeElement : this.nodeElements) {
			EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(
					nodeElement);

			this.viewer.getSelectionManager().appendSelection(editPart);
		}
	}
}
