package org.insightech.er.editor.view.dialog.word;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class AbstractWordDialog extends AbstractDialog {

	protected static int WIDTH = -1;

	protected Combo typeCombo;

	protected Text logicalNameText;

	protected Text physicalNameText;

	private String oldPhysicalName;

	protected Text lengthText;

	protected Text decimalText;

	protected Button arrayCheck;

	protected Text arrayDimensionText;

	protected Button unsignedCheck;

	protected Button zerofillCheck;

	protected Button binaryCheck;

	protected boolean add;

	protected Text descriptionText;

	protected Text argsText;

	protected Button byteSemanticsRadio;

	protected Button charSemanticsRadio;

	protected ERDiagram diagram;

	public AbstractWordDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell);

		this.diagram = diagram;
		this.oldPhysicalName = "";
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		Composite rootComposite = this.createRootComposite(composite);

		this.initializeComposite(rootComposite);
		this.initializeTypeCombo();

		this.physicalNameText.setFocus();
	}

	protected Composite createRootComposite(Composite parent) {
		return CompositeFactory.createComposite(parent,
				this.getCompositeNumColumns(), false);
	}

	protected int getCompositeNumColumns() {
		return 6;
	}

	protected void initializeComposite(Composite composite) {
		int numColumns = this.getCompositeNumColumns();

		this.physicalNameText = CompositeFactory.createText(this, composite,
				"label.physical.name", numColumns - 1, WIDTH, false, true);

		this.logicalNameText = CompositeFactory.createText(this, composite,
				"label.logical.name", numColumns - 1, WIDTH, true, true);

		this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite,
				"label.column.type");

		this.lengthText = CompositeFactory.createNumText(this, composite,
				"label.column.length", 1, 30, false);
		this.lengthText.setEnabled(false);

		this.decimalText = CompositeFactory.createNumText(this, composite,
				"label.column.decimal", 1, 30, false);
		this.decimalText.setEnabled(false);

		if (PostgresDBManager.ID.equals(this.diagram.getDatabase())) {
			CompositeFactory.filler(composite, 1);

			Composite typeOptionComposite = new Composite(composite, SWT.NONE);
			GridData gridData = new GridData();
			gridData.horizontalSpan = this.getCompositeNumColumns() - 1;
			typeOptionComposite.setLayoutData(gridData);

			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			typeOptionComposite.setLayout(layout);

			this.arrayCheck = CompositeFactory.createCheckbox(this,
					typeOptionComposite, "label.column.array", true);
			this.arrayCheck.setEnabled(true);
			this.arrayDimensionText = CompositeFactory.createNumText(this,
					typeOptionComposite, "label.column.array.dimension", 1, 30,
					false);
			this.arrayDimensionText.setEnabled(false);

			this.arrayCheck.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					arrayDimensionText.setEnabled(arrayCheck.getSelection());

					super.widgetSelected(e);
				}
			});

		}

		if (MySQLDBManager.ID.equals(this.diagram.getDatabase())) {
			CompositeFactory.filler(composite, 1);

			Composite childComposite = CompositeFactory.createChildComposite(
					composite, 5, 3);

			this.unsignedCheck = CompositeFactory.createCheckbox(this,
					childComposite, "label.column.unsigned", true);
			this.unsignedCheck.setEnabled(false);

			this.zerofillCheck = CompositeFactory.createCheckbox(this,
					childComposite, "label.column.zerofill", false);
			this.zerofillCheck.setEnabled(false);

			this.binaryCheck = CompositeFactory.createCheckbox(this,
					childComposite, "label.column.binary", false);
			this.binaryCheck.setEnabled(false);

			CompositeFactory.filler(composite, 1);

			childComposite = CompositeFactory.createChildComposite(composite,
					5, 3);
			CompositeFactory.createLabel(childComposite,
					"label.column.type.enum.set", 1, -1, true, true);
			this.argsText = CompositeFactory.createText(this, childComposite,
					null, 1, false, false);
			this.argsText.setEnabled(false);
		}

		if (OracleDBManager.ID.equals(this.diagram.getDatabase())) {
			CompositeFactory.filler(composite, 1);

			Composite childComposite = CompositeFactory.createChildComposite(
					composite, 5, 2);

			this.byteSemanticsRadio = CompositeFactory.createRadio(this,
					childComposite, "label.column.byte", 1, true);
			this.byteSemanticsRadio.setEnabled(false);
			this.byteSemanticsRadio.setSelection(true);

			this.charSemanticsRadio = CompositeFactory.createRadio(this,
					childComposite, "label.column.char");
			this.charSemanticsRadio.setEnabled(false);
		}

		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.column.description", -1, 100, numColumns - 1, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final protected void setData() {
		this.initData();

		if (!this.add) {
			this.setWordData();
		}

		this.setEnabledBySqlType();
	}

	protected void initData() {
	}

	protected void setData(String physicalName, String logicalName,
			SqlType sqlType, TypeData typeData, String description) {

		this.physicalNameText.setText(Format.toString(physicalName));
		this.logicalNameText.setText(Format.toString(logicalName));
		this.oldPhysicalName = physicalNameText.getText();

		if (sqlType != null) {
			String database = this.diagram.getDatabase();

			if (sqlType.getAlias(database) != null) {
				this.typeCombo.setText(sqlType.getAlias(database));
			}

			if (!sqlType.isNeedLength(database)) {
				this.lengthText.setEnabled(false);
			}
			if (!sqlType.isNeedDecimal(database)) {
				this.decimalText.setEnabled(false);
			}

			if (this.unsignedCheck != null && !sqlType.isNumber()) {
				this.unsignedCheck.setEnabled(false);
			}

			if (this.zerofillCheck != null && !sqlType.isNumber()) {
				this.zerofillCheck.setEnabled(false);
			}

			if (this.binaryCheck != null && !sqlType.isFullTextIndexable()) {
				this.binaryCheck.setEnabled(false);
			}

			if (this.argsText != null) {
				if (sqlType.doesNeedArgs()) {
					argsText.setEnabled(true);
				} else {
					argsText.setEnabled(false);
				}
			}

		} else {
			this.lengthText.setEnabled(false);
			this.decimalText.setEnabled(false);
			if (this.unsignedCheck != null) {
				this.unsignedCheck.setEnabled(false);
			}
			if (this.zerofillCheck != null) {
				this.zerofillCheck.setEnabled(false);
			}
			if (this.binaryCheck != null) {
				this.binaryCheck.setEnabled(false);
			}
			if (this.argsText != null) {
				this.argsText.setEnabled(false);
			}
		}

		this.lengthText.setText(Format.toString(typeData.getLength()));
		this.decimalText.setText(Format.toString(typeData.getDecimal()));

		if (this.arrayDimensionText != null) {
			this.arrayCheck.setSelection(typeData.isArray());
			this.arrayDimensionText.setText(Format.toString(typeData
					.getArrayDimension()));
			this.arrayDimensionText.setEnabled(this.arrayCheck.getSelection());
		}

		if (this.unsignedCheck != null) {
			this.unsignedCheck.setSelection(typeData.isUnsigned());
		}

		if (this.zerofillCheck != null) {
			this.zerofillCheck.setSelection(typeData.isZerofill());
		}

		if (this.binaryCheck != null) {
			this.binaryCheck.setSelection(typeData.isBinary());
		}

		if (this.argsText != null) {
			this.argsText.setText(Format.null2blank(typeData.getArgs()));
		}

		this.descriptionText.setText(Format.toString(description));

		if (this.byteSemanticsRadio != null) {
			boolean charSemantics = typeData.isCharSemantics();
			this.byteSemanticsRadio.setSelection(!charSemantics);
			this.charSemanticsRadio.setSelection(charSemantics);
		}
	}

	protected SqlType getSelectedType() {
		String database = this.diagram.getDatabase();

		SqlType selectedType = SqlType.valueOf(database,
				this.typeCombo.getText());

		return selectedType;
	}

	protected void setEnabledBySqlType() {
		String database = this.diagram.getDatabase();

		SqlType selectedType = SqlType.valueOf(database,
				this.typeCombo.getText());

		if (selectedType != null) {
			if (!selectedType.isNeedLength(database)) {
				this.lengthText.setEnabled(false);
			} else {
				this.lengthText.setEnabled(true);
			}

			if (!selectedType.isNeedDecimal(database)) {
				this.decimalText.setEnabled(false);
			} else {
				this.decimalText.setEnabled(true);
			}

			if (this.unsignedCheck != null) {
				if (!selectedType.isNumber()) {
					this.unsignedCheck.setEnabled(false);
				} else {
					this.unsignedCheck.setEnabled(true);
				}
			}

			if (this.zerofillCheck != null) {
				if (!selectedType.isNumber()) {
					this.zerofillCheck.setEnabled(false);
				} else {
					this.zerofillCheck.setEnabled(true);
				}
			}

			if (this.binaryCheck != null) {
				if (!selectedType.isFullTextIndexable()) {
					this.binaryCheck.setEnabled(false);
				} else {
					this.binaryCheck.setEnabled(true);
				}
			}

			if (this.argsText != null) {
				if (selectedType.doesNeedArgs()) {
					this.argsText.setEnabled(true);
				} else {
					this.argsText.setEnabled(false);
				}
			}

			if (this.charSemanticsRadio != null) {
				if (selectedType.isNeedCharSemantics(database)) {
					this.byteSemanticsRadio.setEnabled(true);
					this.charSemanticsRadio.setEnabled(true);

				} else {
					this.byteSemanticsRadio.setEnabled(false);
					this.charSemanticsRadio.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.typeCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				setEnabledBySqlType();
			}

		});

		this.physicalNameText.addFocusListener(new FocusAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if (logicalNameText.getText().equals("")) {
					logicalNameText.setText(physicalNameText.getText());
				}
			}
		});

		this.physicalNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String logicalName = logicalNameText.getText();
				String physicalName = physicalNameText.getText();

				if (oldPhysicalName.equals(logicalName)
						|| logicalName.equals("")) {
					logicalNameText.setText(physicalName);
					oldPhysicalName = physicalName;
				}
			}
		});

		if (this.zerofillCheck != null) {
			this.zerofillCheck.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (zerofillCheck.getSelection()) {
						unsignedCheck.setSelection(true);
						unsignedCheck.setEnabled(false);

					} else {
						unsignedCheck.setSelection(false);
						unsignedCheck.setEnabled(true);
					}
				}
			});
		}

	}

	abstract protected void setWordData();

	private void initializeTypeCombo() {
		this.typeCombo.add("");

		String database = this.diagram.getDatabase();

		for (String alias : SqlType.getAliasList(database)) {
			this.typeCombo.add(alias);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		String text = physicalNameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			if (this.diagram.getDiagramContents().getSettings()
					.isValidatePhysicalName()) {
				return "error.column.physical.name.not.alphabet";
			}
		}

		String logicalName = this.logicalNameText.getText().trim();
		if (Check.isEmpty(text) && Check.isEmpty(logicalName)) {
			return "error.column.name.empty";
		}

		if (this.lengthText.isEnabled()) {

			text = this.lengthText.getText();

			if (text.equals("")) {
				return "error.column.length.empty";

			} else {
				try {
					int len = Integer.parseInt(text);
					if (len < 0) {
						return "error.column.length.zero";
					}

				} catch (NumberFormatException e) {
					return "error.column.length.degit";
				}
			}
		}

		if (this.decimalText.isEnabled()) {

			text = this.decimalText.getText();

			if (text.equals("")) {
				return "error.column.decimal.empty";

			} else {
				try {
					int len = Integer.parseInt(text);
					if (len < 0) {
						return "error.column.decimal.zero";
					}

				} catch (NumberFormatException e) {
					return "error.column.decimal.degit";
				}
			}
		}

		if (arrayDimensionText != null) {
			text = arrayDimensionText.getText();

			if (!text.equals("")) {
				try {
					int len = Integer.parseInt(text);
					if (len < 1) {
						return "error.column.array.dimension.one";
					}

				} catch (NumberFormatException e) {
					return "error.column.array.dimension.degit";
				}

			} else {
				if (this.arrayCheck.getSelection()) {
					return "error.column.array.dimension.one";
				}
			}
		}

		SqlType selectedType = SqlType.valueOf(diagram.getDatabase(),
				typeCombo.getText());

		if (selectedType != null && this.argsText != null) {
			text = argsText.getText();

			if (selectedType.doesNeedArgs()) {
				if (text.equals("")) {
					return "error.column.type.enum.set";
				}
			}
		}

		return null;
	}

}
