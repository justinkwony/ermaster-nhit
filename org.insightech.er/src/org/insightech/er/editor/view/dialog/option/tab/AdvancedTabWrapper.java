package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

	private Settings settings;

	private ERDiagram diagram;

	private AdvancedComposite composite;

	public AdvancedTabWrapper(OptionSettingDialog dialog, TabFolder parent,
			Settings settings, ERDiagram diagram) {
		super(dialog, parent, "label.advanced.settings");

		this.diagram = diagram;
		this.settings = settings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.composite.validate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initComposite() {
		if (this.composite != null) {
			this.composite.dispose();
		}

		this.composite = EclipseDBManagerFactory.getEclipseDBManager(
				this.settings.getDatabase()).createAdvancedComposite(this);
		this.composite.initialize(this.dialog,
				(TableProperties) this.settings.getTableViewProperties(),
				this.diagram, null);

		this.pack();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitFocus() {
		this.composite.setInitFocus();
	}

	@Override
	public void reset() {
		this.init();
	}

	@Override
	public void perfomeOK() {
	}
}
