package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.dbexport.ddl.ExportToDDLManager;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToDDLDialog extends AbstractExportDialog {

	private Combo environmentCombo;

	private FileText outputFileText;

	private Combo fileEncodingCombo;

	private Combo lineFeedCombo;

	// private Combo categoryCombo;
	private Label categoryLabel;

	private Button inlineTableComment;

	private Button inlineColumnComment;

	private Button dropTablespace;

	private Button dropSequence;

	private Button dropTrigger;

	private Button dropView;

	private Button dropIndex;

	private Button dropTable;

	private Button createTablespace;

	private Button createSequence;

	private Button createTrigger;

	private Button createView;

	private Button createIndex;

	private Button createTable;

	private Button createForeignKey;

	private Button createComment;

	private Button commentValueDescription;

	private Button commentValueLogicalName;

	private Button commentValueLogicalNameDescription;

	private Button commentReplaceLineFeed;

	private Text commentReplaceString;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		this.environmentCombo = CompositeFactory.createReadOnlyCombo(this,
				parent, "label.tablespace.environment", 2, -1);
		for (Environment environment : this.settings.getEnvironmentSetting()
				.getEnvironments()) {
			this.environmentCombo.add(environment.getName());
		}

		this.outputFileText = CompositeFactory.createFileText(true, this,
				parent, "label.output.file", this.getBaseDir(),
				this.getDefaultOutputFileName(".sql"), "*.sql");

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(
				this.diagram.getEditor().getDefaultCharset(), this, parent,
				"label.output.file.encoding", 2);

		this.lineFeedCombo = CompositeFactory.createReadOnlyCombo(this, parent,
				"label.line.feed.code", 2);
		this.lineFeedCombo.add(ExportDDLSetting.CRLF);
		this.lineFeedCombo.add(ExportDDLSetting.LF);

		CompositeFactory.createLabel(parent, "label.category");
		this.categoryLabel = CompositeFactory.createLabelAsValue(parent, "", 2);
		// this.categoryCombo = CompositeFactory.createReadOnlyCombo(this,
		// parent,
		// "label.category", 2, -1);
		// this.initCategoryCombo(this.categoryCombo);

		this.createCheckboxComposite(parent);

		this.createCommentComposite(parent);

		Composite checkboxArea = this.createCheckboxArea(parent, false);

		this.createOpenAfterSavedButton(checkboxArea, false, 3);
	}

	private void createCheckboxComposite(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);

		this.createDropCheckboxGroup(composite);
		this.createCreateCheckboxGroup(composite);
	}

	private void createDropCheckboxGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		group.setLayoutData(gridData);

		group.setText("DROP");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);

		this.dropTablespace = CompositeFactory.createCheckbox(this, group,
				"label.tablespace", false);
		this.dropSequence = CompositeFactory.createCheckbox(this, group,
				"label.sequence", false);
		this.dropTrigger = CompositeFactory.createCheckbox(this, group,
				"label.trigger", false);
		this.dropView = CompositeFactory.createCheckbox(this, group,
				"label.view", false);
		this.dropIndex = CompositeFactory.createCheckbox(this, group,
				"label.index", false);
		this.dropTable = CompositeFactory.createCheckbox(this, group,
				"label.table", false);
	}

	private void createCreateCheckboxGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		group.setLayoutData(gridData);

		group.setText("CREATE");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);

		this.createTablespace = CompositeFactory.createCheckbox(this, group,
				"label.tablespace", false);
		this.createSequence = CompositeFactory.createCheckbox(this, group,
				"label.sequence", false);
		this.createTrigger = CompositeFactory.createCheckbox(this, group,
				"label.trigger", false);
		this.createView = CompositeFactory.createCheckbox(this, group,
				"label.view", false);
		this.createIndex = CompositeFactory.createCheckbox(this, group,
				"label.index", false);
		this.createTable = CompositeFactory.createCheckbox(this, group,
				"label.table", false);
		this.createForeignKey = CompositeFactory.createCheckbox(this, group,
				"label.foreign.key", false);
		this.createComment = CompositeFactory.createCheckbox(this, group,
				"label.comment", false);
	}

	private void createCommentComposite(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);

		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginWidth = 0;
		composite.setLayout(compositeLayout);

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData);
		group.setText(ResourceString.getResourceString("label.comment"));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);

		GridData commentValueGridData = new GridData();
		commentValueGridData.horizontalSpan = 3;
		commentValueGridData.horizontalAlignment = GridData.FILL;
		commentValueGridData.grabExcessHorizontalSpace = true;

		Group commentValueGroup = new Group(group, SWT.NONE);
		commentValueGroup.setLayoutData(commentValueGridData);
		commentValueGroup.setText(ResourceString
				.getResourceString("label.comment.value"));

		GridLayout commentValueLayout = new GridLayout();
		commentValueLayout.numColumns = 1;
		commentValueGroup.setLayout(commentValueLayout);

		this.commentValueDescription = CompositeFactory.createRadio(this,
				commentValueGroup, "label.comment.value.description");
		this.commentValueLogicalName = CompositeFactory.createRadio(this,
				commentValueGroup, "label.comment.value.logical.name");
		this.commentValueLogicalNameDescription = CompositeFactory.createRadio(
				this, commentValueGroup,
				"label.comment.value.logical.name.description");

		this.commentReplaceLineFeed = CompositeFactory.createCheckbox(this,
				group, "label.comment.replace.line.feed", false);
		this.commentReplaceString = CompositeFactory.createText(this, group,
				"label.comment.replace.string", 1, 20, false, false);

		this.inlineTableComment = CompositeFactory.createCheckbox(this, group,
				"label.comment.inline.table", false, 4);
		this.inlineColumnComment = CompositeFactory.createCheckbox(this, group,
				"label.comment.inline.column", false, 4);
	}

	@Override
	protected String getErrorMessage() {
		if (isBlank(this.environmentCombo)) {
			return "error.tablespace.environment.empty";
		}

		if (this.outputFileText.isBlank()) {
			return "error.output.file.is.empty";
		}

		if (!Charset.isSupported(this.fileEncodingCombo.getText())) {
			return "error.file.encoding.is.not.supported";
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		ExportDDLSetting exportDDLSetting = this.settings.getExportSetting()
				.getExportDDLSetting();

		String outputFile = Format.null2blank(exportDDLSetting.getDdlOutput());

		if (Check.isEmpty(outputFile)) {
			outputFile = this.getDefaultOutputFilePath(".sql");
		}

		this.outputFileText.setText(FileUtils.getRelativeFilePath(
				this.getBaseDir(), outputFile));

		// this.setCategoryComboData(this.categoryCombo,
		// exportDDLSetting.getCategory());
		this.setCategoryData(this.categoryLabel);

		DDLTarget ddlTarget = exportDDLSetting.getDdlTarget();

		this.dropIndex.setSelection(ddlTarget.dropIndex);
		this.dropSequence.setSelection(ddlTarget.dropSequence);
		this.dropTable.setSelection(ddlTarget.dropTable);
		this.dropTablespace.setSelection(ddlTarget.dropTablespace);
		this.dropTrigger.setSelection(ddlTarget.dropTrigger);
		this.dropView.setSelection(ddlTarget.dropView);
		this.createComment.setSelection(ddlTarget.createComment);
		this.createForeignKey.setSelection(ddlTarget.createForeignKey);
		this.createIndex.setSelection(ddlTarget.createIndex);
		this.createSequence.setSelection(ddlTarget.createSequence);
		this.createTable.setSelection(ddlTarget.createTable);
		this.createTablespace.setSelection(ddlTarget.createTablespace);
		this.createTrigger.setSelection(ddlTarget.createTrigger);
		this.createView.setSelection(ddlTarget.createView);
		this.inlineColumnComment.setSelection(ddlTarget.inlineColumnComment);
		this.inlineTableComment.setSelection(ddlTarget.inlineTableComment);
		this.commentReplaceLineFeed
				.setSelection(ddlTarget.commentReplaceLineFeed);
		this.commentReplaceString.setText(Format
				.null2blank(ddlTarget.commentReplaceString));
		this.commentValueDescription
				.setSelection(ddlTarget.commentValueDescription);
		this.commentValueLogicalName
				.setSelection(ddlTarget.commentValueLogicalName);
		this.commentValueLogicalNameDescription
				.setSelection(ddlTarget.commentValueLogicalNameDescription);

		if (!ddlTarget.commentValueDescription
				&& !ddlTarget.commentValueLogicalName
				&& !ddlTarget.commentValueLogicalNameDescription) {
			this.commentValueDescription.setSelection(true);
		}

		this.environmentCombo.select(0);

		if (exportDDLSetting.getEnvironment() != null) {
			int index = this.settings.getEnvironmentSetting().getEnvironments()
					.indexOf(exportDDLSetting.getEnvironment());

			if (index != -1) {
				this.environmentCombo.select(index);
			}
		}

		if (!Check.isEmpty(exportDDLSetting.getSrcFileEncoding())) {
			this.fileEncodingCombo.setText(exportDDLSetting
					.getSrcFileEncoding());
		}

		if (!Check.isEmpty(exportDDLSetting.getLineFeed())) {
			this.lineFeedCombo.setText(exportDDLSetting.getLineFeed());
			
		} else {
			if ("\n".equals(System.getProperty("line.separator"))) {
				this.lineFeedCombo.setText(ExportDDLSetting.LF);
			} else {
				this.lineFeedCombo.setText(ExportDDLSetting.CRLF);
			}
		}

		this.openAfterSavedButton.setSelection(exportDDLSetting
				.isOpenAfterSaved());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.ddl";
	}

	@Override
	protected ExportWithProgressManager getExportWithProgressManager(
			ExportSetting exportSetting) throws InputException {
		ExportDDLSetting exportDDLSetting = exportSetting.getExportDDLSetting();

		String saveFilePath = this.outputFileText.getFilePath();

		// File outputFile = FileUtils.getFile(this.getProjectDir(),
		// saveFilePath);
		// File outputDir = outputFile.getParentFile();
		//
		// if (!outputDir.exists()) {
		// if (!Activator.showConfirmDialog(ResourceString.getResourceString(
		// "dialog.message.create.parent.dir",
		// new String[] { outputDir.getAbsolutePath() }))) {
		// throw new InputException();
		//
		// } else {
		// outputDir.mkdirs();
		// }
		// }

		exportDDLSetting.setDdlOutput(saveFilePath);
		exportDDLSetting.setOpenAfterSaved(this.openAfterSavedButton
				.getSelection());

		// exportDDLSetting.setCategory(this
		// .getSelectedCategory(this.categoryCombo));
		exportDDLSetting.setCategory(this.diagram.getCurrentCategory());

		int index = this.environmentCombo.getSelectionIndex();
		Environment environment = this.settings.getEnvironmentSetting()
				.getEnvironments().get(index);
		exportDDLSetting.setEnvironment(environment);

		exportDDLSetting.setSrcFileEncoding(this.fileEncodingCombo.getText());
		exportDDLSetting.setLineFeed(this.lineFeedCombo.getText());

		exportDDLSetting.setDdlTarget(this.createDDLTarget());

		return new ExportToDDLManager(exportDDLSetting);
	}

	private DDLTarget createDDLTarget() {
		DDLTarget ddlTarget = new DDLTarget();

		ddlTarget.dropTablespace = this.dropTablespace.getSelection();
		ddlTarget.dropSequence = this.dropSequence.getSelection();
		ddlTarget.dropTrigger = this.dropTrigger.getSelection();
		ddlTarget.dropView = this.dropView.getSelection();
		ddlTarget.dropIndex = this.dropIndex.getSelection();
		ddlTarget.dropTable = this.dropTable.getSelection();
		ddlTarget.createTablespace = this.createTablespace.getSelection();
		ddlTarget.createSequence = this.createSequence.getSelection();
		ddlTarget.createTrigger = this.createTrigger.getSelection();
		ddlTarget.createView = this.createView.getSelection();
		ddlTarget.createIndex = this.createIndex.getSelection();
		ddlTarget.createTable = this.createTable.getSelection();
		ddlTarget.createForeignKey = this.createForeignKey.getSelection();
		ddlTarget.createComment = this.createComment.getSelection();
		ddlTarget.inlineTableComment = this.inlineTableComment.getSelection();
		ddlTarget.inlineColumnComment = this.inlineColumnComment.getSelection();
		ddlTarget.commentReplaceLineFeed = this.commentReplaceLineFeed
				.getSelection();
		ddlTarget.commentReplaceString = this.commentReplaceString.getText();
		ddlTarget.commentValueDescription = this.commentValueDescription
				.getSelection();
		ddlTarget.commentValueLogicalName = this.commentValueLogicalName
				.getSelection();
		ddlTarget.commentValueLogicalNameDescription = this.commentValueLogicalNameDescription
				.getSelection();

		return ddlTarget;
	}

	@Override
	protected void perfomeOK() throws Exception {
		Validator validator = new Validator();

		List<ValidateResult> errorList = validator.validate(this.diagram);

		if (!errorList.isEmpty()) {
			ExportWarningDialog dialog = new ExportWarningDialog(
					this.getShell(), errorList);

			if (dialog.open() != IDialogConstants.OK_ID) {
				throw new InputException();
			}
		}

		super.perfomeOK();
	}

	@Override
	protected File openAfterSaved() {
		File file = FileUtils.getFile(this.getBaseDir(), this.settings
				.getExportSetting().getExportDDLSetting().getDdlOutput());

		return file;
	}

}
