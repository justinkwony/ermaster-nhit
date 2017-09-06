package org.insightech.er.editor.view.action.dbimport;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObjectSet;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.insightech.er.editor.view.dialog.dbimport.ImportDBSettingDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedObjectFromDBDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedSchemaDialog;

public class ImportFromDBAction extends AbstractImportAction {

	public static final String ID = ImportFromDBAction.class.getName();

	private DBSetting dbSetting;

	public ImportFromDBAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.import.db"),
				editor);
		this.setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.DATABASE));
	}

	protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(
			DBObjectSet dbObjectSet) {
		ERDiagram diagram = this.getDiagram();

		return new SelectImportedObjectFromDBDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet,
				this.getEditorPart());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		int step = 0;

		ImportDBSettingDialog settingDialog = null;
		SelectImportedSchemaDialog selectDialog = null;
		PreImportFromDBManager preTableImportManager = null;
		AbstractSelectImportedObjectDialog importDialog = null;

		DBManager manager = null;
		Connection con = null;
		List<String> selectedSchemaList = new ArrayList<String>();
		int dialogResult = -1;

		try {
			while (true) {
				if (step == 0) {
					settingDialog = new ImportDBSettingDialog(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), diagram);
					dialogResult = settingDialog.open();

				} else if (step == 1) {
					if (dialogResult == IDialogConstants.OK_ID) {
						this.dbSetting = settingDialog.getDbSetting();
						manager = DBManagerFactory.getDBManager(this.dbSetting
								.getDbsystem());
						if (con != null) {
							con.close();
							con = null;
						}
						con = dbSetting.connect();
					}

				} else if (step == 2) {
					List<String> schemaList = manager.getImportSchemaList(con);

					if (!schemaList.isEmpty()) {
						selectDialog = new SelectImportedSchemaDialog(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(),
								diagram, this.dbSetting.getDbsystem(),
								schemaList, selectedSchemaList);

						dialogResult = selectDialog.open();

						selectedSchemaList = selectDialog.getSelectedSchemas();

					} else {
						selectedSchemaList.clear();
					}

				} else if (step == 3) {
					if (dialogResult == IDialogConstants.OK_ID) {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell());

						preTableImportManager = manager
								.getPreTableImportManager();
						preTableImportManager.init(con, this.dbSetting,
								diagram, selectedSchemaList);

						try {
							dialog.run(true, true, preTableImportManager);

							Exception e = preTableImportManager.getException();
							if (e != null) {
								ERDiagramActivator.showMessageDialog(e.getMessage());
								throw new InputException("error.jdbc.version");

							}

							dialogResult = IDialogConstants.OK_ID;

						} catch (InterruptedException e1) {
							dialogResult = IDialogConstants.BACK_ID;
						}
					}

				} else if (step == 4) {
					DBObjectSet dbObjectSet = preTableImportManager
							.getImportObjects();

					importDialog = this
							.createSelectImportedObjectDialog(dbObjectSet);

					dialogResult = importDialog.open();

				} else if (step == 5) {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell());
					ImportFromDBManagerEclipseBase tableImportManager = (ImportFromDBManagerEclipseBase) manager
							.getTableImportManager();
					tableImportManager.init(con, this.dbSetting, diagram,
							importDialog.getSelectedDbObjects(),
							importDialog.isUseCommentAsLogicalName(),
							importDialog.isMergeWord());

					try {
						dialog.run(true, true, tableImportManager);

						Exception e1 = tableImportManager.getException();
						if (e1 != null) {
							throw e1;

						} else {
							this.importedNodeElements = new ArrayList<NodeElement>();

							this.importedNodeElements.addAll(tableImportManager
									.getImportedTables());
							this.importedNodeElements.addAll(tableImportManager
									.getImportedViews());
							this.importedSequences = tableImportManager
									.getImportedSequences();
							this.importedTriggers = tableImportManager
									.getImportedTriggers();
							this.importedTablespaces = tableImportManager
									.getImportedTablespaces();

							this.showData();
							break;
						}

					} catch (InterruptedException e1) {
						dialogResult = IDialogConstants.BACK_ID;
					}

				} else {
					break;
				}

				if (dialogResult == IDialogConstants.OK_ID) {
					step++;

				} else if (dialogResult == IDialogConstants.BACK_ID) {
					step--;

				} else {
					break;
				}
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}

	}
}
