package org.insightech.er.ant_task;

import org.apache.tools.ant.Task;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public class AntConsoleProgressMonitor implements ProgressMonitor {

	private Task task;

	private int totalCount;

	private int currentCount;

	public AntConsoleProgressMonitor(Task task) {
		this.task = task;
	}

	public void beginTask(String message, int totalCount) {
		this.totalCount = totalCount;
		this.task.log(message);
	}

	public void done() {
		this.task.log("Finish!");
	}

	public void subTaskWithCounter(String message) {
		this.subTask("(" + (this.getCurrentCount() + 1) + "/"
				+ this.getTotalCount() + ") " + message);

	}

	public void subTask(String message) {
		this.task.log(message);
	}

	public void worked(int count) {
		this.currentCount += count;
	}

	public boolean isCanceled() {
		return false;
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public int getCurrentCount() {
		return this.currentCount;
	}
}
