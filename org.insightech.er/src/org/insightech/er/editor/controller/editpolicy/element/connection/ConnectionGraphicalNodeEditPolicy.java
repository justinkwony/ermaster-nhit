package org.insightech.er.editor.controller.editpolicy.element.connection;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;

public class ConnectionGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		CreateCommentConnectionCommand command = (CreateCommentConnectionCommand) request
				.getStartCommand();

		command.setTarget(request.getTargetEditPart());

		if (!command.canExecute()) {
			return null;
		}

		return command;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Object object = request.getNewObject();

		if (object instanceof CommentConnection) {
			CommentConnection connection = (CommentConnection) object;

			CreateConnectionCommand command = new CreateCommentConnectionCommand(
					connection);

			command.setSource(request.getTargetEditPart());
			request.setStartCommand(command);

			return command;
		}

		return null;
	}

	@Override
	protected Command getReconnectTargetCommand(
			ReconnectRequest paramReconnectRequest) {
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(
			ReconnectRequest paramReconnectRequest) {
		return null;
	}

}
