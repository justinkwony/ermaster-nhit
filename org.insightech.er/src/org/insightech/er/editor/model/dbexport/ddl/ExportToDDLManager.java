package org.insightech.er.editor.model.dbexport.ddl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.io.FileUtils;

public class ExportToDDLManager extends AbstractExportManager {

	private ExportDDLSetting exportDDLSetting;

	public ExportToDDLManager(ExportDDLSetting exportDDLSetting) {
		super("dialog.message.export.ddl");
		this.exportDDLSetting = exportDDLSetting;
	}

	@Override
	protected int getTotalTaskCount() {
		return 2;
	}

	@Override
	protected void doProcess(ProgressMonitor monitor) throws Exception {

		PrintWriter out = null;

		try {
			DDLCreator ddlCreator = DBManagerFactory.getDBManager(this.diagram)
					.getDDLCreator(this.diagram,
							this.exportDDLSetting.getCategory(), true);

			ddlCreator.init(this.exportDDLSetting.getEnvironment(),
					this.exportDDLSetting.getDdlTarget(),
					this.exportDDLSetting.getLineFeed());

			File file = FileUtils.getFile(this.projectDir,
					this.exportDDLSetting.getDdlOutput());
			file.getParentFile().mkdirs();

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file),
					this.exportDDLSetting.getSrcFileEncoding())));

			monitor.subTaskWithCounter("writing drop ddl");

			out.print(ddlCreator.getDropDDL(this.diagram));

			monitor.worked(1);

			monitor.subTaskWithCounter("writing create ddl");

			out.print(ddlCreator.getCreateDDL(this.diagram));

			monitor.worked(1);

		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	public File getOutputFileOrDir() {
		return FileUtils.getFile(this.projectDir,
				this.exportDDLSetting.getDdlOutput());
	}
}
