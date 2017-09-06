package org.insightech.er.common.dialog;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.ListenerAppender;

public abstract class AbstractTabbedDialog extends AbstractDialog {

	private TabFolder tabFolder;

	private List<ValidatableTabWrapper> tabWrapperList;

	public AbstractTabbedDialog(Shell parentShell) {
		super(parentShell);
	}

	protected void createTabFolder(Composite parent) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		this.tabFolder = new TabFolder(parent, SWT.NONE);
		this.tabFolder.setLayoutData(gridData);

		this.tabWrapperList = this.createTabWrapperList(this.tabFolder);

		for (ValidatableTabWrapper tab : this.tabWrapperList) {
			tab.init();
		}

		ListenerAppender.addTabListener(tabFolder, tabWrapperList);

		this.tabWrapperList.get(0).setInitFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		try {
			for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
				tabWrapper.validatePage();
			}

		} catch (InputException e) {
			return e.getMessage();
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		for (ValidatableTabWrapper tab : this.tabWrapperList) {
			tab.perfomeOK();
		}
	}

	@Override
	protected void setData() {
	}

	protected abstract List<ValidatableTabWrapper> createTabWrapperList(
			TabFolder tabFolder);

	public void resetTabs() {
		for (ValidatableTabWrapper tab : tabWrapperList) {
			// tab.setVisible(false);
			tab.reset();
			// tab.setVisible(true);
		}
	}

}
