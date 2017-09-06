package org.insightech.er.editor.controller.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.editor.model.ERDiagram;

public class WithoutUpdateCommandWrapper extends Command {

	private CompoundCommand command;

	private ERDiagram diagram;

	public WithoutUpdateCommandWrapper(CompoundCommand command,
			ERDiagram diagram) {
		this.command = command;
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		ERDiagram.setUpdateable(false);

		this.diagram.getEditor().getActiveEditor().getGraphicalViewer()
				.deselectAll();

		this.command.execute();

		ERDiagram.setUpdateable(true);

		this.diagram.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		ERDiagram.setUpdateable(false);

		this.command.undo();

		ERDiagram.setUpdateable(true);

		this.diagram.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return this.command.canExecute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canUndo() {
		return this.command.canUndo();
	}

}
