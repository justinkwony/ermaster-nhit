package org.insightech.er.editor.model.dbexport;

import java.io.File;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public abstract class AbstractExportManager implements
		ExportWithProgressManager {

	protected ERDiagram diagram;

	protected List<Category> categoryList;

	protected File projectDir;

	private String taskMessage;

	public AbstractExportManager(String taskMessage) {
		this.taskMessage = taskMessage;
	}

	public void init(ERDiagram diagram, File projectDir) throws Exception {
		this.diagram = diagram;
		this.diagram.getDiagramContents().sort();

		this.categoryList = this.diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories();

		this.projectDir = projectDir;
	}

	public void run(ProgressMonitor monitor) throws Exception {
		int totalTaskCount = this.getTotalTaskCount();

		monitor.beginTask(ResourceString.getResourceString(taskMessage),
				totalTaskCount);

		this.doProcess(monitor);

		monitor.done();
	}

	protected abstract int getTotalTaskCount();

	protected abstract void doProcess(ProgressMonitor monitor) throws Exception;

}
