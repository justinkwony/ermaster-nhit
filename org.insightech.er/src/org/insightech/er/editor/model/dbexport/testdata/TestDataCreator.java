package org.insightech.er.editor.model.dbexport.testdata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;

public abstract class TestDataCreator {

	protected ERDiagram diagram;

	protected File baseDir;

	protected ExportTestDataSetting exportTestDataSetting;

	protected TestData testData;

	protected Map<NormalColumn, List<String>> valueListMap;

	public TestDataCreator() {
	}

	public void init(TestData testData, File baseDir) {
		this.testData = testData;
		this.baseDir = baseDir;
		this.valueListMap = new HashMap<NormalColumn, List<String>>();
	}

	public String getMergedRepeatTestDataValue(int count,
			RepeatTestDataDef repeatTestDataDef, NormalColumn column) {
		String modifiedValue = repeatTestDataDef.getModifiedValues().get(count);

		if (modifiedValue != null) {
			return modifiedValue;

		} else {
			String value = this.getRepeatTestDataValue(count,
					repeatTestDataDef, column);

			if (value == null) {
				return "null";
			}

			return value;
		}
	}

	public String getRepeatTestDataValue(int count,
			RepeatTestDataDef repeatTestDataDef, NormalColumn column) {
		if (repeatTestDataDef == null) {
			return null;
		}

		String type = repeatTestDataDef.getType();
		int repeatNum = repeatTestDataDef.getRepeatNum();

		if (RepeatTestDataDef.TYPE_FORMAT.equals(type)) {
			String fromStr = repeatTestDataDef.getFrom();
			String incrementStr = repeatTestDataDef.getIncrement();
			String toStr = repeatTestDataDef.getTo();

			int fromDecimalPlaces = 0;
			if (fromStr.indexOf(".") != -1) {
				fromDecimalPlaces = fromStr.length() - fromStr.indexOf(".") - 1;
			}
			int incrementDecimalPlaces = 0;
			if (incrementStr.indexOf(".") != -1) {
				incrementDecimalPlaces = incrementStr.length()
						- incrementStr.indexOf(".") - 1;
			}
			int toDecimalPlaces = 0;
			if (toStr.indexOf(".") != -1) {
				toDecimalPlaces = toStr.length() - toStr.indexOf(".") - 1;
			}

			int decimalPlaces = Math.max(
					Math.max(fromDecimalPlaces, incrementDecimalPlaces),
					toDecimalPlaces);
			int from = (int) (Double.parseDouble(fromStr) * Math.pow(10,
					decimalPlaces));
			int increment = (int) (Double.parseDouble(incrementStr) * Math.pow(
					10, decimalPlaces));
			int to = (int) (Double.parseDouble(toStr) * Math.pow(10,
					decimalPlaces));

			String template = repeatTestDataDef.getTemplate();

			int num = from;

			if (repeatNum != 0 && to - from + 1 != 0) {
				num = from
						+ (((count / repeatNum) * increment) % (to - from + 1));
			}

			String value = null;

			if (decimalPlaces == 0) {
				value = template.replaceAll("%", String.valueOf(num));

			} else {
				value = template.replaceAll("%",
						String.valueOf(num / Math.pow(10, decimalPlaces)));
			}

			if (column.getType() != null && column.getType().isTimestamp()) {
				SimpleDateFormat format1 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");

				try {
					value = format1.format(format1.parse(value));

				} catch (ParseException e1) {
					SimpleDateFormat format2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");

					try {
						value = format2.format(format2.parse(value));

					} catch (ParseException e2) {
						SimpleDateFormat format3 = new SimpleDateFormat(
								"yyyy-MM-dd");

						try {
							value = format3.format(format3.parse(value));

						} catch (ParseException e3) {
						}
					}

				}

			}

			return value;

		} else if (RepeatTestDataDef.TYPE_FOREIGNKEY.equals(type)) {
			NormalColumn referencedColumn = column.getFirstReferencedColumn();
			if (referencedColumn == null) {
				return null;
			}

			List<String> referencedValueList = this
					.getValueList(referencedColumn);

			if (referencedValueList.size() == 0) {
				return null;
			}

			int index = (count / repeatNum) % referencedValueList.size();

			return referencedValueList.get(index);

		} else if (RepeatTestDataDef.TYPE_ENUM.equals(type)) {
			String[] selects = repeatTestDataDef.getSelects();

			if (selects.length == 0) {
				return null;
			}

			return selects[(count / repeatNum) % selects.length];
		}

		return null;
	}

	private List<String> getValueList(NormalColumn column) {
		List<String> valueList = this.valueListMap.get(column);

		if (valueList == null) {
			valueList = new ArrayList<String>();

			ERTable table = (ERTable) column.getColumnHolder();
			TableTestData tableTestData = this.testData.getTableTestDataMap()
					.get(table);

			if (tableTestData != null) {
				DirectTestData directTestData = tableTestData
						.getDirectTestData();
				RepeatTestData repeatTestData = tableTestData
						.getRepeatTestData();

				if (this.testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
					for (Map<NormalColumn, String> data : directTestData
							.getDataList()) {
						String value = data.get(column);
						valueList.add(value);
					}

					for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
						String value = this.getMergedRepeatTestDataValue(i,
								repeatTestData.getDataDef(column), column);
						valueList.add(value);
					}

				} else {
					for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
						String value = this.getRepeatTestDataValue(i,
								repeatTestData.getDataDef(column), column);
						valueList.add(value);
					}

					for (Map<NormalColumn, String> data : directTestData
							.getDataList()) {
						String value = data.get(column);
						valueList.add(value);
					}

				}
			}
		}

		return valueList;
	}

	final public void write(ExportTestDataSetting exportTestDataSetting,
			ERDiagram diagram) throws Exception {
		this.exportTestDataSetting = exportTestDataSetting;
		this.diagram = diagram;
		this.diagram.getDiagramContents().sort();

		try {
			this.openFile();

			this.write();

		} finally {
			this.closeFile();
		}
	}

	protected abstract void openFile() throws IOException;

	protected void write() throws Exception {
		for (Map.Entry<ERTable, TableTestData> entry : this.testData
				.getTableTestDataMap().entrySet()) {
			ERTable table = entry.getKey();

			if (skipTable(table)) {
				continue;
			}

			TableTestData tableTestData = entry.getValue();

			DirectTestData directTestData = tableTestData.getDirectTestData();
			RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

			this.writeTableHeader(diagram, table);

			if (this.testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
				for (Map<NormalColumn, String> data : directTestData
						.getDataList()) {
					this.writeDirectTestData(table, data, diagram.getDatabase());
				}

				this.writeRepeatTestData(table, repeatTestData,
						diagram.getDatabase());

			} else {
				this.writeRepeatTestData(table, repeatTestData,
						diagram.getDatabase());

				for (Map<NormalColumn, String> data : directTestData
						.getDataList()) {
					this.writeDirectTestData(table, data, diagram.getDatabase());
				}
			}

			this.writeTableFooter(table);
		}

	}

	protected abstract boolean skipTable(ERTable table);

	protected abstract void writeTableHeader(ERDiagram diagram, ERTable table);

	protected abstract void writeTableFooter(ERTable table);

	protected abstract void writeDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database);

	protected abstract void writeRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database);

	protected abstract void closeFile() throws IOException;
}
