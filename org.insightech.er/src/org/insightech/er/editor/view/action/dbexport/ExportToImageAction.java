package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToImageDialog;

public class ExportToImageAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToImageAction.class.getName();

	public ExportToImageAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.image", ImageKey.EXPORT_TO_IMAGE, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToImageDialog();
	}

}
