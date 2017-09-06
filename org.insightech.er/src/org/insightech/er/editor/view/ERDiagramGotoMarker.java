package org.insightech.er.editor.view;

import org.eclipse.core.resources.IMarker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.ide.IGotoMarker;
import org.insightech.er.editor.ERDiagramMultiPageEditor;

public class ERDiagramGotoMarker implements IGotoMarker {

	private ERDiagramMultiPageEditor editor;

	public ERDiagramGotoMarker(ERDiagramMultiPageEditor editor) {
		this.editor = editor;
	}

	public void gotoMarker(IMarker marker) {
		focus(this.editor.getMarkedObject(marker));
	}

	private void focus(Object object) {
		GraphicalViewer viewer = this.editor.getActiveEditor()
				.getGraphicalViewer();
		EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(object);

		if (editPart != null) {
			viewer.select(editPart);
			viewer.reveal(editPart);
		}
	}
}
