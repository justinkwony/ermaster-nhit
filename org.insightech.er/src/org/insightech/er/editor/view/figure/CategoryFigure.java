package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;

public class CategoryFigure extends RectangleFigure {

	private Label label;

	public CategoryFigure(String name) {
		this.setOpaque(true);

		ToolbarLayout layout = new ToolbarLayout();
		this.setLayoutManager(layout);

		this.label = new Label();
		this.label.setText(name);
		this.label.setBorder(new MarginBorder(7));
		this.add(this.label);
	}

	public void setName(String name) {
		this.label.setText(name);
	}

	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAlpha(100);
		super.fillShape(graphics);
	}

}
