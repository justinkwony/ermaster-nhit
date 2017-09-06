package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;

public class GroupSet extends AbstractModel implements Iterable<ColumnGroup> {

	private static final long serialVersionUID = 6192280105150073360L;

	private String database;

	private List<ColumnGroup> groups;

	public GroupSet() {
		this.groups = new ArrayList<ColumnGroup>();
	}

	public void sort() {
		Collections.sort(this.groups);
	}

	public void add(ColumnGroup group) {
		this.groups.add(group);
	}

	public void remove(ColumnGroup group) {
		this.groups.remove(group);
	}

	public Iterator<ColumnGroup> iterator() {
		return this.groups.iterator();
	}

	public List<ColumnGroup> getGroupList() {
		return this.groups;
	}

	public void clear() {
		this.groups.clear();
	}

	public boolean contains(ColumnGroup group) {
		return this.groups.contains(group);
	}

	public ColumnGroup get(int index) {
		return this.groups.get(index);
	}

	public ColumnGroup find(ColumnGroup group) {
		int index = this.groups.indexOf(group);

		if (index != -1) {
			return this.groups.get(this.groups.indexOf(group));
		}

		return null;
	}

	public ColumnGroup findSame(ColumnGroup group) {
		for (ColumnGroup columnGroup : this.groups) {
			if (columnGroup == group) {
				return columnGroup;
			}
		}

		return null;
	}

	public int indexOf(ColumnGroup group) {
		return this.groups.indexOf(group);
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
