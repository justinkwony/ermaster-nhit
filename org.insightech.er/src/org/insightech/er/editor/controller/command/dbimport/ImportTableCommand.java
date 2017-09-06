package org.insightech.er.editor.controller.command.dbimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.AbstractCreateElementCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class ImportTableCommand extends AbstractCreateElementCommand {

	private static final int AUTO_GRAPH_LIMIT = 100;

	private static final int ORIGINAL_X = 20;
	private static final int ORIGINAL_Y = 20;

	private static final int DISTANCE_X = 300;
	private static final int DISTANCE_Y = 300;

	private SequenceSet sequenceSet;

	private TriggerSet triggerSet;

	private TablespaceSet tablespaceSet;

	private GroupSet columnGroupSet;

	private List<NodeElement> nodeElementList;

	private List<Sequence> sequences;

	private List<Trigger> triggers;

	private List<Tablespace> tablespaces;

	private List<ColumnGroup> columnGroups;

	public ImportTableCommand(ERDiagram diagram,
			List<NodeElement> nodeElementList, List<Sequence> sequences,
			List<Trigger> triggers, List<Tablespace> tablespaces,
			List<ColumnGroup> columnGroups) {
		super(diagram);

		this.nodeElementList = nodeElementList;
		this.sequences = sequences;
		this.triggers = triggers;
		this.tablespaces = tablespaces;
		this.columnGroups = columnGroups;

		DiagramContents diagramContents = this.diagram.getDiagramContents();

		this.sequenceSet = diagramContents.getSequenceSet();
		this.triggerSet = diagramContents.getTriggerSet();
		this.tablespaceSet = diagramContents.getTablespaceSet();
		this.columnGroupSet = diagramContents.getGroups();

		this.decideLocation();
	}

	@SuppressWarnings("unchecked")
	private void decideLocation() {

		if (this.nodeElementList.size() < AUTO_GRAPH_LIMIT) {
			DirectedGraph graph = new DirectedGraph();

			Map<NodeElement, Node> nodeElementNodeMap = new HashMap<NodeElement, Node>();

			int fontSize = this.diagram.getFontSize();

			Insets insets = new Insets(5 * fontSize, 10 * fontSize,
					35 * fontSize, 20 * fontSize);

			for (NodeElement nodeElement : this.nodeElementList) {
				Node node = new Node();

				node.setPadding(insets);
				graph.nodes.add(node);
				nodeElementNodeMap.put(nodeElement, node);
			}

			for (NodeElement nodeElement : this.nodeElementList) {
				for (ConnectionElement outgoing : nodeElement.getOutgoings()) {
					Node sourceNode = nodeElementNodeMap.get(outgoing
							.getSource());
					Node targetNode = nodeElementNodeMap.get(outgoing
							.getTarget());
					if (sourceNode != targetNode) {
						Edge edge = new Edge(sourceNode, targetNode);
						graph.edges.add(edge);
					}
				}
			}

			DirectedGraphLayout layout = new DirectedGraphLayout();

			layout.visit(graph);

			for (NodeElement nodeElement : nodeElementNodeMap.keySet()) {
				Node node = nodeElementNodeMap.get(nodeElement);

				if (nodeElement.getWidth() == 0) {
					nodeElement
							.setLocation(new Location(node.x, node.y, -1, -1));
				}
			}

		} else {
			int numX = (int) Math.sqrt(this.nodeElementList.size());

			int x = ORIGINAL_X;
			int y = ORIGINAL_Y;

			for (NodeElement nodeElement : this.nodeElementList) {
				if (nodeElement.getWidth() == 0) {
					nodeElement.setLocation(new Location(x, y, -1, -1));

					x += DISTANCE_X;
					if (x > DISTANCE_X * numX) {
						x = ORIGINAL_X;
						y += DISTANCE_Y;
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.getEditor().getActiveEditor().removeSelection();

		if (this.columnGroups != null) {
			for (ColumnGroup columnGroup : columnGroups) {
				this.columnGroupSet.add(columnGroup);
			}
		}

		for (NodeElement nodeElement : this.nodeElementList) {
			this.diagram.addNewContent(nodeElement);
			this.addToCategory(nodeElement);

			if (nodeElement instanceof TableView) {
				for (NormalColumn normalColumn : ((TableView) nodeElement)
						.getNormalColumns()) {
					if (normalColumn.isForeignKey()) {
						for (Relation relation : normalColumn.getRelationList()) {
							if (relation.getSourceTableView() == nodeElement) {
								this.setSelfRelation(relation);
							}
						}
					}
				}
			}
		}

		for (Sequence sequence : sequences) {
			this.sequenceSet.addObject(sequence);
		}

		for (Trigger trigger : triggers) {
			this.triggerSet.addObject(trigger);
		}

		for (Tablespace tablespace : tablespaces) {
			this.tablespaceSet.addObject(tablespace);
		}

		this.diagram.refreshChildren();
		this.diagram.refreshOutline();
		
		if (this.category != null) {
			this.category.refresh();
		}
	}

	private void setSelfRelation(Relation relation) {
		boolean anotherSelfRelation = false;

		TableView sourceTable = relation.getSourceTableView();
		for (Relation otherRelation : sourceTable.getOutgoingRelations()) {
			if (otherRelation == relation) {
				continue;
			}
			if (otherRelation.getSource() == otherRelation.getTarget()) {
				anotherSelfRelation = true;
				break;
			}
		}

		int rate = 0;

		if (anotherSelfRelation) {
			rate = 50;

		} else {
			rate = 100;
		}

		Bendpoint bendpoint0 = new Bendpoint(rate, rate);
		bendpoint0.setRelative(true);

		int xp = 100 - (rate / 2);
		int yp = 100 - (rate / 2);

		relation.setSourceLocationp(100, yp);
		relation.setTargetLocationp(xp, 100);

		relation.addBendpoint(0, bendpoint0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.diagram.getEditor().getActiveEditor().removeSelection();

		for (NodeElement nodeElement : this.nodeElementList) {
			this.diagram.removeContent(nodeElement);
			this.removeFromCategory(nodeElement);

			if (nodeElement instanceof TableView) {
				for (NormalColumn normalColumn : ((TableView) nodeElement)
						.getNormalColumns()) {
					this.diagram.getDiagramContents().getDictionary()
							.remove(normalColumn);
				}
			}
		}

		for (Sequence sequence : sequences) {
			this.sequenceSet.remove(sequence);
		}

		for (Trigger trigger : triggers) {
			this.triggerSet.remove(trigger);
		}

		for (Tablespace tablespace : tablespaces) {
			this.tablespaceSet.remove(tablespace);
		}

		if (this.columnGroups != null) {
			for (ColumnGroup columnGroup : columnGroups) {
				this.columnGroupSet.remove(columnGroup);
			}
		}

		this.diagram.refreshChildren();
		this.diagram.refreshOutline();
		
		if (this.category != null) {
			this.category.refresh();
		}
	}
}
