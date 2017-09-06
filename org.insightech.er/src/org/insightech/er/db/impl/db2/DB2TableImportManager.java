package org.insightech.er.db.impl.db2;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class DB2TableImportManager extends ImportFromDBManagerEclipseBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWSCHEMA = ? AND VIEWNAME = ?";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.indexOf("graphic") != -1 || type.indexOf("dbclob") != -1) {
			columnData.size = columnData.size / 2;
		}

		return columnData;
	}

	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con
					.prepareStatement("SELECT * FROM SYSCAT.SEQUENCES WHERE SEQSCHEMA = ? AND SEQNAME = ?");
			stmt.setString(1, schema);
			stmt.setString(2, sequenceName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT"));
				sequence.setMinValue(rs.getLong("MINVALUE"));

				BigDecimal maxValue = rs.getBigDecimal("MAXVALUE");

				int dataTypeId = rs.getInt("DATATYPEID");
				String dataType = null;

				if (dataTypeId == 16) {
					dataType = "DECIMAL(p)";
					sequence.setDecimalSize(rs.getInt("PRECISION"));

				} else if (dataTypeId == 24) {
					dataType = "INTEGER";
					if (maxValue.intValue() == Integer.MAX_VALUE) {
						maxValue = null;
					}

				} else if (dataTypeId == 20) {
					dataType = "BIGINT";
					if (maxValue.longValue() == Long.MAX_VALUE) {
						maxValue = null;
					}

				} else if (dataTypeId == 28) {
					dataType = "SMALLINT";
					if (maxValue.intValue() == Short.MAX_VALUE) {
						maxValue = null;
					}

				} else {
					dataType = "";

				}
				sequence.setDataType(dataType);

				sequence.setMaxValue(maxValue);
				sequence.setStart(rs.getLong("START"));

				int cache = rs.getInt("CACHE");

				if (cache <= 1) {
					sequence.setNocache(true);
				} else {
					sequence.setCache(cache);
				}

				boolean cycle = false;
				if ("Y".equals(rs.getString("CYCLE"))) {
					cycle = true;
				}

				sequence.setCycle(cycle);

				boolean order = false;
				if ("Y".equals(rs.getString("ORDER"))) {
					order = true;
				}

				sequence.setOrder(order);

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

}
