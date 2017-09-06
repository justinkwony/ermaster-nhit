package org.insightech.er.ant_task.impl;

import org.insightech.er.ant_task.ERMasterAntTaskBase;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.settings.export.ExportHtmlSetting;

public class HtmlReportAntTask extends ERMasterAntTaskBase {

	private String outputDir;

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportWithProgressManager createExportManager(ERDiagram diagram)
			throws Exception {
		this.outputDir = getAbsolutePath(this.outputDir);

		ExportHtmlSetting exportHtmlSetting = new ExportHtmlSetting();
		exportHtmlSetting.setOutputDir(this.outputDir);
		exportHtmlSetting.setWithCategoryImage(true);
		exportHtmlSetting.setWithImage(true);

		return new ExportToHtmlManager(exportHtmlSetting);
	}

	@Override
	protected void logUsage() {
		this.log("<ermaster.htmlReport> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("      outputDir   - The path of the output directory.");
		this.log("                    The directory named 'dbdocs' is made under this directory.");
		this.log("                    When not specified, the project base directory is used.");
	}

	@Override
	protected void postProcess() {
		this.log("Output to : " + this.outputDir);
	}

}
