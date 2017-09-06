package org.insightech.er.editor.model.testdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;

public class RepeatTestDataDef implements Cloneable {

	private static final String TYPE_PREFIX = "label.testdata.repeat.type.";

	public static final String TYPE_FORMAT = "format";

	public static final String TYPE_FOREIGNKEY = "foreign.key";

	public static final String TYPE_ENUM = "enum";

	public static final String TYPE_NULL = "null";

	private static final List<String> ALL_TYPE_LIST = new ArrayList<String>();

	static {
		ALL_TYPE_LIST.add(TYPE_FORMAT);
		ALL_TYPE_LIST.add(TYPE_FOREIGNKEY);
		ALL_TYPE_LIST.add(TYPE_ENUM);
		ALL_TYPE_LIST.add(TYPE_NULL);
	}

	private String type;

	private int repeatNum;

	private String template;

	private String from;

	private String to;

	private String increment;

	private String[] selects;

	private Map<Integer, String> modifiedValues;

	public RepeatTestDataDef() {
		this.modifiedValues = new HashMap<Integer, String>();
	}

	public String getType() {
		return this.type;
	}

	public String getTypeLabel() {
		return getTypeLabel(this.type);
	}

	public static String getTypeLabel(String type) {
		return ResourceString.getResourceString(TYPE_PREFIX + type);
	}

	public static String getType(String type) {
		for (String typeId : ALL_TYPE_LIST) {
			if (typeId.equals(type)
					|| ResourceString.equals(TYPE_PREFIX + typeId, type)) {
				return typeId;
			}
		}

		return type;
	}

	public static boolean equalType(String typeId, String type) {
		if (typeId.equals(type)
				|| ResourceString.equals(TYPE_PREFIX + typeId, type)) {
			return true;
		}

		return false;
	}

	public void setType(String type) {
		for (String typeId : ALL_TYPE_LIST) {
			if (typeId.equals(type)
					|| ResourceString.equals(TYPE_PREFIX + typeId, type)) {
				this.type = typeId;
				return;
			}
		}

		this.type = type;
	}

	public int getRepeatNum() {
		return repeatNum;
	}

	public void setRepeatNum(int repeatNum) {
		this.repeatNum = repeatNum;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getIncrement() {
		return increment;
	}

	public void setIncrement(String increment) {
		this.increment = increment;
	}

	public String[] getSelects() {
		return selects;
	}

	public void setSelects(String[] selects) {
		this.selects = selects;
	}

	public void setModifiedValue(Integer row, String value) {
		this.modifiedValues.put(row, value);
	}

	public void removeModifiedValue(Integer row) {
		this.modifiedValues.remove(row);
	}

	public Map<Integer, String> getModifiedValues() {
		return this.modifiedValues;
	}

	@Override
	public RepeatTestDataDef clone() {
		try {
			RepeatTestDataDef clone = (RepeatTestDataDef) super.clone();

			if (this.selects != null) {
				clone.selects = new String[this.selects.length];
				for (int i = 0; i < clone.selects.length; i++) {
					clone.selects[i] = this.selects[i];
				}
			}

			clone.modifiedValues = new HashMap<Integer, String>();
			clone.modifiedValues.putAll(this.modifiedValues);

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
