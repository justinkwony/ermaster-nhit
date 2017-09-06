package org.insightech.er.db.impl.oracle;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public class OracleTableImportManager extends ImportFromDBManagerEclipseBase {

	private static Logger logger = Logger
			.getLogger(OracleTableImportManager.class.getName());

	private static final Pattern INTERVAL_YEAR_TO_MONTH_PATTERN = Pattern
			.compile("interval year\\((.)\\) to month");

	private static final Pattern INTERVAL_DAY_TO_SECCOND_PATTERN = Pattern
			.compile("interval day\\((.)\\) to second\\((.)\\)");

	private static final Pattern TIMESTAMP_PATTERN = Pattern
			.compile("timestamp\\((.)\\).*");

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
			stmt = this.con
					.prepareStatement("SELECT OWNER, TABLE_NAME, COLUMN_NAME, COMMENTS FROM SYS.ALL_COL_COMMENTS WHERE COMMENTS IS NOT NULL");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String schema = rs.getString("OWNER");

				String columnName = rs.getString("COLUMN_NAME");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				Map<String, ColumnData> cache = this.columnDataCache
						.get(tableName);
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
	protected void cacheTableComment(ProgressMonitor monitor)
			throws SQLException, InterruptedException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con
					.prepareStatement("SELECT OWNER, TABLE_NAME, COMMENTS FROM SYS.ALL_TAB_COMMENTS WHERE COMMENTS IS NOT NULL");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");

				String schema = rs.getString("OWNER");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

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
		if (schema != null) {
			return "SELECT TEXT FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?";

		} else {
			return "SELECT TEXT FROM ALL_VIEWS WHERE VIEW_NAME = ?";

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, sequenceName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_NAME = ?");
				stmt.setString(1, sequenceName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_BY"));
				BigDecimal minValue = rs.getBigDecimal("MIN_VALUE");
				sequence.setMinValue(minValue.longValue());
				BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");
				sequence.setMaxValue(maxValue);
				BigDecimal lastNumber = rs.getBigDecimal("LAST_NUMBER");
				sequence.setStart(lastNumber.longValue());

				int cache = rs.getInt("CACHE_SIZE");
				if (cache <= 1) {
					sequence.setNocache(true);
				} else {
					sequence.setCache(cache);
				}

				String cycle = rs.getString("CYCLE_FLAG").toLowerCase();
				if ("y".equals(cycle)) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Trigger importTrigger(String schema, String name)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, name);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE TRIGGER_NAME = ?");
				stmt.setString(1, name);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Trigger trigger = new Trigger();

				trigger.setName(name);
				trigger.setSchema(schema);
				trigger.setDescription(rs.getString("DESCRIPTION"));
				trigger.setSql(rs.getString("TRIGGER_BODY"));

				return trigger;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	public static boolean isValidObjectName(String s) {
		return s.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {
		if (!isValidObjectName(table.getPhysicalName())) {
			logger.info("is not valid object name : " + table.getPhysicalName());
			return new ArrayList<Index>();
		}

		try {
			return super.getIndexes(table, metaData, primaryKeys);

		} catch (SQLException e) {
			if (e.getErrorCode() == 38029) {
				logger.info(table.getPhysicalName() + " : " + e.getMessage());
				return new ArrayList<Index>();
			}

			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ERTable> importSynonyms() throws SQLException,
			InterruptedException {
		List<ERTable> list = new ArrayList<ERTable>();

		// if (this.isOnlyUserTable()) {
		// PreparedStatement stmt = null;
		// ResultSet rs = null;
		//
		// try {
		// String sql =
		// "SELECT SYNONYM_NAME, TABLE_OWNER, TABLE_NAME FROM USER_SYNONYMS";
		//
		// stmt = this.con.prepareStatement(sql);
		// rs = stmt.executeQuery();
		//
		// while (rs.next()) {
		// String tableName = rs.getString("TABLE_NAME");
		// String schema = rs.getString("TABLE_OWNER");
		// String tableNameWithSchema = DBSetting
		// .getTableNameWithSchema(tableName, schema);
		//
		// if (!this.dbSetting.getUser().equalsIgnoreCase(schema)) {
		// this.cacheColumnData(schema, null, null);
		//
		// ERTable table = this.importTable(tableNameWithSchema
		// , tableName, schema);
		//
		// if (table != null) {
		// list.add(table);
		// }
		// }
		// }
		//
		// } finally {
		// this.close(rs);
		// this.close(stmt);
		// }
		// }

		return list;
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);

		Matcher yearToMonthMatcber = INTERVAL_YEAR_TO_MONTH_PATTERN
				.matcher(columnData.type);
		Matcher dayToSecondMatcber = INTERVAL_DAY_TO_SECCOND_PATTERN
				.matcher(columnData.type);
		Matcher timestampMatcber = TIMESTAMP_PATTERN.matcher(columnData.type);

		if (yearToMonthMatcber.matches()) {
			columnData.type = "interval year to month";

			if (columnData.size == 2) {
				columnData.type = "interval year to month";

			} else {
				columnData.type = "interval year(p) to month";
			}

		} else if (dayToSecondMatcber.matches()) {

			if (columnData.size == 2) {
				if (columnData.decimalDegits == 6) {
					columnData.type = "interval day to second";

				} else {
					columnData.type = "interval day to second(p)";
					columnData.size = columnData.decimalDegits;
					columnData.decimalDegits = 0;
				}

			} else if (columnData.decimalDegits == 6) {
				columnData.type = "interval day(p) to second";

			} else {
				columnData.type = "interval day(p) to second(p)";
			}

		} else if (timestampMatcber.matches()) {
			columnData.type = columnData.type.replaceAll("\\(.\\)", "");
			columnData.size = columnData.decimalDegits;
		}

		return columnData;
	}

}
