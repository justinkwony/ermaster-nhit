package org.insightech.er.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.DBSetting;

public abstract class PreImportFromDBManager implements IRunnableWithProgress {

	private static Logger logger = Logger
			.getLogger(PreImportFromDBManager.class.getName());

	protected Connection con;

	private DatabaseMetaData metaData;

	protected DBSetting dbSetting;

	private DBObjectSet importObjects;

	protected List<String> schemaList;

	private Exception exception;

	private int taskTotalCount = 1;

	private int taskCount = 0;

	IProgressMonitor monitor;

	public void init(Connection con, DBSetting dbSetting, ERDiagram diagram,
			List<String> schemaList) throws SQLException {
		this.con = con;
		this.dbSetting = dbSetting;

		this.metaData = con.getMetaData();

		this.importObjects = new DBObjectSet();
		this.schemaList = schemaList;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			if (!this.schemaList.isEmpty()) {
				this.taskTotalCount = this.schemaList.size();
			}
			this.taskTotalCount *= 4;
			this.monitor = monitor;

			monitor.beginTask(
					ResourceString
							.getResourceString("dialog.message.import.schema.information"),
					this.taskTotalCount);

			this.importObjects.addAll(this.importTables());
			this.importObjects.addAll(this.importSequences());
			this.importObjects.addAll(this.importViews());
			this.importObjects.addAll(this.importTriggers());

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			this.exception = e;
		}
	}

	protected List<DBObject> importTables() throws SQLException,
			InterruptedException {
		return this.importObjects(new String[] { "TABLE", "SYSTEM TABLE",
				"SYSTEM TOAST TABLE", "TEMPORARY TABLE" }, DBObject.TYPE_TABLE);
	}

	protected List<DBObject> importSequences() throws SQLException,
			InterruptedException {
		return this.importObjects(new String[] { "SEQUENCE" },
				DBObject.TYPE_SEQUENCE);
	}

	protected List<DBObject> importViews() throws SQLException,
			InterruptedException {
		return this.importObjects(new String[] { "VIEW", "SYSTEM VIEW" },
				DBObject.TYPE_VIEW);
	}

	protected List<DBObject> importTriggers() throws SQLException,
			InterruptedException {
		return this.importObjects(new String[] { "TRIGGER" },
				DBObject.TYPE_TRIGGER);
	}

	private List<DBObject> importObjects(String[] types, String dbObjectType)
			throws SQLException, InterruptedException {
		List<DBObject> list = new ArrayList<DBObject>();

		ResultSet resultSet = null;

		if (this.schemaList.isEmpty()) {
			this.schemaList.add(null);
		}

		for (String schemaPattern : this.schemaList) {
			try {
				this.taskCount++;

				monitor.subTask("(" + this.taskCount + "/"
						+ this.taskTotalCount + ")  [TYPE : "
						+ dbObjectType.toUpperCase() + ",  SCHEMA : "
						+ schemaPattern + "]");
				monitor.worked(1);

				resultSet = metaData
						.getTables(null, schemaPattern, null, types);

				while (resultSet.next()) {
					String schema = resultSet.getString("TABLE_SCHEM");
					String name = resultSet.getString("TABLE_NAME");

					if (DBObject.TYPE_TABLE.equals(dbObjectType)) {
						try {
							this.getAutoIncrementColumnName(con, schema, name);

						} catch (SQLException e) {
							logger.log(Level.WARNING, e.getMessage());
							// テーブル情報が取得できない場合（他のユーザの所有物などの場合）、
							// このテーブルは使用しない。
							continue;
						}
					}

					DBObject dbObject = new DBObject(schema, name, dbObjectType);
					list.add(dbObject);
				}

				if (monitor.isCanceled()) {
					throw new InterruptedException("Cancel has been requested.");
				}

			} finally {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}

			}
		}

		return list;
	}

	private String getAutoIncrementColumnName(Connection con, String schema,
			String tableName) throws SQLException {
		String autoIncrementColumnName = null;

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT 1 FROM "
					+ this.getTableNameWithSchema(schema, tableName));

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		}

		return autoIncrementColumnName;
	}

	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema(tableName, schema);
	}

	public DBObjectSet getImportObjects() {
		return this.importObjects;
	}

	public Exception getException() {
		return exception;
	}

}
