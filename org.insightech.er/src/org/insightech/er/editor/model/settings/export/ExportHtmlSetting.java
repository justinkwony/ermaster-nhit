package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;

public class ExportHtmlSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 8062761326645885449L;

	private String outputDir;

//	private String srcFileEncoding;

	private boolean withImage = true;

	private boolean withCategoryImage = true;

	private boolean openAfterSaved = true;

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

//	public String getSrcFileEncoding() {
//		return srcFileEncoding;
//	}
//
//	public void setSrcFileEncoding(String srcFileEncoding) {
//		this.srcFileEncoding = srcFileEncoding;
//	}

	public boolean isWithImage() {
		return withImage;
	}

	public void setWithImage(boolean withImage) {
		this.withImage = withImage;
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
		result = prime * result + (openAfterSaved ? 1231 : 1237);
		result = prime * result
				+ ((outputDir == null) ? 0 : outputDir.hashCode());
		result = prime * result + (withCategoryImage ? 1231 : 1237);
		result = prime * result + (withImage ? 1231 : 1237);
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
		ExportHtmlSetting other = (ExportHtmlSetting) obj;
		if (openAfterSaved != other.openAfterSaved)
			return false;
		if (outputDir == null) {
			if (other.outputDir != null)
				return false;
		} else if (!outputDir.equals(other.outputDir))
			return false;
		if (withCategoryImage != other.withCategoryImage)
			return false;
		if (withImage != other.withImage)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportHtmlSetting clone() {
		try {
			ExportHtmlSetting clone = (ExportHtmlSetting) super.clone();

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
