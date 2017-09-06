package org.insightech.er.editor.view.action.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.ChangeBackgroundColorCommand;
import org.insightech.er.editor.controller.command.display.ChangeConnectionColorCommand;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ChangeBackgroundColorAction extends SelectionAction {

	public static final String ID = ChangeBackgroundColorAction.class.getName();

	private RGB rgb;

	private Image image;

	public ChangeBackgroundColorAction(IWorkbenchPart part, ERDiagram diagram) {
		super(part, Action.AS_DROP_DOWN_MENU);

		this.setId(ID);

		this.setText(ResourceString
				.getResourceString("action.title.change.background.color"));
		this.setToolTipText(ResourceString
				.getResourceString("action.title.change.background.color"));

		int[] defaultColor = diagram.getDefaultColor();

		this.rgb = new RGB(defaultColor[0], defaultColor[1], defaultColor[2]);
		this.setColorToImage();
	}

	private void setColorToImage() {
		ImageData imageData = ERDiagramActivator.getImageDescriptor(
				ImageKey.CHANGE_BACKGROUND_COLOR).getImageData();
		int blackPixel = imageData.palette.getPixel(new RGB(0, 0, 0));
		imageData.transparentPixel = imageData.palette.getPixel(new RGB(255,
				255, 255));
		imageData.palette.colors[blackPixel] = this.rgb;

		// if (this.image != null) {
		// this.image.dispose();
		// }
		this.image = new Image(Display.getCurrent(), imageData);

		ImageDescriptor descriptor = ImageDescriptor.createFromImage(image);
		this.setImageDescriptor(descriptor);
	}

	private void setRGB(RGB rgb) {
		this.rgb = rgb;

		EditPart editPart = ((ERDiagramEditor) this.getWorkbenchPart())
				.getGraphicalViewer().getContents();
		ERDiagram diagram = (ERDiagram) editPart.getModel();
		diagram.setDefaultColor(this.rgb.red, this.rgb.green, this.rgb.blue);

		this.setColorToImage();
	}
	
	public void setRGB() {
		EditPart editPart = ((ERDiagramEditor) this.getWorkbenchPart())
				.getGraphicalViewer().getContents();
		ERDiagram diagram = (ERDiagram) editPart.getModel();
		
		this.rgb = diagram.getDefaultColorAsGRB();
		
		this.setColorToImage();		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void runWithEvent(Event event) {
		Command command = this.createCommand(this.getSelectedObjects(), rgb);
		this.getCommandStack().execute(command);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getSelectedObjects() {
		List objects = new ArrayList(super.getSelectedObjects());
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			if (iter.next() instanceof NormalColumnEditPart) {
				iter.remove();
			}
		}
		return objects;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		List objects = this.getSelectedObjects();

		if (objects.isEmpty()) {
			return false;
		}

		if (!(objects.get(0) instanceof GraphicalEditPart)) {
			return false;
		}

		return true;
	}

	private Command createCommand(List objects, RGB rgb) {
		if (objects.isEmpty()) {
			return null;
		}

		if (!(objects.get(0) instanceof GraphicalEditPart)) {
			return null;
		}

		CompoundCommand command = new CompoundCommand();

		for (int i = 0; i < objects.size(); i++) {
			GraphicalEditPart part = (GraphicalEditPart) objects.get(i);
			Object modelObject = part.getModel();
			
			if (modelObject instanceof ViewableModel) {
				command.add(new ChangeBackgroundColorCommand(
						(ViewableModel) modelObject, rgb.red, rgb.green,
						rgb.blue));
				
			} else if (modelObject instanceof ConnectionElement) {
				command.add(new ChangeConnectionColorCommand(
						(ConnectionElement) modelObject, rgb.red, rgb.green,
						rgb.blue));
			
			}
		}

		return command;
	}

	public static class ChangeBackgroundColorRetargetAction extends
			LabelRetargetAction {
		public ChangeBackgroundColorRetargetAction() {
			super(ID, ResourceString
					.getResourceString("action.title.change.background.color"),
					Action.AS_DROP_DOWN_MENU);

			this.setImageDescriptor(ERDiagramActivator
					.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR));
			this.setDisabledImageDescriptor(ERDiagramActivator
					.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR_DISABLED));
			this.setToolTipText(ResourceString
					.getResourceString("action.title.change.background.color"));

			setMenuCreator(new IMenuCreator() {
				public Menu getMenu(Control parent) {
					Menu menu = new Menu(parent);

					try {
						MenuItem item1 = new MenuItem(menu, SWT.NONE);
						item1.setText(ResourceString
								.getResourceString("action.title.select.color"));
						item1.setImage(ERDiagramActivator.getImage(ImageKey.PALETTE));

						item1.addSelectionListener(new SelectionAdapter() {

							/**
							 * {@inheritDoc}
							 */
							@Override
							public void widgetSelected(SelectionEvent e) {
								ColorDialog colorDialog = new ColorDialog(
										PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow()
												.getShell(), SWT.NULL);

								colorDialog.setText(ResourceString
										.getResourceString("dialog.title.change.background.color"));

								ChangeBackgroundColorAction action = (ChangeBackgroundColorAction) getActionHandler();

								RGB rgb = colorDialog.open();

								action.setRGB(rgb);
								action.runWithEvent(null);
							}
						});
					} catch (Exception e) {
						ERDiagramActivator.showExceptionDialog(e);
					}
					return menu;
				}

				public Menu getMenu(Menu parent) {
					return null;
				}

				public void dispose() {

				}
			});
		}
	}

	@Override
	public void dispose() {
		this.image.dispose();

		super.dispose();
	}
}
