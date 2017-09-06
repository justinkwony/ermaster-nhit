package org.insightech.er.ant_task.impl;

import org.apache.tools.ant.BuildException;
import org.insightech.er.ant_task.ERMasterAntTaskBase;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.model.settings.export.ExportImageSetting;

public class ImageAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	private boolean withCategory = true;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setWithCategory(boolean withCategory) {
		this.withCategory = withCategory;
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

		this.log("With categories : " + this.withCategory);

		ExportImageSetting settings = new ExportImageSetting();
		settings.setOutputFilePath(this.outputFile);
		settings.setWithCategoryImage(this.withCategory);

		return new ExportToImageManager(settings);
	}

	@Override
	protected void logUsage() {
		this.log("<ermaster.image> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile  - The path of the input .erm file.");
		this.log("    * outputFile   - The path of the output image file. The png/jpg/jpeg/bmp format are supported.");
		this.log("      withCategory - Boolean. Whether images of each category are output. Default value is true.");
		this.log("                     If true is specified, then the directory named 'images' will be made,");
		this.log("                     and images of each category are output under this directory.");
	}

	@Override
	protected void postProcess() {
		this.log("Output to : " + this.outputFile);
	}

}
