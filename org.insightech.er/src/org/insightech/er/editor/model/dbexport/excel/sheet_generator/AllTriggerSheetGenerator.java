package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.util.POIUtils;

public class AllTriggerSheetGenerator extends TriggerSheetGenerator {

	@Override
	public void generate(ProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap)
			throws InterruptedException {

		LoopDefinition loopDefinition = loopDefinitionMap.get(this
				.getTemplateSheetName());

		HSSFSheet newSheet = createNewSheet(workbook, sheetNo,
				loopDefinition.sheetName, sheetNameMap);

		String sheetName = workbook.getSheetName(workbook
				.getSheetIndex(newSheet));

		sheetObjectMap.put(
				sheetName,
				diagram.getDiagramContents().getTriggerSet());

		HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);

		boolean first = true;

		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
			monitor.subTaskWithCounter(sheetName + " - " + trigger.getName());

			if (first) {
				first = false;

			} else {
				POIUtils.copyRow(oldSheet, newSheet,
						loopDefinition.startLine - 1, oldSheet.getLastRowNum(),
						newSheet.getLastRowNum() + loopDefinition.spaceLine + 1);
			}

			this.setTriggerData(workbook, newSheet, trigger);

			newSheet.setRowBreak(newSheet.getLastRowNum()
					+ loopDefinition.spaceLine);

			monitor.worked(1);
		}

		if (first) {
			for (int i = loopDefinition.startLine - 1; i <= newSheet
					.getLastRowNum(); i++) {
				HSSFRow row = newSheet.getRow(i);
				if (row != null) {
					newSheet.removeRow(row);
				}
			}
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "all_trigger_template";
	}

}
