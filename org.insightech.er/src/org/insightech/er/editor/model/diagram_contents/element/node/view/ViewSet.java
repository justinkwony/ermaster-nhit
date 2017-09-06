package org.insightech.er.editor.model.diagram_contents.element.node.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class ViewSet extends AbstractModel implements ObjectListModel,
		Iterable<View> {

	private static final long serialVersionUID = -120487815554383179L;

	private List<View> viewList;

	public ViewSet() {
		this.viewList = new ArrayList<View>();
	}

	public void sort() {
		Collections.sort(this.viewList);
	}
	
	public void add(View view) {
		this.viewList.add(view);
	}

	public void add(int index, View view) {
		this.viewList.add(index, view);
	}

	public int remove(View view) {
		int index = this.viewList.indexOf(view);
		this.viewList.remove(index);

		return index;
	}

	public List<View> getList() {
		;
		return this.viewList;
	}

	public Iterator<View> iterator() {
		return this.viewList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewSet clone() {
		ViewSet viewSet = (ViewSet) super.clone();
		List<View> newViewList = new ArrayList<View>();

		for (View view : viewList) {
			View newView = (View) view.clone();
			newViewList.add(newView);
		}

		viewSet.viewList = newViewList;

		return viewSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.view_list");
	}

	public String getObjectType() {
		return "list";
	}
}
