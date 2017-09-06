package org.insightech.er.editor.view.figure.table.style.funny;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.Resources;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class FunnyStyleSupport extends AbstractStyleSupport {

	private Label nameLabel;

	public FunnyStyleSupport(TableFigure tableFigure) {
		super(tableFigure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(TableFigure tableFigure) {
		tableFigure.setCornerDimensions(new Dimension(20, 20));
		tableFigure.setForegroundColor(ColorConstants.black);
		tableFigure.setBorder(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTitleBar(Figure top) {
		top.setLayoutManager(new BorderLayout());

		Figure title = new Figure();
		top.add(title, BorderLayout.TOP);
		FlowLayout titleLayout = new FlowLayout();
		titleLayout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		title.setLayoutManager(titleLayout);

		ImageFigure image = new ImageFigure();
		image.setBorder(new MarginBorder(new Insets(5, 10, 5, 2)));
		image.setImage(ERDiagramActivator.getImage(this.getTableFigure().getImageKey()));
		title.add(image);

		this.nameLabel = new Label();
		this.nameLabel.setBorder(new MarginBorder(new Insets(5, 0, 5, 20)));
		title.add(this.nameLabel);

		Figure separater = new Figure();
		separater.setSize(-1, 1);
		separater.setBackgroundColor(ColorConstants.black);
		separater.setOpaque(true);

		top.add(separater, BorderLayout.BOTTOM);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createColumnArea(IFigure columns) {
		this.initColumnArea(columns);

		columns.setBorder(new MarginBorder(0, 0, 0, 0));
		columns.setBackgroundColor(ColorConstants.white);
		columns.setOpaque(true);

		Figure centerFigure = new Figure();
		centerFigure.setLayoutManager(new BorderLayout());
		centerFigure.setBorder(new MarginBorder(new Insets(0, 2, 0, 2)));

		centerFigure.add(columns, BorderLayout.CENTER);
		this.getTableFigure().add(centerFigure, BorderLayout.CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createFooter() {
		IFigure footer = new Figure();
		BorderLayout footerLayout = new BorderLayout();
		footer.setLayoutManager(footerLayout);
		footer.setBorder(new MarginBorder(new Insets(0, 2, 0, 2)));

		IFigure footer1 = new Figure();
		footer1.setSize(-1, 10);
		footer1.setBackgroundColor(Resources.VERY_LIGHT_GRAY);
		footer1.setOpaque(true);

		footer.add(footer1, BorderLayout.TOP);

		IFigure footer2 = new Figure();
		footer2.setSize(-1, 7);

		footer.add(footer2, BorderLayout.BOTTOM);

		this.getTableFigure().add(footer, BorderLayout.BOTTOM);
	}

	public void setName(String name) {
		this.nameLabel.setForegroundColor(this.getTextColor());
		this.nameLabel.setText(name);
	}

	public void setFont(Font font, Font titleFont) {
		this.nameLabel.setFont(titleFont);
	}

	public void addColumn(NormalColumnFigure columnFigure, int viewMode,
			String physicalName, String logicalName, String type,
			boolean primaryKey, boolean foreignKey, boolean isNotNull,
			boolean uniqueKey, boolean displayKey, boolean displayDetail,
			boolean displayType, boolean isSelectedReferenced,
			boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated,
			boolean isRemoved) {

		Label label = this.createColumnLabel();
		label.setForegroundColor(ColorConstants.black);

		StringBuilder text = new StringBuilder();
		text.append(this.getColumnText(viewMode, physicalName, logicalName,
				type, isNotNull, uniqueKey, displayDetail, displayType));

		if (displayKey) {
			if (primaryKey) {
				ImageFigure image = new ImageFigure();
				image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
				image.setImage(ERDiagramActivator.getImage(ImageKey.PRIMARY_KEY));
				columnFigure.add(image);

			} else {
				Label filler = new Label();
				filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
				columnFigure.add(filler);

			}

			if (foreignKey) {
				ImageFigure image = new ImageFigure();
				image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
				image.setImage(ERDiagramActivator.getImage(ImageKey.FOREIGN_KEY));
				columnFigure.add(image);

			} else {
				Label filler = new Label();
				filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
				columnFigure.add(filler);

			}

			if (primaryKey && foreignKey) {
				label.setForegroundColor(ColorConstants.blue);

			} else if (primaryKey) {
				label.setForegroundColor(ColorConstants.red);

			} else if (foreignKey) {
				label.setForegroundColor(ColorConstants.darkGreen);

			}
		}

		label.setText(text.toString());

		this.setColumnFigureColor(columnFigure, isSelectedReferenced,
				isSelectedForeignKey, isAdded, isUpdated, isRemoved);

		columnFigure.add(label);
	}

	@Override
	public void addColumnGroup(GroupColumnFigure columnFigure, int viewMode,
			String name, boolean isAdded, boolean isUpdated, boolean isRemoved) {

		Label filler = new Label();
		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
		columnFigure.add(filler);

		filler = new Label();
		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
		columnFigure.add(filler);

		StringBuilder text = new StringBuilder();
		text.append(name);
		text.append(" (GROUP)");

		this.setColumnFigureColor(columnFigure, false, false, isAdded,
				isUpdated, isRemoved);

		Label label = this.createColumnLabel();

		label.setForegroundColor(ColorConstants.black);

		label.setText(text.toString());

		columnFigure.add(label);
	}
}
