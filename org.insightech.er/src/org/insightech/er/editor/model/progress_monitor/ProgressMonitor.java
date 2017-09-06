package org.insightech.er.editor.model.progress_monitor;

public interface ProgressMonitor {

	public void beginTask(String message, int counter);

	public void worked(int counter) throws InterruptedException;

	public boolean isCanceled();

	public void done();

	public void subTask(String message);

	public void subTaskWithCounter(String message);

	public int getTotalCount();

	public int getCurrentCount();
}