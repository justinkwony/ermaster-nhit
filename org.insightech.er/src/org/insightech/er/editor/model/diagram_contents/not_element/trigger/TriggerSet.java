package org.insightech.er.editor.model.diagram_contents.not_element.trigger;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.not_element.ObjectSet;

public class TriggerSet extends ObjectSet<Trigger> {

	private static final long serialVersionUID = 1L;

	@Override
	public TriggerSet clone() {
		return (TriggerSet) super.clone();
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.trigger_list");
	}

}
