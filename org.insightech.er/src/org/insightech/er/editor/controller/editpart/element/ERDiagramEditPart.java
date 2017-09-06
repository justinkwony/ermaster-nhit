package org.insightech.er.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.settings.Settings;

public class ERDiagramEditPart extends AbstractModelEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		try {
			super.deactivate();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());

		return layer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ERDiagramLayoutEditPolicy());
		this.installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();

		ERDiagram diagram = (ERDiagram) this.getModel();

		// category must be first.
		modelChildren.addAll(diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories());

		modelChildren.addAll(diagram.getDiagramContents().getContents()
				.getNodeElementList());

		if (diagram.getChangeTrackingList().isCalculated()) {
			modelChildren.addAll(diagram.getChangeTrackingList()
					.getRemovedNodeElementSet());
		}

		modelChildren.add(diagram.getDiagramContents().getSettings()
				.getModelProperties());

		return modelChildren;
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("refreshChildren")) {
			this.refreshChildren();

		} else if (event.getPropertyName().equals("refreshConnection")) {
			for (NodeElement nodeElement : this.getDiagram()
					.getDiagramContents().getContents().getNodeElementList()) {
				for (ConnectionElement connection : nodeElement.getIncomings()) {
					connection.refreshVisuals();
				}
			}

		} else if (event.getPropertyName().equals("refreshSettings")) {
			this.refreshChildren();
			this.refreshSettings();

		} else if (event.getPropertyName().equals("refreshWithConnection")) {
			this.refresh();

			for (NodeElement nodeElement : this.getDiagram()
					.getDiagramContents().getContents().getNodeElementList()) {
				for (ConnectionElement connection : nodeElement.getIncomings()) {
					connection.refreshVisuals();
				}
			}

			this.getViewer().deselectAll();
			/*
			 * List<NodeElement> nodeElementList = (List<NodeElement>) event
			 * .getNewValue();
			 * 
			 * if (nodeElementList != null) { SelectionManager selectionManager
			 * = this.getViewer() .getSelectionManager();
			 * 
			 * Map<NodeElement, EditPart> modelToEditPart =
			 * getModelToEditPart();
			 * 
			 * for (NodeElement nodeElement : nodeElementList) {
			 * selectionManager.appendSelection(modelToEditPart
			 * .get(nodeElement)); } }
			 */
		}

		/*
		 * } else if (event.getPropertyName()
		 * .equals(ERDiagram.PROPERTY_CHANGE_ALL)) {
		 * 
		 * this.refresh(); this.refreshRelations();
		 * 
		 * List<NodeElement> nodeElementList = (List<NodeElement>) event
		 * .getNewValue();
		 * 
		 * if (nodeElementList != null) { this.getViewer().deselectAll();
		 * SelectionManager selectionManager = this.getViewer()
		 * .getSelectionManager();
		 * 
		 * Map<NodeElement, EditPart> modelToEditPart = getModelToEditPart();
		 * 
		 * for (NodeElement nodeElement : nodeElementList) {
		 * selectionManager.appendSelection(modelToEditPart .get(nodeElement));
		 * } }
		 */

		super.doPropertyChange(event);
	}

	@Override
	final public void refresh() {
		refreshChildren();
		refreshVisuals();

		refreshSourceConnections();
		refreshTargetConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		ERDiagram element = (ERDiagram) this.getModel();

		int[] color = element.getColor();

		if (color != null) {
			Color bgColor = Resources.getColor(color);
			this.getViewer().getControl().setBackground(bgColor);
		}

		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshVisuals();
			}
		}
	}

	private void refreshSettings() {
		ERDiagram diagram = (ERDiagram) this.getModel();
		Settings settings = diagram.getDiagramContents().getSettings();

		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshSettings(settings);
			}
		}
	}

	// private Map<NodeElement, EditPart> getModelToEditPart() {
	// Map<NodeElement, EditPart> modelToEditPart = new HashMap<NodeElement,
	// EditPart>();
	// List children = getChildren();
	//
	// for (int i = 0; i < children.size(); i++) {
	// EditPart editPart = (EditPart) children.get(i);
	// modelToEditPart.put((NodeElement) editPart.getModel(), editPart);
	// }
	//
	// return modelToEditPart;
	// }

	@Override
	public Object getAdapter(Class key) {

		if (key == SnapToHelper.class) {
			List<SnapToHelper> helpers = new ArrayList<SnapToHelper>();

			helpers.add(new SnapToGeometry(this));

			if (Boolean.TRUE.equals(getViewer().getProperty(
					SnapToGeometry.PROPERTY_SNAP_ENABLED))) {
				helpers.add(new SnapToGrid(this));
			}

			// if (Boolean.TRUE.equals(getViewer().getProperty(
			// SnapToGrid.PROPERTY_GRID_ENABLED))) {
			// helpers.add(new SnapToGrid(this));
			// }

			if (helpers.size() == 0) {
				return null;

			} else {
				return new CompoundSnapToHelper(
						helpers.toArray(new SnapToHelper[0]));
			}
		}

		return super.getAdapter(key);
	}

}
