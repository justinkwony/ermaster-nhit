package org.insightech.er.editor.model.dbexport.image;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ImageInfoSet {

	private static final int MAX_NAME_LENGTH = 26;

	private ImageInfo diagramImageInfo;

	private Map<Category, ImageInfo> categoryImageInfoMap;

	private Map<String, Integer> fileNameMap = new HashMap<String, Integer>();

	public ImageInfoSet(ImageInfo diagramImageInfo) {
		this.diagramImageInfo = diagramImageInfo;
		this.categoryImageInfoMap = new HashMap<Category, ImageInfo>();
	}

	public ImageInfo getDiagramImageInfo() {
		return this.diagramImageInfo;
	}

	public void addImageInfo(Category category, ImageInfo imageInfo) {
		this.categoryImageInfoMap.put(category, imageInfo);
	}

	public ImageInfo getImageInfo(Category category) {
		return this.categoryImageInfoMap.get(category);
	}

	public String decideFileName(String name, String extension) {
		if (name.length() > MAX_NAME_LENGTH) {
			name = name.substring(0, MAX_NAME_LENGTH);
		}

		String fileName = null;

		Integer sameNameNum = fileNameMap.get(name.toUpperCase());
		if (sameNameNum == null) {
			sameNameNum = 0;
			fileName = name;

		} else {
			do {
				sameNameNum++;
				fileName = name + "(" + sameNameNum + ")";
			} while (fileNameMap.containsKey(fileName.toUpperCase()));
		}

		fileNameMap.put(name.toUpperCase(), sameNameNum);

		return fileName + extension;
	}

}
