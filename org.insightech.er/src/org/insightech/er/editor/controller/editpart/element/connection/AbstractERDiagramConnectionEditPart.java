package org.insightech.er.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public abstract class AbstractERDiagramConnectionEditPart extends
		AbstractConnectionEditPart implements PropertyChangeListener {

	private static Logger logger = Logger
			.getLogger(AbstractERDiagramConnectionEditPart.class.getName());

	private static final boolean DEBUG = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();

		AbstractModel model = (AbstractModel) this.getModel();
		model.addPropertyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		AbstractModel model = (AbstractModel) this.getModel();
		model.removePropertyChangeListener(this);

		super.deactivate();
	}

	protected ERDiagramConnection createERDiagramConnection() {
		boolean bezier = this.getDiagram().getDiagramContents().getSettings()
				.isUseBezierCurve();
		ERDiagramConnection connection = new ERDiagramConnection(bezier);
		connection.setConnectionRouter(new BendpointConnectionRouter());

		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		// this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
		// new ConnectionGraphicalNodeEditPolicy());
	}

	public final void propertyChange(PropertyChangeEvent event) {
		try {
			if (DEBUG) {
				logger.log(
						Level.INFO,
						this.getClass().getName() + ":"
								+ event.getPropertyName() + ":"
								+ event.toString());
			}

			this.doPropertyChange(event);

		} catch (Exception e) {
			ERDiagramActivator.showExceptionDialog(e);
		}
	}

	protected void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("refreshBendpoint")) {
			this.refreshBendpoints();

		} else if (event.getPropertyName().equals("refreshVisuals")) {
			this.refreshVisuals();
		}
	}

	protected ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		if (this.isActive()) {
			ConnectionElement element = (ConnectionElement) this.getModel();

			((ERDiagramConnection) this.figure).setColor(Resources
					.getColor(element.getColor()));

			this.fillterConnectionByCategory();
			this.decorateRelation();
			this.calculateAnchorLocation();
			this.refreshBendpoints();
		}
	}

	public void refreshVisualsWithColumn() {
		this.refreshVisuals();

		TableViewEditPart sourceTableViewEditPart = (TableViewEditPart) this
				.getSource();
		if (sourceTableViewEditPart != null) {
			sourceTableViewEditPart.refreshVisuals();
		}
		TableViewEditPart targetTableViewEditPart = (TableViewEditPart) this
				.getTarget();
		if (targetTableViewEditPart != null) {
			targetTableViewEditPart.refreshVisuals();
		}
	}

	private void fillterConnectionByCategory() {
		EditPart sourceEditPart = this.getSource();
		EditPart targetEditPart = this.getTarget();

		ERDiagram diagram = this.getDiagram();

		if (diagram != null) {
			Category category = this.getCurrentCategory();

			if (category != null) {
				this.figure.setVisible(false);

				CategorySetting categorySettings = this.getDiagram()
						.getDiagramContents().getSettings()
						.getCategorySetting();

				if (sourceEditPart != null && targetEditPart != null) {
					NodeElement sourceModel = (NodeElement) sourceEditPart
							.getModel();
					NodeElement targetModel = (NodeElement) targetEditPart
							.getModel();

					boolean containsSource = false;

					if (category.contains(sourceModel)) {
						containsSource = true;

					} else if (categorySettings.isShowReferredTables()) {
						for (NodeElement referringElement : sourceModel
								.getReferringElementList()) {
							if (category.contains(referringElement)) {
								containsSource = true;
								break;
							}
						}
					}

					if (containsSource) {
						if (category.contains(targetModel)) {
							this.figure.setVisible(true);

						} else if (categorySettings.isShowReferredTables()) {
							for (NodeElement referringElement : targetModel
									.getReferringElementList()) {
								if (category.contains(referringElement)) {
									this.figure.setVisible(true);
									break;
								}
							}
						}
					}
				}

			} else {
				this.figure.setVisible(true);
			}
		}
	}

	private void calculateAnchorLocation() {
		ConnectionElement connection = (ConnectionElement) this.getModel();

		NodeElementEditPart sourceEditPart = (NodeElementEditPart) this
				.getSource();

		Point sourcePoint = null;
		Point targetPoint = null;

		if (sourceEditPart != null && connection.getSourceXp() != -1
				&& connection.getSourceYp() != -1) {
			Rectangle bounds = sourceEditPart.getFigure().getBounds();
			sourcePoint = new Point(bounds.x
					+ (bounds.width * connection.getSourceXp() / 100), bounds.y
					+ (bounds.height * connection.getSourceYp() / 100));
		}

		NodeElementEditPart targetEditPart = (NodeElementEditPart) this
				.getTarget();

		if (targetEditPart != null && connection.getTargetXp() != -1
				&& connection.getTargetYp() != -1) {
			Rectangle bounds = targetEditPart.getFigure().getBounds();
			targetPoint = new Point(bounds.x
					+ (bounds.width * connection.getTargetXp() / 100), bounds.y
					+ (bounds.height * connection.getTargetYp() / 100));
		}

		ConnectionAnchor sourceAnchor = this.getConnectionFigure()
				.getSourceAnchor();

		if (sourceAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) sourceAnchor).setLocation(sourcePoint);
		}

		ConnectionAnchor targetAnchor = this.getConnectionFigure()
				.getTargetAnchor();

		if (targetAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) targetAnchor).setLocation(targetPoint);
		}
	}

	protected void refreshBendpoints() {
		ConnectionElement connection = (ConnectionElement) this.getModel();

		List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

		for (Bendpoint bendPoint : connection.getBendpoints()) {
			List<org.eclipse.draw2d.Bendpoint> realPointList = this
					.getRealBendpoint(bendPoint);

			constraint.addAll(realPointList);
		}

		this.getConnectionFigure().setRoutingConstraint(constraint);
	}

	protected List<org.eclipse.draw2d.Bendpoint> getRealBendpoint(
			Bendpoint bendPoint) {
		List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

		constraint
				.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));

		return constraint;
	}

	protected void decorateRelation() {
	}
}
