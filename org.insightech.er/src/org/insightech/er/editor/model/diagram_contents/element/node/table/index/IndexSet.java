package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class IndexSet extends AbstractModel implements ObjectListModel {

	private static final long serialVersionUID = 3691276015432133679L;

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.index_list");
	}

	public String getObjectType() {
		return "list";
	}
}
