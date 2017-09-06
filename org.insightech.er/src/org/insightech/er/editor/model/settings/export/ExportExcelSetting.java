package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ExportExcelSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 1616332815609079246L;

	private String usedDefaultTemplateLang;
	
	private String excelTemplate;

	private String excelTemplatePath;

	private String excelOutput;

	private String imageOutput;

	private Category category;

	private boolean useLogicalNameAsSheet = true;

	private boolean putERDiagramOnExcel = true;

	private boolean openAfterSaved = true;

	public String getExcelTemplate() {
		return excelTemplate;
	}

	public void setExcelTemplate(String excelTemplate) {
		this.excelTemplate = excelTemplate;
	}

	public String getExcelTemplatePath() {
		return excelTemplatePath;
	}

	public void setExcelTemplatePath(String excelTemplatePath) {
		this.excelTemplatePath = excelTemplatePath;
	}

	public String getExcelOutput() {
		return excelOutput;
	}

	public void setExcelOutput(String excelOutput) {
		this.excelOutput = excelOutput;
	}

	public String getImageOutput() {
		return imageOutput;
	}

	public void setImageOutput(String imageOutput) {
		this.imageOutput = imageOutput;
	}

	public boolean isOpenAfterSaved() {
		return openAfterSaved;
	}

	public void setOpenAfterSaved(boolean openAfterSaved) {
		this.openAfterSaved = openAfterSaved;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isUseLogicalNameAsSheet() {
		return useLogicalNameAsSheet;
	}

	public void setUseLogicalNameAsSheet(boolean useLogicalNameAsSheet) {
		this.useLogicalNameAsSheet = useLogicalNameAsSheet;
	}

	public boolean isPutERDiagramOnExcel() {
		return putERDiagramOnExcel;
	}

	public void setPutERDiagramOnExcel(boolean putERDiagramOnExcel) {
		this.putERDiagramOnExcel = putERDiagramOnExcel;
	}

	public String getUsedDefaultTemplateLang() {
		return usedDefaultTemplateLang;
	}

	public void setUsedDefaultTemplateLang(String usedDefaultTemplateLang) {
		this.usedDefaultTemplateLang = usedDefaultTemplateLang;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((excelOutput == null) ? 0 : excelOutput.hashCode());
		result = prime * result
				+ ((excelTemplate == null) ? 0 : excelTemplate.hashCode());
		result = prime
				* result
				+ ((excelTemplatePath == null) ? 0 : excelTemplatePath
						.hashCode());
		result = prime * result
				+ ((imageOutput == null) ? 0 : imageOutput.hashCode());
		result = prime * result + (openAfterSaved ? 1231 : 1237);
		result = prime * result + (putERDiagramOnExcel ? 1231 : 1237);
		result = prime * result + (useLogicalNameAsSheet ? 1231 : 1237);
		result = prime
				* result
				+ ((usedDefaultTemplateLang == null) ? 0
						: usedDefaultTemplateLang.hashCode());
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
		ExportExcelSetting other = (ExportExcelSetting) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (excelOutput == null) {
			if (other.excelOutput != null)
				return false;
		} else if (!excelOutput.equals(other.excelOutput))
			return false;
		if (excelTemplate == null) {
			if (other.excelTemplate != null)
				return false;
		} else if (!excelTemplate.equals(other.excelTemplate))
			return false;
		if (excelTemplatePath == null) {
			if (other.excelTemplatePath != null)
				return false;
		} else if (!excelTemplatePath.equals(other.excelTemplatePath))
			return false;
		if (imageOutput == null) {
			if (other.imageOutput != null)
				return false;
		} else if (!imageOutput.equals(other.imageOutput))
			return false;
		if (openAfterSaved != other.openAfterSaved)
			return false;
		if (putERDiagramOnExcel != other.putERDiagramOnExcel)
			return false;
		if (useLogicalNameAsSheet != other.useLogicalNameAsSheet)
			return false;
		if (usedDefaultTemplateLang == null) {
			if (other.usedDefaultTemplateLang != null)
				return false;
		} else if (!usedDefaultTemplateLang
				.equals(other.usedDefaultTemplateLang))
			return false;
		return true;
	}

	public ExportExcelSetting clone(Map<Category, Category> categoryCloneMap) {
		try {
			ExportExcelSetting clone = (ExportExcelSetting) super.clone();

			clone.setCategory(categoryCloneMap.get(category));

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
