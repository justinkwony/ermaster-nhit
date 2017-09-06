package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class ChangeFontCommand extends AbstractCommand {

	private ViewableModel viewableModel;

	private String oldFontName;

	private String newFontName;

	private int oldFontSize;

	private int newFontSize;

	public ChangeFontCommand(ViewableModel viewableModel, String fontName,
			int fontSize) {
		this.viewableModel = viewableModel;

		this.oldFontName = viewableModel.getFontName();
		this.oldFontSize = viewableModel.getFontSize();

		this.newFontName = fontName;
		this.newFontSize = fontSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.viewableModel.setFontName(this.newFontName);
		this.viewableModel.setFontSize(this.newFontSize);

		this.viewableModel.refreshFont();

		if (this.viewableModel instanceof NodeElement) {
			// to expand categories including this element.
			((NodeElement) this.viewableModel).refreshCategory();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.viewableModel.setFontName(this.oldFontName);
		this.viewableModel.setFontSize(this.oldFontSize);

		this.viewableModel.refreshFont();
	}
}
