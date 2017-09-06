/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class CUBRIDSqlTypeManager extends SqlTypeManagerBase {

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		return 0;
	}

}
