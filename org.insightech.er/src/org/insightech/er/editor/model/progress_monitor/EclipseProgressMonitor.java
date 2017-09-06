package org.insightech.er.editor.model.progress_monitor;

import org.eclipse.core.runtime.IProgressMonitor;

public class EclipseProgressMonitor implements ProgressMonitor {

	private IProgressMonitor progressMonitor;

	private int totalCount;

	private int currentCount;

	public EclipseProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public void beginTask(String message, int totalCount) {
		this.totalCount = totalCount;
		this.progressMonitor.beginTask(message, totalCount);
	}

	public void worked(int count) throws InterruptedException {
		this.currentCount += count;

		this.progressMonitor.worked(count);

		if (this.isCanceled()) {
			throw new InterruptedException("Cancel has been requested.");
		}
	}

	public boolean isCanceled() {
		return this.progressMonitor.isCanceled();
	}

	public void done() {
		this.progressMonitor.done();
	}

	public void subTask(String message) {
		this.progressMonitor.subTask(message);
	}

	public void subTaskWithCounter(String message) {
		this.subTask("(" + this.getCurrentCount() + "/" + this.getTotalCount()
				+ ") " + message);
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public int getCurrentCount() {
		return this.currentCount;
	}

}
