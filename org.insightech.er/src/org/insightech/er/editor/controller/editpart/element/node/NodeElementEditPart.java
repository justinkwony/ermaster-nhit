package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.AbstractERDiagramConnectionEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;
import org.insightech.er.util.Check;

public abstract class NodeElementEditPart extends AbstractModelEditPart
		implements NodeEditPart, DeleteableEditPart {

	private Font font;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		super.deactivate();
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("refreshFont")) {
			this.changeFont(this.figure);
			this.refreshVisuals();

		} else if (event.getPropertyName().equals("refreshSourceConnections")) {
			this.refreshSourceConnections();

		} else if (event.getPropertyName().equals("refreshTargetConnections")) {
			this.refreshTargetConnections();

			// this.getFigure().getUpdateManager().performValidation();
		}

		super.doPropertyChange(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new NodeElementGraphicalNodeEditPolicy());
		this.installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
	}

	protected void setVisible() {
		NodeElement element = (NodeElement) this.getModel();
		Category category = this.getCurrentCategory();

		if (category != null) {
			this.figure.setVisible(category.isVisible(element,
					this.getDiagram()));

		} else {
			this.figure.setVisible(true);
		}
	}

	protected Font changeFont(IFigure figure) {
		NodeElement nodeElement = (NodeElement) this.getModel();

		String fontName = nodeElement.getFontName();
		int fontSize = nodeElement.getFontSize();

		if (Check.isEmpty(fontName)) {
			FontData fontData = Display.getCurrent().getSystemFont()
					.getFontData()[0];
			fontName = fontData.getName();
			nodeElement.setFontName(fontName);
		}
		if (fontSize <= 0) {
			fontSize = ViewableModel.DEFAULT_FONT_SIZE;
			nodeElement.setFontSize(fontSize);
		}

		this.font = Resources.getFont(fontName, fontSize);

		figure.setFont(this.font);

		return font;
	}

	protected void doRefreshVisuals() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void refreshVisuals() {
		this.refreshChildren();
		this.doRefreshVisuals();
		this.setVisible();

		NodeElement element = (NodeElement) this.getModel();
		IFigure figure = this.getFigure();

		int[] color = element.getColor();

		if (color != null) {
			ChangeTrackingList changeTrackingList = this.getDiagram()
					.getChangeTrackingList();

			if (changeTrackingList.isCalculated()
					&& (element instanceof Note || element instanceof ERTable)) {
				if (changeTrackingList.isAdded(element)) {
					figure.setBackgroundColor(Resources.ADDED_COLOR);

				} else if (changeTrackingList.getUpdatedNodeElement(element) != null) {
					figure.setBackgroundColor(Resources.UPDATED_COLOR);

				} else {
					figure.setBackgroundColor(ColorConstants.white);
				}

			} else {
				Color bgColor = Resources.getColor(color);
				figure.setBackgroundColor(bgColor);
			}

		}

		Rectangle rectangle = this.getRectangle();

		GraphicalEditPart parent = (GraphicalEditPart) this.getParent();

		parent.setLayoutConstraint(this, figure, rectangle);

		this.getFigure().getUpdateManager().performValidation();

		element.setActualLocation(this.toLocation(this.getFigure().getBounds()));

		this.refreshMovedAnchor();
	}

	private Location toLocation(Rectangle rectangle) {
		return new Location(rectangle.x, rectangle.y, rectangle.width,
				rectangle.height);
	}

	private void refreshMovedAnchor() {
		for (Object sourceConnection : this.getSourceConnections()) {
			ConnectionEditPart editPart = (ConnectionEditPart) sourceConnection;
			ConnectionElement connectinoElement = (ConnectionElement) editPart
					.getModel();
			if (connectinoElement.isSourceAnchorMoved()) {
				((AbstractERDiagramConnectionEditPart) editPart)
						.refreshVisuals();
			}
		}

		for (Object targetConnection : this.getTargetConnections()) {
			ConnectionEditPart editPart = (ConnectionEditPart) targetConnection;
			ConnectionElement connectinoElement = (ConnectionElement) editPart
					.getModel();
			if (connectinoElement.isTargetAnchorMoved()) {
				if (connectinoElement.getSource() != connectinoElement
						.getTarget()) {
					((AbstractERDiagramConnectionEditPart) editPart)
							.refreshVisuals();
				}
			}
		}
	}

	protected Rectangle getRectangle() {
		NodeElement element = (NodeElement) this.getModel();

		Point point = new Point(element.getX(), element.getY());

		Dimension dimension = new Dimension(element.getWidth(),
				element.getHeight());

		Dimension minimumSize = this.figure.getMinimumSize();

		if (dimension.width != -1 && dimension.width < minimumSize.width) {
			dimension.width = minimumSize.width;
		}
		if (dimension.height != -1 && dimension.height < minimumSize.height) {
			dimension.height = minimumSize.height;
		}

		return new Rectangle(point, dimension);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelSourceConnections() {
		NodeElement element = (NodeElement) this.getModel();
		return element.getOutgoings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelTargetConnections() {
		NodeElement element = (NodeElement) this.getModel();
		return element.getIncomings();
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart editPart) {
		// if (!(editPart instanceof RelationEditPart)) {
		// return super.getSourceConnectionAnchor(editPart);
		// }

		ConnectionElement connection = (ConnectionElement) editPart.getModel();

		Rectangle bounds = this.getFigure().getBounds();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		if (connection.getSourceXp() != -1 && connection.getSourceYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * connection.getSourceXp() / 100), bounds.y
					+ (bounds.height * connection.getSourceYp() / 100)));
		}

		return anchor;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			// if (!(connectionEditPart instanceof RelationEditPart)) {
			// return super.getSourceConnectionAnchor(request);
			// }

			ConnectionElement connection = (ConnectionElement) connectionEditPart
					.getModel();
			if (connection.getSource() == connection.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(connection.getSource())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure sourceFigure = ((TableViewEditPart) connectionEditPart
					.getSource()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = sourceFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, sourceFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return new ChopboxAnchor(this.getFigure());
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart editPart) {
		// if (!(editPart instanceof RelationEditPart)) {
		// return new ChopboxAnchor(this.getFigure());
		// }

		ConnectionElement connection = (ConnectionElement) editPart.getModel();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		Rectangle bounds = this.getFigure().getBounds();

		if (connection.getTargetXp() != -1 && connection.getTargetYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * connection.getTargetXp() / 100), bounds.y
					+ (bounds.height * connection.getTargetYp() / 100)));
		}

		return anchor;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			// if (!(connectionEditPart instanceof RelationEditPart)) {
			// return super.getTargetConnectionAnchor(request);
			// }

			ConnectionElement connection = (ConnectionElement) connectionEditPart
					.getModel();
			if (connection.getSource() == connection.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(connection.getTarget())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure targetFigure = ((AbstractModelEditPart) connectionEditPart
					.getTarget()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = targetFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, targetFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return new ChopboxAnchor(this.getFigure());
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	public static Point getIntersectionPoint(Point s, IFigure figure) {

		Rectangle r = figure.getBounds();

		int x1 = s.x - r.x;
		int x2 = r.x + r.width - s.x;
		int y1 = s.y - r.y;
		int y2 = r.y + r.height - s.y;

		int x = 0;
		int dx = 0;
		if (x1 < x2) {
			x = r.x;
			dx = x1;

		} else {
			x = r.x + r.width;
			dx = x2;
		}

		int y = 0;
		int dy = 0;

		if (y1 < y2) {
			y = r.y;
			dy = y1;

		} else {
			y = r.y + r.height;
			dy = y2;
		}

		if (dx < dy) {
			y = s.y;
		} else {
			x = s.x;
		}

		return new Point(x, y);
	}

	public void refreshSettings(Settings settings) {
		this.refresh();

		for (Object object : this.getSourceConnections()) {
			AbstractERDiagramConnectionEditPart editPart = (AbstractERDiagramConnectionEditPart) object;
			ERDiagramConnection connection = (ERDiagramConnection) editPart
					.getFigure();
			connection.setBezier(settings.isUseBezierCurve());

			editPart.refresh();
		}
	}

	public boolean isDeleteable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelected(int value) {
		if (value != 0) {
			for (Object editPartObject : this.getViewer()
					.getSelectedEditParts()) {
				if (editPartObject instanceof ColumnEditPart) {
					((ColumnEditPart) editPartObject).setSelected(0);
				}
			}
		}

		super.setSelected(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				performRequestOpen();

			} catch (Exception e) {
				ERDiagramActivator.showExceptionDialog(e);
			}
		}

		super.performRequest(request);
	}

	public void reorder() {
		IFigure parentFigure = this.figure.getParent();
		parentFigure.remove(this.figure);
		parentFigure.add(this.figure);
		this.figure.repaint();
	}

	abstract protected void performRequestOpen();
}
