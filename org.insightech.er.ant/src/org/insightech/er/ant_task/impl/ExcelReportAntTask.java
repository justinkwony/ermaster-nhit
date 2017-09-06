package org.insightech.er.ant_task.impl;

import org.apache.tools.ant.BuildException;
import org.insightech.er.ant_task.ERMasterAntTaskBase;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager;
import org.insightech.er.editor.model.settings.export.ExportExcelSetting;
import org.insightech.er.util.Check;

public class ExcelReportAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	private boolean outputImage = true;

	private String template;

	private String templateFile;

	private boolean useLogicalNameAsSheetName = false;

	public void setUseLogicalNameAsSheetName(boolean useLogicalNameAsSheetName) {
		this.useLogicalNameAsSheetName = useLogicalNameAsSheetName;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setOutputImage(boolean outputImage) {
		this.outputImage = outputImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportWithProgressManager createExportManager(ERDiagram diagram)
			throws Exception {
		if (this.outputFile == null || this.outputFile.trim().equals("")) {
			throw new BuildException("outputFile attribute must be set!");
		}
		if (Check.isEmpty(template) && Check.isEmpty(templateFile)) {
			throw new BuildException(
					"Either template or templateFile attribute must be set!");
		}

		this.outputFile = this.getAbsolutePath(this.outputFile);

		this.log("Output image : " + this.outputImage);
		this.log("Use logical name as sheet name : "
				+ this.useLogicalNameAsSheetName);

		ExportExcelSetting exportExcelSetting = new ExportExcelSetting();
		exportExcelSetting.setExcelOutput(this.outputFile);
		exportExcelSetting
				.setUseLogicalNameAsSheet(this.useLogicalNameAsSheetName);
		exportExcelSetting.setPutERDiagramOnExcel(this.outputImage);

		if (!Check.isEmpty(this.template)) {
			this.log("Use registered template : " + this.template);

			if ("default_en".equals(this.template)) {
				exportExcelSetting.setUsedDefaultTemplateLang("en");

			} else if ("default_ja".equals(this.template)) {
				exportExcelSetting.setUsedDefaultTemplateLang("ja");

			} else if ("default_ko".equals(this.template)) {
				exportExcelSetting.setUsedDefaultTemplateLang("ko");

			} else {
				exportExcelSetting.setExcelTemplate(this.template);
			}

		} else {
			this.templateFile = this.getAbsolutePath(this.templateFile);

			this.log("Use template file : " + this.templateFile);
			exportExcelSetting.setExcelTemplatePath(this.templateFile);
		}

		return new ExportToExcelManager(exportExcelSetting);
	}

	@Override
	protected void logUsage() {
		this.log("<ermaster.reportExcel> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile     - The path of the input .erm file.");
		this.log("    * outputFile      - The path of the output excel file.");
		this.log("      template        - The template for the output excel file.");
		this.log("                      - The available values are \"default_en\", \"default_ja\", or the names of custom templates.");
		this.log("                      - Either template or templateFile attribute must be set.");
		this.log("      templateFile    - The path of the template file for the output excel file.");
		this.log("                      - Either template or templateFile attribute must be set.");
		this.log("      outputImage     - Boolean. Whether image is output on excel.");
		this.log("                        Default value is true.");
		this.log("      useLogicalNameAsSheetName");
		this.log("                      - Boolean. Whether the logical name is used for the seat name or not.");
		this.log("                        Default value is false.");
	}

	@Override
	protected void postProcess() {
		this.log("Output to : " + this.outputFile);
	}
	
	
}
