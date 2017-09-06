package org.insightech.er.editor.model.dbexport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.editor.model.progress_monitor.EclipseProgressMonitor;

public class ExportManagerRunner implements IRunnableWithProgress {

	private ExportWithProgressManager exportManager;

	private Exception exception;

	public ExportManagerRunner(ExportWithProgressManager exportManager) {
		this.exportManager = exportManager;
	}

	public void run(IProgressMonitor monitor) {
		try {
			this.exportManager.run(new EclipseProgressMonitor(monitor));

		} catch (Exception e) {
			this.exception = e;
		}
	}

	public Exception getException() {
		return this.exception;
	}

}
