package org.insightech.er.editor.view.property_source;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.controller.command.settings.ChangeSettingsCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ERDiagramPropertySource extends AbstractPropertySource {

	private ERDiagram diagram;

	public ERDiagramPropertySource(ERDiagramMultiPageEditor editor,
			ERDiagram diagram) {
		super(editor);
		this.diagram = diagram;
	}

	public Object getEditableValue() {
		return this.diagram;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<String> dbList = DBManagerFactory.getAllDBList();

		return new IPropertyDescriptor[] { new ComboBoxPropertyDescriptor(
				"database", ResourceString.getResourceString("label.database"),
				dbList.toArray(new String[dbList.size()])) };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals("database")) {
			List<String> dbList = DBManagerFactory.getAllDBList();

			for (int i = 0; i < dbList.size(); i++) {
				if (dbList.get(i).equals(this.diagram.getDatabase())) {
					return new Integer(i);
				}
			}

			return new Integer(0);
		}

		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals("database")) {
			return true;
		}
		return false;
	}

	@Override
	protected Command createSetPropertyCommand(Object id, Object value) {
		if (id.equals("database")) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION
					| SWT.OK | SWT.CANCEL);
			messageBox.setText(ResourceString
					.getResourceString("dialog.title.change.database"));
			messageBox.setMessage(ResourceString
					.getResourceString("dialog.message.change.database"));

			if (messageBox.open() == SWT.OK) {
				List<String> dbList = DBManagerFactory.getAllDBList();

				int index = Integer.parseInt(String.valueOf(value));

				Settings settings = (Settings) diagram.getDiagramContents()
						.getSettings().clone();
				settings.setDatabase(dbList.get(index));

				ChangeSettingsCommand command = new ChangeSettingsCommand(
						this.diagram, settings, true);

				return command;
			}

		}

		return null;
	}

}
