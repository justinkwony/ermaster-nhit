package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.util.io.FileUtils;

public abstract class AbstractTextTestDataCreator extends TestDataCreator {

	protected PrintWriter out;

	public AbstractTextTestDataCreator() {
	}

	@Override
	protected void openFile() throws IOException {
		File file = new File(FileUtils.getFile(this.baseDir,
				this.exportTestDataSetting.getExportFilePath()),
				this.testData.getName() + this.getFileExtention());

		file.getParentFile().mkdirs();

		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file),
				this.exportTestDataSetting.getExportFileEncoding())));

	}

	@Override
	protected void write() throws Exception {
		out.print(this.getHeader());

		super.write();

		out.print(this.getFooter());
	}

	@Override
	protected boolean skipTable(ERTable table) {
		return false;
	}

	protected abstract String getHeader();

	protected abstract String getFileExtention();

	protected abstract String getFooter();

	@Override
	protected void closeFile() throws IOException {
		if (out != null) {
			out.close();
		}
	}

}
