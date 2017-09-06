package org.insightech.er.editor.view.dialog.element.table.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class IndexDialog extends AbstractDialog {

	private Text tableNameText;

	private Text nameText;

	private Button addButton;

	private Button removeButton;

	private Button upButton;

	private Button downButton;

	private org.eclipse.swt.widgets.List allColumnList;

	private Table indexColumnList;

	private List<NormalColumn> selectedColumns;

	private List<NormalColumn> allColumns;

	private ERTable table;

	private Combo typeCombo;

	private Text descriptionText;

	private Button uniqueCheckBox;

	private Button fullTextCheckBox;

	private Index targetIndex;

	private Index resultIndex;

	private Map<Column, Button> descCheckBoxMap = new HashMap<Column, Button>();

	private Map<Column, TableEditor> columnCheckMap = new HashMap<Column, TableEditor>();

	public IndexDialog(Shell parentShell, Index targetIndex, ERTable table) {
		super(parentShell);

		this.targetIndex = targetIndex;
		this.table = table;
		this.allColumns = table.getExpandedColumns();
		this.selectedColumns = new ArrayList<NormalColumn>();
	}

	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.numColumns = 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createHeaderComposite(composite);
		this.createColumnListComposite(composite);

		this.nameText.setFocus();
	}

	private void createHeaderComposite(Composite parent) {
		Composite composite = CompositeFactory.createChildComposite(parent, 1,
				2);
		this.createCheckComposite(composite);

		this.tableNameText = CompositeFactory.createText(this, composite,
				"label.table.name", 1, -1, SWT.READ_ONLY | SWT.BORDER, false,
				true);
		this.nameText = CompositeFactory.createText(this, composite,
				"label.index.name", false, true);
		this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite,
				"label.index.type");

		this.initTypeCombo();

		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.description", -1, 100, 1, true);
	}

	private void initTypeCombo() {
		java.util.List<String> indexTypeList = DBManagerFactory.getDBManager(
				this.table.getDiagram()).getIndexTypeList(this.table);

		this.typeCombo.add("");

		for (String indexType : indexTypeList) {
			this.typeCombo.add(indexType);
		}
	}

	private void createCheckComposite(Composite composite) {
		Composite checkComposite = CompositeFactory.createChildComposite(
				composite, 2, 4);

		this.uniqueCheckBox = CompositeFactory.createCheckbox(this,
				checkComposite, "label.index.unique", false);

		DBManager dbManager = DBManagerFactory.getDBManager(this.table
				.getDiagram());

		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			this.fullTextCheckBox = CompositeFactory.createCheckbox(this,
					checkComposite, "label.index.fulltext", false);
		}
	}

	private void createColumnListComposite(Composite parent) {
		Composite composite = CompositeFactory.createChildComposite(parent, 220, 1,
				3);

		this.createAllColumnsGroup(composite);
		
		this.addButton = CompositeFactory.createAddButton(composite);
		
		this.createIndexColumnGroup(composite);
		
		this.removeButton = CompositeFactory.createRemoveButton(composite);
	}

	private void createAllColumnsGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);

		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		group.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.marginHeight = 10;

		group.setLayout(layout);

		group.setText(ResourceString.getResourceString("label.all.column.list"));

		this.allColumnList = new org.eclipse.swt.widgets.List(group, SWT.BORDER
				| SWT.V_SCROLL);

		GridData allColumnListGridData = new GridData();
		allColumnListGridData.widthHint = 150;
		allColumnListGridData.verticalAlignment = GridData.FILL;
		allColumnListGridData.grabExcessVerticalSpace = true;

		this.allColumnList.setLayoutData(allColumnListGridData);

		this.initializeAllList();
	}

	private void initializeAllList() {
		for (NormalColumn column : this.allColumns) {
			this.allColumnList.add(column.getPhysicalName());
		}
	}

	private void createIndexColumnGroup(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 20;
		gridLayout.marginHeight = 10;

		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString
				.getResourceString("label.index.column.list"));
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);

		this.initializeIndexColumnList(group);

		// indexColumnList = new List(group, SWT.BORDER | SWT.V_SCROLL);
		// indexColumnList.setLayoutData(gridData5);

		this.upButton = CompositeFactory.createUpButton(group);
		this.downButton = CompositeFactory.createDownButton(group);
	}

	private void initializeIndexColumnList(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		indexColumnList = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		indexColumnList.setHeaderVisible(true);
		indexColumnList.setLayoutData(gridData);
		indexColumnList.setLinesVisible(false);

		TableColumn tableColumn = new TableColumn(indexColumnList, SWT.CENTER);
		tableColumn.setWidth(150);
		tableColumn.setText(ResourceString
				.getResourceString("label.column.name"));

		if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(
				DBManager.SUPPORT_DESC_INDEX)) {
			TableColumn tableColumn1 = new TableColumn(indexColumnList,
					SWT.CENTER);
			tableColumn1.setWidth(50);
			tableColumn1.setText(ResourceString
					.getResourceString("label.order.desc"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		this.tableNameText.setText(Format.null2blank(this.table
				.getPhysicalName()));

		if (this.targetIndex != null) {
			this.tableNameText.setText(Format.null2blank(this.targetIndex
					.getTable().getPhysicalName()));

			this.nameText.setText(this.targetIndex.getName());

			this.descriptionText.setText(Format.null2blank(this.targetIndex
					.getDescription()));

			if (!Check.isEmpty(this.targetIndex.getType())) {
				boolean selected = false;

				for (int i = 0; i < this.typeCombo.getItemCount(); i++) {
					if (this.typeCombo.getItem(i).equals(
							this.targetIndex.getType())) {
						this.typeCombo.select(i);
						selected = true;
						break;
					}
				}

				if (!selected) {
					typeCombo.setText(this.targetIndex.getType());
				}
			}

			java.util.List<Boolean> descs = this.targetIndex.getDescs();
			int i = 0;

			for (NormalColumn column : this.targetIndex.getColumns()) {
				Boolean desc = Boolean.FALSE;

				if (descs.size() > i && descs.get(i) != null) {
					desc = descs.get(i);
				}

				this.addIndexColumn(column, desc);
				i++;
			}

			this.uniqueCheckBox.setSelection(!this.targetIndex.isNonUnique());

			DBManager dbManager = DBManagerFactory.getDBManager(table
					.getDiagram());
			if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
				this.fullTextCheckBox.setSelection(this.targetIndex
						.isFullText());
			}
		}
	}

	private void addIndexColumn(NormalColumn column, Boolean desc) {
		TableItem tableItem = new TableItem(this.indexColumnList, SWT.NONE);

		tableItem.setText(0, column.getPhysicalName());

		this.setTableEditor(column, tableItem, desc);

		this.selectedColumns.add(column);

	}

	private void setTableEditor(final NormalColumn normalColumn,
			TableItem tableItem, Boolean desc) {
		Button descCheckButton = new Button(this.indexColumnList, SWT.CHECK);
		descCheckButton.pack();

		if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(
				DBManager.SUPPORT_DESC_INDEX)) {

			TableEditor editor = new TableEditor(this.indexColumnList);

			editor.minimumWidth = descCheckButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(descCheckButton, tableItem, 1);

			this.columnCheckMap.put(normalColumn, editor);
		}

		this.descCheckBoxMap.put(normalColumn, descCheckButton);
		descCheckButton.setSelection(desc.booleanValue());
	}

	@Override
	protected void addListener() {
		this.upButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1 || index == 0) {
					return;
				}

				changeColumn(index - 1, index);
				indexColumnList.setSelection(index - 1);
			}

		});

		this.downButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1 || index == indexColumnList.getItemCount() - 1) {
					return;
				}

				changeColumn(index, index + 1);
				indexColumnList.setSelection(index + 1);
			}

		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = allColumnList.getSelectionIndex();

				if (index == -1) {
					return;
				}

				NormalColumn column = allColumns.get(index);
				if (selectedColumns.contains(column)) {
					return;
				}

				addIndexColumn(column, Boolean.FALSE);

				validate();
			}

		});

		this.removeButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1) {
					return;
				}

				indexColumnList.remove(index);
				NormalColumn column = selectedColumns.remove(index);
				descCheckBoxMap.remove(column);

				disposeCheckBox(column);

				for (int i = index; i < indexColumnList.getItemCount(); i++) {
					column = selectedColumns.get(i);

					Button descCheckBox = descCheckBoxMap.get(column);
					boolean desc = descCheckBox.getSelection();
					disposeCheckBox(column);

					TableItem tableItem = indexColumnList.getItem(i);
					setTableEditor(column, tableItem, desc);
				}

				validate();
			}

		});

	}

	public void changeColumn(int index1, int index2) {
		NormalColumn column1 = selectedColumns.remove(index1);
		NormalColumn column2 = null;

		if (index1 < index2) {
			column2 = selectedColumns.remove(index2 - 1);
			selectedColumns.add(index1, column2);
			selectedColumns.add(index2, column1);

		} else if (index1 > index2) {
			column2 = selectedColumns.remove(index2);
			selectedColumns.add(index1 - 1, column2);
			selectedColumns.add(index2, column1);
		}

		boolean desc1 = this.descCheckBoxMap.get(column1).getSelection();
		boolean desc2 = this.descCheckBoxMap.get(column2).getSelection();

		TableItem[] tableItems = indexColumnList.getItems();

		this.column2TableItem(column1, desc1, tableItems[index2]);
		this.column2TableItem(column2, desc2, tableItems[index1]);

	}

	private void column2TableItem(NormalColumn column, boolean desc,
			TableItem tableItem) {
		this.disposeCheckBox(column);

		tableItem.setText(0, column.getPhysicalName());

		this.setTableEditor(column, tableItem, new Boolean(desc));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		String text = nameText.getText();

		this.resultIndex = new Index(table, text,
				!this.uniqueCheckBox.getSelection(), this.typeCombo.getText(),
				null);
		this.resultIndex.setDescription(this.descriptionText.getText().trim());

		for (NormalColumn selectedColumn : selectedColumns) {
			Boolean desc = Boolean.valueOf(this.descCheckBoxMap.get(
					selectedColumn).getSelection());
			this.resultIndex.addColumn(selectedColumn, desc);
		}

		DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			this.resultIndex.setFullText(this.fullTextCheckBox.getSelection());
		}
	}

	public Index getResultIndex() {
		return this.resultIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		String text = nameText.getText().trim();

		if (text.equals("")) {
			return "error.index.name.empty";
		}

		if (!Check.isAlphabet(text)) {
			return "error.index.name.not.alphabet";
		}

		if (indexColumnList.getItemCount() == 0) {
			return "error.index.column.empty";
		}

		DBManager dbManager = DBManagerFactory.getDBManager(this.table
				.getDiagram());

		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			if (fullTextCheckBox.getSelection()) {
				for (NormalColumn indexColumn : selectedColumns) {
					if (!indexColumn.isFullTextIndexable()) {
						return "error.index.fulltext.impossible";
					}
				}
			}
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.index";
	}

	private void disposeCheckBox(Column column) {
		TableEditor oldEditor = this.columnCheckMap.get(column);

		if (oldEditor != null) {
			if (oldEditor.getEditor() != null) {
				oldEditor.getEditor().dispose();
			}
			oldEditor.dispose();
		}

		this.columnCheckMap.remove(column);
	}
}
