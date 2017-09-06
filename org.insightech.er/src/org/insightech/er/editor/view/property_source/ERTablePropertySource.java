package org.insightech.er.editor.view.property_source;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ERTablePropertySource extends AbstractPropertySource {

	private ERTable table;

	public ERTablePropertySource(ERDiagramMultiPageEditor editor, ERTable table) {
		super(editor);
		this.table = table;
	}

	public Object getEditableValue() {
		return this.table;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor("physicalName",
						ResourceString.getResourceString("label.physical.name")),
				new TextPropertyDescriptor("logicalName",
						ResourceString.getResourceString("label.logical.name")) };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals("physicalName")) {
			return this.table.getPhysicalName() != null ? this.table
					.getPhysicalName() : "";
		}
		if (id.equals("logicalName")) {
			return this.table.getLogicalName() != null ? this.table
					.getLogicalName() : "";
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals("physicalName")) {
			return true;
		}
		if (id.equals("logicalName")) {
			return true;
		}
		return false;
	}

	@Override
	protected Command createSetPropertyCommand(Object id, Object value) {
		ERTable copyTable = table.copyData();

		if (id.equals("physicalName")) {
			copyTable.setPhysicalName(String.valueOf(value));

		} else if (id.equals("logicalName")) {
			copyTable.setLogicalName(String.valueOf(value));
		}

		ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(
				this.table, copyTable);

		return command;
	}

}
