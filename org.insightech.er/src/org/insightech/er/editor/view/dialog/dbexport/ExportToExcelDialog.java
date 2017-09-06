package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportExcelSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToExcelDialog extends AbstractExportDialog {

	// private static final String DEFAULT_IMAGE_EXTENTION = ".png";

	private Combo templateCombo;

	private FileText templateFileText;

	private FileText outputExcelFileText;

	// private FileText outputImageFileText;

	// private Combo categoryCombo;
	private Label categoryLabel;

	private MultiLineCheckbox useLogicalNameAsSheetNameButton;

	private MultiLineCheckbox outputImageButton;

	private Button selectTemplateFromRegistryRadio;

	private Button selectTemplateFromFilesRadio;

	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.numColumns = 3;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		this.outputExcelFileText = CompositeFactory.createFileText(true, this,
				parent, "label.output.excel.file", this.getBaseDir(),
				this.getDefaultOutputFileName(".xls"), "*.xls");

		CompositeFactory.createLabel(parent, "label.category");
		this.categoryLabel = CompositeFactory.createLabelAsValue(parent, "", 2);

		this.createTemplateGroup(parent);

		// this.categoryCombo = CompositeFactory.createReadOnlyCombo(this,
		// parent,
		// "label.category", 2);
		// this.initCategoryCombo(this.categoryCombo);

		Composite checkboxArea = this.createCheckboxArea(parent, false);

		this.outputImageButton = CompositeFactory.createMultiLineCheckbox(this,
				checkboxArea, "label.output.image.to.excel", false, 1);

		// CompositeFactory.createLabel(parent, "label.output.image.file");
		// this.outputImageFileText = new FileText(parent, this.getProjectDir(),
		// this.getDefaultOutputFileName(DEFAULT_IMAGE_EXTENTION),
		// new String[] { "*.png", "*.jpeg" });

		this.useLogicalNameAsSheetNameButton = CompositeFactory
				.createMultiLineCheckbox(this, checkboxArea,
						"label.use.logical.name.as.sheet.name", false, 1);

		this.createOpenAfterSavedButton(checkboxArea, false, 1);
	}

	private void createTemplateGroup(Composite parent) {
		Group group = CompositeFactory.createGroup(parent, "label.template", 3,
				2);

		this.selectTemplateFromRegistryRadio = CompositeFactory.createRadio(
				this, group, "label.select.from.registry", 2);
		this.templateCombo = CompositeFactory.createReadOnlyCombo(this, group,
				null, 2);
		this.initTemplateCombo();

		CompositeFactory.fillLine(group, 5);

		this.selectTemplateFromFilesRadio = CompositeFactory.createRadio(this,
				group, "label.select.from.file", 2);
		this.templateFileText = CompositeFactory.createFileText(false, this,
				group, null, this.getBaseDir(), null, "*.xls", false);
	}

	private void initTemplateCombo() {
		this.templateCombo.setVisibleItemCount(20);

		this.templateCombo.add(ResourceString
				.getResourceString("label.template.default.en"));
		this.templateCombo.add(ResourceString
				.getResourceString("label.template.default.ja"));
		this.templateCombo.add(ResourceString
				.getResourceString("label.template.default.ko"));

		List<String> fileNames = PreferenceInitializer
				.getAllExcelTemplateFiles();

		for (String fileName : fileNames) {
			File file = new File(
					PreferenceInitializer.getTemplatePath(fileName));
			if (file.exists()) {
				this.templateCombo.add(fileName);
			}
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.selectTemplateFromRegistryRadio
				.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						templateCombo.setEnabled(true);
						templateFileText.setEnabled(false);
					}
				});

		this.selectTemplateFromFilesRadio
				.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						templateCombo.setEnabled(false);
						templateFileText.setEnabled(true);
					}
				});
	}

	@Override
	protected String getErrorMessage() {
		// this.outputImageFileText.setEnabled(this.outputImageButton
		// .getSelection());

		if (this.selectTemplateFromRegistryRadio.getSelection()) {
			if (isBlank(this.templateCombo)) {
				return "error.template.is.empty";
			}

		} else {
			if (this.templateFileText.isBlank()) {
				return "error.template.is.empty";
			}
		}

		if (this.outputExcelFileText.isBlank()) {
			return "error.output.excel.file.is.empty";
		}

		// if (this.outputImageButton.getSelection()
		// && this.outputImageFileText.isBlank()) {
		// return "error.output.image.file.is.empty";
		// }

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		ExportExcelSetting exportExcelSetting = this.settings
				.getExportSetting().getExportExcelSetting();

		String outputExcel = Format.null2blank(exportExcelSetting
				.getExcelOutput());
		// String outputImage = Format.null2blank(exportExcelSetting
		// .getImageOutput());

		if ("".equals(outputExcel)) {
			outputExcel = this.getDefaultOutputFilePath(".xls");
		}

		// if ("".equals(outputImage)) {
		// outputImage = this
		// .getDefaultOutputFilePath(DEFAULT_IMAGE_EXTENTION);
		// }

		this.outputExcelFileText.setText(FileUtils.getRelativeFilePath(
				this.getBaseDir(), outputExcel));
		// this.outputImageFileText.setText(outputImage);

		// this.setCategoryComboData(this.categoryCombo,
		// exportExcelSetting.getCategory());
		this.setCategoryData(this.categoryLabel);

		this.useLogicalNameAsSheetNameButton.setSelection(exportExcelSetting
				.isUseLogicalNameAsSheet());
		this.outputImageButton.setSelection(exportExcelSetting
				.isPutERDiagramOnExcel());
		this.openAfterSavedButton.setSelection(exportExcelSetting
				.isOpenAfterSaved());

		this.setTemplateData(exportExcelSetting);

		String excelTemplatePath = exportExcelSetting.getExcelTemplatePath();

		if (!Check.isEmpty(excelTemplatePath)) {
			this.templateFileText.setText(excelTemplatePath);
			this.selectTemplateFromFilesRadio.setSelection(true);
			this.templateCombo.setEnabled(false);

		} else {
			this.selectTemplateFromRegistryRadio.setSelection(true);
			this.templateFileText.setEnabled(false);

		}
	}

	private void setTemplateData(ExportExcelSetting exportExcelSetting) {
		String lang = exportExcelSetting.getUsedDefaultTemplateLang();

		if ("en".equals(lang)) {
			this.templateCombo.select(0);

		} else if ("ja".equals(lang)) {
			this.templateCombo.select(1);

		} else if ("ko".equals(lang)) {
			this.templateCombo.select(2);

		} else {
			this.templateCombo.select(0);

			String template = exportExcelSetting.getExcelTemplate();

			for (int i = 2; i < this.templateCombo.getItemCount(); i++) {
				String item = this.templateCombo.getItem(i);
				if (item.equals(template)) {
					this.templateCombo.select(i);
					break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.excel";
	}

	@Override
	protected ExportWithProgressManager getExportWithProgressManager(
			ExportSetting exportSetting) throws Exception {

		ExportExcelSetting exportExcelSetting = exportSetting
				.getExportExcelSetting();

		String outputExcelFilePath = this.outputExcelFileText.getFilePath();

		// String outputImageFilePath = this.outputImageFileText.getFilePath();

		// this.outputExcelFile = new File(outputExcelFilePath);
		//
		// if (!outputExcelFile.isAbsolute()) {
		// outputExcelFile = new File(this.getProjectDir(),
		// outputExcelFilePath);
		// }
		//
		// File outputExcelDir = outputExcelFile.getParentFile();
		//
		// if (!outputExcelDir.exists()) {
		// if (!Activator.showConfirmDialog(ResourceString.getResourceString(
		// "dialog.message.create.parent.dir",
		// new String[] { outputExcelDir.getAbsolutePath() }))) {
		// throw new InputException();
		//
		// } else {
		// outputExcelDir.mkdirs();
		// }
		// }

		exportExcelSetting.setExcelOutput(outputExcelFilePath);
		// exportExcelSetting.setImageOutput(outputImageFilePath);

		exportExcelSetting
				.setUseLogicalNameAsSheet(this.useLogicalNameAsSheetNameButton
						.getSelection());
		exportExcelSetting.setPutERDiagramOnExcel(this.outputImageButton
				.getSelection());
		// exportExcelSetting.setCategory(this
		// .getSelectedCategory(this.categoryCombo));
		exportExcelSetting.setCategory(this.diagram.getCurrentCategory());
		exportExcelSetting.setOpenAfterSaved(this.openAfterSavedButton
				.getSelection());

		int templateIndex = this.templateCombo.getSelectionIndex();

		String template = null;

		if (templateIndex == 0) {
			exportExcelSetting.setUsedDefaultTemplateLang("en");
		} else if (templateIndex == 1) {
			exportExcelSetting.setUsedDefaultTemplateLang("ja");
		} else if (templateIndex == 2) {
			exportExcelSetting.setUsedDefaultTemplateLang("ko");
		} else {
			exportExcelSetting.setUsedDefaultTemplateLang(null);
			template = this.templateCombo.getText();
		}

		if (this.selectTemplateFromRegistryRadio.getSelection()) {
			exportExcelSetting.setExcelTemplate(template);

		} else {
			exportExcelSetting.setExcelTemplatePath(this.templateFileText
					.getFilePath());
		}

		return new ExportToExcelManager(exportExcelSetting);
	}

	@Override
	protected File openAfterSaved() {
		return FileUtils.getFile(this.getBaseDir(), this.settings
				.getExportSetting().getExportExcelSetting().getExcelOutput());
	}

	@Override
	protected boolean openWithExternalEditor() {
		return true;
	}

}
