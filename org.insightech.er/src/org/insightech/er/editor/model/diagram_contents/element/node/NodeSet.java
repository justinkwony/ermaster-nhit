package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImageSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.note.NoteSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;

public class NodeSet extends AbstractModel implements Iterable<NodeElement> {

	private static final long serialVersionUID = -120487815554383179L;

	private NoteSet noteSet;

	private TableSet tableSet;

	private ViewSet viewSet;

	private List<NodeElement> nodeElementList;

	private InsertedImageSet insertedImageSet;

	public NodeSet() {
		this.tableSet = new TableSet();
		this.viewSet = new ViewSet();
		this.noteSet = new NoteSet();
		this.insertedImageSet = new InsertedImageSet();

		this.nodeElementList = new ArrayList<NodeElement>();
	}

	public void sort() {
		this.tableSet.sort();
		this.viewSet.sort();
		this.noteSet.sort();
		this.insertedImageSet.sort();
	}

	public void addNodeElement(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			this.tableSet.add((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.add((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.add((Note) nodeElement);

		} else if (nodeElement instanceof InsertedImage) {
			this.insertedImageSet.add((InsertedImage) nodeElement);

		}

		this.nodeElementList.add(nodeElement);
	}

	public void remove(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			this.tableSet.remove((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.remove((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.remove((Note) nodeElement);

		} else if (nodeElement instanceof InsertedImage) {
			this.insertedImageSet.remove((InsertedImage) nodeElement);

		}

		this.nodeElementList.remove(nodeElement);
	}

	public boolean contains(NodeElement nodeElement) {
		return this.nodeElementList.contains(nodeElement);
	}

	public void clear() {
		this.tableSet.getList().clear();
		this.viewSet.getList().clear();
		this.noteSet.getList().clear();
		this.insertedImageSet.getList().clear();

		this.nodeElementList.clear();
	}

	public boolean isEmpty() {
		return this.nodeElementList.isEmpty();
	}

	public List<NodeElement> getNodeElementList() {
		return this.nodeElementList;
	}

	public List<TableView> getTableViewList() {
		List<TableView> nodeElementList = new ArrayList<TableView>();

		nodeElementList.addAll(this.tableSet.getList());
		nodeElementList.addAll(this.viewSet.getList());

		return nodeElementList;
	}

	public Iterator<NodeElement> iterator() {
		return this.getNodeElementList().iterator();
	}

	public ViewSet getViewSet() {
		return viewSet;
	}

	public NoteSet getNoteSet() {
		return noteSet;
	}

	public TableSet getTableSet() {
		return tableSet;
	}

	public InsertedImageSet getInsertedImageSet() {
		return insertedImageSet;
	}
}
