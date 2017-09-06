package org.insightech.er.editor.controller.editpart.element.connection;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ChangeRelationPropertyCommand;
import org.insightech.er.editor.controller.editpolicy.element.connection.CommentConnectionEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.ERDiagramBendpointEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class CommentConnectionEditPart extends
		AbstractERDiagramConnectionEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ERDiagramConnection connection = this.createERDiagramConnection();

		connection.setLineStyle(SWT.LINE_DASH);

		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();

		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new CommentConnectionEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new ERDiagramBendpointEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		Relation relation = (Relation) this.getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			Relation copy = relation.copy();

			RelationDialog dialog = new RelationDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

			if (dialog.open() == IDialogConstants.OK_ID) {
				ChangeRelationPropertyCommand command = new ChangeRelationPropertyCommand(
						relation, copy);
				this.getViewer().getEditDomain().getCommandStack()
						.execute(command);
			}
		}

		super.performRequest(request);
	}

}
