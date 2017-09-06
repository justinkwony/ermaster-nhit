package org.insightech.er.editor.view.property_source;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertySource;
import org.insightech.er.editor.ERDiagramMultiPageEditor;

public abstract class AbstractPropertySource implements IPropertySource {

	private ERDiagramMultiPageEditor editor;

	private boolean processing = false;

	public AbstractPropertySource(ERDiagramMultiPageEditor editor) {
		this.editor = editor;
	}

	public void resetPropertyValue(Object paramObject) {
	}

	public synchronized void setPropertyValue(Object id, Object value) {
		if (!this.processing) {
			try {
				this.processing = true;

				Command command = this.createSetPropertyCommand(id, value);

				if (command != null) {
					this.editor.getActiveEditor().getGraphicalViewer()
							.getEditDomain().getCommandStack().execute(command);
				}

			} finally {
				this.processing = false;
			}
		}
	}

	abstract protected Command createSetPropertyCommand(Object id, Object value);
}
