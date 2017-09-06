package org.insightech.er.db.impl.tibero;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.tibero.tablespace.TiberoTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class TiberoEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return TiberoDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new TiberoAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return new TiberoTablespaceDialog();
	}

}
