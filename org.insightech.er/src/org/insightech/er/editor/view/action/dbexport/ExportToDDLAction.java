package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToDDLDialog;

public class ExportToDDLAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToDDLAction.class.getName();

	public ExportToDDLAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.ddl", ImageKey.EXPORT_DDL, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToDDLDialog();
	}

}
