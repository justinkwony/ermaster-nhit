package org.insightech.er.common.widgets;

import java.awt.Component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;

public class MultiLineCheckbox extends Component {

	private static final long serialVersionUID = 1L;

	private Button checkboxButton;

	private Label label;

	public MultiLineCheckbox(final AbstractDialog dialog, Composite parent,
			String title, boolean indent, int span) {
		super();

		Composite box = new Composite(parent, SWT.NONE);

		GridData boxGridData = new GridData(SWT.FILL, SWT.LEFT, true, false,
				span, 1);
		if (indent) {
			boxGridData.horizontalIndent = Resources.INDENT;
		}
		box.setLayoutData(boxGridData);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		box.setLayout(layout);

		this.checkboxButton = new Button(box, SWT.CHECK);
		GridData checkboxGridData = new GridData();
		checkboxGridData.verticalAlignment = SWT.TOP;
		this.checkboxButton.setLayoutData(checkboxGridData);

		this.label = new Label(box, SWT.NONE);
		GridData labelGridData = new GridData();
		labelGridData.horizontalIndent = Resources.CHECKBOX_INDENT;

		this.label.setLayoutData(labelGridData);
		this.label.setText(ResourceString.getResourceString(title));

		ListenerAppender.addCheckBoxListener(this.checkboxButton, dialog);

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				checkboxButton.setSelection(!checkboxButton.getSelection());
				dialog.validate();
			}
		});
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.checkboxButton.setEnabled(enabled);
		this.label.setEnabled(enabled);
	}

	public boolean getSelection() {
		return this.checkboxButton.getSelection();
	}

	public void setSelection(boolean selected) {
		this.checkboxButton.setSelection(selected);
	}
}
