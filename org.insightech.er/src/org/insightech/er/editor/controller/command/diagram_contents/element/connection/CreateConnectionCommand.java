package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

	private ConnectionElement connection;

	public CreateConnectionCommand(ConnectionElement connection) {
		super();
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.connection.setSource(this.getSourceModel());
		this.connection.setTarget(this.getTargetModel());

		this.getTargetModel().refreshTargetConnections();
		this.getSourceModel().refreshSourceConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.connection.setSource(null);
		this.connection.setTarget(null);

		this.getTargetModel().refreshTargetConnections();
		this.getSourceModel().refreshSourceConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String validate() {
		return null;
	}

}
