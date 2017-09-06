package org.insightech.er.editor.controller.editpolicy.not_element.group;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.DeleteGroupCommand;
import org.insightech.er.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class GroupComponentEditPolicy extends NotElementComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(ERDiagram diagram, Object model) {
		ColumnGroup deleteColumnGroup = (ColumnGroup) model;

		return new DeleteGroupCommand(diagram, deleteColumnGroup);
	}

}
