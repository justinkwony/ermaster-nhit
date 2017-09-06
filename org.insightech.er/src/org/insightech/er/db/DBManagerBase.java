package org.insightech.er.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.page.jdbc.JDBCPathDialog;

public abstract class DBManagerBase implements DBManager {

	private Set<String> reservedWords = new HashSet<String>();

	private Map<String, ClassLoader> loaderMap;

	public DBManagerBase() {
		DBManagerFactory.addDB(this);

		this.reservedWords = this.getReservedWords();

		this.loaderMap = new HashMap<String, ClassLoader>();
	}

	public String getURL(String serverName, String dbName, int port) {
		// String temp = serverName.replaceAll("\\\\", "\\\\\\\\");
		String url = this.getURL().replaceAll("<SERVER NAME>",
				Matcher.quoteReplacement(serverName));
		url = url.replaceAll("<PORT>", String.valueOf(port));

		// temp = dbName.replaceAll("\\\\", "\\\\\\\\");
		url = url.replaceAll("<DB NAME>", Matcher.quoteReplacement(dbName));

		return url;
	}

	@SuppressWarnings("unchecked")
	public Class<Driver> getDriverClass(String driverClassName) {
		String path = null;
		Class clazz = null;

		try {
			if (driverClassName.equals("sun.jdbc.odbc.JdbcOdbcDriver")) {
				return (Class<Driver>) Class
						.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			} else {
				path = PreferenceInitializer.getJDBCDriverPath(this.getId(),
						driverClassName);

				// Cache the class loader to map.
				// Because if I use the another loader with the driver using
				// native library(.dll)
				// next error occur.
				//
				// java.lang.UnsatisfiedLinkError: Native Library xxx.dll
				// already loaded in another classloader
				//
				ClassLoader loader = this.loaderMap.get(path);
				if (loader == null) {
					loader = this.getClassLoader(path);
					this.loaderMap.put(path, loader);
				}

				clazz = loader.loadClass(driverClassName);
			}

		} catch (Exception e) {
			JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					this.getId(), driverClassName, path,
					new ArrayList<JDBCDriverSetting>(), false);

			if (dialog.open() == IDialogConstants.OK_ID) {
				JDBCDriverSetting newDriverSetting = new JDBCDriverSetting(
						this.getId(), dialog.getDriverClassName(),
						dialog.getPath());

				List<JDBCDriverSetting> driverSettingList = PreferenceInitializer
						.getJDBCDriverSettingList();

				if (driverSettingList.contains(newDriverSetting)) {
					driverSettingList.remove(newDriverSetting);
				}
				driverSettingList.add(newDriverSetting);

				PreferenceInitializer
						.saveJDBCDriverSettingList(driverSettingList);

				clazz = this.getDriverClass(dialog.getDriverClassName());
			}
		}

		return clazz;
	}

	private ClassLoader getClassLoader(String uri) throws SQLException,
			MalformedURLException {

		StringTokenizer tokenizer = new StringTokenizer(uri, ";");
		int count = tokenizer.countTokens();

		URL[] urls = new URL[count];

		for (int i = 0; i < urls.length; i++) {
			urls[i] = new URL("file", "", tokenizer.nextToken());
		}

		URLClassLoader loader = new URLClassLoader(urls,
				ERDiagramActivator.getClassLoader());

		return loader;
	}

	abstract protected String getURL();

	abstract public String getDriverClassName();

	protected Set<String> getReservedWords() {
		Set<String> reservedWords = new HashSet<String>();

		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
				.getPackage().getName()
				+ ".reserved_word");

		Enumeration<String> keys = bundle.getKeys();

		while (keys.hasMoreElements()) {
			reservedWords.add(keys.nextElement().toUpperCase());
		}

		return reservedWords;
	}

	public boolean isReservedWord(String str) {
		return reservedWords.contains(str.toUpperCase());
	}

	public boolean isSupported(int supportItem) {
		int[] supportItems = this.getSupportItems();

		for (int i = 0; i < supportItems.length; i++) {
			if (supportItems[i] == supportItem) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doesNeedURLDatabaseName() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doesNeedURLServerName() {
		return true;
	}

	abstract protected int[] getSupportItems();

	public List<String> getImportSchemaList(Connection con) throws SQLException {
		List<String> schemaList = new ArrayList<String>();

		DatabaseMetaData metaData = con.getMetaData();
		try {
			ResultSet rs = metaData.getSchemas();

			while (rs.next()) {
				schemaList.add(rs.getString(1));
			}

		} catch (SQLException e) {
			// when schema is not supported
		}

		return schemaList;
	}

	public List<String> getSystemSchemaList() {
		List<String> list = new ArrayList<String>();

		return list;
	}

	public List<String> getForeignKeyRuleList() {
		List<String> list = new ArrayList<String>();

		list.add("RESTRICT");
		list.add("CASCADE");
		list.add("NO ACTION");
		list.add("SET NULL");
		list.add("SET DEFAULT");

		return list;
	}
}
