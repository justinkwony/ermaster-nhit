package org.insightech.er.editor.model.diagram_contents.not_element.tablespace;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.not_element.ObjectSet;

public class TablespaceSet extends ObjectSet<Tablespace> {

	private static final long serialVersionUID = 9018173533566296453L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceSet clone() {
		return (TablespaceSet) super.clone();
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.tablespace_list");
	}
}
