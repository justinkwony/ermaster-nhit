package org.insightech.er.editor.view.dialog.element.table_view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

	protected TableView tableView;

	private AdvancedComposite composite;

	public AdvancedTabWrapper(AbstractTabbedDialog dialog, TabFolder parent,
			TableView tableView) {
		super(dialog, parent, "label.advanced.settings");

		this.tableView = tableView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.composite.validate();
	}

	protected AdvancedComposite createAdvancedComposite() {
		return new AdvancedComposite(this);
	}

	@Override
	public void initComposite() {
		this.composite = this.createAdvancedComposite();

		ERTable table = null;

		if (this.tableView instanceof ERTable) {
			table = (ERTable) this.tableView;
		}

		this.composite.initialize(this.dialog,
				this.tableView.getTableViewProperties(),
				this.tableView.getDiagram(), table);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitFocus() {
		this.composite.setInitFocus();
	}

	@Override
	public void perfomeOK() {
	}
}
