package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedTabWrapper;

public class TableAdvancedTabWrapper extends AdvancedTabWrapper {

	public TableAdvancedTabWrapper(AbstractTabbedDialog dialog,
			TabFolder parent, ERTable table) {
		super(dialog, parent, table);
	}

	@Override
	protected AdvancedComposite createAdvancedComposite() {
		return EclipseDBManagerFactory.getEclipseDBManager(
				this.tableView.getDiagram()).createAdvancedComposite(this);
	}

}
