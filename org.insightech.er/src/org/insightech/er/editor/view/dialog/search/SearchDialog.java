package org.insightech.er.editor.view.dialog.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.controller.command.edit.ReplaceCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.search.ReplaceManager;
import org.insightech.er.editor.model.search.SearchManager;
import org.insightech.er.editor.model.search.SearchResult;
import org.insightech.er.editor.model.search.SearchResultRow;

public class SearchDialog extends Dialog {

	public static final int REPLACE_ID = 100;

	public static final int SEARCH_ALL_ID = 101;

	public static final int SEARCH_NEXT_ID = 102;

	private static final int NUM_COLUMNS = 4;

	private Button replaceButton;

	private Button allCheckBox;

	private Button wordCheckBox;

	private Button physicalWordNameCheckBox;

	private Button logicalWordNameCheckBox;

	private Button wordTypeCheckBox;

	private Button wordLengthCheckBox;

	private Button wordDecimalCheckBox;

	private Button wordDescriptionCheckBox;

	private Button tableCheckBox;

	private Button physicalTableNameCheckBox;

	private Button logicalTableNameCheckBox;

	private Button physicalColumnNameCheckBox;

	private Button logicalColumnNameCheckBox;

	private Button columnTypeCheckBox;

	private Button columnLengthCheckBox;

	private Button columnDecimalCheckBox;

	private Button columnDefaultValueCheckBox;

	private Button columnDescriptionCheckBox;

	private Button columnGroupNameCheckBox;

	private Button groupCheckBox;

	private Button groupNameCheckBox;

	private Button physicalGroupColumnNameCheckBox;

	private Button logicalGroupColumnNameCheckBox;

	private Button groupColumnTypeCheckBox;

	private Button groupColumnLengthCheckBox;

	private Button groupColumnDecimalCheckBox;

	private Button groupColumnDefaultValueCheckBox;

	private Button groupColumnDescriptionCheckBox;

	private Button modelPropertiesCheckBox;

	private Button indexCheckBox;

	private Button relationCheckBox;

	private Button noteCheckBox;

	private Combo keywordCombo;

	private Combo replaceCombo;

	private Table resultTable;

	private GraphicalViewer viewer;

	private ERDiagram diagram;

	private SearchManager searchManager;

	private SearchResult searchResult;

	private boolean all;

	private TabFolder tabFolder;

	public SearchDialog(Shell parentShell, GraphicalViewer viewer,
			ERDiagram diagram) {
		super(parentShell);

		this.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		this.setBlockOnOpen(false);

		this.viewer = viewer;
		this.diagram = diagram;

		this.searchManager = new SearchManager(this.diagram);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				ResourceString.getResourceString("dialog.title.search"));

		Composite composite = null;
		composite = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout();
		this.initLayout(layout);
		composite.setLayout(layout);

		this.initialize(composite);

