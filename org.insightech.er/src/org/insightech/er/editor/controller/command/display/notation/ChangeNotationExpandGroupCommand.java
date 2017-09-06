package org.insightech.er.editor.controller.command.display.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationExpandGroupCommand extends AbstractCommand {

	private ERDiagram diagram;

	private boolean oldNotationExpandGroup;

	private boolean newNotationExpandGroup;

	private Settings settings;

	public ChangeNotationExpandGroupCommand(ERDiagram diagram,
			boolean notationExpandGroup) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newNotationExpandGroup = notationExpandGroup;
		this.oldNotationExpandGroup = this.settings.isNotationExpandGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setNotationExpandGroup(this.newNotationExpandGroup);
		this.diagram.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setNotationExpandGroup(this.oldNotationExpandGroup);
		this.diagram.refreshVisuals();
	}
}
