package org.insightech.er.ant_task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.persistent.Persistent;

public abstract class ERMasterAntTaskBase extends Task {

	private String diagramFile;

	public void setDiagramFile(String diagramFile) {
		this.diagramFile = diagramFile;
	}

	protected String getAbsolutePath(String path) {
		if (path == null) {
			path = this.getProjectBaseDir().getAbsolutePath();

		} else if (!new File(path).isAbsolute() && !path.startsWith("/")) {
			path = this.getProjectBaseDir().getAbsolutePath() + File.separator
					+ path;
		}

		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws BuildException {
		this.logUsage();

		Persistent persistent = Persistent.getInstance();

		InputStream in = null;

		try {
			if (this.diagramFile == null || this.diagramFile.trim().equals("")) {
				throw new BuildException("diagramFile attribute must be set!");
			}

			this.log("Base Location : " + this.getLocation().getFileName());

			File file = new File(this.diagramFile);

			if (!file.isAbsolute()) {
				file = new File(this.getProjectBaseDir(), this.diagramFile);
			}

			this.log("Load the diagram file : " + file.getAbsolutePath());

			try {
				in = new BufferedInputStream(new FileInputStream(file));
			} catch (Exception e) {
				throw new BuildException("Diagram file can not be found : "
						+ file.getAbsolutePath());
			}

			ERDiagram diagram = persistent.load(in);

			ExportWithProgressManager exportManager = this
					.createExportManager(diagram);
			exportManager.init(diagram, this.getProjectBaseDir());

			exportManager.run(new AntConsoleProgressMonitor(this));

			this.postProcess();

		} catch (InputException e) {
			throw new BuildException(ResourceString.getResourceString(e
					.getMessage()));
		} catch (IOException e) {
			throw new BuildException(e.getMessage());

		} catch (BuildException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e);

		} finally {
			if (in != null) {
				try {
					in.close();

				} catch (IOException e) {
					throw new BuildException(e);
				}
			}
		}
	}

	protected abstract void logUsage();

	protected void postProcess() {
	}

	protected abstract ExportWithProgressManager createExportManager(
			ERDiagram diagram) throws Exception;

	protected File getProjectBaseDir() {
		return this.getProject().getBaseDir();
	}
}
