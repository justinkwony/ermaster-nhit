/**
 * 20140415 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public class CUBRIDTableImportManager extends ImportFromDBManagerEclipseBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cacheColumnData(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		super.cacheColumnData(dbObjectList, monitor);

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement("SELECT NULL AS OWNER, TABLE_NAME, COLUMN_NAME, DESCRIPTION AS COMMENTS FROM _CUB_SCHEMA_COMMENTS a, DB_ATTRIBUTE b WHERE a.COLUMN_NAME = b.ATTR_NAME AND a.TABLE_NAME = b.CLASS_NAME");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String schema = rs.getString("OWNER");

				String columnName = rs.getString("COLUMN_NAME");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName, schema);

				Map<String, ColumnData> cache = this.columnDataCache.get(tableName);
				if (cache != null) {
					ColumnData columnData = cache.get(columnName);
					if (columnData != null) {
						columnData.description = comments;
					}
				}
			}
		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cacheTableComment(ProgressMonitor monitor) throws SQLException, InterruptedException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con.prepareStatement("SELECT NULL AS OWNER, TABLE_NAME, DESCRIPTION AS COMMENTS FROM _CUB_SCHEMA_COMMENTS WHERE COLUMN_NAME = '*'");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");

				String schema = rs.getString("OWNER");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName, schema);

				this.tableCommentMap.put(tableName, comments);
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT REPLACE(REPLACE(vclass_def, '['), ']') FROM db_vclass WHERE vclass_name = ?";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Sequence importSequence(String schema, String sequenceName) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con.prepareStatement("SELECT * FROM DB_SERIAL WHERE NAME = ? AND CLASS_NAME IS NULL");
			stmt.setString(1, sequenceName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_VAL"));
				BigDecimal minValue = rs.getBigDecimal("MIN_VAL");
				sequence.setMinValue(minValue.longValue());
				BigDecimal maxValue = rs.getBigDecimal("MAX_VAL");
				sequence.setMaxValue(maxValue);
				BigDecimal currentValue = rs.getBigDecimal("CURRENT_VAL");
				sequence.setStart(currentValue.longValue());
				
				int cache = rs.getInt("CACHED_NUM");
				if (cache == 0) {
					sequence.setNocache(true);
				} else {
					sequence.setCache(cache);
				}
				
				int cycle = rs.getInt("CYCLIC");
				if (cycle == 1) {
					sequence.setCycle(true);
				} else {
					sequence.setCycle(false);
				}

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData, List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = super.getIndexes(table, metaData, primaryKeys);
		
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			//Bug : CUBRID JDBC, DatabaseMetaData metaData returns 'ASC', always.
			stmt = this.con.prepareStatement("SELECT asc_desc AS ASC_OR_DESC FROM db_index_key where index_name = ? AND class_name = ?");
			for(Index index : indexes) {
				List<Boolean> descs = new ArrayList<Boolean>();
				String indexName = index.getName();
				String tableName = index.getTable().getName();

				stmt.setString(1, indexName);
				stmt.setString(2, tableName);

				rs = stmt.executeQuery();

				while (rs.next()) {
					Boolean desc = "DESC".equals(rs.getString("ASC_OR_DESC"));
					descs.add(desc);
				}
				index.setDescs(descs);
				index.setType(null); //avoid BTREE
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}
		
		return indexes;
	}
}
