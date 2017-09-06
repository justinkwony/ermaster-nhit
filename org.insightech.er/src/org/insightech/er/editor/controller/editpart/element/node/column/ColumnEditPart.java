package org.insightech.er.editor.controller.editpart.element.node.column;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.ColumnSelectionHandlesEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.NormalColumnComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;

public abstract class ColumnEditPart extends AbstractModelEditPart {

	public abstract void refreshTableColumns(UpdatedNodeElement updated);

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new ColumnSelectionHandlesEditPolicy());
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NormalColumnComponentEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart getTargetEditPart(Request request) {
		EditPart editPart = super.getTargetEditPart(request);

		if (!this.getDiagram().isDisableSelectColumn()) {
			return editPart;
		}

		if (editPart != null) {
			return editPart.getParent();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				performRequestOpen();

			} catch (Exception e) {
				ERDiagramActivator.showExceptionDialog(e);
			}
		}

		super.performRequest(request);
	}

	public void performRequestOpen() {
		Object parent = this.getParent().getModel();

		ERDiagram diagram = this.getDiagram();

		if (parent instanceof ERTable) {
			ERTable table = (ERTable) parent;

			ERTable copyTable = table.copyData();

			TableDialog dialog = new TableDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), this.getViewer(),
					copyTable);

			if (dialog.open() == IDialogConstants.OK_ID) {
				CompoundCommand command = ERTableEditPart
						.createChangeTablePropertyCommand(diagram, table,
								copyTable);

				this.executeCommand(command.unwrap());
			}

		} else if (parent instanceof View) {
			View view = (View) parent;

			View copyView = view.copyData();

			ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), this.getViewer(),
					copyView);

			if (dialog.open() == IDialogConstants.OK_ID) {
				CompoundCommand command = ViewEditPart
						.createChangeViewPropertyCommand(diagram, view,
								copyView);

				this.executeCommand(command.unwrap());
			}
		}
	}

}
