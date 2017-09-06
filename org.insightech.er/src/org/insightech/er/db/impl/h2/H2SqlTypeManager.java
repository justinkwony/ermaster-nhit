package org.insightech.er.db.impl.h2;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class H2SqlTypeManager extends SqlTypeManagerBase {

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		return 0;
	}

}
