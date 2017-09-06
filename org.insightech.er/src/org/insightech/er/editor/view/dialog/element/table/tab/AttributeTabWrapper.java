package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AbstractAttributeTabWrapper;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.editor.view.dialog.word.column.real.ColumnDialog;

public class AttributeTabWrapper extends AbstractAttributeTabWrapper {

	private ERTable copyData;

	public AttributeTabWrapper(TableDialog tableDialog, TabFolder parent,
			ERTable copyData) {
		super(tableDialog, parent, copyData);

		this.copyData = copyData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		super.validatePage();

		boolean needPrimaryKey = false;

		for (Relation relation : this.copyData.getOutgoingRelations()) {
			if (relation.isReferenceForPK()) {
				needPrimaryKey = true;
				break;
			}
		}

		if (needPrimaryKey) {
			if (this.copyData.getPrimaryKeySize() == 0) {
				throw new InputException("error.primary.key.is.referenced");
			}
		}
	}

	@Override
	protected AbstractColumnDialog createColumnDialog() {
		return new ColumnDialog(this.getShell(), this.copyData);
	}

	@Override
	protected String getGroupAddButtonLabel() {
		return "label.button.add.group.to.table";
	}

}
