package org.insightech.er.editor.view.dialog.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.word.column.real.GroupColumnDialog;

public class GroupManageDialog extends AbstractDialog implements
		ERTableCompositeHolder {

	private Text groupNameText;

	private org.eclipse.swt.widgets.List groupList;

	private Button groupUpdateButton;

	private Button groupCancelButton;

	private Button groupAddButton;

	private Button groupEditButton;

	private Button groupDeleteButton;

	private Button addToGlobalGroupButton;

	private List<CopyGroup> copyGroups;

	private int editTargetIndex = -1;

	private CopyGroup copyData;

	private ERDiagram diagram;

	private boolean globalGroup;

	private ERTableComposite tableComposite;

	public GroupManageDialog(Shell parentShell, GroupSet columnGroups,
			ERDiagram diagram, boolean globalGroup, int editTargetIndex) {
		super(parentShell);

		this.copyGroups = new ArrayList<CopyGroup>();

		for (ColumnGroup columnGroup : columnGroups) {
			this.copyGroups.add(new CopyGroup(columnGroup));
		}

		this.diagram = diagram;

		this.globalGroup = globalGroup;

		this.editTargetIndex = editTargetIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createGroupListComposite(composite);
		this.createGroupDetailComposite(composite);

		this.setGroupEditEnabled(false);
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createGroupListComposite(Composite parent) {
		Group composite = CompositeFactory.createGroup(parent,
				"label.group.list", 1, 3);

		this.createGroupList(composite);

		this.groupAddButton = CompositeFactory.createMiddleButton(composite,
				"label.button.group.add");

		this.groupEditButton = CompositeFactory.createMiddleButton(composite,
				"label.button.group.edit");

		this.groupDeleteButton = CompositeFactory.createMiddleButton(composite,
				"label.button.group.delete");

		this.addToGlobalGroupButton = CompositeFactory.createLargeButton(
				composite, "label.button.add.to.global.group", 3);

		if (this.globalGroup) {
			this.addToGlobalGroupButton.setVisible(false);
		}

		this.setButtonEnabled(false);
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroupList(Composite parent) {
		GridData listGridData = new GridData();
		listGridData.grabExcessHorizontalSpace = true;
		listGridData.horizontalAlignment = GridData.FILL;
		listGridData.grabExcessVerticalSpace = true;
		listGridData.verticalAlignment = GridData.FILL;
		listGridData.horizontalSpan = 3;

		this.groupList = new org.eclipse.swt.widgets.List(parent, SWT.BORDER
				| SWT.V_SCROLL);
		this.groupList.setLayoutData(listGridData);

		this.initGroupList();
	}

	private void initGroupList() {
		Collections.sort(this.copyGroups);

		this.groupList.removeAll();

		for (ColumnGroup columnGroup : this.copyGroups) {
			this.groupList.add(columnGroup.getGroupName());
		}
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createGroupDetailComposite(Composite parent) {
		Group composite = CompositeFactory.createGroup(parent,
				"label.group.info", 1, 2);

		this.groupNameText = CompositeFactory.createText(this, composite,
				"label.group.name", 1, 200, true, false);

		GroupColumnDialog columnDialog = new GroupColumnDialog(this.getShell(),
				this.diagram);

		this.tableComposite = new ERTableComposite(this, composite,
				this.diagram, null, null, columnDialog, this, 2, true, true);

		this.createDetailButtonComposite(composite);
	}

	private void createDetailButtonComposite(Composite parent) {
		Composite composite = CompositeFactory.createChildComposite(parent, 2,
				2);

		this.groupUpdateButton = CompositeFactory.createLargeButton(composite,
				"label.button.update");

		this.groupCancelButton = CompositeFactory.createLargeButton(composite,
				"label.button.cancel");
	}

	@SuppressWarnings("unchecked")
	private void initColumnGroup() {
		String text = this.copyData.getGroupName();

		if (text == null) {
			text = "";
		}

		this.groupNameText.setText(text);

		this.tableComposite.setColumnList((List) this.copyData.getColumns());
	}

	private void setGroupEditEnabled(boolean enabled) {
		this.tableComposite.setEnabled(enabled);

		this.groupUpdateButton.setEnabled(enabled);
		this.groupCancelButton.setEnabled(enabled);
		this.groupNameText.setEnabled(enabled);

		this.groupList.setEnabled(!enabled);

		this.groupAddButton.setEnabled(!enabled);
		if (this.groupList.getSelectionIndex() != -1 && !enabled) {
			this.setButtonEnabled(true);

		} else {
			this.setButtonEnabled(false);
		}

		if (enabled) {
			this.groupNameText.setFocus();
		} else {
			this.groupList.setFocus();
		}

		this.enabledButton(!enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		if (this.groupNameText.getEnabled()) {
			String text = this.groupNameText.getText().trim();

			if (text.equals("")) {
				return "error.group.name.empty";
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
	}

	@Override
	protected String getTitle() {
		if (this.globalGroup) {
			return "dialog.title.manage.global.group";
		}
		return "dialog.title.manage.group";
	}

	@Override
	protected void setData() {
		if (this.editTargetIndex != -1) {
			this.groupList.setSelection(editTargetIndex);

			this.copyData = new CopyGroup(copyGroups.get(editTargetIndex));
			this.initColumnGroup();

			this.setGroupEditEnabled(true);
		}
	}

	public List<CopyGroup> getCopyColumnGroups() {
		return copyGroups;
	}

	private void setButtonEnabled(boolean enabled) {
		this.groupEditButton.setEnabled(enabled);
		this.groupDeleteButton.setEnabled(enabled);
		this.addToGlobalGroupButton.setEnabled(enabled);
	}

	public void selectGroup(ColumnGroup selectedColumn) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.groupAddButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = -1;

				copyData = new CopyGroup(new ColumnGroup());
				initColumnGroup();
				setGroupEditEnabled(true);
			}
		});

		this.groupEditButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				setGroupEditEnabled(true);
			}
		});

		this.groupDeleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				copyGroups.remove(editTargetIndex);

				initGroupList();

				if (copyGroups.size() == 0) {
					editTargetIndex = -1;

				} else if (editTargetIndex >= copyGroups.size()) {
					editTargetIndex = copyGroups.size() - 1;
				}

				if (editTargetIndex != -1) {
					groupList.setSelection(editTargetIndex);
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();

				} else {
					copyData = new CopyGroup(new ColumnGroup());
					initColumnGroup();
					setButtonEnabled(false);
				}

			}
		});

		this.addToGlobalGroupButton
				.addSelectionListener(new SelectionAdapter() {

					/**
					 * {@inheritDoc}
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						editTargetIndex = groupList.getSelectionIndex();
						if (editTargetIndex == -1) {
							return;
						}

						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), SWT.ICON_QUESTION | SWT.OK
								| SWT.CANCEL);
						messageBox.setText(ResourceString
								.getResourceString("label.button.add.to.global.group"));
						messageBox.setMessage(ResourceString
								.getResourceString("dialog.message.add.to.global.group"));

						if (messageBox.open() == SWT.OK) {
							CopyGroup columnGroup = copyGroups
									.get(editTargetIndex);

							GroupSet columnGroups = GlobalGroupSet.load();

							columnGroups.add(columnGroup);

							GlobalGroupSet.save(columnGroups);
						}

					}
				});

		this.groupList.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				setGroupEditEnabled(true);
			}
		});

		this.groupList.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					editTargetIndex = groupList.getSelectionIndex();
					if (editTargetIndex == -1) {
						return;
					}
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();
					setButtonEnabled(true);

				} catch (Exception ex) {
					ERDiagramActivator.showExceptionDialog(ex);
				}
			}
		});

		this.groupUpdateButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (validate()) {
						String text = groupNameText.getText().trim();
						copyData.setGroupName(text);

						if (editTargetIndex == -1) {
							copyGroups.add(copyData);

						} else {
							copyGroups.remove(editTargetIndex);
							copyData = (CopyGroup) copyData.restructure(null);

							copyGroups.add(editTargetIndex, copyData);
						}

						setGroupEditEnabled(false);
						initGroupList();

						for (int i = 0; i < copyGroups.size(); i++) {
							ColumnGroup columnGroup = copyGroups.get(i);

							if (columnGroup == copyData) {
								groupList.setSelection(i);
								copyData = new CopyGroup(copyGroups.get(i));
								initColumnGroup();
								setButtonEnabled(true);
								break;
							}

						}
					}
				} catch (Exception ex) {
					ERDiagramActivator.showExceptionDialog(ex);
				}
			}

		});

		this.groupCancelButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setGroupEditEnabled(false);
				if (editTargetIndex != -1) {
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();
				}
			}
		});
	}

}
