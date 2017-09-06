package org.insightech.er.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.editor.model.progress_monitor.EclipseProgressMonitor;
import org.insightech.er.editor.model.progress_monitor.EmptyProgressMonitor;

public abstract class ImportFromDBManagerEclipseBase extends
		ImportFromDBManagerBase implements IRunnableWithProgress {

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		EclipseProgressMonitor eclipseProgressMonitor = new EclipseProgressMonitor(
				monitor);
		this.run(eclipseProgressMonitor);
	}

	public void run() throws InvocationTargetException, InterruptedException {
		this.run(new EmptyProgressMonitor());
	}

}
