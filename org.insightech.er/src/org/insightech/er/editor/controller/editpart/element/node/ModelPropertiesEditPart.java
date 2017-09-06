package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.model_properties.ChangeModelPropertiesCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.ModelPropertiesDialog;
import org.insightech.er.editor.view.figure.ModelPropertiesFigure;

public class ModelPropertiesEditPart extends NodeElementEditPart implements
		IResizable {

	public ModelPropertiesEditPart() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		ModelPropertiesFigure figure = new ModelPropertiesFigure();

		this.changeFont(figure);

		figure.setVisible(settings.getModelProperties().isDisplay());

		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doRefreshVisuals() {
		ERDiagram diagram = this.getDiagram();
		ModelProperties modelProperties = (ModelProperties) this.getModel();

		ModelPropertiesFigure figure = (ModelPropertiesFigure) this.getFigure();

		figure.setData(modelProperties.getProperties(),
				modelProperties.getCreationDate(),
				modelProperties.getUpdatedDate(), diagram.getDiagramContents()
						.getSettings().getTableStyle(),
				modelProperties.getColor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshSettings(Settings settings) {
		this.figure.setVisible(settings.getModelProperties().isDisplay());
		super.refreshSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setVisible() {
		ERDiagram diagram = this.getDiagram();

		Settings settings = diagram.getDiagramContents().getSettings();

		this.figure.setVisible(settings.getModelProperties().isDisplay());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequestOpen() {
		ERDiagram diagram = this.getDiagram();

		ModelProperties copyModelProperties = (ModelProperties) diagram
				.getDiagramContents().getSettings().getModelProperties()
				.clone();

		ModelPropertiesDialog dialog = new ModelPropertiesDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				copyModelProperties);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeModelPropertiesCommand command = new ChangeModelPropertiesCommand(
					diagram, copyModelProperties);

			this.executeCommand(command);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeleteable() {
		return false;
	}
}
