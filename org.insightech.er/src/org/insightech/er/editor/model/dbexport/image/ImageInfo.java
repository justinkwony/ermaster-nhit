package org.insightech.er.editor.model.dbexport.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.SimpleRootEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ImageInfo {

	private Image image;

	private Map<TableView, Location> tableLocationMap;

	private int format;

	private String path;

	private byte[] imageData;

	public ImageInfo(Image image, Map<TableView, Location> tableLocationMap) {
		this.image = image;
		this.tableLocationMap = tableLocationMap;
	}

	public void dispose() {
		this.image.dispose();
	}

	public Image getImage() {
		return image;
	}

	public Map<TableView, Location> getTableLocationMap() {
		return tableLocationMap;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public int getExcelPictureType() throws InputException {
		if (this.format == SWT.IMAGE_JPEG) {
			return HSSFWorkbook.PICTURE_TYPE_JPEG;

		} else if (this.format == SWT.IMAGE_PNG) {
			return HSSFWorkbook.PICTURE_TYPE_PNG;

		} else {
			throw new InputException(
					"dialog.message.export.image.not.supported");
		}
	}

	public void toImageData() throws InterruptedException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// try {
		this.format = SWT.IMAGE_PNG;
		//
		ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = new ImageData[] { this.image.getImageData() };
		imgLoader.save(out, this.format);
		//
		// } catch (SWTException e) {
		// if (this.format == SWT.IMAGE_PNG) {
		// BufferedImage bufferedImage = new BufferedImage(
		// image.getBounds().width, image.getBounds().height,
		// BufferedImage.TYPE_INT_RGB);
		//
		// ImageUtils.drawAtBufferedImage(bufferedImage, image, 0, 0);

		// BufferedImage bufferedImage =
		// ImageUtils.convertToBufferedImage(image);
		//
		// String formatName = "png";
		//
		// ImageIO.write(bufferedImage, formatName, out);

		// } else {
		// throw e;
		// }
		// }

		this.imageData = out.toByteArray();
	}

	public static ImageInfo createImage(GraphicalViewer viewer, int format,
			String path) {
		GC figureCanvasGC = null;
		GC imageGC = null;

		try {
			ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer
					.getRootEditPart();
			rootEditPart.refresh();
			IFigure rootFigure = ((LayerManager) rootEditPart)
					.getLayer(LayerConstants.PRINTABLE_LAYERS);

			EditPart editPart = viewer.getContents();
			editPart.refresh();
			ERDiagram diagram = (ERDiagram) editPart.getModel();

			Rectangle rootFigureBounds = getBounds(diagram, rootEditPart,
					rootFigure.getBounds());

			Control figureCanvas = viewer.getControl();
			figureCanvasGC = new GC(figureCanvas);

			Image img = new Image(Display.getCurrent(),
					rootFigureBounds.width + 20, rootFigureBounds.height + 20);
			imageGC = new GC(img);

			imageGC.setBackground(figureCanvasGC.getBackground());
			imageGC.setForeground(figureCanvasGC.getForeground());
			imageGC.setFont(figureCanvasGC.getFont());
			imageGC.setLineStyle(figureCanvasGC.getLineStyle());
			imageGC.setLineWidth(figureCanvasGC.getLineWidth());
			imageGC.setAntialias(SWT.OFF);
			// imageGC.setInterpolation(SWT.HIGH);

			Graphics imgGraphics = new SWTGraphics(imageGC);
			imgGraphics.setBackgroundColor(figureCanvas.getBackground());
			imgGraphics.fillRectangle(0, 0, rootFigureBounds.width + 20,
					rootFigureBounds.height + 20);

			int translateX = translateX(rootFigureBounds.x);
			int translateY = translateY(rootFigureBounds.y);

			imgGraphics.translate(translateX, translateY);

			rootFigure.paint(imgGraphics);

			Map<TableView, Location> tableLoacationMap = getTableLocationMap(
					rootEditPart, translateX, translateY, diagram);

			ImageInfo imageInfo = new ImageInfo(img, tableLoacationMap);
			imageInfo.setFormat(format);
			imageInfo.setPath(path);

			return imageInfo;

		} finally {
			if (figureCanvasGC != null) {
				figureCanvasGC.dispose();
			}
			if (imageGC != null) {
				imageGC.dispose();
			}
		}
	}

	private static Map<TableView, Location> getTableLocationMap(
			ScalableFreeformRootEditPart rootEditPart, int translateX,
			int translateY, ERDiagram diagram) {
		Map<TableView, Location> tableLocationMap = new HashMap<TableView, Location>();

		Category category = diagram.getCurrentCategory();

		for (Object child : rootEditPart.getContents().getChildren()) {
			NodeElementEditPart editPart = (NodeElementEditPart) child;
			NodeElement nodeElement = (NodeElement) editPart.getModel();
			if (!(nodeElement instanceof TableView)) {
				continue;
			}

			if (category == null || category.isVisible(nodeElement, diagram)) {
				IFigure figure = editPart.getFigure();
				Rectangle figureRectangle = figure.getBounds();

				Location location = new Location(
						figureRectangle.x + translateX, figureRectangle.y
								+ translateY, figureRectangle.width,
						figureRectangle.height);
				tableLocationMap.put((TableView) nodeElement, location);
			}
		}

		return tableLocationMap;
	}

	private static int translateX(int x) {
		return -x + 10;
	}

	private static int translateY(int y) {
		return -y + 10;
	}

	private static Rectangle getBounds(ERDiagram diagram,
			SimpleRootEditPart rootEditPart, Rectangle rootFigureBounds) {
		Category category = diagram.getCurrentCategory();

		if (category == null) {
			return rootFigureBounds;

		} else {
			Rectangle rectangle = new Rectangle();

			int minX = rootFigureBounds.x + rootFigureBounds.width;
			int minY = rootFigureBounds.y + rootFigureBounds.height;
			int maxX = rootFigureBounds.x;
			int maxY = rootFigureBounds.y;

			Map<NodeElement, IFigure> visibleElements = new HashMap<NodeElement, IFigure>();

			for (Object child : rootEditPart.getContents().getChildren()) {
				NodeElementEditPart editPart = (NodeElementEditPart) child;
				NodeElement nodeElement = (NodeElement) editPart.getModel();

				if (category.isVisible(nodeElement, diagram)) {
					IFigure figure = editPart.getFigure();
					Rectangle figureRectangle = figure.getBounds();

					visibleElements.put(nodeElement, figure);

					if (figureRectangle.x < minX) {
						minX = figureRectangle.x;

					}
					if (figureRectangle.x + figureRectangle.width > maxX) {
						maxX = figureRectangle.x + figureRectangle.width;
					}

					if (figureRectangle.y < minY) {
						minY = figureRectangle.y;
					}
					if (figureRectangle.y + figureRectangle.height > maxY) {
						maxY = figureRectangle.y + figureRectangle.height;
					}
				}
			}

			for (NodeElement sourceElement : visibleElements.keySet()) {
				for (ConnectionElement connection : sourceElement
						.getOutgoings()) {
					if (visibleElements.containsKey(connection.getTarget())) {
						for (Bendpoint bendpoint : connection.getBendpoints()) {
							int x = bendpoint.getX();
							int y = bendpoint.getY();

							if (bendpoint.isRelative()) {
								IFigure figure = visibleElements
										.get(sourceElement);
								Rectangle figureRectangle = figure.getBounds();
								x = figureRectangle.x + figureRectangle.width
										* 2;
								y = figureRectangle.y + figureRectangle.height
										* 2;
							}

							if (x < minX) {
								minX = x;

							} else if (x > maxX) {
								maxX = x;
							}

							if (y < minY) {
								minY = y;

							} else if (y > maxY) {
								maxY = y;
							}

						}
					}
				}
			}

			rectangle.x = minX;
			rectangle.y = minY;
			rectangle.width = maxX - minX;
			rectangle.height = maxY - minY;
			if (rectangle.width < 0) {
				rectangle.x = 0;
				rectangle.width = 0;
			}
			if (rectangle.height < 0) {
				rectangle.y = 0;
				rectangle.height = 0;
			}

			return rectangle;
		}
	}

}
