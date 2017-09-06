package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.view.figure.CategoryFigure;

public class CategoryEditPart extends NodeElementEditPart implements IResizable {

	public CategoryEditPart() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		Category category = (Category) this.getModel();
		CategoryFigure figure = new CategoryFigure(category.getName());

		this.changeFont(figure);
		
		return figure;
	}

	/**
	 * When category management (Change Setting Command) is executed, new
	 * categorie's bounds will be calculated. If undo, replaced all with old
	 * categories.
	 */
	@Override
	protected Rectangle getRectangle() {
		Rectangle rectangle = super.getRectangle();

		Category category = (Category) this.getModel();
		ERDiagramEditPart rootEditPart = (ERDiagramEditPart) this.getRoot()
				.getContents();

		for (Object child : rootEditPart.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart editPart = (NodeElementEditPart) child;
				NodeElement element = (NodeElement) editPart.getModel();

				if (category.contains((NodeElement) editPart.getModel())) {
					Location bounds = element.getActualLocation();

					if (bounds.x < rectangle.x) {
						rectangle.width += rectangle.x - bounds.x;
						rectangle.x = bounds.x;
					}
					if (bounds.y < rectangle.y) {
						rectangle.height += rectangle.y - bounds.y;
						rectangle.y = bounds.y;
					}
					if (bounds.x + bounds.width > rectangle.x + rectangle.width) {
						rectangle.width = bounds.x + bounds.width - rectangle.x;
					}
					if (bounds.y + bounds.height > rectangle.y
							+ rectangle.height) {
						rectangle.height = bounds.y + bounds.height
								- rectangle.y;
					}

				}
			}
		}

		if (rectangle.x != category.getX() || rectangle.y != category.getY()
				|| rectangle.width != category.getWidth()
				|| rectangle.height != category.getHeight()) {
			category.setLocation(new Location(rectangle.x, rectangle.y,
					rectangle.width, rectangle.height));
		}

		return rectangle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());

		super.createEditPolicies();
	}

	@Override
	protected void performRequestOpen() {
	}

	@Override
	public void doRefreshVisuals() {
		CategoryFigure figure = (CategoryFigure) this.getFigure();
		Category category = (Category) this.getModel();

		figure.setName(category.getName());
	}

}
