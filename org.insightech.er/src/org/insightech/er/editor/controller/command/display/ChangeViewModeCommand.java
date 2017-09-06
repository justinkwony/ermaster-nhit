package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeViewModeCommand extends AbstractCommand {

	private ERDiagram diagram;

	private int oldViewMode;

	private int newViewMode;

	private Settings settings;

	public ChangeViewModeCommand(ERDiagram diagram, int viewMode) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newViewMode = viewMode;
		this.oldViewMode = this.settings.getViewMode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setViewMode(this.newViewMode);
		this.diagram.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setViewMode(this.oldViewMode);
		this.diagram.refreshVisuals();
	}
}
