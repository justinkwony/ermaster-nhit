package org.insightech.er.editor.model.diagram_contents.not_element.tablespace;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.AbstractObjectModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Environment;

public class Tablespace extends AbstractObjectModel {

	private static final long serialVersionUID = 1861168804265437031L;

	private Map<Environment, TablespaceProperties> propertiesMap = new HashMap<Environment, TablespaceProperties>();

	public void copyTo(Tablespace to) {
		to.setName(this.getName());

		to.propertiesMap = new HashMap<Environment, TablespaceProperties>();
		for (Map.Entry<Environment, TablespaceProperties> entry : this.propertiesMap
				.entrySet()) {
			to.propertiesMap.put(entry.getKey(), entry.getValue().clone());
		}
	}

	public TablespaceProperties getProperties(Environment environment,
			ERDiagram diagram) {
		return DBManagerFactory.getDBManager(diagram)
				.checkTablespaceProperties(this.propertiesMap.get(environment));
	}

	public void putProperties(Environment environment,
			TablespaceProperties tablespaceProperties) {
		this.propertiesMap.put(environment, tablespaceProperties);
	}

	public Map<Environment, TablespaceProperties> getPropertiesMap() {
		return propertiesMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tablespace clone() {
		Tablespace clone = (Tablespace) super.clone();

		this.copyTo(clone);

		return clone;
	}

	public String getDescription() {
		return "";
	}

	public String getObjectType() {
		return "tablespace";
	}

}
