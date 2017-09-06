package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToJavaDialog;

public class ExportToJavaAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToJavaAction.class.getName();

	public ExportToJavaAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.java", ImageKey.EXPORT_TO_JAVA, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToJavaDialog();
	}
}
