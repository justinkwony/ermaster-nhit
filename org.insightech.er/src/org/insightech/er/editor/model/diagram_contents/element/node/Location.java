package org.insightech.er.editor.model.diagram_contents.element.node;

import java.io.Serializable;

public class Location implements Serializable, Cloneable {

	private static final long serialVersionUID = -6000221452172017444L;

	public int x;

	public int y;

	public int width;

	public int height;

	public Location(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Location clone() {
		try {
			return (Location) super.clone();

		} catch (CloneNotSupportedException ignore) {
		}

		return null;
	}

	@Override
	public String toString() {
		return "Location (x:" + this.x + ", y:" + this.y + ", width:"
				+ this.width + ", height:" + this.height + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
