package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class DeleteElementCommand extends AbstractCommand {

	private ERDiagram diagram;

	private NodeElement element;

	private List<Category> categoryList;

	public DeleteElementCommand(ERDiagram diagram, NodeElement element) {
		this.diagram = diagram;
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.removeContent(this.element);

		this.categoryList = new ArrayList<Category>();

		for (Category category : this.diagram.getDiagramContents()
				.getSettings().getCategorySetting().getAllCategories()) {
			if (category.contains(this.element)) {
				category.remove(this.element);
				this.categoryList.add(category);
			}
		}

		this.diagram.refreshChildren();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		for (Category category : this.categoryList) {
			category.add(this.element);
		}

		this.diagram.addContent(this.element);
		this.diagram.refreshChildren();
	}
}
