package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.RightAngleLineCommand;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class RightAngleLineAction extends AbstractBaseSelectionAction {

	public static final String ID = RightAngleLineAction.class.getName();

	public RightAngleLineAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.right.angle.line"), editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Command> getCommand(EditPart editPart, Event event) {
		List<Command> commandList = new ArrayList<Command>();

		if (editPart instanceof IResizable) {
			NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) editPart;

			for (Object obj : nodeElementEditPart.getSourceConnections()) {
				AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;

				if (connectionEditPart.getSource() != connectionEditPart
						.getTarget()) {
					commandList.add(this
							.getRightAngleLineCommand(connectionEditPart));
				}
			}

		} else if (editPart instanceof AbstractConnectionEditPart) {
			AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;

			if (connectionEditPart.getSource() != connectionEditPart
					.getTarget()) {
				commandList.add(this
						.getRightAngleLineCommand(connectionEditPart));
			}
		}

		return commandList;
	}

	private Command getRightAngleLineCommand(
			AbstractConnectionEditPart connectionEditPart) {
		int sourceX = -1;
		int sourceY = -1;
		int targetX = -1;
		int targetY = -1;

		// ConnectionEditPart connectionEditPart = (ConnectionEditPart)
		// connectionEditPart;

		ConnectionElement connection = (ConnectionElement) connectionEditPart
				.getModel();

		if (connection.getSourceXp() != -1) {
			NodeEditPart editPart = (NodeEditPart) connectionEditPart
					.getSource();
			Rectangle bounds = editPart.getFigure().getBounds();

			sourceX = bounds.x + (bounds.width * connection.getSourceXp() / 100);
			sourceY = bounds.y + (bounds.height * connection.getSourceYp() / 100);
		}

		if (connection.getTargetXp() != -1) {
			NodeEditPart editPart = (NodeEditPart) connectionEditPart
					.getTarget();
			Rectangle bounds = editPart.getFigure().getBounds();

			targetX = bounds.x + (bounds.width * connection.getTargetXp() / 100);
			targetY = bounds.y + (bounds.height * connection.getTargetYp() / 100);
		}

		if (sourceX == -1) {
			NodeElementEditPart sourceEditPart = (NodeElementEditPart) connectionEditPart
					.getSource();

			Point sourcePoint = sourceEditPart.getFigure().getBounds()
					.getCenter();
			sourceX = sourcePoint.x;
			sourceY = sourcePoint.y;
		}

		if (targetX == -1) {
			NodeElementEditPart targetEditPart = (NodeElementEditPart) connectionEditPart
					.getTarget();

			Point targetPoint = targetEditPart.getFigure().getBounds()
					.getCenter();
			targetX = targetPoint.x;
			targetY = targetPoint.y;
		}

		RightAngleLineCommand command = new RightAngleLineCommand(sourceX,
				sourceY, targetX, targetY, connectionEditPart);

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		GraphicalViewer viewer = this.getGraphicalViewer();

		for (Object object : viewer.getSelectedEditParts()) {
			if (object instanceof ConnectionEditPart) {
				return true;

			} else if (object instanceof NodeElementEditPart) {
				NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) object;

				if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}
}