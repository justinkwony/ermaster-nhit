package org.insightech.er.editor.view.dialog.option;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.DBSelectTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.EnvironmentTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.OptionTabWrapper;

public class OptionSettingDialog extends AbstractTabbedDialog {

	private Settings settings;

	private ERDiagram diagram;

	public OptionSettingDialog(Shell parentShell, Settings settings,
			ERDiagram diagram) {
		super(parentShell);

		this.diagram = diagram;
		this.settings = settings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createTabFolder(composite);
	}

	@Override
	protected String getTitle() {
		return "dialog.title.option";
	}

	@Override
	protected List<ValidatableTabWrapper> createTabWrapperList(
			TabFolder tabFolder) {
		List<ValidatableTabWrapper> list = new ArrayList<ValidatableTabWrapper>();

		list.add(new DBSelectTabWrapper(this, tabFolder, this.settings));
		list.add(new EnvironmentTabWrapper(this, tabFolder, this.settings));
		list.add(new AdvancedTabWrapper(this, tabFolder, this.settings,
				this.diagram));
		list.add(new OptionTabWrapper(this, tabFolder, this.settings));

		return list;
	}
}
