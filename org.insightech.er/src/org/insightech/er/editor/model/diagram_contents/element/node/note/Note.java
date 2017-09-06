package org.insightech.er.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.Format;

public class Note extends NodeElement implements Comparable<Note> {

	private static final long serialVersionUID = -8810455349879962852L;

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NodeElement> getReferringElementList() {
		List<NodeElement> referringElementList = super.getReferringElementList();

		for (ConnectionElement connectionElement : this.getIncomings()) {
			NodeElement sourceElement = connectionElement.getSource();
			referringElementList.add(sourceElement);
		}

		return referringElementList;
	}

	public String getDescription() {
		return "";
	}

	public int compareTo(Note other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.text).compareTo(
				Format.null2blank(other.text));

		return compareTo;
	}

	public String getName() {
		String name = text;
		if (name == null) {
			name = "";

		} else if (name.length() > 20) {
			name = name.substring(0, 20);
		}

		return name;
	}

	public String getObjectType() {
		return "note";
	}
}
