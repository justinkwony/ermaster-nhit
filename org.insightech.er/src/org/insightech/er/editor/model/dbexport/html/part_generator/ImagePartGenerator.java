package org.insightech.er.editor.model.dbexport.html.part_generator;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.image.ImageInfo;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ImagePartGenerator {

	private Map<Object, Integer> idMap;

	private Category category;

	public ImagePartGenerator(Map<Object, Integer> idMap) {
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

	public String generateImage(ImageInfo imageInfo, String relativePath)
			throws IOException {
		if (imageInfo.getPath() == null) {
			return "";
		}

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_image_template.html");

		String pathToImageFile = relativePath + ExportToHtmlManager.IMAGE_DIR
				+ File.separator + imageInfo.getPath();

		Object[] args = {
				pathToImageFile,
				this.generateImageMap(imageInfo.getTableLocationMap(),
						relativePath) };

		return MessageFormat.format(template, args);
	}

	private String generateImageMap(Map<TableView, Location> tableLocationMap,
			String relativePath) throws IOException {
		StringBuilder sb = new StringBuilder();

		if (tableLocationMap != null) {
			String template = ExportToHtmlManager
					.getTemplate("overview/overview-summary_image_map_template.html");

			for (Map.Entry<TableView, Location> entry : tableLocationMap
					.entrySet()) {
				if (this.category == null
						|| this.category.contains(entry.getKey())) {
					Location location = entry.getValue();

					String pathToHtmlFile = entry.getKey().getObjectType()
							+ "/" + this.getObjectId(entry.getKey()) + ".html";

					pathToHtmlFile = relativePath + pathToHtmlFile;

					Object[] args = { String.valueOf(location.x),
							String.valueOf(location.y),
							String.valueOf(location.x + location.width),
							String.valueOf(location.y + location.height),
							pathToHtmlFile, };
					String row = MessageFormat.format(template, args);

					sb.append(row);
				}
			}
		}

		return sb.toString();
	}
}
