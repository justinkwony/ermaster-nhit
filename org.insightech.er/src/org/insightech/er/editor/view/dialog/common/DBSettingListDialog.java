package org.insightech.er.editor.view.dialog.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class DBSettingListDialog extends AbstractDialog {

	private Table settingTable;

	private List<DBSetting> dbSettingList;

	private DBSetting result;

	private String database;

	public DBSettingListDialog(Shell parentShell, String database) {
		super(parentShell);

		this.database = database;
		this.dbSettingList = new ArrayList<DBSetting>();
	}

	@Override
	protected void initialize(Composite composite) {
		this.settingTable = CompositeFactory.createTable(composite, 150);

		CompositeFactory.createTableColumn(this.settingTable, "label.database",
				150);
		CompositeFactory.createTableColumn(this.settingTable,
				"label.server.name", 130);
		CompositeFactory.createTableColumn(this.settingTable, "label.port", 80,
				SWT.RIGHT);
		CompositeFactory.createTableColumn(this.settingTable,
				"label.database.name", 130);
		CompositeFactory.createTableColumn(this.settingTable,
				"label.user.name", 100);
		CompositeFactory.createTableColumn(this.settingTable, "label.url", 350);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.settingTable.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				buttonPressed(IDialogConstants.OK_ID);
			}
		});

		this.settingTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();

				int index = settingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				selectTable(index);
			}
		});

	}

	@Override
	protected void perfomeOK() throws InputException {
		int index = settingTable.getSelectionIndex();
		this.result = this.dbSettingList.get(index);
	}

	public DBSetting getResult() {
		return this.result;
	}

	public int getResultIndex() {
		return this.dbSettingList.indexOf(this.result);
	}

	@Override
	protected void setData() {
		this.dbSettingList = PreferenceInitializer
				.getDBSettingList(this.database);

		for (DBSetting dbSetting : this.dbSettingList) {
			TableItem item = new TableItem(this.settingTable, SWT.NONE);
			item.setText(0, dbSetting.getDbsystem());
			item.setText(1, dbSetting.getServer());
			if (dbSetting.getPort() != 0) {
				item.setText(2, String.valueOf(dbSetting.getPort()));
			}
			item.setText(3, dbSetting.getDatabase());
			item.setText(4, dbSetting.getUser());
			item.setText(5, Format.null2blank(dbSetting.getUrl()));
		}

		this.setButtonEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				ResourceString.getResourceString("label.load.setting"), true);
		createButton(parent, IDialogConstants.STOP_ID,
				ResourceString.getResourceString("label.delete"), false);
		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);

		setButtonEnabled(false);
	}

	private void setButtonEnabled(boolean enabled) {
		Button okButton = this.getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(enabled);
		}

		Button deleteButton = this.getButton(IDialogConstants.STOP_ID);
		if (deleteButton != null) {
			deleteButton.setEnabled(enabled);
		}
	}

	private void selectTable(int index) {
		this.settingTable.select(index);

		if (index >= 0) {
			this.setButtonEnabled(true);
		} else {
			this.setButtonEnabled(false);
		}
	}

	@Override
	protected String getErrorMessage() {
		int index = settingTable.getSelectionIndex();
		if (index == -1) {
			return "dialog.message.load.db.setting";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "label.load.database.setting";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.STOP_ID) {
			int index = this.settingTable.getSelectionIndex();

			if (index != -1) {
				this.settingTable.remove(index);
				this.dbSettingList.remove(index);

				PreferenceInitializer.saveSetting(this.dbSettingList);

				if (index >= this.settingTable.getItemCount()) {
					index = this.settingTable.getItemCount() - 1;
				}

				this.selectTable(index);
			}
		}

		super.buttonPressed(buttonId);
	}

}
