package org.insightech.er.editor.view.action.dbexport;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.settings.ChangeSettingsCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;

public abstract class AbstractExportWithDialogAction extends AbstractBaseAction {

	public AbstractExportWithDialogAction(String id, String titleResource,
			String imageKey, ERDiagramEditor editor) {
		super(id, ResourceString.getResourceString(titleResource), editor);
		this.setImageDescriptor(ERDiagramActivator.getImageDescriptor(imageKey));
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		AbstractExportDialog dialog = this.getExportDialog();
		dialog.init(diagram);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
					dialog.getSettings(), false);
			this.execute(command);
		}
	}

	protected abstract AbstractExportDialog getExportDialog();

}