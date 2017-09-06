package org.insightech.er.editor.view.tool;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.insightech.er.editor.model.ERDiagram;

public class MovablePanningSelectionTool extends PanningSelectionTool {

	public static boolean shift = false;

	@Override
	protected boolean handleKeyUp(KeyEvent event) {
		if (event.keyCode == SWT.SHIFT) {
			shift = true;
		}

		return super.handleKeyUp(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleKeyDown(KeyEvent event) {
		int dx = 0;
		int dy = 0;

		if (event.keyCode == SWT.SHIFT) {
			shift = true;
		}

		if (event.keyCode == SWT.ARROW_DOWN) {
			dy = 1;

		} else if (event.keyCode == SWT.ARROW_LEFT) {
			dx = -1;

		} else if (event.keyCode == SWT.ARROW_RIGHT) {
			dx = 1;

		} else if (event.keyCode == SWT.ARROW_UP) {
			dy = -1;
		}

		if (dx != 0 || dy != 0) {
			CompoundCommand compoundCommand = new CompoundCommand();

			ERDiagram diagram = (ERDiagram) this.getCurrentViewer()
					.getContents().getModel();

			List selectedEditParts = this.getCurrentViewer()
					.getSelectedEditParts();

			for (Object object : selectedEditParts) {
				if (!(object instanceof NodeElementEditPart)) {
					continue;
				}

				NodeElementEditPart editPart = (NodeElementEditPart) object;

				Rectangle rectangle = editPart.getFigure().getBounds().getCopy();
				
				rectangle.x += dx;
				rectangle.y += dy;

				Command command = ERDiagramLayoutEditPolicy
						.createChangeConstraintCommand(diagram,
								selectedEditParts, editPart, rectangle);

				if (command != null) {
					compoundCommand.add(command);
				}
			}

			this.getCurrentViewer().getEditDomain().getCommandStack()
					.execute(compoundCommand.unwrap());
		}

		return super.handleKeyDown(event);
	}

	@Override
	public void mouseDown(MouseEvent e, EditPartViewer viewer) {
		if (viewer.getContents() instanceof ERDiagramEditPart) {
			ERDiagramEditPart editPart = (ERDiagramEditPart) viewer
					.getContents();
			ERDiagram diagram = (ERDiagram) editPart.getModel();

			diagram.mousePoint = new Point(e.x, e.y);

			editPart.getFigure().translateToRelative(diagram.mousePoint);
		}

		super.mouseDown(e, viewer);
	}

}
