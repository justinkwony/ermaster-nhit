package org.insightech.er.preference.page.template;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.editor.FileListEditor;

public class TemplateFileListEditor extends FileListEditor {

	public TemplateFileListEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent, "*.xls");
	}

	@Override
	protected String getStorePath(String name) {
		return PreferenceInitializer.getTemplatePath(name);
	}

}
