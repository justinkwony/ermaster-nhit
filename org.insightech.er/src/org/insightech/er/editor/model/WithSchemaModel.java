package org.insightech.er.editor.model;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.util.Format;

public abstract class WithSchemaModel extends AbstractObjectModel {

	private static final long serialVersionUID = -7450893485538582071L;

	private String schema;

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getNameWithSchema(String database) {
		if (this.schema == null) {
			return Format.null2blank(this.getName());
		}

		DBManager dbManager = DBManagerFactory.getDBManager(database);

		if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
			return Format.null2blank(this.getName());
		}

		return this.schema + "." + Format.null2blank(this.getName());
	}

	@Override
	public int compareTo(AbstractObjectModel other) {
		WithSchemaModel otherModel = (WithSchemaModel) other;

		int compareTo = 0;

		compareTo = Format.null2blank(this.schema).toUpperCase()
				.compareTo(Format.null2blank(otherModel.schema).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = Format.null2blank(this.getName()).toUpperCase()
				.compareTo(Format.null2blank(other.getName()).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		return compareTo;
	}
}
