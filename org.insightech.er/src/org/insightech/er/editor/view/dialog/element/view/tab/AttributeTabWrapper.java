package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AbstractAttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.editor.view.dialog.word.column.ViewColumnDialog;

public class AttributeTabWrapper extends AbstractAttributeTabWrapper {

	private View copyData;

	public AttributeTabWrapper(ViewDialog viewDialog, TabFolder parent,
			View copyData) {
		super(viewDialog, parent, copyData);

		this.copyData = copyData;
	}

	@Override
	protected AbstractColumnDialog createColumnDialog() {
		return new ViewColumnDialog(this.getShell(), this.copyData);
	}

	@Override
	protected String getGroupAddButtonLabel() {
		return "label.button.add.group.to.view";
	}
}
