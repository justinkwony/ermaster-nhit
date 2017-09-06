package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;
import org.insightech.er.util.Check;

public class EnvironmentTabWrapper extends ValidatableTabWrapper {

	private List environmentList;

	private Text nameText;

	private Button addButton;

	private Button editButton;

	private Button deleteButton;

	private Settings settings;

	private static final int LIST_HEIGHT = 230;

	public EnvironmentTabWrapper(OptionSettingDialog dialog, TabFolder parent,
			Settings settings) {
		super(dialog, parent, "label.tablespace.environment");

		this.settings = settings;
	}

	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);
		layout.numColumns = 3;
	}

	@Override
	public void initComposite() {
		this.createEnvironmentGroup(this);

		this.nameText = CompositeFactory.createText(null, this, null, 3, true,
				false);

		this.addButton = CompositeFactory.createSmallButton(this,
				"label.button.add");
		this.editButton = CompositeFactory.createSmallButton(this,
				"label.button.edit");
		this.deleteButton = CompositeFactory.createSmallButton(this,
				"label.button.delete");

		this.buttonEnabled(false);
		this.addButton.setEnabled(false);
	}

	private void createEnvironmentGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.heightHint = LIST_HEIGHT;

		this.environmentList = new List(parent, SWT.BORDER | SWT.V_SCROLL);
		this.environmentList.setLayoutData(gridData);
	}

	@Override
	protected void addListener() {
		this.environmentList.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = environmentList.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				Environment environment = settings.getEnvironmentSetting()
						.getEnvironments().get(targetIndex);
				nameText.setText(environment.getName());
				buttonEnabled(true);
			}
		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = nameText.getText().trim();
				if (!Check.isEmpty(name)) {
					settings.getEnvironmentSetting().getEnvironments()
							.add(new Environment(name));
					setData();
					environmentList.select(environmentList.getItemCount() - 1);
				}
			}
		});

		this.editButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = environmentList.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				String name = nameText.getText().trim();
				if (!Check.isEmpty(name)) {
					Environment environment = settings.getEnvironmentSetting()
							.getEnvironments().get(targetIndex);
					environment.setName(name);
					setData();
					environmentList.select(targetIndex);
				}
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = environmentList.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				settings.getEnvironmentSetting().getEnvironments()
						.remove(targetIndex);
				setData();

				if (settings.getEnvironmentSetting().getEnvironments().size() > targetIndex) {
					environmentList.select(targetIndex);
					Environment environment = settings.getEnvironmentSetting()
							.getEnvironments().get(targetIndex);
					nameText.setText(environment.getName());

				} else {
					nameText.setText("");
					buttonEnabled(false);
				}
			}
		});

		this.nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String name = nameText.getText().trim();
				if (name.length() == 0) {
					addButton.setEnabled(false);
					editButton.setEnabled(false);

				} else {
					addButton.setEnabled(true);
					if (environmentList.getSelectionIndex() != -1) {
						editButton.setEnabled(true);
					} else {
						editButton.setEnabled(false);
					}
				}
			}
		});
	}

	private void buttonEnabled(boolean enabled) {
		this.editButton.setEnabled(enabled);

		if (environmentList.getItemCount() <= 1) {
			enabled = false;
		}
		this.deleteButton.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
	}

	@Override
	public void setInitFocus() {
		this.environmentList.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		super.setData();

		this.environmentList.removeAll();

		for (Environment environment : this.settings.getEnvironmentSetting()
				.getEnvironments()) {
			this.environmentList.add(environment.getName());
		}
	}

	@Override
	public void perfomeOK() {
	}

}
