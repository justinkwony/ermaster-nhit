package org.insightech.er.ant_task.impl;

import java.nio.charset.Charset;

import org.apache.tools.ant.BuildException;
import org.insightech.er.ant_task.ERMasterAntTaskBase;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.dbexport.ddl.ExportToDDLManager;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;

public class DDLAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	private String encoding;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ExportWithProgressManager createExportManager(ERDiagram diagram)
			throws Exception {
		if (this.outputFile == null || this.outputFile.trim().equals("")) {
			throw new BuildException("outputFile attribute must be set!");
		}

		this.outputFile = this.getAbsolutePath(this.outputFile);

		Environment environment = diagram.getDiagramContents().getSettings()
				.getExportSetting().getExportDDLSetting().getEnvironment();

		DDLTarget ddlTarget = diagram.getDiagramContents().getSettings()
				.getExportSetting().getExportDDLSetting().getDdlTarget();

		if (this.encoding == null) {
			this.encoding = Charset.defaultCharset().name();
		}

		this.log("Encoding : " + this.encoding);

		ExportDDLSetting exportDDLSetting = new ExportDDLSetting();
		exportDDLSetting.setDdlOutput(this.outputFile);
		exportDDLSetting.setDdlTarget(ddlTarget);
		exportDDLSetting.setEnvironment(environment);
		exportDDLSetting.setSrcFileEncoding(this.encoding);

		return new ExportToDDLManager(exportDDLSetting);
	}

	@Override
	protected void logUsage() {
		this.log("<ermaster.ddl> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("    * outputFile  - The path of the output ddl file.");
		this.log("      encoding    - The encoding of the output ddl file.");
	}

	@Override
	protected void postProcess() {
		this.log("Output to : " + this.outputFile);
	}

}
