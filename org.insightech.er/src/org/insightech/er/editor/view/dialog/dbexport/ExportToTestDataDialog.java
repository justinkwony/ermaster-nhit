package org.insightech.er.editor.view.dialog.dbexport;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.testdata.ExportToTestDataManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.util.Check;

public class ExportToTestDataDialog extends AbstractExportDialog {

	private ContainerCheckedTreeViewer testDataTable;

	private Button formatSqlRadio;

	private Button formatDBUnitRadio;

	private Button formatDBUnitFlatXmlRadio;

	private Button formatDBUnitXlsRadio;

	private DirectoryText outputDirectoryText;

	private Combo fileEncodingCombo;

	private List<TestData> testDataList;

	private int targetIndex;

	public ExportToTestDataDialog(List<TestData> testDataList) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				testDataList, -1);
	}

	public ExportToTestDataDialog(Shell parentShell,
			List<TestData> testDataList, int targetIndex) {
		super(parentShell);

		// from TestDataManagementDialog
		// testDataList is different from
		// diagram.getDiagramContents().getTestDataList()
		this.testDataList = testDataList;
		this.targetIndex = targetIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		this.createTestDataTableGroup(parent);
		this.createFormatGroup(parent);
		this.createFileGroup(parent);
	}

	private void createTestDataTableGroup(Composite parent) {
		this.testDataTable = CompositeFactory.createCheckedTreeViewer(this,
				parent, 100, 3);
	}

	private void createFormatGroup(Composite parent) {
		Group group = CompositeFactory
				.createGroup(parent, "label.format", 3, 2);

		this.formatSqlRadio = CompositeFactory.createRadio(this, group,
				"label.sql", 2);
		this.formatDBUnitRadio = CompositeFactory.createRadio(this, group,
				"label.dbunit", 2);
		this.formatDBUnitFlatXmlRadio = CompositeFactory.createRadio(this,
				group, "label.dbunit.flat.xml", 2);
		this.formatDBUnitXlsRadio = CompositeFactory.createRadio(this, group,
				"label.dbunit.xls", 2);

		CompositeFactory.fillLine(group);

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(
				this.diagram.getEditor().getDefaultCharset(), this, group,
				"label.output.file.encoding", 1);
	}

	private void createFileGroup(Composite parent) {
		this.outputDirectoryText = CompositeFactory.createDirectoryText(this,
				parent, "label.output.dir", this.getBaseDir(), "");
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.formatSqlRadio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fileEncodingCombo.setEnabled(true);
			}
		});

		this.formatDBUnitRadio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fileEncodingCombo.setEnabled(true);
			}
		});

		this.formatDBUnitFlatXmlRadio
				.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						fileEncodingCombo.setEnabled(true);
					}
				});

		this.formatDBUnitXlsRadio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fileEncodingCombo.setEnabled(false);
			}
		});

	}

	@Override
	protected String getErrorMessage() {

		if (this.testDataTable.getCheckedElements().length == 0) {
			return "error.testdata.not.selected";
		}

		// if (this.outputDirectoryText.isBlank()) {
		// return "error.output.dir.is.empty";
		// }

		if (!Charset.isSupported(this.fileEncodingCombo.getText())) {
			return "error.file.encoding.is.not.supported";
		}

		return null;
	}

	@Override
	protected ExportWithProgressManager getExportWithProgressManager(
			ExportSetting exportSetting) {

		ExportTestDataSetting exportTestDataSetting = exportSetting
				.getExportTestDataSetting();

		if (this.formatSqlRadio.getSelection()) {
			exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_SQL);

		} else if (this.formatDBUnitRadio.getSelection()) {
			exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_DBUNIT);

		} else if (this.formatDBUnitFlatXmlRadio.getSelection()) {
			exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_FLAT_XML);

		} else if (this.formatDBUnitXlsRadio.getSelection()) {
			exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_XLS);

		}

		exportTestDataSetting.setExportFilePath(this.outputDirectoryText
				.getFilePath());
		exportTestDataSetting.setExportFileEncoding(this.fileEncodingCombo
				.getText());

		List<TestData> exportTestDataList = new ArrayList<TestData>();

		for (Object selectedNode : this.testDataTable.getCheckedElements()) {
			Object value = ((TreeNode) selectedNode).getValue();

			if (value instanceof TestData) {
				exportTestDataList.add((TestData) value);
			}
		}

		return new ExportToTestDataManager(exportTestDataSetting,
				exportTestDataList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		ExportTestDataSetting exportTestDataSetting = this.settings
				.getExportSetting().getExportTestDataSetting();

		setTestDataTable();

		if (this.targetIndex >= 0) {
			TreeNode rootNode = ((TreeNode[]) this.testDataTable.getInput())[0];
			for (TreeNode treeNode : rootNode.getChildren()) {
				if (treeNode.getValue() == this.testDataList
						.get(this.targetIndex)) {
					this.testDataTable.setChecked(treeNode, true);
				}
			}

		} else {
			this.testDataTable
					.setCheckedElements((TreeNode[]) this.testDataTable
							.getInput());
		}

		this.fileEncodingCombo.setEnabled(true);

		if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT) {
			this.formatDBUnitRadio.setSelection(true);

		} else if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
			this.formatDBUnitFlatXmlRadio.setSelection(true);

		} else if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_XLS) {
			this.formatDBUnitXlsRadio.setSelection(true);
			this.fileEncodingCombo.setEnabled(false);

		} else {
			this.formatSqlRadio.setSelection(true);

		}

		String outputDirectoryPath = exportTestDataSetting.getExportFilePath();

		if (Check.isEmpty(outputDirectoryPath)) {
			outputDirectoryPath = "testdata";
		}

		this.outputDirectoryText.setText(outputDirectoryPath);

		this.fileEncodingCombo.setText(exportTestDataSetting
				.getExportFileEncoding());
	}

	private void setTestDataTable() {
		List<TreeNode> treeNodeList = createTreeNodeList();

		TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList
				.size()]);
		this.testDataTable.setInput(treeNodes);
		this.testDataTable.expandAll();
	}

	protected List<TreeNode> createTreeNodeList() {
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

		TreeNode topNode = new TreeNode(new StringObjectModel(
				ResourceString.getResourceString("label.testdata")));
		treeNodeList.add(topNode);

		List<TreeNode> nodeList = new ArrayList<TreeNode>();

		for (TestData testData : this.testDataList) {
			TreeNode objectNode = new TreeNode(testData);
			objectNode.setParent(topNode);

			nodeList.add(objectNode);
		}

		topNode.setChildren(nodeList.toArray(new TreeNode[nodeList.size()]));

		return treeNodeList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID,
				ResourceString.getResourceString("label.button.export"), true);
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.testdata";
	}

}
