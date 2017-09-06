/**
 * 20170903 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class CUBRIDEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return CUBRIDDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new CUBRIDAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return null;
	}

}
