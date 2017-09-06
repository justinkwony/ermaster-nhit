package org.insightech.er.editor.view.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;

public abstract class AbstractBaseAction extends Action {

	private ERDiagramEditor editor;

	public AbstractBaseAction(String id, String text, ERDiagramEditor editor) {
		this(id, text, SWT.NONE, editor);
	}

	public AbstractBaseAction(String id, String text, int style,
			ERDiagramEditor editor) {
		super(text, style);
		this.setId(id);

		this.editor = editor;
	}

	protected void refreshProject() {
		IEditorInput input = this.getEditorPart().getEditorInput();

		if (input instanceof IFileEditorInput) {
			IFile iFile = ((IFileEditorInput) this.getEditorPart()
					.getEditorInput()).getFile();
			IProject project = iFile.getProject();

			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);

			} catch (CoreException e) {
				ERDiagramActivator.showExceptionDialog(e);
			}
		}
	}

	protected ERDiagram getDiagram() {
		EditPart editPart = this.editor.getGraphicalViewer().getContents();
		ERDiagram diagram = (ERDiagram) editPart.getModel();

		return diagram;
	}

	protected String getBasePath() {
		return this.getDiagram().getEditor().getBasePath();
	}

	protected GraphicalViewer getGraphicalViewer() {
		return this.editor.getGraphicalViewer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void runWithEvent(Event event) {
		try {
			execute(event);

		} catch (Exception e) {
			ERDiagramActivator.showExceptionDialog(e);
		}
	}

	abstract public void execute(Event event) throws Exception;

	protected void execute(Command command) {
		this.editor.getGraphicalViewer().getEditDomain().getCommandStack()
				.execute(command);
	}

	protected ERDiagramEditor getEditorPart() {
		return this.editor;
	}
}
