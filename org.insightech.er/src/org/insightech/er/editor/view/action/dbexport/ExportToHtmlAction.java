package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToHtmlDialog;

public class ExportToHtmlAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToHtmlAction.class.getName();

	public ExportToHtmlAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.html", ImageKey.EXPORT_TO_HTML, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToHtmlDialog();
	}

}
