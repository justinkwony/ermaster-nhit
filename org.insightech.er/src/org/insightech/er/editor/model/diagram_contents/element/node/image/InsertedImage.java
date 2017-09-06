package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.IOUtils;

public class InsertedImage extends NodeElement implements Comparable<InsertedImage> {

	private static final long serialVersionUID = -2035035973213266486L;

	private String base64EncodedData;

	private int hue;

	private int saturation;

	private int brightness;

	private int alpha;
	
	private boolean fixAspectRatio;
	
	public InsertedImage() {
		this.alpha = 255;
		this.fixAspectRatio = true;
	}

	public String getBase64EncodedData() {
		return base64EncodedData;
	}

	public void setBase64EncodedData(String base64EncodedData) {
		this.base64EncodedData = base64EncodedData;
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "image";
	}

	public void setImageFilePath(String imageFilePath) {
		InputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(imageFilePath));

			byte[] data = IOUtils.toByteArray(in);

			String encodedData = new String(Base64.encodeBase64(data));
			this.setBase64EncodedData(encodedData);

		} catch (Exception e) {
			ERDiagramActivator.showExceptionDialog(e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					ERDiagramActivator.showExceptionDialog(e);
				}
			}
		}
	}

	public int getHue() {
		return hue;
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	public int getSaturation() {
		return saturation;
	}

	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public boolean isFixAspectRatio() {
		return fixAspectRatio;
	}

	public void setFixAspectRatio(boolean fixAspectRatio) {
		this.fixAspectRatio = fixAspectRatio;
	}

	public void setDirty() {
	}

	public int compareTo(InsertedImage other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.base64EncodedData).compareTo(
				Format.null2blank(other.base64EncodedData));

		return compareTo;	}
}
