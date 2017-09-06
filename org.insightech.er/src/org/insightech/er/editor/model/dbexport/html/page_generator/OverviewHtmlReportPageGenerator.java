package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.html.part_generator.ImagePartGenerator;
import org.insightech.er.editor.model.dbexport.image.ImageInfoSet;

public class OverviewHtmlReportPageGenerator {

	private Map<Object, Integer> idMap;

	public OverviewHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		this.idMap = idMap;
	}

	public String getObjectId(Object object) {
		Integer id = (Integer) idMap.get(object);

		if (id == null) {
			id = new Integer(idMap.size());
			this.idMap.put(object, id);
		}

		return String.valueOf(id);
	}

	public String generateFrame(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_template.html");

		Object[] args = { this.generateFrameTable(htmlReportPageGeneratorList) };
		return MessageFormat.format(template, args);
	}

	private String generateFrameTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateSummary(ImageInfoSet imageInfoSet,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_template.html");

		String imagePart = "";

		if (imageInfoSet != null) {
			ImagePartGenerator imagePartGenerator = new ImagePartGenerator(
					this.idMap);

			imagePart = imagePartGenerator.generateImage(
					imageInfoSet.getDiagramImageInfo(), "");
		}

		Object[] args = { imagePart,
				this.generateSummaryTable(htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private String generateSummaryTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("allclasses_template.html");

		Object[] args = { this.generateAllClassesTable(diagram,
				htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private String generateAllClassesTable(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("allclasses_row_template.html");

		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
			HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList
					.get(i);

			for (Object object : pageGenerator.getObjectList(diagram)) {
				Object[] args = {
						pageGenerator.getType() + "/"
								+ pageGenerator.getObjectId(object) + ".html",
						pageGenerator.getObjectName(object) };
				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	public int countAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList) {
		int count = 0;

		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
			HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList
					.get(i);
			count += pageGenerator.getObjectList(diagram).size();
		}

		return count;
	}
}
