package org.insightech.er;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class Resources {

	public static final int PREFERENCE_PAGE_MARGIN_TOP = 30;

	public static final int SMALL_BUTTON_WIDTH = 70;

	public static final int MIDDLE_BUTTON_WIDTH = 120;

	public static final int LARGE_BUTTON_WIDTH = 250;

	public static final int BUTTON_ADD_REMOVE_WIDTH = 80;

	public static final int DESCRIPTION_WIDTH = 400;

	public static final int INDENT = 20;

	public static final int VERTICAL_SPACING = 15;

	public static final int MARGIN = 10;

	public static final int MARGIN_TAB = 10;

	public static final int CHECKBOX_INDENT = 5;

	public static Color PINK = new Color(Display.getCurrent(), 255, 0, 255);

	public static Color ADDED_COLOR = new Color(Display.getCurrent(), 128, 128,
			255);

	public static Color UPDATED_COLOR = new Color(Display.getCurrent(), 128,
			255, 128);

	public static Color REMOVED_COLOR = new Color(Display.getCurrent(), 255,
			128, 128);

	public static Color GRID_COLOR = new Color(Display.getCurrent(), 220, 220,
			255);

	public static Color DEFAULT_TABLE_COLOR = new Color(Display.getCurrent(),
			128, 128, 192);

	public static Color SELECTED_REFERENCED_COLUMN = new Color(
			Display.getCurrent(), 255, 230, 230);

	public static Color SELECTED_FOREIGNKEY_COLUMN = new Color(
			Display.getCurrent(), 230, 255, 230);

	public static Color SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN = new Color(
			Display.getCurrent(), 230, 230, 255);

	public static Color VERY_LIGHT_GRAY = new Color(Display.getCurrent(), 230,
			230, 230);

	public static Color LINE_COLOR = new Color(Display.getCurrent(), 180, 180,
			255);

	public static Color TEST_COLOR = new Color(Display.getCurrent(), 230, 230,
			230);

	public static final Color PRIMARY_COLOR = new Color(Display.getCurrent(),
			252, 250, 167);

	public static final Color FOREIGN_COLOR = new Color(Display.getCurrent(),
			211, 231, 245);

	public static final Color NOT_NULL_COLOR = new Color(Display.getCurrent(),
			254, 228, 207);

	private static Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

	private static Map<FontInfo, Font> fontMap = new HashMap<FontInfo, Font>();

	private static class FontInfo {

		private String fontName;

		private int fontSize;

		private FontInfo(String fontName, int fontSize) {
			this.fontName = fontName;
			this.fontSize = fontSize;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fontName == null) ? 0 : fontName.hashCode());
			result = prime * result + fontSize;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FontInfo other = (FontInfo) obj;
			if (fontName == null) {
				if (other.fontName != null)
					return false;
			} else if (!fontName.equals(other.fontName))
				return false;
			if (fontSize != other.fontSize)
				return false;
			return true;
		}
	}

	public static Color getColor(int[] rgb) {
		int key = rgb[0] * 1000000 + rgb[1] * 1000 + rgb[2];

		Color color = colorMap.get(key);

		if (color != null) {
			return color;
		}

		color = new Color(Display.getCurrent(), rgb[0], rgb[1], rgb[2]);
		colorMap.put(key, color);

		return color;
	}

	public static void disposeColorMap() {
		for (Color color : colorMap.values()) {
			if (!color.isDisposed()) {
				color.dispose();
			}
		}

		colorMap.clear();
	}

	public static Font getFont(String fontName, int fontSize) {
		return getFont(fontName, fontSize, SWT.NORMAL);
	}

	public static Font getFont(String fontName, int fontSize, int style) {
		FontInfo fontInfo = new FontInfo(fontName, fontSize);

		Font font = fontMap.get(fontInfo);

		if (font != null) {
			return font;
		}

		font = new Font(Display.getCurrent(), fontName, fontSize, style);
		fontMap.put(fontInfo, font);

		return font;
	}

	public static void disposeFontMap() {
		for (Font font : fontMap.values()) {
			if (!font.isDisposed()) {
				font.dispose();
			}
		}

		fontMap.clear();
	}

}
