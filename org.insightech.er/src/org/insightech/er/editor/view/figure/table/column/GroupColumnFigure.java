package org.insightech.er.editor.view.figure.table.column;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;

public class GroupColumnFigure extends Figure {

	public GroupColumnFigure() {
		FlowLayout layout = new FlowLayout();
		this.setLayoutManager(layout);
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
