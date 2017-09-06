package org.insightech.er.preference.page.translation;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private TranslationFileListEditor fileListEditor;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		CompositeFactory.fillLine(composite,
				Resources.PREFERENCE_PAGE_MARGIN_TOP);

		this.fileListEditor = new TranslationFileListEditor(
				PreferenceInitializer.TRANSLATION_FILE_LIST,
				ResourceString
						.getResourceString("label.custom.dictionary.for.translation"),
				composite);
		this.fileListEditor.load();

		CompositeFactory.fillLine(composite);

		CompositeFactory.createLabel(composite,
				"dialog.message.translation.file.store", 2);

		CompositeFactory.createLabel(composite,
				"dialog.message.translation.file.encode", 2);

		return composite;
	}

	@Override
	protected void performDefaults() {
		this.fileListEditor.loadDefault();

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		this.fileListEditor.store();

		return super.performOk();
	}

}
