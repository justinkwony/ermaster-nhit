package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class MoveElementCommand extends AbstractCommand {

	protected int x;

	protected int oldX;

	protected int y;

	protected int oldY;

	protected int width;

	protected int oldWidth;

	protected int height;

	protected int oldHeight;

	private NodeElement element;

	private Map<Category, Location> oldCategoryLocationMap;

	private Map<Category, Location> newCategoryLocationMap;

	private List<Category> removedCategories;

	private List<Category> addCategories;

	protected ERDiagram diagram;

	private Category currentCategory;

	private Rectangle bounds;

	public MoveElementCommand(ERDiagram diagram, Rectangle bounds, int x,
			int y, int width, int height, NodeElement element) {

		this.element = element;
		this.setNewRectangle(x, y, width, height);

		this.oldX = element.getX();
		this.oldY = element.getY();
		this.oldWidth = element.getWidth();
		this.oldHeight = element.getHeight();

		this.removedCategories = new ArrayList<Category>();
		this.addCategories = new ArrayList<Category>();

		this.bounds = bounds;
		this.diagram = diagram;
		this.currentCategory = diagram.getCurrentCategory();

		this.oldCategoryLocationMap = new HashMap<Category, Location>();
		this.newCategoryLocationMap = new HashMap<Category, Location>();
	}

	protected void setNewRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private void initCategory(ERDiagram diagram, Location elementLocation) {
		for (Category category : diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories()) {
			if (category.contains(element)) {
				if (this.currentCategory == null) {
					if (elementLocation.x + elementLocation.width < category
							.getX()
							|| elementLocation.x > category.getX()
									+ category.getWidth()
							|| elementLocation.y + elementLocation.height < category
									.getY()
							|| elementLocation.y > category.getY()
									+ category.getHeight()) {

						this.removedCategories.add(category);

						continue;
					}
				}

				Location newCategoryLocation = category
						.getNewCategoryLocation(elementLocation);

				if (newCategoryLocation != null) {
					this.newCategoryLocationMap.put(category,
							newCategoryLocation);
					this.oldCategoryLocationMap.put(category,
							category.getLocation());
				}

			} else {
				if (diagram.getCurrentCategory() == null) {
					if (elementLocation.x >= category.getX()
							&& elementLocation.x + elementLocation.width <= category
									.getX() + category.getWidth()
							&& elementLocation.y >= category.getY()
							&& elementLocation.y + bounds.height <= category
									.getY() + category.getHeight()) {
						this.addCategories.add(category);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (this.bounds != null) {
			Location elementLocation = new Location(x, y, bounds.width,
					bounds.height);

			if (elementLocation.width < width) {
				elementLocation.width = width;
			}
			if (elementLocation.height < height) {
				elementLocation.height = height;
			}

			this.initCategory(diagram, elementLocation);
		}

		for (Category category : this.newCategoryLocationMap.keySet()) {
			category.setLocation(this.newCategoryLocationMap.get(category));
			category.refreshVisuals();
		}

		for (Category category : this.removedCategories) {
			category.remove(this.element);
		}

		for (Category category : this.addCategories) {
			category.add(this.element);
		}

		this.element.setLocation(new Location(x, y, width, height));
		this.element.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.element.setLocation(new Location(oldX, oldY, oldWidth, oldHeight));
		this.element.refreshVisuals();

		for (Category category : this.oldCategoryLocationMap.keySet()) {
			category.setLocation(this.oldCategoryLocationMap.get(category));
			category.refreshVisuals();
		}

		for (Category category : this.removedCategories) {
			category.add(this.element);
		}

		for (Category category : this.addCategories) {
			category.remove(this.element);
		}
	}
}
