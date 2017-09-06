package org.insightech.er.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.TranslationResources;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWordDictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class ImportFromDBManagerBase implements ImportFromDBManager {

	private static Logger logger = Logger
			.getLogger(ImportFromDBManagerBase.class.getName());

	private static final boolean LOG_SQL_TYPE = false;

	private static final Pattern AS_PATTERN = Pattern
			.compile("(.+) [aA][sS] (.+)");

	private static final Pattern TYPE_WITH_LENGTH_PATTERN = Pattern
			.compile("(.+)\\((\\d+)\\).*");

	private static final Pattern TYPE_WITH_DECIMAL_PATTERN = Pattern
			.compile("(.+)\\((\\d+),(\\d+)\\).*");

	protected Connection con;

	private DatabaseMetaData metaData;

	protected DBSetting dbSetting;

	private ERDiagram diagram;

	private List<DBObject> dbObjectList;

	private Map<String, ERTable> tableMap;

	protected Map<String, String> tableCommentMap;

	protected Map<String, Map<String, ColumnData>> columnDataCache;

	protected Map<String, List<ForeignKeyData>> tableForeignKeyDataMap;

	private UniqueWordDictionary dictionary;

	private List<ERTable> importedTables;

	private List<Sequence> importedSequences;

	private List<Trigger> importedTriggers;

	private List<Tablespace> importedTablespaces;

	private List<View> importedViews;

	private Exception exception;

	protected TranslationResources translationResources;

	private boolean useCommentAsLogicalName;

	private boolean mergeWord;

	private int taskCount;

	private int taskTotalCount;

	protected static class ColumnData {
		public String columnName;

		public String type;

		public int size;

		public int decimalDegits;

		public int nullable;

		public String defaultValue;

		public String description;

		public String constraint;

		public String enumData;

		public String characterSet;

		public String collation;

		public boolean isBinary;

		public boolean charSemantics;

		@Override
		public String toString() {
			return "ColumnData [columnName=" + columnName + ", type=" + type
					+ ", size=" + size + ", decimalDegits=" + decimalDegits
					+ "]";
		}

	}

	private static class ForeignKeyData {
		private String name;

		private String sourceTableName;

		private String sourceSchemaName;

		private String sourceColumnName;

		private String targetTableName;

		private String targetSchemaName;

		private String targetColumnName;

		private short updateRule;

		private short deleteRule;
	}

	protected static class PrimaryKeyData {
		private String columnName;

		private String constraintName;
	}

	public ImportFromDBManagerBase() {
		this.tableMap = new HashMap<String, ERTable>();
		this.tableCommentMap = new HashMap<String, String>();
		this.columnDataCache = new HashMap<String, Map<String, ColumnData>>();
		this.tableForeignKeyDataMap = new HashMap<String, List<ForeignKeyData>>();
		this.dictionary = new UniqueWordDictionary();
	}

	public void init(Connection con, DBSetting dbSetting, ERDiagram diagram,
			List<DBObject> dbObjectList, boolean useCommentAsLogicalName,
			boolean mergeWord) throws SQLException {
		this.con = con;
		this.dbSetting = dbSetting;
		this.diagram = diagram;
		this.dbObjectList = dbObjectList;
		this.useCommentAsLogicalName = useCommentAsLogicalName;
		this.mergeWord = mergeWord;

		this.metaData = con.getMetaData();
		this.translationResources = new TranslationResources(diagram
				.getDiagramContents().getSettings().getTranslationSetting());

		if (this.mergeWord) {
			this.dictionary.init(this.diagram);
		}
	}

	public void run(ProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			this.taskTotalCount = this.dbObjectList.size();

			monitor.beginTask(ResourceString
					.getResourceString("dialog.message.import.db.objects"),
					this.taskTotalCount);

			this.cacheTableComment(monitor);
			this.cacheColumnData(dbObjectList, monitor);

			this.importedSequences = this.importSequences(this.dbObjectList,
					monitor);
			this.importedTriggers = this.importTriggers(this.dbObjectList,
					monitor);
			this.importedTablespaces = this.importTablespaces(
					this.dbObjectList, monitor);
			this.importedTables = this.importTables(this.dbObjectList, monitor);
			this.importedTables.addAll(this.importSynonyms());

			this.setForeignKeys(this.importedTables);

			this.importedViews = this.importViews(this.dbObjectList, monitor);

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			this.exception = e;

		}

		monitor.done();
	}

	protected void cacheColumnData(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		Set<String> schemas = new HashSet<String>();

		for (DBObject dbObject : dbObjectList) {
			if (!dbObject.getType().equals(DBObject.TYPE_TABLE)) {
				continue;
			}

			String schemaName = dbObject.getSchema();

			if (!schemas.contains(schemaName)) {
				if (monitor != null) {
					String displayName = schemaName;

					if (schemaName == null) {
						displayName = ResourceString
								.getResourceString("label.none");
					}
					monitor.subTask("reading schema: " + displayName);
				}

				this.cacheColumnDataX(schemaName, null, dbObjectList, monitor);

				schemas.add(schemaName);

				if (monitor != null && monitor.isCanceled()) {
					throw new InterruptedException("Cancel has been requested.");
				}
			}
		}
	}

	protected void cacheColumnDataX(String schemaName, String tableName,
			List<DBObject> dbObjectList, ProgressMonitor monitor)
			throws SQLException, InterruptedException {
		ResultSet columnSet = null;

		try {
			columnSet = metaData.getColumns(null, schemaName, tableName, null);

			while (columnSet.next()) {
				tableName = columnSet.getString("TABLE_NAME");
				String schema = columnSet.getString("TABLE_SCHEM");

				String tableNameWithSchema = this.dbSetting
						.getTableNameWithSchema(tableName, schema);

				Map<String, ColumnData> cache = this.columnDataCache
						.get(tableNameWithSchema);
				if (cache == null) {
					cache = new LinkedHashMap<String, ColumnData>();
					this.columnDataCache.put(tableNameWithSchema, cache);
				}

				ColumnData columnData = this.createColumnData(columnSet);

				this.cacheOtherColumnData(tableName, schema, columnData);

				cache.put(columnData.columnName, columnData);
			}

		} finally {
			if (columnSet != null) {
				columnSet.close();
			}
		}
	}

	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = new ColumnData();
		columnData.columnName = columnSet.getString("COLUMN_NAME");
		columnData.type = columnSet.getString("TYPE_NAME").toLowerCase();
		columnData.size = columnSet.getInt("COLUMN_SIZE");
		columnData.decimalDegits = columnSet.getInt("DECIMAL_DIGITS");
		columnData.nullable = columnSet.getInt("NULLABLE");
		columnData.defaultValue = columnSet.getString("COLUMN_DEF");
		columnData.charSemantics = columnSet.getInt("CHAR_OCTET_LENGTH") == columnData.size;

		if (columnData.defaultValue != null) {
			if ("bit".equals(columnData.type)) {
				byte[] bits = columnData.defaultValue.getBytes();

				columnData.defaultValue = "";

				for (int i = 0; i < bits.length; i++) {
					columnData.defaultValue += bits[i];
				}
			}
		}

		columnData.description = columnSet.getString("REMARKS");

		return columnData;
	}

	protected void cacheOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {
	}

	protected void cacheTableComment(ProgressMonitor monitor)
			throws SQLException, InterruptedException {
	}

	private List<Sequence> importSequences(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		List<Sequence> list = new ArrayList<Sequence>();

		for (Iterator<DBObject> iter = dbObjectList.iterator(); iter.hasNext();) {
			DBObject dbObject = iter.next();

			if (DBObject.TYPE_SEQUENCE.equals(dbObject.getType())) {
				iter.remove();
				this.taskCount++;

				String name = dbObject.getName();
				String schema = dbObject.getSchema();
				String nameWithSchema = this.dbSetting.getTableNameWithSchema(
						name, schema);

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ") ["
						+ dbObject.getType().toUpperCase() + "] "
						+ nameWithSchema);
				monitor.worked(1);

				Sequence sequence = this.importSequence(schema, name);

				if (sequence != null) {
					list.add(sequence);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sequenceNameWithSchema = this.getTableNameWithSchema(schema,
				sequenceName);

		try {
			stmt = this.con.prepareStatement("SELECT * FROM "
					+ sequenceNameWithSchema);
			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_BY"));
				sequence.setMinValue(rs.getLong("MIN_VALUE"));

				BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");

				sequence.setMaxValue(maxValue);
				sequence.setStart(rs.getLong("LAST_VALUE"));
				sequence.setCache(rs.getInt("CACHE_VALUE"));
				sequence.setCycle(rs.getBoolean("IS_CYCLED"));

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	private List<Trigger> importTriggers(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		List<Trigger> list = new ArrayList<Trigger>();

		for (Iterator<DBObject> iter = dbObjectList.iterator(); iter.hasNext();) {
			DBObject dbObject = iter.next();

			if (DBObject.TYPE_TRIGGER.equals(dbObject.getType())) {
				iter.remove();
				this.taskCount++;

				String name = dbObject.getName();
				String schema = dbObject.getSchema();
				String nameWithSchema = this.dbSetting.getTableNameWithSchema(
						name, schema);

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ") ["
						+ dbObject.getType().toUpperCase() + "] "
						+ nameWithSchema);
				monitor.worked(1);

				Trigger trigger = this.importTrigger(schema, name);

				if (trigger != null) {
					list.add(trigger);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	protected Trigger importTrigger(String schema, String triggerName)
			throws SQLException {
		//
		return null;
	}

	protected List<ERTable> importTables(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		List<ERTable> list = new ArrayList<ERTable>();

		for (Iterator<DBObject> iter = dbObjectList.iterator(); iter.hasNext();) {
			DBObject dbObject = iter.next();

			if (DBObject.TYPE_TABLE.equals(dbObject.getType())) {
				iter.remove();
				this.taskCount++;

				String tableName = dbObject.getName();
				String schema = dbObject.getSchema();
				String tableNameWithSchema = this.dbSetting
						.getTableNameWithSchema(tableName, schema);

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ") ["
						+ dbObject.getType().toUpperCase() + "] "
						+ tableNameWithSchema);
				monitor.worked(1);

				ERTable table = this.importTable(tableNameWithSchema,
						tableName, schema);

				if (table != null) {
					list.add(table);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	protected List<ERTable> importSynonyms() throws SQLException,
			InterruptedException {
		return new ArrayList<ERTable>();
	}

	protected String getConstraintName(PrimaryKeyData data) {
		return data.constraintName;
	}

	protected ERTable importTable(String tableNameWithSchema, String tableName,
			String schema) throws SQLException, InterruptedException {
		String autoIncrementColumnName = null;
		try {
			autoIncrementColumnName = this.getAutoIncrementColumnName(con,
					this.getTableNameWithSchema(schema, tableName));
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			return null;
		}

		ERTable table = new ERTable();
		TableViewProperties tableProperties = table
				.getTableViewProperties(this.dbSetting.getDbsystem());
		tableProperties.setSchema(schema);

		table.setPhysicalName(tableName);
		table.setLogicalName(this.translationResources.translate(tableName));

		table.setDescription(this.tableCommentMap.get(tableNameWithSchema));

		List<PrimaryKeyData> primaryKeys = this.getPrimaryKeys(table,
				this.metaData);
		if (!primaryKeys.isEmpty()) {
			table.setPrimaryKeyName(getConstraintName(primaryKeys.get(0)));
		}

		List<Index> indexes = this
				.getIndexes(table, this.metaData, primaryKeys);

		List<Column> columns = this.getColumns(tableNameWithSchema, tableName,
				schema, indexes, primaryKeys, autoIncrementColumnName);

		table.setColumns(columns);
		table.setIndexes(indexes);

		this.tableMap.put(tableNameWithSchema, table);

		for (Index index : indexes) {
			this.setIndexColumn(table, index);
		}

		this.setTableViewProperties(tableName, tableProperties);

		return table;
	}

	protected void setTableViewProperties(String tableName,
			TableViewProperties tableViewProperties) {
	}

	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema(tableName, schema);
	}

	protected void setForeignKeys(List<ERTable> list) throws SQLException {
		this.cacheForeignKeyData();

		for (ERTable target : list) {
			if (this.tableForeignKeyDataMap != null) {
				this.setForeignKeysUsingCache(target);
			} else {
				this.setForeignKeys(target);
			}
		}
	}

	private String getAutoIncrementColumnName(Connection con,
			String tableNameWithSchema) throws SQLException {
		String autoIncrementColumnName = null;

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT * FROM " + tableNameWithSchema);
			ResultSetMetaData md = rs.getMetaData();

			for (int i = 0; i < md.getColumnCount(); i++) {
				if (md.isAutoIncrement(i + 1)) {
					autoIncrementColumnName = md.getColumnName(i + 1);
					break;
				}
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}

		return autoIncrementColumnName;
	}

	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = new ArrayList<Index>();

		Map<String, Index> indexMap = new HashMap<String, Index>();

		ResultSet indexSet = null;

		try {
			indexSet = metaData.getIndexInfo(null, table
					.getTableViewProperties(this.dbSetting.getDbsystem())
					.getSchema(), table.getPhysicalName(), false, true);

			while (indexSet.next()) {
				String name = null;
				try {
					name = indexSet.getString("INDEX_NAME");

				} catch (SQLException e) {
					logger.log(
							Level.WARNING,
							"Cannot get Index Info of ["
									+ table.getTableViewProperties(
											this.dbSetting.getDbsystem())
											.getSchema() + ":"
									+ table.getPhysicalName() + "]");
					continue;
				}

				if (name == null) {
					continue;
				}

				Index index = indexMap.get(name);

				if (index == null) {
					boolean nonUnique = indexSet.getBoolean("NON_UNIQUE");
					String type = null;
					short indexType = indexSet.getShort("TYPE");
					if (indexType == DatabaseMetaData.tableIndexOther) {
						type = "BTREE";
					}

					// DatabaseMetaData.tableIndexClustered
					// DatabaseMetaData.tableIndexOther
					// DatabaseMetaData.tableIndexStatistic

					index = new Index(table, name, nonUnique, type, null);

					indexMap.put(name, index);
					indexes.add(index);
				}

				String columnName = indexSet.getString("COLUMN_NAME");
				String ascDesc = indexSet.getString("ASC_OR_DESC");

				if (columnName.startsWith("\"") && columnName.endsWith("\"")) {
					columnName = columnName.substring(1,
							columnName.length() - 1);
				}

				Boolean desc = null;

				if ("A".equals(ascDesc)) {
					desc = Boolean.FALSE;
				} else if ("D".equals(ascDesc)) {
					desc = Boolean.TRUE;
				}

				index.addColumnName(columnName, desc);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			this.close(indexSet);
		}

		for (Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
			Index index = iter.next();
			List<String> indexColumns = index.getColumnNames();

			if (indexColumns.size() == primaryKeys.size()) {
				boolean equals = true;

				for (int i = 0; i < indexColumns.size(); i++) {
					if (!indexColumns.get(i).equals(
							primaryKeys.get(i).columnName)) {
						equals = false;
						break;
					}
				}

				if (equals) {
					iter.remove();
				}
			}
		}

		return indexes;
	}

	private void setIndexColumn(ERTable erTable, Index index) {
		for (String columnName : index.getColumnNames()) {
			for (Column column : erTable.getColumns()) {
				if (column instanceof NormalColumn) {
					NormalColumn normalColumn = (NormalColumn) column;

					if (normalColumn.getPhysicalName().equals(columnName)) {
						index.addColumn(normalColumn);
						break;
					}
				}
			}
		}
	}

	private List<PrimaryKeyData> getPrimaryKeys(ERTable table,
			DatabaseMetaData metaData) throws SQLException {
		List<PrimaryKeyData> primaryKeys = new ArrayList<PrimaryKeyData>();

		ResultSet primaryKeySet = null;

		try {
			primaryKeySet = metaData.getPrimaryKeys(null, table
					.getTableViewProperties(this.dbSetting.getDbsystem())
					.getSchema(), table.getPhysicalName());
			while (primaryKeySet.next()) {
				PrimaryKeyData data = new PrimaryKeyData();

				data.columnName = primaryKeySet.getString("COLUMN_NAME");
				data.constraintName = primaryKeySet.getString("PK_NAME");

				primaryKeys.add(data);
			}

		} catch (SQLException e) {
			// Microsoft Access does not support getPrimaryKeys

		} finally {
			this.close(primaryKeySet);
		}

		return primaryKeys;
	}

	protected Map<String, ColumnData> getColumnDataMap(
			String tableNameWithSchema, String tableName, String schema)
			throws SQLException, InterruptedException {
		return this.columnDataCache.get(tableNameWithSchema);
	}

	private List<Column> getColumns(String tableNameWithSchema,
			String tableName, String schema, List<Index> indexes,
			List<PrimaryKeyData> primaryKeys, String autoIncrementColumnName)
			throws SQLException, InterruptedException {
		List<Column> columns = new ArrayList<Column>();

		Map<String, ColumnData> columnDataMap = this.getColumnDataMap(
				tableNameWithSchema, tableName, schema);
		if (columnDataMap == null) {
			return new ArrayList<Column>();
		}

		Collection<ColumnData> columnSet = columnDataMap.values();

		for (ColumnData columnData : columnSet) {
			String columnName = columnData.columnName;
			String type = columnData.type;

			boolean array = false;
			Integer arrayDimension = null;
			boolean unsigned = false;

			boolean zerofill = false;

			int zerofillIndex = type.toUpperCase().indexOf(" ZEROFILL");
			if (zerofillIndex != -1) {
				zerofill = true;
				type = type.substring(0, zerofillIndex);
			}

			int unsignedIndex = type.toUpperCase().indexOf(" UNSIGNED");
			if (unsignedIndex != -1) {
				unsigned = true;
				type = type.substring(0, unsignedIndex);
			}

			int arrayStartIndex = type.indexOf("[");
			if (arrayStartIndex != -1) {
				array = true;
				String str = type.substring(arrayStartIndex + 1,
						type.indexOf("]"));
				arrayDimension = Integer.parseInt(str);
				type = type.substring(0, arrayStartIndex);
			}

			int size = 0;
			int decimalDegits = 0;

			if (zerofillIndex != -1) {
				Matcher matcher = TYPE_WITH_DECIMAL_PATTERN.matcher(type);
				if (matcher.find()) {
					type = matcher.group(1);
					size = Integer.parseInt(matcher.group(2));
					decimalDegits = Integer.parseInt(matcher.group(3));

				} else {
					matcher = TYPE_WITH_LENGTH_PATTERN.matcher(type);

					if (matcher.find()) {
						type = matcher.group(1);
						size = Integer.parseInt(matcher.group(2));
					}
				}

			} else {
				size = columnData.size;
				decimalDegits = columnData.decimalDegits;
			}

			Integer length = new Integer(size);
			Integer decimal = new Integer(decimalDegits);

			SqlType sqlType = SqlType.valueOf(this.dbSetting.getDbsystem(),
					type, size, decimal);

			if (sqlType == null || LOG_SQL_TYPE) {
				logger.info(columnName + ": " + type + ", " + size + ", "
						+ columnData.decimalDegits);
			}

			boolean notNull = false;
			if (columnData.nullable == DatabaseMetaData.columnNoNulls) {
				notNull = true;
			}

			String defaultValue = Format.null2blank(columnData.defaultValue);
			if (sqlType != null) {
				if (SqlType.SQL_TYPE_ID_SERIAL.equals(sqlType.getId())
						|| SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(sqlType
								.getId())) {
					defaultValue = "";
				}
			}

			String description = Format.null2blank(columnData.description);
			String constraint = Format.null2blank(columnData.constraint);

			boolean primaryKey = false;

			for (PrimaryKeyData primaryKeyData : primaryKeys) {
				if (columnName.equals(primaryKeyData.columnName)) {
					primaryKey = true;
					break;
				}
			}

			boolean uniqueKey = this.isUniqueKey(columnName, indexes,
					primaryKeys);

			boolean autoIncrement = columnName
					.equalsIgnoreCase(autoIncrementColumnName);

			String logicalName = null;
			if (this.useCommentAsLogicalName && !Check.isEmpty(description)) {
				logicalName = description.replaceAll("[\r\n]", "");
			}
			if (Check.isEmpty(logicalName)) {
				logicalName = this.translationResources.translate(columnName);
			}

			String args = columnData.enumData;

			TypeData typeData = new TypeData(length, decimal, array,
					arrayDimension, unsigned, zerofill, columnData.isBinary,
					args, columnData.charSemantics);

			Word word = new Word(columnName, logicalName, sqlType, typeData,
					description, this.diagram.getDatabase());
			word = this.dictionary.getUniqueWord(word);

			// TODO UNIQUE KEY

			NormalColumn column = new NormalColumn(word, notNull, primaryKey,
					uniqueKey, autoIncrement, defaultValue, constraint, null,
					columnData.characterSet, columnData.collation);

			columns.add(column);
		}

		return columns;
	}

	private boolean isUniqueKey(String columnName, List<Index> indexes,
			List<PrimaryKeyData> primaryKeys) {
		String primaryKey = null;

		if (primaryKeys.size() == 1) {
			primaryKey = primaryKeys.get(0).columnName;
		}

		if (columnName == null) {
			return false;
		}

		for (Index index : indexes) {
			List<String> columnNames = index.getColumnNames();
			if (columnNames.size() == 1) {
				String indexColumnName = columnNames.get(0);
				if (columnName.equals(indexColumnName)) {
					if (!index.isNonUnique()) {
						if (!columnName.equals(primaryKey)) {
							indexes.remove(index);
							return true;
						}
						return false;
					}
				}
			}
		}

		return false;
	}

	private boolean isCyclicForeignKye(ForeignKeyData foreignKeyData) {
		if (foreignKeyData.sourceSchemaName == null) {
			if (foreignKeyData.targetSchemaName != null) {
				return false;
			}

		} else if (!foreignKeyData.sourceSchemaName
				.equals(foreignKeyData.targetSchemaName)) {
			return false;
		}

		if (!foreignKeyData.sourceTableName
				.equals(foreignKeyData.targetTableName)) {
			return false;
		}

		if (!foreignKeyData.sourceColumnName
				.equals(foreignKeyData.targetColumnName)) {
			return false;
		}

		return true;
	}

	protected void cacheForeignKeyData() throws SQLException {
		ResultSet foreignKeySet = null;
		try {
			foreignKeySet = metaData.getImportedKeys(null, null, null);

			while (foreignKeySet.next()) {
				ForeignKeyData foreignKeyData = new ForeignKeyData();

				foreignKeyData.name = foreignKeySet.getString("FK_NAME");
				foreignKeyData.sourceSchemaName = foreignKeySet
						.getString("PKTABLE_SCHEM");
				foreignKeyData.sourceTableName = foreignKeySet
						.getString("PKTABLE_NAME");
				foreignKeyData.sourceColumnName = foreignKeySet
						.getString("PKCOLUMN_NAME");
				foreignKeyData.targetSchemaName = foreignKeySet
						.getString("FKTABLE_SCHEM");
				foreignKeyData.targetTableName = foreignKeySet
						.getString("FKTABLE_NAME");
				foreignKeyData.targetColumnName = foreignKeySet
						.getString("FKCOLUMN_NAME");
				foreignKeyData.updateRule = foreignKeySet
						.getShort("UPDATE_RULE");
				foreignKeyData.deleteRule = foreignKeySet
						.getShort("DELETE_RULE");

				if (this.isCyclicForeignKye(foreignKeyData)) {
					continue;
				}

				String key = this.dbSetting.getTableNameWithSchema(
						foreignKeyData.targetTableName,
						foreignKeyData.targetSchemaName);

				List<ForeignKeyData> foreignKeyDataList = tableForeignKeyDataMap
						.get(key);

				if (foreignKeyDataList == null) {
					foreignKeyDataList = new ArrayList<ForeignKeyData>();
					tableForeignKeyDataMap.put(key, foreignKeyDataList);
				}

				foreignKeyDataList.add(foreignKeyData);
			}
		} catch (SQLException e) {
			tableForeignKeyDataMap = null;

		} finally {
			this.close(foreignKeySet);
		}
	}

	private void setForeignKeysUsingCache(ERTable target) throws SQLException {
		String tableName = target.getPhysicalName();
		String schema = target.getTableViewProperties(
				this.dbSetting.getDbsystem()).getSchema();

		tableName = this.dbSetting.getTableNameWithSchema(tableName, schema);

		List<ForeignKeyData> foreignKeyList = this.tableForeignKeyDataMap
				.get(tableName);

		if (foreignKeyList == null) {
			return;
		}

		Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = this
				.collectSameNameForeignKeyData(foreignKeyList);

		for (Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap
				.entrySet()) {
			this.createRelation(target, entry.getValue());
		}
	}

	private void setForeignKeys(ERTable target) throws SQLException {
		String tableName = target.getPhysicalName();
		String schemaName = target.getTableViewProperties(
				this.dbSetting.getDbsystem()).getSchema();

		ResultSet foreignKeySet = null;

		try {
			foreignKeySet = this.metaData.getImportedKeys(null, schemaName,
					tableName);

			List<ForeignKeyData> foreignKeyList = new ArrayList<ForeignKeyData>();

			while (foreignKeySet.next()) {
				ForeignKeyData foreignKeyData = new ForeignKeyData();

				foreignKeyData.name = foreignKeySet.getString("FK_NAME");
				foreignKeyData.sourceTableName = foreignKeySet
						.getString("PKTABLE_NAME");
				foreignKeyData.sourceSchemaName = foreignKeySet
						.getString("PKTABLE_SCHEM");
				foreignKeyData.sourceColumnName = foreignKeySet
						.getString("PKCOLUMN_NAME");
				foreignKeyData.targetSchemaName = foreignKeySet
						.getString("FKTABLE_SCHEM");
				foreignKeyData.targetColumnName = foreignKeySet
						.getString("FKCOLUMN_NAME");
				foreignKeyData.updateRule = foreignKeySet
						.getShort("UPDATE_RULE");
				foreignKeyData.deleteRule = foreignKeySet
						.getShort("DELETE_RULE");

				foreignKeyList.add(foreignKeyData);
			}

			if (foreignKeyList.isEmpty()) {
				return;
			}

			Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = this
					.collectSameNameForeignKeyData(foreignKeyList);

			for (Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap
					.entrySet()) {
				this.createRelation(target, entry.getValue());
			}

		} catch (SQLException e) {
			// microsoft access does not support getImportedKeys

		} finally {
			this.close(foreignKeySet);
		}
	}

	private Map<String, List<ForeignKeyData>> collectSameNameForeignKeyData(
			List<ForeignKeyData> foreignKeyList) {
		Map<String, List<ForeignKeyData>> map = new HashMap<String, List<ForeignKeyData>>();

		for (ForeignKeyData foreignKyeData : foreignKeyList) {
			List<ForeignKeyData> list = map.get(foreignKyeData.name);
			if (list == null) {
				list = new ArrayList<ForeignKeyData>();
				map.put(foreignKyeData.name, list);
			}

			list.add(foreignKyeData);
		}

		return map;
	}

	private Relation createRelation(ERTable target,
			List<ForeignKeyData> foreignKeyDataList) {
		ForeignKeyData representativeData = foreignKeyDataList.get(0);

		String sourceTableName = representativeData.sourceTableName;
		String sourceSchemaName = representativeData.sourceSchemaName;

		sourceTableName = this.dbSetting.getTableNameWithSchema(
				sourceTableName, sourceSchemaName);

		ERTable source = this.tableMap.get(sourceTableName);
		if (source == null) {
			return null;
		}

		boolean referenceForPK = true;

		List<NormalColumn> primaryKeys = source.getPrimaryKeys();
		if (primaryKeys.size() != foreignKeyDataList.size()) {
			referenceForPK = false;
		}

		Map<NormalColumn, NormalColumn> referenceMap = new HashMap<NormalColumn, NormalColumn>();

		for (ForeignKeyData foreignKeyData : foreignKeyDataList) {
			NormalColumn sourceColumn = null;

			for (NormalColumn normalColumn : source.getNormalColumns()) {
				if (normalColumn.getPhysicalName().equals(
						foreignKeyData.sourceColumnName)) {
					sourceColumn = normalColumn;
					break;
				}
			}

			if (sourceColumn == null) {
				return null;
			}

			if (!sourceColumn.isPrimaryKey()) {
				referenceForPK = false;
			}

			NormalColumn targetColumn = null;

			for (NormalColumn normalColumn : target.getNormalColumns()) {
				if (normalColumn.getPhysicalName().equals(
						foreignKeyData.targetColumnName)) {
					targetColumn = normalColumn;
					break;
				}
			}

			if (targetColumn == null) {
				return null;
			}

			referenceMap.put(sourceColumn, targetColumn);
		}

		ComplexUniqueKey referencedComplexUniqueKey = null;
		NormalColumn referencedColumn = null;

		if (!referenceForPK) {
			if (referenceMap.size() > 1) {
				// TODO
				referencedComplexUniqueKey = new ComplexUniqueKey("");
				for (NormalColumn column : referenceMap.keySet()) {
					referencedComplexUniqueKey.addColumn(column);
				}
				// TODO
				source.getComplexUniqueKeyList()
						.add(referencedComplexUniqueKey);

			} else {
				referencedColumn = referenceMap.keySet().iterator().next();
			}

		}

		NormalColumn representedForeignKeyColumn = referenceMap.entrySet()
				.iterator().next().getValue();

		Relation relation = new Relation(referenceForPK,
				referencedComplexUniqueKey, referencedColumn,
				representedForeignKeyColumn.isNotNull(),
				representedForeignKeyColumn.isUniqueKey()
						|| representedForeignKeyColumn.isSinglePrimaryKey());
		relation.setName(representativeData.name);
		relation.setSource(source);
		relation.setTargetWithoutForeignKey(target);

		String onUpdateAction = null;
		if (representativeData.updateRule == DatabaseMetaData.importedKeyCascade) {
			onUpdateAction = "CASCADE";
		} else if (representativeData.updateRule == DatabaseMetaData.importedKeyRestrict) {
			onUpdateAction = "RESTRICT";
		} else if (representativeData.updateRule == DatabaseMetaData.importedKeyNoAction) {
			onUpdateAction = "NO ACTION";
		} else if (representativeData.updateRule == DatabaseMetaData.importedKeySetDefault) {
			onUpdateAction = "SET DEFAULT";
		} else if (representativeData.updateRule == DatabaseMetaData.importedKeySetNull) {
			onUpdateAction = "SET NULL";
		} else {
			onUpdateAction = "";
		}

		relation.setOnUpdateAction(onUpdateAction);

		String onDeleteAction = null;
		if (representativeData.deleteRule == DatabaseMetaData.importedKeyCascade) {
			onDeleteAction = "CASCADE";
		} else if (representativeData.deleteRule == DatabaseMetaData.importedKeyRestrict) {
			onDeleteAction = "RESTRICT";
		} else if (representativeData.deleteRule == DatabaseMetaData.importedKeyNoAction) {
			onDeleteAction = "NO ACTION";
		} else if (representativeData.deleteRule == DatabaseMetaData.importedKeySetDefault) {
			onDeleteAction = "SET DEFAULT";
		} else if (representativeData.deleteRule == DatabaseMetaData.importedKeySetNull) {
			onDeleteAction = "SET NULL";
		} else {
			onDeleteAction = "";
		}

		relation.setOnDeleteAction(onDeleteAction);

		for (Map.Entry<NormalColumn, NormalColumn> entry : referenceMap
				.entrySet()) {
			entry.getValue().addReference(entry.getKey(), relation);
		}

		return relation;
	}

	public List<ERTable> getImportedTables() {
		return importedTables;
	}

	public List<Sequence> getImportedSequences() {
		return importedSequences;
	}

	public List<View> getImportedViews() {
		return importedViews;
	}

	private List<View> importViews(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		List<View> list = new ArrayList<View>();

		for (Iterator<DBObject> iter = dbObjectList.iterator(); iter.hasNext();) {
			DBObject dbObject = iter.next();

			if (DBObject.TYPE_VIEW.equals(dbObject.getType())) {
				iter.remove();
				this.taskCount++;

				String name = dbObject.getName();
				String schema = dbObject.getSchema();
				String nameWithSchema = this.dbSetting.getTableNameWithSchema(
						name, schema);

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ") ["
						+ dbObject.getType().toUpperCase() + "] "
						+ nameWithSchema);
				monitor.worked(1);

				View view = this.importView(schema, name);

				if (view != null) {
					list.add(view);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	protected View importView(String schema, String viewName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = getViewDefinitionSQL(schema);
		if (sql == null) {
			return null;
		}

		try {
			stmt = this.con.prepareStatement(sql);

			if (schema != null) {
				stmt.setString(1, schema);
				stmt.setString(2, viewName);

			} else {
				stmt.setString(1, viewName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				View view = new View();

				view.setPhysicalName(viewName);
				view.setLogicalName(this.translationResources
						.translate(viewName));
				String definitionSQL = rs.getString(1);
				view.setSql(definitionSQL);
				view.getTableViewProperties().setSchema(schema);

				List<Column> columnList = this.getViewColumnList(definitionSQL);
				view.setColumns(columnList);

				return view;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	protected abstract String getViewDefinitionSQL(String schema);

	private List<Column> getViewColumnList(String sql) {
		List<Column> columnList = new ArrayList<Column>();

		sql = sql.replaceAll("\\s+", " ");
		String upperSql = sql.toUpperCase();

		int selectIndex = upperSql.indexOf("SELECT ");
		int fromIndex = upperSql.indexOf(" FROM ");

		if (selectIndex == -1) {
			return null;
		}

		String columnsPart = null;
		String fromPart = null;

		if (fromIndex != -1) {
			columnsPart = sql.substring(selectIndex + "SELECT ".length(),
					fromIndex);
			fromPart = sql.substring(fromIndex + " FROM ".length());

		} else {
			columnsPart = sql.substring(selectIndex + "SELECT ".length());
			fromPart = "";
		}

		int whereIndex = fromPart.toUpperCase().indexOf(" WHERE ");

		if (whereIndex != -1) {
			fromPart = fromPart.substring(0, whereIndex);
		}

		Map<String, String> aliasTableMap = new HashMap<String, String>();

		StringTokenizer fromTokenizer = new StringTokenizer(fromPart, ",");

		while (fromTokenizer.hasMoreTokens()) {
			String tableName = fromTokenizer.nextToken().trim();

			tableName.replaceAll(" AS", "");
			tableName.replaceAll(" as", "");
			tableName.replaceAll(" As", "");
			tableName.replaceAll(" aS", "");

			String tableAlias = null;

			int asIndex = tableName.toUpperCase().indexOf(" ");
			if (asIndex != -1) {
				tableAlias = tableName.substring(asIndex + 1).trim();
				tableName = tableName.substring(0, asIndex).trim();

				// TODO schema
				int dotIndex = tableName.indexOf(".");
				if (dotIndex != -1) {
					tableName = tableName.substring(dotIndex + 1);
				}

				aliasTableMap.put(tableAlias, tableName);
			}
		}

		StringTokenizer columnTokenizer = new StringTokenizer(columnsPart, ",");

		String previousColumn = null;

		while (columnTokenizer.hasMoreTokens()) {
			String columnName = columnTokenizer.nextToken();

			if (previousColumn != null) {
				columnName = previousColumn + "," + columnName;
				previousColumn = null;
			}

			if (columnName.split("\\(").length > columnName.split("\\)").length) {
				previousColumn = columnName;
				continue;
			}

			columnName = columnName.trim();
			columnName = columnName.replaceAll("\"", "");

			String columnAlias = null;

			Matcher matcher = AS_PATTERN.matcher(columnName);

			if (matcher.matches()) {
				columnAlias = matcher.toMatchResult().group(2).trim();
				columnName = matcher.toMatchResult().group(1).trim();

			} else {
				int asIndex = columnName.indexOf(" ");
				if (asIndex != -1) {
					columnAlias = columnName.substring(asIndex + 1).trim();
					columnName = columnName.substring(0, asIndex).trim();
				}
			}

			int dotIndex = columnName.indexOf(".");

			String tableName = null;

			if (dotIndex != -1) {
				String aliasTableName = columnName.substring(0, dotIndex);
				columnName = columnName.substring(dotIndex + 1);

				dotIndex = columnName.indexOf(".");
				if (dotIndex != -1) {
					aliasTableName = columnName.substring(0, dotIndex);
					columnName = columnName.substring(dotIndex + 1);
				}

				tableName = aliasTableMap.get(aliasTableName);

				if (tableName == null) {
					tableName = aliasTableName;
				}
			}

			if (columnAlias == null) {
				columnAlias = columnName;
			}

			NormalColumn targetColumn = null;

			if (columnName != null) {
				if (tableName != null) {
					tableName = tableName.toLowerCase();
				}
				columnName = columnName.toLowerCase();

				if (!"*".equals(columnName)) {
					for (ERTable table : this.importedTables) {
						if (tableName == null
								|| (table.getPhysicalName() != null && tableName
										.equals(table.getPhysicalName()
												.toLowerCase()))) {
							for (NormalColumn column : table
									.getExpandedColumns()) {
								if (column.getPhysicalName() != null
										&& columnName.equals(column
												.getPhysicalName()
												.toLowerCase())) {
									targetColumn = column;

									break;
								}
							}

							if (targetColumn != null) {
								break;
							}
						}

					}

					try {
						this.addColumnToView(columnList, targetColumn,
								columnAlias);
					} catch (NullPointerException e) {
						throw e;
					}
				} else {
					for (ERTable table : this.importedTables) {
						if (tableName == null
								|| (table.getPhysicalName() != null && tableName
										.equals(table.getPhysicalName()
												.toLowerCase()))) {
							for (NormalColumn column : table
									.getExpandedColumns()) {
								this.addColumnToView(columnList, column, null);
							}
						}
					}
				}
			}
		}

		return columnList;
	}

	private void addColumnToView(List<Column> columnList,
			NormalColumn targetColumn, String columnAlias) {
		Word word = null;

		if (targetColumn != null) {
			while ((word = targetColumn.getWord()) == null) {
				targetColumn = targetColumn.getReferencedColumnList().get(0);
			}

			word = new Word(word);
			if (columnAlias != null) {
				word.setPhysicalName(columnAlias);
			}

		} else {
			word = new Word(columnAlias,
					this.translationResources.translate(columnAlias), null,
					new TypeData(null, null, false, null, false, false, false,
							null, false), null, null);

		}

		this.dictionary.getUniqueWord(word);

		NormalColumn column = new NormalColumn(word, false, false, false,
				false, null, null, null, null, null);
		columnList.add(column);
	}

	public List<Tablespace> getImportedTablespaces() {
		return importedTablespaces;
	}

	private List<Tablespace> importTablespaces(List<DBObject> dbObjectList,
			ProgressMonitor monitor) throws SQLException, InterruptedException {
		List<Tablespace> list = new ArrayList<Tablespace>();

		for (Iterator<DBObject> iter = dbObjectList.iterator(); iter.hasNext();) {
			DBObject dbObject = iter.next();

			if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
				iter.remove();
				this.taskCount++;

				String name = dbObject.getName();
				String schema = dbObject.getSchema();
				String nameWithSchema = this.dbSetting.getTableNameWithSchema(
						name, schema);

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ") ["
						+ dbObject.getType().toUpperCase() + "] "
						+ nameWithSchema);
				monitor.worked(1);

				Tablespace tablespace = this.importTablespace(name);

				if (tablespace != null) {
					list.add(tablespace);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	public List<Trigger> getImportedTriggers() {
		return importedTriggers;
	}

	protected Tablespace importTablespace(String tablespaceName)
			throws SQLException {
		// TODO
		return null;
	}

	public Exception getException() {
		return exception;
	}

	public static void main(String[] args) throws InputException,
			InstantiationException, IllegalAccessException, SQLException {
		new ERDiagramActivator();

		DBSetting setting = new DBSetting("Oracle", "localhost", 1521, "XE",
				"nakajima", "nakajima", true, null, null);

		Connection con = null;
		try {
			con = setting.connect();
			DatabaseMetaData metaData = con.getMetaData();

			metaData.getIndexInfo(null, "SYS", "ALERT_QT", false, false);

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	protected void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	protected void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

}
