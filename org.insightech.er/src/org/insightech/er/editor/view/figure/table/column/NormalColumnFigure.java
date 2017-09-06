package org.insightech.er.editor.view.figure.table.column;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Insets;

public class NormalColumnFigure extends Figure {

	public NormalColumnFigure() {
		FlowLayout layout = new FlowLayout();
		layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		this.setLayoutManager(layout);

		this.setBorder(new MarginBorder(new Insets(0, 5, 0, 0)));
	}

	public void clearLabel() {
		this.removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		if (graphics.getBackgroundColor().equals(
				this.getParent().getBackgroundColor())) {
			graphics.setAlpha(0);
		}

		super.paintFigure(graphics);
	}

}
