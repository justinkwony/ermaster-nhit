package org.insightech.er.common.widgets;

import java.io.File;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Resources;
import org.insightech.er.util.io.FileUtils;

public abstract class AbstractPathText {

	// private String PROJECT_BASE_STRING = "<project_dir>" + File.separator;

	private Text text;

	private Button openBrowseButton;

	protected File projectDir;

	public AbstractPathText(Composite parent, final File argProjectDir,
			boolean indent) {
		this.text = new Text(parent, SWT.BORDER);
		this.projectDir = argProjectDir;

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gridData.grabExcessHorizontalSpace = true;

		if (indent) {
			gridData.horizontalIndent = Resources.INDENT;
		}

		this.text.setLayoutData(gridData);

		this.openBrowseButton = new Button(parent, SWT.LEFT);
		this.openBrowseButton.setText(" "
				+ JFaceResources.getString("openBrowse") + " ");

		this.openBrowseButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String saveFilePath = selectPathByDilaog();

				if (saveFilePath != null) {
					saveFilePath = FileUtils.getRelativeFilePath(projectDir,
							saveFilePath);
					setText(saveFilePath);
				}
			}
		});
	}

	protected abstract String selectPathByDilaog();

	public void setLayoutData(Object layoutData) {
		this.text.setLayoutData(layoutData);
	}

	public void setText(String text) {
		// if (!FileUtils.isAbsolutePath(text)) {
		// text = PROJECT_BASE_STRING + Format.null2blank(text);
		// }

		this.text.setText(text);
		this.text.setSelection(text.length());
	}

	public boolean isBlank() {
		if (this.text.getText().trim().length() == 0) {
			return true;
		}

		return false;
	}

	public String getFilePath() {
		String path = this.text.getText().trim();
		// if (path.startsWith(PROJECT_BASE_STRING)) {
		// path = path.substring(PROJECT_BASE_STRING.length());
		// }

		return path;
	}

	public void addModifyListener(ModifyListener listener) {
		this.text.addModifyListener(listener);
	}

	public void setEnabled(boolean enabled) {
		this.text.setEnabled(enabled);
		this.openBrowseButton.setEnabled(enabled);
	}

}
