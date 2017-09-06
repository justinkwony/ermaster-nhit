package org.insightech.er.editor.view.action.dbexport;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.dialog.dbexport.AbstractExportDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToTestDataDialog;

public class ExportToTestDataAction extends AbstractExportWithDialogAction {

	public static final String ID = ExportToTestDataAction.class.getName();

	public ExportToTestDataAction(ERDiagramEditor editor) {
		super(ID, "action.title.export.test.data",
				ImageKey.EXPORT_TO_TEST_DATA, editor);
	}

	@Override
	protected AbstractExportDialog getExportDialog() {
		return new ExportToTestDataDialog(this.getDiagram()
				.getDiagramContents().getTestDataList());
	}

}
