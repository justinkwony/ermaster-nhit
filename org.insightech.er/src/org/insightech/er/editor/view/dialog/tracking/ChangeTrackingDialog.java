package org.insightech.er.editor.view.dialog.tracking;

import java.text.DateFormat;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.controller.command.tracking.AddChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.CalculateChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.DeleteChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.DisplaySelectedChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.ResetChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.UpdateChangeTrackingCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.util.Check;

public class ChangeTrackingDialog extends Dialog {

	private Table changeTrackingTable;

	private Text textArea = null;

	private Button registerButton;

	private Button updateButton;

	private Button deleteButton;

	private Button replaceButton;

	private Button comparisonDisplayButton;

	private Button comparisonResetButton;

	private GraphicalViewer viewer;

	private ERDiagram diagram;

	public ChangeTrackingDialog(Shell parentShell, GraphicalViewer viewer,
			ERDiagram diagram) {
		super(parentShell);

		this.viewer = viewer;
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				ResourceString
						.getResourceString("dialog.title.change.tracking"));

		Composite composite = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout();
		this.initLayout(layout);

		composite.setLayout(layout);

		this.initialize(composite);

		this.setData();

		return composite;
	}

	protected void initLayout(GridLayout layout) {
		layout.numColumns = 6;

		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginBottom = 15;
		layout.marginTop = 15;
	}

	private void initialize(Composite composite) {
		this.changeTrackingTable = CompositeFactory.createTable(composite, 150,
				6);

		CompositeFactory.createLeftLabel(composite, "label.contents.of.change",
				6);

		this.textArea = CompositeFactory.createTextArea(null, composite, null,
				-1, 100, 6, true);

		this.registerButton = CompositeFactory.createSmallButton(composite,
				"label.button.add");

		this.updateButton = CompositeFactory.createSmallButton(composite,
				"label.button.update");

		this.deleteButton = CompositeFactory.createSmallButton(composite,
				"label.button.delete");

		this.replaceButton = CompositeFactory.createButton(composite,
				"label.button.change.tracking", 1, -1);
		this.comparisonDisplayButton = CompositeFactory.createButton(composite,
				"label.button.comparison.display", 1, -1);
		this.comparisonResetButton = CompositeFactory.createButton(composite,
				"label.button.comparison.reset", 1, -1);

		CompositeFactory.createTableColumn(this.changeTrackingTable,
				"label.date", 200);
		CompositeFactory.createTableColumn(this.changeTrackingTable,
				"label.contents.of.change", 500);

		this.changeTrackingTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				selectChangeTracking(index);
			}
		});

		this.registerButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChangeTracking changeTracking = new ChangeTracking(diagram
						.getDiagramContents());
				changeTracking.setComment(textArea.getText());

				Command command = new AddChangeTrackingCommand(diagram,
						changeTracking);

				viewer.getEditDomain().getCommandStack().execute(command);

				int index = changeTrackingTable.getItemCount();

				setData();

				selectChangeTracking(index);
			}
		});

		this.updateButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				ChangeTracking changeTracking = diagram.getChangeTrackingList()
						.get(index);

				Command command = new UpdateChangeTrackingCommand(
						changeTracking, textArea.getText());

				viewer.getEditDomain().getCommandStack().execute(command);

				setData();

				selectChangeTracking(index);
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				Command command = new DeleteChangeTrackingCommand(diagram,
						index);

				viewer.getEditDomain().getCommandStack().execute(command);

				setData();

				if (changeTrackingTable.getItemCount() > 0) {
					if (index >= changeTrackingTable.getItemCount()) {
						index = changeTrackingTable.getItemCount() - 1;
					}

					selectChangeTracking(index);
				}
			}
		});

		this.replaceButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}
				MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText(ResourceString
						.getResourceString("dialog.title.change.tracking"));
				messageBox.setMessage(ResourceString
						.getResourceString("dialog.message.change.tracking"));

				if (messageBox.open() == SWT.YES) {
					ChangeTracking changeTracking = new ChangeTracking(diagram
							.getDiagramContents());
					changeTracking.setComment("");

					diagram.getChangeTrackingList().addChangeTracking(
							changeTracking);

					setData();

					changeTrackingTable.select(index);
				}

				ChangeTracking changeTracking = diagram.getChangeTrackingList()
						.get(index);

				ChangeTracking copy = new ChangeTracking(changeTracking
						.getDiagramContents());

				Command command = new DisplaySelectedChangeTrackingCommand(
						diagram, copy.getDiagramContents());

				viewer.getEditDomain().getCommandStack().execute(command);
			}
		});

		this.comparisonDisplayButton
				.addSelectionListener(new SelectionAdapter() {

					/**
					 * {@inheritDoc}
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						int index = changeTrackingTable.getSelectionIndex();
						if (index == -1) {
							return;
						}

						ChangeTracking changeTracking = diagram
								.getChangeTrackingList().get(index);

						NodeSet nodeElementList = changeTracking
								.getDiagramContents().getContents();

						Command command = new CalculateChangeTrackingCommand(
								diagram, nodeElementList);

						viewer.getEditDomain().getCommandStack()
								.execute(command);

						comparisonResetButton.setEnabled(true);
					}
				});

		this.comparisonResetButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Command command = new ResetChangeTrackingCommand(diagram);
				viewer.getEditDomain().getCommandStack().execute(command);

				comparisonResetButton.setEnabled(false);
			}
		});

		this.textArea.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			setReturnCode(buttonId);
			close();
		}

		super.buttonPressed(buttonId);
	}

	private void setData() {
		this.changeTrackingTable.removeAll();

		this.setButtonEnabled(false);
		this.comparisonDisplayButton.setEnabled(false);

		for (ChangeTracking changeTracking : this.diagram
				.getChangeTrackingList().getList()) {
			TableItem tableItem = new TableItem(this.changeTrackingTable,
					SWT.NONE);

			String date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.SHORT).format(changeTracking.getUpdatedDate());
			tableItem.setText(0, date);

			if (!Check.isEmpty(changeTracking.getComment())) {
				tableItem.setText(1, changeTracking.getComment());
			} else {
				tableItem.setText(1, "*** empty log message ***");
			}
		}

		this.comparisonResetButton.setEnabled(this.diagram
				.getChangeTrackingList().isCalculated());
	}

	private void setButtonEnabled(boolean enabled) {
		this.updateButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);
		this.replaceButton.setEnabled(enabled);
		this.comparisonDisplayButton.setEnabled(enabled);
	}

	private void selectChangeTracking(int index) {
		this.changeTrackingTable.select(index);

		ChangeTracking changeTracking = this.diagram.getChangeTrackingList()
				.get(index);

		if (changeTracking.getComment() != null) {
			this.textArea.setText(changeTracking.getComment());
		} else {
			this.textArea.setText("");
		}

		if (index >= 0) {
			this.setButtonEnabled(true);
		} else {
			this.setButtonEnabled(false);
		}
	}
}
