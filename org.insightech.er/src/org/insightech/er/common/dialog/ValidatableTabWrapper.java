package org.insightech.er.common.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.exception.InputException;

public abstract class ValidatableTabWrapper extends Composite {

	protected TabItem tabItem;

	protected AbstractTabbedDialog dialog;

	public ValidatableTabWrapper(AbstractTabbedDialog dialog, TabFolder parent,
			String title) {
		super(parent, SWT.NONE);

		this.dialog = dialog;

		this.tabItem = new TabItem(parent, SWT.NONE);
		this.tabItem.setText(ResourceString.getResourceString(title));

		this.tabItem.setControl(this);
	}

	abstract public void validatePage() throws InputException;

	protected final void init() {
		GridLayout layout = new GridLayout();
		this.initLayout(layout);
		this.setLayout(layout);

		this.initComposite();
		this.addListener();
		this.setData();
	}

	protected void initLayout(GridLayout layout) {
		layout.marginTop = Resources.MARGIN_TAB;
		layout.marginLeft = Resources.MARGIN_TAB;
		layout.marginRight = Resources.MARGIN_TAB;
		layout.marginBottom = Resources.MARGIN_TAB;
	}

	public void reset() {
	}

	public void restruct() {
	}

	abstract protected void initComposite();

	protected void addListener() {
	}

	protected void setData() {
	}

	abstract public void perfomeOK();

	abstract public void setInitFocus();

}
