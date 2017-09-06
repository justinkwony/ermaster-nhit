package org.insightech.er.editor.view.dialog.dbexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.TranslationResources;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Check;

public class ExportToTranslationDictionaryDialog extends AbstractDialog {

	private Text dictionaryNameText;

	private Table dictionaryTable;

	private ERDiagram diagram;

	public ExportToTranslationDictionaryDialog(Shell parentShell,
			ERDiagram diagram) {
		super(parentShell);

		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		this.dictionaryNameText = CompositeFactory.createText(this, parent,
				"label.translation.dictionary.name", false, true);

		CompositeFactory.fillLine(parent);

		CompositeFactory.createLeftLabel(parent,
				"dialog.message.export.translation.dictionary1", 2);

		CompositeFactory.fillLine(parent);

		CompositeFactory.createLeftLabel(parent,
				"dialog.message.export.translation.dictionary2", 2);

		CompositeFactory.fillLine(parent);

		this.createTable(parent);
	}

	private void createTable(Composite parent) {
		GridData gridData = new GridData();
		gridData.heightHint = 150;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		this.dictionaryTable = new Table(parent, SWT.FULL_SELECTION
				| SWT.BORDER | SWT.MULTI);
		this.dictionaryTable.setHeaderVisible(true);
		this.dictionaryTable.setLinesVisible(true);
		this.dictionaryTable.setLayoutData(gridData);

		parent.pack();

		int width = this.dictionaryTable.getBounds().width;

		TableColumn physicalNameTableColumn = new TableColumn(
				this.dictionaryTable, SWT.LEFT);
		physicalNameTableColumn.setText(ResourceString
				.getResourceString("label.physical.name"));
		physicalNameTableColumn.setWidth(width / 2 - 5);

		TableColumn logicalNameTableColumn = new TableColumn(
				this.dictionaryTable, SWT.LEFT);

		logicalNameTableColumn.setText(ResourceString
				.getResourceString("label.logical.name"));
		logicalNameTableColumn.setWidth(width / 2 - 5);
	}

	@Override
	protected String getErrorMessage() {
		if (isBlank(this.dictionaryNameText)) {
			return "error.translation.dictionary.name.empty";
		}

		String fileName = this.dictionaryNameText.getText().trim();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus result = workspace.validateName(fileName, IResource.FILE);
		if (!result.isOK()) {
			return result.getMessage();
		}

		File file = new File(PreferenceInitializer.getTranslationPath(fileName));
		if (file.exists()) {
			return "error.translation.dictionary.name.duplicated";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		String fileName = this.dictionaryNameText.getText().trim();
		File file = new File(PreferenceInitializer.getTranslationPath(fileName));
		file.getParentFile().mkdirs();

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			for (TableItem tableItem : this.dictionaryTable.getItems()) {
				writer.write(tableItem.getText(0));
				writer.write(",");
				writer.write(tableItem.getText(1));
				writer.write("\r\n");
			}

			PreferenceInitializer.addPreferenceValue(fileName);

		} catch (IOException e) {
			ERDiagramActivator.showExceptionDialog(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					ERDiagramActivator.showExceptionDialog(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		DiagramContents diagramContents = this.diagram.getDiagramContents();

		TranslationResources resources = new TranslationResources(
				diagramContents.getSettings().getTranslationSetting());

		Map<String, String> newDictionary = new TreeMap<String, String>();

		for (TableView tableView : diagramContents.getContents()
				.getTableViewList()) {
			String physicalName = tableView.getPhysicalName();
			String logicalName = tableView.getLogicalName();

			this.addNewWord(physicalName, logicalName, resources, newDictionary);

			for (NormalColumn normalColumn : tableView.getExpandedColumns()) {
				physicalName = normalColumn.getPhysicalName();
				logicalName = normalColumn.getLogicalName();

				this.addNewWord(physicalName, logicalName, resources,
						newDictionary);
			}
		}

		for (Map.Entry<String, String> entry : newDictionary.entrySet()) {
			TableItem tableItem = new TableItem(this.dictionaryTable, SWT.NONE);
			tableItem.setText(0, entry.getKey());
			tableItem.setText(1, entry.getValue());
		}
	}

	private void addNewWord(String physicalName, String logicalName,
			TranslationResources resources, Map<String, String> newDictionary) {
		physicalName = physicalName.toLowerCase();
		logicalName = logicalName.toLowerCase();

		if (!Check.isEmpty(physicalName) && !Check.isEmpty(logicalName)
				&& !resources.contains(physicalName)
				&& !newDictionary.containsKey(physicalName)) {
			newDictionary.put(physicalName, logicalName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.translation.dictionary";
	}
}
