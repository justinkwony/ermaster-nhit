package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractCreateElementCommand extends AbstractCommand {

	protected ERDiagram diagram;

	protected Category category;

	protected Location newCategoryLocation;

	protected Location oldCategoryLocation;

	public AbstractCreateElementCommand(ERDiagram diagram) {
		this.diagram = diagram;
		this.category = this.diagram.getCurrentCategory();
		if (this.category != null) {
			this.oldCategoryLocation = this.category.getLocation();
		}
	}

	protected void addToCategory(NodeElement nodeElement) {
		if (this.category != null) {
			this.category.add(nodeElement);
			Location newLocation = category
					.getNewCategoryLocation(nodeElement);

			if (newLocation != null) {
				this.newCategoryLocation = newLocation;
				this.category.setLocation(this.newCategoryLocation);
			}
		}
	}

	protected void removeFromCategory(NodeElement nodeElement) {
		if (this.category != null) {
			this.category.remove(nodeElement);

			if (this.newCategoryLocation != null) {
				this.category.setLocation(this.oldCategoryLocation);
			}
		}
	}

}
