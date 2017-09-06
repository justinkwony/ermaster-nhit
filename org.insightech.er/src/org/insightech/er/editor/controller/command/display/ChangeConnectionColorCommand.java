package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ChangeConnectionColorCommand extends AbstractCommand {

	private ConnectionElement connection;

	private int red;

	private int green;

	private int blue;

	private int[] oldColor;

	public ChangeConnectionColorCommand(ConnectionElement connection, int red,
			int green, int blue) {
		this.connection = connection;

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.oldColor = this.connection.getColor();

		this.connection.setColor(red, green, blue);

		this.connection.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		if (this.oldColor == null) {
			this.oldColor = new int[3];
			this.oldColor[0] = 0;
			this.oldColor[1] = 0;
			this.oldColor[2] = 0;
		}

		this.connection.setColor(this.oldColor[0], this.oldColor[1],
				this.oldColor[2]);

		this.connection.refreshVisuals();
	}
}
