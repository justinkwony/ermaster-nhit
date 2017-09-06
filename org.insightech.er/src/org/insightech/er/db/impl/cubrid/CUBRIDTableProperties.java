/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class CUBRIDTableProperties extends TableProperties {

	private static final long serialVersionUID = 3126556935094407067L;

	private boolean reuseOID;
	private String collation;

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public boolean isReuseOID() {
		return reuseOID;
	}

	public void setReuseOID(boolean reuseOID) {
		this.reuseOID = reuseOID;
	}
}
