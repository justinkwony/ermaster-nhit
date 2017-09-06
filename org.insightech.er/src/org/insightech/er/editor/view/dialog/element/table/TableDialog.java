package org.insightech.er.editor.view.dialog.element.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.dialog.element.table.tab.AttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ComplexUniqueKeyTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ConstraintTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.IndexTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.TableAdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.element.table_view.TableViewDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.DescriptionTabWrapper;

public class TableDialog extends TableViewDialog {

	private ERTable copyData;

	public TableDialog(Shell parentShell, EditPartViewer viewer,
			ERTable copyData) {
		super(parentShell, viewer);

		this.copyData = copyData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.table";
	}

	@Override
	protected List<ValidatableTabWrapper> createTabWrapperList(
			TabFolder tabFolder) {
		List<ValidatableTabWrapper> list = new ArrayList<ValidatableTabWrapper>();

		list.add(new AttributeTabWrapper(this, tabFolder, this.copyData));
		list.add(new DescriptionTabWrapper(this, tabFolder, this.copyData));
		list.add(new ComplexUniqueKeyTabWrapper(this, tabFolder, this.copyData));
		list.add(new ConstraintTabWrapper(this, tabFolder, this.copyData));
		list.add(new IndexTabWrapper(this, tabFolder, this.copyData));
		list.add(new TableAdvancedTabWrapper(this, tabFolder, this.copyData));

		return list;
	}
}
