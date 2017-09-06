package org.insightech.er;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class ResourceString {

	public static final String KEY_DEFAULT_VALUE_EMPTY_STRING = "label.empty.string";

	public static final String KEY_DEFAULT_VALUE_CURRENT_DATE_TIME = "label.current.date.time";

	private static ResourceBundle resource = ResourceBundle
			.getBundle("org.insightech.er.ERDiagram");

	private static ResourceBundle resourceEn = ResourceBundle.getBundle(
			"org.insightech.er.ERDiagram", Locale.ROOT);

	private static ResourceBundle resourceJa = ResourceBundle.getBundle(
			"org.insightech.er.ERDiagram", Locale.JAPAN);

	public static String getResourceString(String key) {
		return getResourceString(key, null);
	}

	public static String normalize(String key, String value) {
		if (equals(key, value)) {
			return getResourceString(key);
		}

		return value;
	}

	public static boolean equals(String key, String value) {
		if (value == null) {
			return false;
		}

		if (value.equals(resourceEn.getString(key))) {
			return true;
		} else if (value.equals(resourceJa.getString(key))) {
			return true;
		}

		return false;
	}

	public static String getResourceString(String key, String[] args) {
		try {
			String string = resource.getString(key);
			string = MessageFormat.format(string, args);
			// string = string.replaceAll("\\\\r\\\\n", "\r\n");

			return string;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static Map getResources(String prefix) {
		Map<String, String> props = new TreeMap<String, String>(
				Collections.reverseOrder());
		Enumeration keys = resource.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				props.put(key, resource.getString(key));
			}
		}

		return props;
	}
}
