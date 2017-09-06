package org.insightech.er.editor.controller.editpart.outline.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;

public class TableOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<AbstractModel> children = new ArrayList<AbstractModel>();

		ERTable table = (ERTable) this.getModel();

		List<Relation> relationList = new ArrayList<Relation>();

		Category category = this.getCurrentCategory();

		for (Relation relation : table.getIncomingRelations()) {
			if (category == null || category.contains(relation.getSource())) {
				relationList.add(relation);
			}
		}

		Collections.sort(relationList);

		children.addAll(relationList);
		children.addAll(table.getIndexes());

		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		ERTable model = (ERTable) this.getModel();

		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		String name = null;

		int viewMode = diagram.getDiagramContents().getSettings()
				.getOutlineViewMode();

		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
			if (model.getPhysicalName() != null) {
				name = model.getPhysicalName();

			} else {
				name = "";
			}

		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
			if (model.getLogicalName() != null) {
				name = model.getLogicalName();

			} else {
				name = "";
			}

		} else {
			if (model.getLogicalName() != null) {
				name = model.getLogicalName();

			} else {
				name = "";
			}

			name += "/";

			if (model.getPhysicalName() != null) {
				name += model.getPhysicalName();

			}
		}

		this.setWidgetText(diagram.filter(name));
		this.setWidgetImage(ERDiagramActivator.getImage(ImageKey.TABLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
		// this.installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		ERTable table = (ERTable) this.getModel();
		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			ERTable copyTable = table.copyData();

			TableDialog dialog = new TableDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), this.getViewer(),
					copyTable);

			if (dialog.open() == IDialogConstants.OK_ID) {
				CompoundCommand command = ERTableEditPart
						.createChangeTablePropertyCommand(diagram, table,
								copyTable);

				this.execute(command.unwrap());
			}
		}

		super.performRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}
}
