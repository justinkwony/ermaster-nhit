package org.insightech.er.editor.view.figure.connection.decoration;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class ERDecoration extends PolygonDecoration {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintFigure(Graphics graphics) {
		ERDiagramConnection connection = (ERDiagramConnection) this.getParent();

		graphics.setAntialias(SWT.ON);

		Color color = connection.getColor();

		if (color != null) {
			graphics.setForegroundColor(color);
			graphics.setBackgroundColor(color);
		}

		super.paintFigure(graphics);
	}
}
