package org.insightech.er.editor.controller.command.settings;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeSettingsCommand extends AbstractCommand {

	private ERDiagram diagram;

	private Settings oldSettings;

	private Settings settings;

	private boolean needRefresh;

	public ChangeSettingsCommand(ERDiagram diagram, Settings settings,
			boolean needRefresh) {
		this.diagram = diagram;
		this.oldSettings = this.diagram.getDiagramContents().getSettings();
		this.settings = settings;
		this.needRefresh = needRefresh;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.setSettings(settings);

		if (this.needRefresh) {
			this.diagram.refreshSettings();
			this.diagram.getEditor().refreshPropertySheet();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.diagram.setSettings(oldSettings);

		if (this.needRefresh) {
			this.diagram.refreshSettings();
			this.diagram.getEditor().refreshPropertySheet();
		}
	}

}
