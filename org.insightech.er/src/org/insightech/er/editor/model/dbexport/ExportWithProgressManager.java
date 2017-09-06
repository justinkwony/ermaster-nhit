package org.insightech.er.editor.model.dbexport;

import java.io.File;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public interface ExportWithProgressManager {

	public void init(ERDiagram diagram, File projectDir) throws Exception;

	public void run(ProgressMonitor progressMonitor) throws Exception;

	public File getOutputFileOrDir();

}
