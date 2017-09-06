package org.insightech.er.editor.controller.editpart.outline.sequence;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.widgets.TreeItem;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class SequenceSetOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		SequenceSet sequenceSet = (SequenceSet) this.getModel();

		return sequenceSet.getObjectList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		if (!DBManagerFactory.getDBManager(this.getDiagram()).isSupported(
				DBManager.SUPPORT_SEQUENCE)) {
			((TreeItem) getWidget()).setForeground(ColorConstants.lightGray);

		} else {
			((TreeItem) getWidget()).setForeground(ColorConstants.black);

		}

		this.setWidgetText(ResourceString.getResourceString("label.sequence")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
	}

}
