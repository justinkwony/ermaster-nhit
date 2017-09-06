package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportManagerRunner;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;

public abstract class AbstractExportDialog extends AbstractDialog {

	protected MultiLineCheckbox openAfterSavedButton;

	protected Settings settings;

	protected ERDiagram diagram;

	private List<Category> categoryList;

	public AbstractExportDialog() {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	public AbstractExportDialog(Shell parentShell) {
		super(parentShell);
	}

	public void init(ERDiagram diagram) {
		this.diagram = diagram;

		this.settings = this.diagram.getDiagramContents().getSettings().clone();
		this.categoryList = this.settings.getCategorySetting()
				.getSelectedCategories();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.numColumns = 3;
		layout.verticalSpacing = Resources.VERTICAL_SPACING;
	}

	protected void createOpenAfterSavedButton(Composite parent, boolean indent,
			int span) {
		this.openAfterSavedButton = CompositeFactory.createMultiLineCheckbox(
				this, parent, "label.open.after.saved", indent, span);
	}

	public Composite createCheckboxArea(Composite parent) {
		return createCheckboxArea(parent, true);
	}

	public Composite createCheckboxArea(Composite parent, boolean separater) {
		if (separater) {
			CompositeFactory.fillLine(parent, 5);
			CompositeFactory.separater(parent);
		}

		Composite checkboxArea = new Composite(parent, SWT.NONE);

		int span = ((GridLayout) parent.getLayout()).numColumns;

		GridData checkboxGridData = new GridData(SWT.FILL, SWT.LEFT, true,
				false, span, 1);
		// checkboxGridData.horizontalIndent = Resources.INDENT;
		checkboxArea.setLayoutData(checkboxGridData);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		checkboxArea.setLayout(layout);

		return checkboxArea;
	}

	public Settings getSettings() {
		return this.settings;
	}

	protected File getBaseDir() {
		return new File(this.diagram.getEditor().getBasePath());
	}

	protected String getDefaultOutputFilePath(String extention) {
		String diagramFilePath = this.diagram.getEditor().getDiagramFilePath();

		return diagramFilePath.substring(0, diagramFilePath.lastIndexOf("."))
				+ extention;
	}

	protected String getDefaultOutputFileName(String extention) {
		File file = new File(this.getDefaultOutputFilePath(extention));

		return file.getName();
	}

	@Override
	protected void perfomeOK() throws Exception {
		try {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(
					this.getShell());

			ExportWithProgressManager manager = this
					.getExportWithProgressManager(this.settings
							.getExportSetting());

			manager.init(this.diagram, this.getBaseDir());

			ExportManagerRunner runner = new ExportManagerRunner(manager);

			monitor.run(true, true, runner);

			if (runner.getException() != null) {
				throw runner.getException();
			}

			if (this.openAfterSavedButton != null
					&& this.openAfterSavedButton.getSelection()) {
				File openAfterSaved = this.openAfterSaved();

				URI uri = openAfterSaved.toURI();

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				if (this.openWithExternalEditor()) {
					IDE.openEditor(page, uri,
							IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);

				} else {
					IFileStore fileStore = EFS.getStore(uri);
					IDE.openEditorOnFileStore(page, fileStore);
				}
			}

			// there is a case in another project
			this.diagram.getEditor().refreshProject();

		} catch (InterruptedException e) {
			throw new InputException();
		}
	}

	protected abstract ExportWithProgressManager getExportWithProgressManager(
			ExportSetting exportSetting) throws Exception;

	protected File openAfterSaved() {
		return null;
	}

	protected boolean openWithExternalEditor() {
		return false;
	}

	protected void initCategoryCombo(Combo categoryCombo) {
		categoryCombo.add(ResourceString.getResourceString("label.all"));

		for (Category category : this.categoryList) {
			categoryCombo.add(category.getName());
		}

		categoryCombo.setVisibleItemCount(20);
	}

	protected void setCategoryData(Label categoryLabel) {
		String categoryName = ResourceString.getResourceString("label.all");
		if (this.diagram.getCurrentCategory() != null) {
			categoryName = this.diagram.getCurrentCategory().getName();
		}
		categoryLabel.setText(categoryName);
	}

	protected void setCategoryComboData(Combo categoryCombo,
			Category selectedCategory) {
		categoryCombo.select(0);

		if (selectedCategory != null) {
			for (int i = 0; i < this.categoryList.size(); i++) {
				Category category = this.categoryList.get(i);

				if (selectedCategory.equals(category)) {
					categoryCombo.select(i + 1);
					break;
				}
			}
		}
	}

	protected Category getSelectedCategory(Combo categoryCombo) {
		Category category = null;

		int categoryIndex = categoryCombo.getSelectionIndex();

		if (categoryIndex != 0) {
			category = this.categoryList.get(categoryIndex - 1);
		}

		return category;
	}

}
