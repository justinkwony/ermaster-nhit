package org.insightech.er.editor.view.dialog.element.table_view.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class AdvancedComposite extends Composite {

	private Combo tableSpaceCombo;

	private Text schemaText;

	protected TableViewProperties tableViewProperties;

	protected ERDiagram diagram;

	protected AbstractDialog dialog;

	protected ERTable table;
	
	public AdvancedComposite(Composite parent) {
		super(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		this.setLayoutData(gridData);
	}

	public final void initialize(AbstractDialog dialog,
			TableViewProperties tableViewProperties, ERDiagram diagram,
			ERTable table) {
		this.tableViewProperties = tableViewProperties;
		this.diagram = diagram;
		this.dialog = dialog;
		this.table = table;
		
		this.initComposite();
		this.addListener();
		this.setData();
	}

	protected void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.setLayout(gridLayout);

		this.tableSpaceCombo = CompositeFactory.createReadOnlyCombo(null, this,
				"label.tablespace");
		this.schemaText = CompositeFactory.createText(null, this,
				"label.schema", 1, 120, false, true);

		this.initTablespaceCombo();
	}

	private void initTablespaceCombo() {
		this.tableSpaceCombo.add("");

		for (Tablespace tablespace : this.diagram.getDiagramContents()
				.getTablespaceSet()) {
			this.tableSpaceCombo.add(tablespace.getName());
		}
	}

	protected void addListener() {		
	}

	protected void setData() {
		Tablespace tablespace = this.tableViewProperties.getTableSpace();

		if (tablespace != null) {
			int index = this.diagram.getDiagramContents().getTablespaceSet()
					.getObjectList().indexOf(tablespace);
			this.tableSpaceCombo.select(index + 1);
		}

		if (this.tableViewProperties.getSchema() != null
				&& this.schemaText != null) {
			this.schemaText.setText(this.tableViewProperties.getSchema());
		}
	}

	public boolean validate() throws InputException {
		if (this.tableSpaceCombo != null) {
			int tablespaceIndex = this.tableSpaceCombo.getSelectionIndex();
			if (tablespaceIndex > 0) {
				Tablespace tablespace = this.diagram.getDiagramContents()
						.getTablespaceSet().getObjectList()
						.get(tablespaceIndex - 1);
				this.tableViewProperties.setTableSpace(tablespace);

			} else {
				this.tableViewProperties.setTableSpace(null);
			}
		}

		if (this.schemaText != null) {
			this.tableViewProperties.setSchema(this.schemaText.getText());
		}

		return true;
	}

	public void setInitFocus() {
		this.tableSpaceCombo.setFocus();
	}
}
