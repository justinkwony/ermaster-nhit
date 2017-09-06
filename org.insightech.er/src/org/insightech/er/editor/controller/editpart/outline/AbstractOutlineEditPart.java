package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractOutlineEditPart extends AbstractTreeEditPart
		implements PropertyChangeListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();
		((AbstractModel) getModel()).addPropertyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		((AbstractModel) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	public void propertyChange(PropertyChangeEvent event) {
		this.refreshOutline();
	}

	public void refreshOutline() {
		refreshChildren();
		refreshVisuals();

		for (Object child : this.getChildren()) {
			AbstractOutlineEditPart part = (AbstractOutlineEditPart) child;
			part.refreshOutline();
		}
	}

	@Override
	public void refresh() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void refreshVisuals() {
		this.refreshOutlineVisuals();
	}

	protected ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	abstract protected void refreshOutlineVisuals();

	protected void execute(Command command) {
		this.getViewer().getEditDomain().getCommandStack().execute(command);
	}
}
