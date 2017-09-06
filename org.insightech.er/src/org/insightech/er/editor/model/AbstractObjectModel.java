package org.insightech.er.editor.model;

import org.insightech.er.util.Format;

public abstract class AbstractObjectModel extends AbstractModel implements
		Comparable<AbstractObjectModel>, ObjectModel {

	private static final long serialVersionUID = -7450893485538582071L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(AbstractObjectModel other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.name).toUpperCase()
				.compareTo(Format.null2blank(other.name).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		return compareTo;
	}
}
