package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ExportImageSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 8062761326645885449L;

	private String outputFilePath;

	private String categoryDirPath;

	private Category category;
	
	private boolean withCategoryImage = true;

	private boolean openAfterSaved = true;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getCategoryDirPath() {
		return categoryDirPath;
	}

	public void setCategoryDirPath(String categoryDirPath) {
		this.categoryDirPath = categoryDirPath;
	}

	public boolean isWithCategoryImage() {
		return withCategoryImage;
	}

	public void setWithCategoryImage(boolean withCategoryImage) {
		this.withCategoryImage = withCategoryImage;
	}

	public boolean isOpenAfterSaved() {
		return openAfterSaved;
	}

	public void setOpenAfterSaved(boolean openAfterSaved) {
		this.openAfterSaved = openAfterSaved;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryDirPath == null) ? 0 : categoryDirPath.hashCode());
		result = prime * result + (openAfterSaved ? 1231 : 1237);
		result = prime * result
				+ ((outputFilePath == null) ? 0 : outputFilePath.hashCode());
		result = prime * result + (withCategoryImage ? 1231 : 1237);
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
		ExportImageSetting other = (ExportImageSetting) obj;
		if (categoryDirPath == null) {
			if (other.categoryDirPath != null)
				return false;
		} else if (!categoryDirPath.equals(other.categoryDirPath))
			return false;
		if (openAfterSaved != other.openAfterSaved)
			return false;
		if (outputFilePath == null) {
			if (other.outputFilePath != null)
				return false;
		} else if (!outputFilePath.equals(other.outputFilePath))
			return false;
		if (withCategoryImage != other.withCategoryImage)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportImageSetting clone() {
		try {
			ExportImageSetting clone = (ExportImageSetting) super.clone();

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
