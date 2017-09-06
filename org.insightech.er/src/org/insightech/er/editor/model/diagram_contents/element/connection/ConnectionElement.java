package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public abstract class ConnectionElement extends AbstractModel implements
		Comparable<ConnectionElement> {

	private static final long serialVersionUID = -5418951773059063716L;

	protected NodeElement source;

	protected NodeElement target;

	private int sourceXp;

	private int sourceYp;

	private int targetXp;

	private int targetYp;

	private int[] color;

	private List<Bendpoint> bendPoints = new ArrayList<Bendpoint>();

	public ConnectionElement() {
		this.sourceXp = -1;
		this.sourceYp = -1;
		this.targetXp = -1;
		this.targetYp = -1;

		this.setColor(0, 0, 0);
	}

	public int compareTo(ConnectionElement other) {
		NodeElement source1 = this.getSource();
		NodeElement source2 = other.getSource();

		if (source1 != source2) {
			if (source1 == null) {
				return 1;
			}
			if (source2 == null) {
				return -1;
			}

			if (!(source1 instanceof TableView)) {
				return 1;
			}
			if (!(source2 instanceof TableView)) {
				return -1;
			}

			TableView tableView1 = (TableView) source1;
			TableView tableView2 = (TableView) source2;

			return tableView1.compareTo(tableView2);
		}

		NodeElement target1 = this.getTarget();
		NodeElement target2 = other.getTarget();

		if (target1 != target2) {
			if (target1 == null) {
				return 1;
			}
			if (target2 == null) {
				return -1;
			}

			if (!(target1 instanceof TableView)) {
				return 1;
			}
			if (!(target2 instanceof TableView)) {
				return -1;
			}

			TableView tableView1 = (TableView) target1;
			TableView tableView2 = (TableView) target2;

			return tableView1.compareTo(tableView2);
		}

		return 0;
	}

	public NodeElement getSource() {
		return source;
	}

	public void setSource(NodeElement source) {
		if (this.source != null) {
			this.source.removeOutgoing(this);
		}

		this.source = source;

		if (this.source != null) {
			this.source.addOutgoing(this);
		}
	}

	public void setSourceAndTarget(NodeElement source, NodeElement target) {
		this.source = source;
		this.target = target;
	}

	public void setTarget(NodeElement target) {
		if (this.target != null) {
			this.target.removeIncoming(this);
		}

		this.target = target;

		if (this.target != null) {
			this.target.addIncoming(this);
		}
	}

	public NodeElement getTarget() {
		return target;
	}

	public void delete() {
		source.removeOutgoing(this);
		target.removeIncoming(this);
	}

	public void connect() {
		if (this.source != null) {
			source.addOutgoing(this);
		}
		if (this.target != null) {
			target.addIncoming(this);
		}
	}

	public void addBendpoint(int index, Bendpoint point) {
		bendPoints.add(index, point);
	}

	public void setBendpoints(List<Bendpoint> points) {
		bendPoints = points;
	}

	public List<Bendpoint> getBendpoints() {
		return bendPoints;
	}

	public void removeBendpoint(int index) {
		bendPoints.remove(index);
	}

	public void replaceBendpoint(int index, Bendpoint point) {
		bendPoints.set(index, point);
	}

	public int getSourceXp() {
		return sourceXp;
	}

	public void setSourceLocationp(int sourceXp, int sourceYp) {
		this.sourceXp = sourceXp;
		this.sourceYp = sourceYp;
	}

	public int getSourceYp() {
		return sourceYp;
	}

	public int getTargetXp() {
		return targetXp;
	}

	public void setTargetLocationp(int targetXp, int targetYp) {
		this.targetXp = targetXp;
		this.targetYp = targetYp;
	}

	public int getTargetYp() {
		return targetYp;
	}

	public boolean isSourceAnchorMoved() {
		if (this.sourceXp != -1) {
			return true;
		}

		return false;
	}

	public boolean isTargetAnchorMoved() {
		if (this.targetXp != -1) {
			return true;
		}

		return false;
	}

	public void setColor(int red, int green, int blue) {
		this.color = new int[3];
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
	}

	public int[] getColor() {
		return this.color;
	}

	public void refreshBendpoint() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshBendpoint", null, null);
		}
	}

	@Override
	public ConnectionElement clone() {
		ConnectionElement clone = (ConnectionElement) super.clone();

		List<Bendpoint> cloneBendPoints = new ArrayList<Bendpoint>();
		for (Bendpoint bendPoint : bendPoints) {
			cloneBendPoints.add((Bendpoint) bendPoint.clone());
		}

		clone.bendPoints = cloneBendPoints;

		if (this.color != null) {
			clone.color = new int[] { this.color[0], this.color[1],
					this.color[2] };
		}

		return clone;
	}
}
