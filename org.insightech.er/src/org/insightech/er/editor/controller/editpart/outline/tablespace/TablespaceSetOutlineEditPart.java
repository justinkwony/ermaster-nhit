package org.insightech.er.editor.controller.editpart.outline.tablespace;

import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class TablespaceSetOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		TablespaceSet tablespaceSet = (TablespaceSet) this.getModel();

		return tablespaceSet.getObjectList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.tablespace")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
	}

}
