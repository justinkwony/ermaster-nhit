package org.insightech.er.editor.model.dbexport.testdata;

import java.io.File;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitFlatXmlTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitXLSTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;

public class ExportToTestDataManager extends AbstractExportManager {

	private ExportTestDataSetting exportTestDataSetting;

	private List<TestData> testDataList;

	public ExportToTestDataManager(ExportTestDataSetting exportTestDataSetting,
			List<TestData> testDataList) {
		super("dialog.message.export.testdata");

		this.exportTestDataSetting = exportTestDataSetting;
		this.testDataList = testDataList;
	}

	@Override
	protected int getTotalTaskCount() {
		return this.testDataList.size();
	}

	@Override
	public void doProcess(ProgressMonitor monitor) throws Exception {
		for (TestData testData : this.testDataList) {
			monitor.subTaskWithCounter("writing : " + testData.getName());

			exportTestData(this.diagram, this.exportTestDataSetting, testData);

			monitor.worked(1);
		}
	}

	public void exportTestData(ERDiagram diagram,
			ExportTestDataSetting exportTestDataSetting, TestData testData)
			throws Exception {
		TestDataCreator testDataCreator = null;

		int format = exportTestDataSetting.getExportFormat();

		if (format == TestData.EXPORT_FORMT_DBUNIT) {
			testDataCreator = new DBUnitTestDataCreator(
					exportTestDataSetting.getExportFileEncoding());

		} else if (format == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
			testDataCreator = new DBUnitFlatXmlTestDataCreator(
					exportTestDataSetting.getExportFileEncoding());

		} else if (format == TestData.EXPORT_FORMT_SQL) {
			testDataCreator = new SQLTestDataCreator();

		} else if (format == TestData.EXPORT_FORMT_DBUNIT_XLS) {
			testDataCreator = new DBUnitXLSTestDataCreator();

		}

		testDataCreator.init(testData, this.projectDir);
		testDataCreator.write(exportTestDataSetting, diagram);
	}

	public File getOutputFileOrDir() {
		return new File(this.exportTestDataSetting.getExportFilePath());
	}

}
