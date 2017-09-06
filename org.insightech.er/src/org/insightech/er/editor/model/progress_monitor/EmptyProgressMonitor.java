package org.insightech.er.editor.model.progress_monitor;

public class EmptyProgressMonitor implements ProgressMonitor {

	public EmptyProgressMonitor() {
	}

	public void beginTask(String message, int counter) {
	}

	public void worked(int counter) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void done() {
	}

	public void subTask(String message) {
	}

	public int getTotalCount() {
		return 0;
	}

	public int getCurrentCount() {
		return 0;
	}

	public void subTaskWithCounter(String message) {
		// TODO Auto-generated method stub
		
	}

}
