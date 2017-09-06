package org.insightech.er.editor.view.dialog.element.table_view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.util.Format;

public class DescriptionTabWrapper extends ValidatableTabWrapper {

	private TableView copyData;

	private Text descriptionText;

	public DescriptionTabWrapper(AbstractTabbedDialog dialog, TabFolder parent,
			TableView copyData) {
		super(dialog, parent, "label.table.description");

		this.copyData = copyData;
	}

	@Override
	public void initComposite() {
		this.descriptionText = CompositeFactory.createTextArea(null, this,
				"label.table.description", -1, 400, 1, true, false);

		this.descriptionText.setText(Format.null2blank(copyData
				.getDescription()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		String text = descriptionText.getText().trim();
		this.copyData.setDescription(text);
	}

	@Override
	public void setInitFocus() {
		this.descriptionText.setFocus();
	}

	@Override
	public void perfomeOK() {
	}

}