		return composite;
	}

	protected void initLayout(GridLayout layout) {
		layout.numColumns = 2;
		layout.verticalSpacing = 15;
		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginBottom = 15;
		layout.marginTop = 20;
	}

	private void initialize(Composite parent) {
		this.keywordCombo = CompositeFactory.createCombo(null, parent,
				"label.search.keyword", 1);

		this.replaceCombo = CompositeFactory.createCombo(null, parent,
				"label.search.replace.word", 1);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;

		this.tabFolder = new TabFolder(parent, SWT.NONE);
		this.tabFolder.setLayoutData(gridData);

		createRegionGroup(this.tabFolder);
		createResultGroup(this.tabFolder);

		this.selectAllCheckBox(true);
	}

	private void createRegionGroup(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(ResourceString.getResourceString("label.search.range"));

		GridLayout layout = new GridLayout();
		layout.numColumns = NUM_COLUMNS;
		layout.verticalSpacing = 0;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginBottom = 10;
		layout.marginTop = 10;

		Composite group = new Composite(tabFolder, SWT.NONE);
		group.setLayout(layout);

		allCheckBox = new Button(group, SWT.CHECK);
		allCheckBox.setText(ResourceString
				.getResourceString("label.search.range.all"));

		GridData allCheckBoxGridData = new GridData();
		allCheckBoxGridData.horizontalSpan = NUM_COLUMNS;
		allCheckBoxGridData.horizontalAlignment = GridData.FILL;
		allCheckBoxGridData.grabExcessHorizontalSpace = true;

		allCheckBox.setLayoutData(allCheckBoxGridData);

		this.allCheckBox.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAllCheckBox(allCheckBox.getSelection());
			}
		});

		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.verticalIndent = 15;

		wordCheckBox = new Button(group, SWT.CHECK);
		wordCheckBox.setLayoutData(gridData);
		wordCheckBox.setText(ResourceString
				.getResourceString("label.search.range.word"));
		this.wordCheckBox.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectWordCheckBox(wordCheckBox.getSelection());
			}
		});

		this.createWordCheckboxGroup(group);

		tableCheckBox = new Button(group, SWT.CHECK);
		tableCheckBox.setLayoutData(gridData);
		tableCheckBox.setText(ResourceString
				.getResourceString("label.search.range.table"));
		this.tableCheckBox.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectTableCheckBox(tableCheckBox.getSelection());
			}
		});

		this.createTableCheckboxGroup(group);

		groupCheckBox = new Button(group, SWT.CHECK);
		groupCheckBox.setLayoutData(gridData);
		groupCheckBox.setText(ResourceString
				.getResourceString("label.search.range.group"));
		this.groupCheckBox.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectGroupCheckBox(groupCheckBox.getSelection());
			}
		});

		this.createGroupCheckboxGroup(group);

		CompositeFactory.fillLine(group);

		modelPropertiesCheckBox = new Button(group, SWT.CHECK);
		modelPropertiesCheckBox.setText(ResourceString
				.getResourceString("label.search.range.model.property"));
		indexCheckBox = new Button(group, SWT.CHECK);
		indexCheckBox.setText(ResourceString
				.getResourceString("label.search.range.index"));
		relationCheckBox = new Button(group, SWT.CHECK);
		relationCheckBox.setText(ResourceString
				.getResourceString("label.search.range.relation"));
		noteCheckBox = new Button(group, SWT.CHECK);
		noteCheckBox.setText(ResourceString
				.getResourceString("label.search.range.note"));

		tabItem.setControl(group);
	}

	private void createWordCheckboxGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = NUM_COLUMNS - 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		GridLayout layout = new GridLayout();
		layout.numColumns = 4;

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(layout);
		group.setLayoutData(gridData);

		physicalWordNameCheckBox = new Button(group, SWT.CHECK);
		physicalWordNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.28"));

		logicalWordNameCheckBox = new Button(group, SWT.CHECK);
		logicalWordNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.29"));

		wordTypeCheckBox = new Button(group, SWT.CHECK);
		wordTypeCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.30"));

		wordLengthCheckBox = new Button(group, SWT.CHECK);
		wordLengthCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.31"));

		wordDecimalCheckBox = new Button(group, SWT.CHECK);
		wordDecimalCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.32"));

		wordDescriptionCheckBox = new Button(group, SWT.CHECK);
		wordDescriptionCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.33"));
	}

	private void createTableCheckboxGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = NUM_COLUMNS - 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);

		physicalTableNameCheckBox = new Button(group, SWT.CHECK);
		physicalTableNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.11"));

		logicalTableNameCheckBox = new Button(group, SWT.CHECK);
		logicalTableNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.12"));

		columnGroupNameCheckBox = new Button(group, SWT.CHECK);
		columnGroupNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.20"));

		new Label(group, SWT.NONE);

		physicalColumnNameCheckBox = new Button(group, SWT.CHECK);
		physicalColumnNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.13"));

		logicalColumnNameCheckBox = new Button(group, SWT.CHECK);
		logicalColumnNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.14"));

		columnTypeCheckBox = new Button(group, SWT.CHECK);
		columnTypeCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.15"));

		columnLengthCheckBox = new Button(group, SWT.CHECK);
		columnLengthCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.16"));

		columnDecimalCheckBox = new Button(group, SWT.CHECK);
		columnDecimalCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.17"));

		columnDefaultValueCheckBox = new Button(group, SWT.CHECK);
		columnDefaultValueCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.18"));

		columnDescriptionCheckBox = new Button(group, SWT.CHECK);
		columnDescriptionCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.19"));
	}

	private void createGroupCheckboxGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = NUM_COLUMNS - 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);

		groupNameCheckBox = new Button(group, SWT.CHECK);
		groupNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.20"));

		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);

		physicalGroupColumnNameCheckBox = new Button(group, SWT.CHECK);
		physicalGroupColumnNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.13"));

		logicalGroupColumnNameCheckBox = new Button(group, SWT.CHECK);
		logicalGroupColumnNameCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.14"));

		groupColumnTypeCheckBox = new Button(group, SWT.CHECK);
		groupColumnTypeCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.15"));

		groupColumnLengthCheckBox = new Button(group, SWT.CHECK);
		groupColumnLengthCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.16"));

		groupColumnDecimalCheckBox = new Button(group, SWT.CHECK);
		groupColumnDecimalCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.17"));

		groupColumnDefaultValueCheckBox = new Button(group, SWT.CHECK);
		groupColumnDefaultValueCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.18"));

		groupColumnDescriptionCheckBox = new Button(group, SWT.CHECK);
		groupColumnDescriptionCheckBox.setText(ResourceString
				.getResourceString("search.result.row.name.19"));
	}

	private void selectAllCheckBox(boolean checked) {
		allCheckBox.setSelection(checked);

		this.selectWordCheckBox(checked);
		wordCheckBox.setEnabled(!checked);

		this.selectTableCheckBox(checked);
		tableCheckBox.setEnabled(!checked);

		modelPropertiesCheckBox.setSelection(checked);
		modelPropertiesCheckBox.setEnabled(!checked);
		indexCheckBox.setSelection(checked);
		indexCheckBox.setEnabled(!checked);
		relationCheckBox.setSelection(checked);
		relationCheckBox.setEnabled(!checked);
		noteCheckBox.setSelection(checked);
		noteCheckBox.setEnabled(!checked);

		this.selectGroupCheckBox(checked);
		groupCheckBox.setEnabled(!checked);
	}

	private void selectWordCheckBox(boolean checked) {
		wordCheckBox.setSelection(checked);
		physicalWordNameCheckBox.setSelection(checked);
		logicalWordNameCheckBox.setSelection(checked);
		wordTypeCheckBox.setSelection(checked);
		wordLengthCheckBox.setSelection(checked);
		wordDecimalCheckBox.setSelection(checked);
		wordDescriptionCheckBox.setSelection(checked);

		physicalWordNameCheckBox.setEnabled(!checked);
		logicalWordNameCheckBox.setEnabled(!checked);
		wordTypeCheckBox.setEnabled(!checked);
		wordLengthCheckBox.setEnabled(!checked);
		wordDecimalCheckBox.setEnabled(!checked);
		wordDescriptionCheckBox.setEnabled(!checked);
	}

	private void selectTableCheckBox(boolean checked) {
		tableCheckBox.setSelection(checked);
		physicalTableNameCheckBox.setSelection(checked);
		logicalTableNameCheckBox.setSelection(checked);
		physicalColumnNameCheckBox.setSelection(checked);
		logicalColumnNameCheckBox.setSelection(checked);
		columnTypeCheckBox.setSelection(checked);
		columnLengthCheckBox.setSelection(checked);
		columnDecimalCheckBox.setSelection(checked);
		columnDefaultValueCheckBox.setSelection(checked);
		columnDescriptionCheckBox.setSelection(checked);
		columnGroupNameCheckBox.setSelection(checked);

		physicalTableNameCheckBox.setEnabled(!checked);
		logicalTableNameCheckBox.setEnabled(!checked);
		physicalColumnNameCheckBox.setEnabled(!checked);
		logicalColumnNameCheckBox.setEnabled(!checked);
		columnTypeCheckBox.setEnabled(!checked);
		columnLengthCheckBox.setEnabled(!checked);
		columnDecimalCheckBox.setEnabled(!checked);
		columnDefaultValueCheckBox.setEnabled(!checked);
		columnDescriptionCheckBox.setEnabled(!checked);
		columnGroupNameCheckBox.setEnabled(!checked);
	}

	private void selectGroupCheckBox(boolean checked) {
		groupCheckBox.setSelection(checked);
		groupNameCheckBox.setSelection(checked);
		physicalGroupColumnNameCheckBox.setSelection(checked);
		logicalGroupColumnNameCheckBox.setSelection(checked);
		groupColumnTypeCheckBox.setSelection(checked);
		groupColumnLengthCheckBox.setSelection(checked);
		groupColumnDecimalCheckBox.setSelection(checked);
		groupColumnDefaultValueCheckBox.setSelection(checked);
		groupColumnDescriptionCheckBox.setSelection(checked);

		groupNameCheckBox.setEnabled(!checked);
		physicalGroupColumnNameCheckBox.setEnabled(!checked);
		logicalGroupColumnNameCheckBox.setEnabled(!checked);
		groupColumnTypeCheckBox.setEnabled(!checked);
		groupColumnLengthCheckBox.setEnabled(!checked);
		groupColumnDecimalCheckBox.setEnabled(!checked);
		groupColumnDefaultValueCheckBox.setEnabled(!checked);
		groupColumnDescriptionCheckBox.setEnabled(!checked);
	}

	private void initKeywordCombo() {
		this.keywordCombo.removeAll();

		for (String str : SearchManager.getKeywordList()) {
			this.keywordCombo.add(str);
		}
	}

	private void initReplaceWordCombo() {
		this.replaceCombo.removeAll();

		for (String str : ReplaceManager.getReplaceWordList()) {
			this.replaceCombo.add(str);
		}
	}

	private void createResultGroup(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(ResourceString.getResourceString("label.search.result"));

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		
		this.resultTable = new Table(tabFolder, SWT.NONE | SWT.FULL_SELECTION
				| SWT.MULTI);
		this.resultTable.setHeaderVisible(true);
		this.resultTable.setLayoutData(gridData);
		this.resultTable.setLinesVisible(true);

		this.resultTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = resultTable.getSelectionIndex();

				if (index == -1) {
					replaceButton.setEnabled(false);
					replaceCombo.setEnabled(false);

				} else {
					replaceButton.setEnabled(true);
					replaceCombo.setEnabled(true);

					SearchResultRow searchResultRow = searchResult.getRows()
							.get(index);

					Object object = searchResultRow.getTargetNode();

					if (object != null) {
						focus(object);
					}
				}
			}
		});

		TableColumn tableColumn0 = new TableColumn(resultTable, SWT.LEFT);
		tableColumn0.setWidth(250);
		tableColumn0.setText(ResourceString
				.getResourceString("label.search.result.table.path"));
		tableColumn0.addSelectionListener(new SearchResultSortListener(
				SearchResult.SORT_TYPE_PATH));

		TableColumn tableColumn1 = new TableColumn(resultTable, SWT.LEFT);
		tableColumn1.setWidth(100);
		tableColumn1.setText(ResourceString
				.getResourceString("label.search.result.table.type"));
		tableColumn1.addSelectionListener(new SearchResultSortListener(
				SearchResult.SORT_TYPE_TYPE));

		TableColumn tableColumn2 = new TableColumn(resultTable, SWT.LEFT);
		tableColumn2.setWidth(200);
		tableColumn2.setText(ResourceString
				.getResourceString("label.search.result.table.name"));
		tableColumn2.addSelectionListener(new SearchResultSortListener(
				SearchResult.SORT_TYPE_NAME));

		TableColumn tableColumn3 = new TableColumn(this.resultTable, SWT.LEFT);
		tableColumn3.setWidth(200);
		tableColumn3.setText(ResourceString
				.getResourceString("label.search.result.table.value"));
		tableColumn3.addSelectionListener(new SearchResultSortListener(
				SearchResult.SORT_TYPE_VALUE));

		tabItem.setControl(this.resultTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.replaceButton = createButton(
				parent,
				REPLACE_ID,
				ResourceString.getResourceString("label.search.replace.button"),
				false);

		createButton(parent, SEARCH_ALL_ID,
				ResourceString.getResourceString("label.search.all.button"),
				false);

		// createButton(parent, SEARCH_NEXT_ID, ResourceString
		// .getResourceString("label.search.next.button"), true);

		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);

		this.replaceButton.setEnabled(false);
		this.replaceCombo.setEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			this.setReturnCode(buttonId);
			this.close();

		} else if (buttonId == SEARCH_NEXT_ID || buttonId == SEARCH_ALL_ID) {
			this.tabFolder.setSelection(1);

			this.all = false;

			if (buttonId == SEARCH_ALL_ID) {
				this.all = true;
			}

			String keyword = this.keywordCombo.getText();
			this.searchResult = this.searchManager.search(keyword, this.all,
					this.physicalWordNameCheckBox.getSelection(),
					this.logicalWordNameCheckBox.getSelection(),
					this.wordTypeCheckBox.getSelection(),
					this.wordLengthCheckBox.getSelection(),
					this.wordDecimalCheckBox.getSelection(),
					this.wordDescriptionCheckBox.getSelection(),
					this.physicalTableNameCheckBox.getSelection(),
					this.logicalTableNameCheckBox.getSelection(),
					this.physicalColumnNameCheckBox.getSelection(),
					this.logicalColumnNameCheckBox.getSelection(),
					this.columnTypeCheckBox.getSelection(),
					this.columnLengthCheckBox.getSelection(),
					this.columnDecimalCheckBox.getSelection(),
					this.columnDefaultValueCheckBox.getSelection(),
					this.columnDescriptionCheckBox.getSelection(),
					this.columnGroupNameCheckBox.getSelection(),
					this.indexCheckBox.getSelection(),
					this.noteCheckBox.getSelection(),
					this.modelPropertiesCheckBox.getSelection(),
					this.relationCheckBox.getSelection(),
					this.groupNameCheckBox.getSelection(),
					this.physicalGroupColumnNameCheckBox.getSelection(),
					this.logicalGroupColumnNameCheckBox.getSelection(),
					this.groupColumnTypeCheckBox.getSelection(),
					this.groupColumnLengthCheckBox.getSelection(),
					this.groupColumnDecimalCheckBox.getSelection(),
					this.groupColumnDefaultValueCheckBox.getSelection(),
					this.groupColumnDescriptionCheckBox.getSelection());

			this.showSearchResult();

			this.initKeywordCombo();
			this.keywordCombo.setText(keyword);

			return;

		} else if (buttonId == REPLACE_ID) {
			// this.tabFolder.setSelection(1);

			List<SearchResultRow> replaceRows = getReplaceRows();

			if (replaceRows.isEmpty()) {
				return;
			}

			CompoundCommand command = new CompoundCommand();

			String keyword = this.keywordCombo.getText();
			String replaceWord = this.replaceCombo.getText();

			for (SearchResultRow row : replaceRows) {
				ReplaceCommand replaceCommand = new ReplaceCommand(
						this.diagram, row.getType(), row.getTarget(), keyword,
						replaceWord);
				command.add(replaceCommand);
			}

			this.viewer.getEditDomain().getCommandStack()
					.execute(command.unwrap());

			this.searchResult = this.searchManager.research();

			showSearchResult();

			this.initKeywordCombo();
			this.keywordCombo.setText(keyword);
			this.initReplaceWordCombo();
			this.replaceCombo.setText(replaceWord);

			this.replaceCombo.setEnabled(false);
			this.replaceButton.setEnabled(false);

			return;
		}

		super.buttonPressed(buttonId);
	}

	private void showSearchResult() {
		if (this.searchResult != null) {
			this.setResultRowData(this.searchResult.getRows());

			Object object = this.searchResult.getResultObject();
			if (object != null) {
				this.focus(object);
			}

		} else {
			this.resultTable.removeAll();
		}
	}

	private void focus(Object object) {
		EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(object);

		if (editPart != null) {
			this.viewer.select(editPart);
			this.viewer.reveal(editPart);
		}
	}

	private void setResultRowData(List<SearchResultRow> rows) {
		this.resultTable.removeAll();

		for (SearchResultRow row : rows) {
			String type = ResourceString
					.getResourceString("search.result.row.type."
							+ row.getType());
			String name = ResourceString
					.getResourceString("search.result.row.name."
							+ row.getType());

			TableItem tableItem = new TableItem(this.resultTable, SWT.NONE);

			String path = row.getPath();
			if (path == null) {
				path = type;
			}

			if (row.getPath() != null) {
				tableItem.setText(0, path);
			}

			tableItem.setText(1, type);
			tableItem.setText(2, name);
			tableItem.setText(3, row.getText());
		}
	}

	private List<SearchResultRow> getReplaceRows() {
		List<SearchResultRow> replaceRows = new ArrayList<SearchResultRow>();

		if (this.searchResult == null) {
			return replaceRows;
		}

		List<SearchResultRow> rows = this.searchResult.getRows();
		if (rows == null) {
			return replaceRows;
		}

		int[] indexes = this.resultTable.getSelectionIndices();
		if (indexes != null) {
			for (int i = 0; i < indexes.length; i++) {
				replaceRows.add(rows.get(indexes[i]));
			}
		}

		return replaceRows;
	}

	private class SearchResultSortListener extends SelectionAdapter {
		private int sortType;

		private SearchResultSortListener(int sortType) {
			this.sortType = sortType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (searchResult == null) {
				return;
			}

			searchResult.sort(sortType);

			showSearchResult();
		}

	}
}