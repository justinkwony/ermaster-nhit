package org.insightech.er.editor.controller.editpart.outline.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class IndexSetOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<Index> children = new ArrayList<Index>();

		ERDiagram diagram = this.getDiagram();
		Category category = this.getCurrentCategory();

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {
			if (category == null || category.contains(table)) {
				children.addAll(table.getIndexes());
			}
		}

		Collections.sort(children);

		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.index")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
	}

}
