package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class CategorySetting implements Serializable, Cloneable {

	private static final long serialVersionUID = -7691417386790834828L;

	private List<Category> allCategories;

	private List<Category> selectedCategories;

	private boolean freeLayout;

	private boolean showReferredTables;

	public boolean isFreeLayout() {
		return freeLayout;
	}

	public void setFreeLayout(boolean freeLayout) {
		this.freeLayout = freeLayout;
	}

	public boolean isShowReferredTables() {
		return showReferredTables;
	}

	public void setShowReferredTables(boolean showReferredTables) {
		this.showReferredTables = showReferredTables;
	}

	public CategorySetting() {
		this.allCategories = new ArrayList<Category>();
		this.selectedCategories = new ArrayList<Category>();
	}

	public void setSelectedCategories(List<Category> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public List<Category> getAllCategories() {
		return this.allCategories;
	}

	public void addCategory(Category category) {
		this.allCategories.add(category);
	}

	public void addCategoryAsSelected(Category category) {
		this.addCategory(category);
		this.selectedCategories.add(category);
	}

	public void removeCategory(Category category) {
		this.allCategories.remove(category);
		this.selectedCategories.remove(category);
	}

	public void removeCategory(int index) {
		this.allCategories.remove(index);
	}

	public boolean isSelected(Category tableCategory) {
		if (this.selectedCategories.contains(tableCategory)) {
			return true;
		}

		return false;
	}

	public List<Category> getSelectedCategories() {
		return selectedCategories;
	}

	public Object clone(Map<Category, Category> categoryCloneMap) {
		try {
			CategorySetting clone = (CategorySetting) super.clone();
			clone.allCategories = new ArrayList<Category>();
			clone.selectedCategories = new ArrayList<Category>();

			for (Category category : this.allCategories) {
				Category cloneCategory = categoryCloneMap.get(category);
				clone.allCategories.add(cloneCategory);

				if (this.selectedCategories.contains(category)) {
					clone.selectedCategories.add(cloneCategory);
				}
			}

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void setAllCategories(List<Category> allCategories) {
		this.allCategories = allCategories;
	}

}
