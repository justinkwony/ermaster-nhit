package org.insightech.er.editor.controller.editpart.element.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class TableViewEditPart extends NodeElementEditPart implements
		IResizable {

	private Font titleFont;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();

		TableView tableView = (TableView) this.getModel();

		ERDiagram diagram = this.getDiagram();
		if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
			modelChildren.addAll(tableView.getExpandedColumns());

		} else {
			modelChildren.addAll(tableView.getColumns());
		}

		return modelChildren;
	}

	@Override
	public void doRefreshVisuals() {
		TableFigure tableFigure = (TableFigure) this.getFigure();
		TableView tableView = (TableView) this.getModel();

		tableFigure.create(tableView.getColor());

		ERDiagram diagram = this.getDiagram();
		tableFigure.setName(getTableViewName(tableView, diagram));

		UpdatedNodeElement updated = null;
		if (diagram.getChangeTrackingList().isCalculated()) {
			updated = diagram.getChangeTrackingList().getUpdatedNodeElement(
					tableView);
		}

		for (Object child : this.getChildren()) {
			ColumnEditPart part = (ColumnEditPart) child;
			part.refreshTableColumns(updated);
		}

		if (updated != null) {
			showRemovedColumns(diagram, tableFigure,
					updated.getRemovedColumns(), true);
		}
	}

	public static void showRemovedColumns(ERDiagram diagram,
			TableFigure tableFigure, Collection<Column> removedColumns,
			boolean isRemoved) {

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		for (Column removedColumn : removedColumns) {

			if (removedColumn instanceof ColumnGroup) {
				if (diagram.getDiagramContents().getSettings()
						.isNotationExpandGroup()) {
					ColumnGroup columnGroup = (ColumnGroup) removedColumn;

					for (NormalColumn normalColumn : columnGroup.getColumns()) {
						if (notationLevel == Settings.NOTATION_LEVLE_KEY
								&& !normalColumn.isPrimaryKey()
								&& !normalColumn.isForeignKey()
								&& !normalColumn.isReferedStrictly()) {
							continue;
						}

						NormalColumnFigure columnFigure = new NormalColumnFigure();
						tableFigure.getColumns().add(columnFigure);

						NormalColumnEditPart.addColumnFigure(diagram,
								tableFigure, columnFigure, normalColumn, false,
								false, false, false, isRemoved);
					}

				} else {
					if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
						continue;
					}

					GroupColumnFigure columnFigure = new GroupColumnFigure();
					tableFigure.getColumns().add(columnFigure);

					GroupColumnEditPart.addGroupColumnFigure(diagram,
							tableFigure, columnFigure, removedColumn, false,
							false, isRemoved);
				}

			} else {
				NormalColumn normalColumn = (NormalColumn) removedColumn;
				if (notationLevel == Settings.NOTATION_LEVLE_KEY
						&& !normalColumn.isPrimaryKey()
						&& !normalColumn.isForeignKey()
						&& !normalColumn.isReferedStrictly()) {
					continue;
				}

				NormalColumnFigure columnFigure = new NormalColumnFigure();
				tableFigure.getColumns().add(columnFigure);

				NormalColumnEditPart.addColumnFigure(diagram, tableFigure,
						columnFigure, normalColumn, false, false, false, false,
						isRemoved);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshSettings(Settings settings) {
		TableFigure figure = (TableFigure) this.getFigure();
		figure.setTableStyle(settings.getTableStyle());

		super.refreshSettings(settings);
	}

	protected Font changeFont(TableFigure tableFigure) {
		Font font = super.changeFont(tableFigure);

		FontData fonData = font.getFontData()[0];

		this.titleFont = Resources.getFont(fonData.getName(),
				fonData.getHeight(), SWT.BOLD);

		tableFigure.setFont(font, this.titleFont);

		return font;
	}

	public static String getTableViewName(TableView tableView, ERDiagram diagram) {
		String name = null;

		int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
			name = diagram.filter(tableView.getPhysicalName());

		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
			name = diagram.filter(tableView.getLogicalName());

		} else {
			name = diagram.filter(tableView.getLogicalName()) + "/"
					+ diagram.filter(tableView.getPhysicalName());
		}

		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) super.getContentPane();

		return figure.getColumns();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new TableViewComponentEditPolicy());
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new TableViewGraphicalNodeEditPolicy());
	}
}
