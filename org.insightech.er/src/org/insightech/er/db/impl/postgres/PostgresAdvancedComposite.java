package org.insightech.er.db.impl.postgres;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;

public class PostgresAdvancedComposite extends AdvancedComposite {

	private Button withoutOIDs;

	public PostgresAdvancedComposite(Composite parent) {
		super(parent);
	}

	@Override
	protected void initComposite() {
		super.initComposite();

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		this.withoutOIDs = new Button(this, SWT.CHECK);
		this.withoutOIDs.setText(ResourceString
				.getResourceString("label.without.oids"));
		this.withoutOIDs.setLayoutData(gridData);
	}

	@Override
	protected void setData() {
		super.setData();

		this.withoutOIDs
				.setSelection(((PostgresTableProperties) this.tableViewProperties)
						.isWithoutOIDs());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate() throws InputException {
		((PostgresTableProperties) this.tableViewProperties)
				.setWithoutOIDs(this.withoutOIDs.getSelection());

		return super.validate();
	}

}
