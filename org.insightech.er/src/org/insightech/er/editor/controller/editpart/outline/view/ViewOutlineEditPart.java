package org.insightech.er.editor.controller.editpart.outline.view;

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
import org.insightech.er.editor.controller.editpart.element.node.ViewEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;

public class ViewOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		View model = (View) this.getModel();

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
		this.setWidgetImage(ERDiagramActivator.getImage(ImageKey.VIEW));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		View view = (View) this.getModel();
		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			View copyView = view.copyData();

			ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), this.getViewer(),
					copyView);

			if (dialog.open() == IDialogConstants.OK_ID) {
				CompoundCommand command = ViewEditPart
						.createChangeViewPropertyCommand(diagram, view,
								copyView);

				this.execute(command.unwrap());
			}
		}

		super.performRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
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
