package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;
import java.util.Map;

import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.Environment;

public class ExportDDLSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 1616332815609079246L;

	public static final String CRLF = "CR+LF";
	
	public static final String LF = "LF";
	
	private String ddlOutput;

	private boolean openAfterSaved = true;

	private String srcFileEncoding;

	private String lineFeed;
	
	private DDLTarget ddlTarget = new DDLTarget();

	private Environment environment;

	private Category category;

	public String getDdlOutput() {
		return ddlOutput;
	}

	public void setDdlOutput(String ddlOutput) {
		this.ddlOutput = ddlOutput;
	}

	public boolean isOpenAfterSaved() {
		return openAfterSaved;
	}

	public void setOpenAfterSaved(boolean openAfterSaved) {
		this.openAfterSaved = openAfterSaved;
	}

	public DDLTarget getDdlTarget() {
		return ddlTarget;
	}

	public void setDdlTarget(DDLTarget ddlTarget) {
		this.ddlTarget = ddlTarget;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String getSrcFileEncoding() {
		return srcFileEncoding;
	}

	public void setSrcFileEncoding(String srcFileEncoding) {
		this.srcFileEncoding = srcFileEncoding;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getLineFeed() {
		return lineFeed;
	}

	public void setLineFeed(String lineFeed) {
		this.lineFeed = lineFeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((ddlOutput == null) ? 0 : ddlOutput.hashCode());
		result = prime * result
				+ ((ddlTarget == null) ? 0 : ddlTarget.hashCode());
		result = prime * result
				+ ((environment == null) ? 0 : environment.hashCode());
		result = prime * result
				+ ((lineFeed == null) ? 0 : lineFeed.hashCode());
		result = prime * result + (openAfterSaved ? 1231 : 1237);
		result = prime * result
				+ ((srcFileEncoding == null) ? 0 : srcFileEncoding.hashCode());
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
		ExportDDLSetting other = (ExportDDLSetting) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (ddlOutput == null) {
			if (other.ddlOutput != null)
				return false;
		} else if (!ddlOutput.equals(other.ddlOutput))
			return false;
		if (ddlTarget == null) {
			if (other.ddlTarget != null)
				return false;
		} else if (!ddlTarget.equals(other.ddlTarget))
			return false;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (lineFeed == null) {
			if (other.lineFeed != null)
				return false;
		} else if (!lineFeed.equals(other.lineFeed))
			return false;
		if (openAfterSaved != other.openAfterSaved)
			return false;
		if (srcFileEncoding == null) {
			if (other.srcFileEncoding != null)
				return false;
		} else if (!srcFileEncoding.equals(other.srcFileEncoding))
			return false;
		return true;
	}

	public ExportDDLSetting clone(Map<Category, Category> categoryCloneMap,
			Map<Environment, Environment> environmentCloneMap) {
		try {
			ExportDDLSetting clone = (ExportDDLSetting) super.clone();

			clone.setDdlTarget(this.ddlTarget.clone());
			clone.setCategory(categoryCloneMap.get(category));
			clone.setEnvironment(environmentCloneMap.get(environment));

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
