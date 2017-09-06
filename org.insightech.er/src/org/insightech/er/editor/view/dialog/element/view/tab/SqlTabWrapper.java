package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.util.Format;

public class SqlTabWrapper extends ValidatableTabWrapper {

	private View copyData;

	private Text sqlText;

	public SqlTabWrapper(ViewDialog viewDialog, TabFolder parent, View copyData) {
		super(viewDialog, parent, "label.sql");

		this.copyData = copyData;
	}

	@Override
	public void initComposite() {
		this.sqlText = CompositeFactory.createTextArea(this.dialog, this,
				"label.sql", -1, 400, 1, true, false);

		this.sqlText.setText(Format.null2blank(copyData.getSql()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		String text = sqlText.getText().trim();

		if (text.equals("")) {
			throw new InputException("error.view.sql.empty");
		}

		this.copyData.setSql(text);
	}

	@Override
	public void setInitFocus() {
		this.sqlText.setFocus();
	}

	@Override
	public void perfomeOK() {
	}

}
