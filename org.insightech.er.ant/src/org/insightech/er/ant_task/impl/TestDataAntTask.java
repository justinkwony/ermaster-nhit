package org.insightech.er.ant_task.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.insightech.er.ant_task.ERMasterAntTaskBase;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.testdata.ExportToTestDataManager;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;

public class TestDataAntTask extends ERMasterAntTaskBase {

	private String outputDir;

	private String encoding;

	private String format;

	private List<TestDataElement> testDataElementList = new ArrayList<TestDataElement>();

	public void addTestData(TestDataElement testData) {
		this.testDataElementList.add(testData);
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ExportWithProgressManager createExportManager(ERDiagram diagram)
			throws Exception {
		ExportTestDataSetting setting = new ExportTestDataSetting();

		if (this.outputDir == null || this.outputDir.trim().equals("")) {
			throw new BuildException("outputDir attribute must be set!");

		}

		this.outputDir = this.getAbsolutePath(this.outputDir);
		setting.setExportFilePath(this.outputDir);

		int formatNo = -1;
		try {
			formatNo = Integer.parseInt(this.format);

		} catch (Exception e) {
		}

		String formatName = null;

		if (formatNo == 0) {
			formatName = "SQL";

		} else if (formatNo == 1) {
			formatName = "DBUnit XML";

		} else if (formatNo == 2) {
			formatName = "DBUnit Flat XML";

		} else if (formatNo == 3) {
			formatName = "DBUnit Excel";

		} else {
			throw new BuildException(
					"format attribute must be 0(SQL) or 1(DBUnit XML) or 2(DBUnit Flat XML) or 3(DBUnit Excel)!");
		}

		this.log("Format : " + formatName);

		if (this.testDataElementList.isEmpty()) {
			throw new BuildException(
					"At least one <testdata> element must be specified!");
		}

		setting.setExportFormat(formatNo);

		if (this.encoding == null) {
			this.encoding = Charset.defaultCharset().name();
		}

		this.log("Encoding : " + this.encoding);

		setting.setExportFileEncoding(this.encoding);

		List<TestData> testDataList = new ArrayList<TestData>();

		for (TestDataElement testDataElement : this.testDataElementList) {

			boolean found = false;

			for (TestData testData : diagram.getDiagramContents()
					.getTestDataList()) {
				if (testDataElement.getName().equals(testData.getName())) {

					testDataList.add(testData);
					found = true;
					break;
				}
			}

			if (!found) {
				this.log("Test Data [" + testDataElement.getName()
						+ "] is not found.");
			}
		}

		if (!testDataList.isEmpty()) {
			return new ExportToTestDataManager(setting, testDataList);

		} else {
			throw new BuildException("No available <testdata> elements!");
		}
	}

	@Override
	protected void logUsage() {
		this.log("<ermaster.testdata> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("    * outputDir   - The path of the output directory.");
		this.log("    * format      - 0(SQL) or 1(DBUnit XML) or 2(DBUnit Flat XML) or 3(DBUnit Excel).");
		this.log("      encoding    - The encoding of the output file.");
		this.log("<ermaster.testdata> have these sub elements. (the element with '*' must be set.) ");
		this.log("    * <testdata>  - The element which specifies the testdata being output.");
		this.log("<testdata> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * name        - The name of the testdata.");

	}

	@Override
	protected void postProcess() {
		this.log("Output to : " + this.outputDir);
	}

	public static class TestDataElement {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
