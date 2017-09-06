package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToExcelDialog;

public class ExportToExcelAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToExcelAction.class.getName();

	public ExportToExcelAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.excel", ImageKey.EXPORT_TO_EXCEL, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToExcelDialog();
	}
}
