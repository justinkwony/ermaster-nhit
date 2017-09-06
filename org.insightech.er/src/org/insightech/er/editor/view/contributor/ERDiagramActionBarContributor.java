package org.insightech.er.editor.view.contributor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.EditorPart;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction.ExportToDBRetargetAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction.ChangeBackgroundColorRetargetAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction.HorizontalLineRetargetAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction.VerticalLineRetargetAction;
import org.insightech.er.editor.view.action.option.notation.LockEditAction;
import org.insightech.er.editor.view.action.option.notation.TooltipAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustRetargetAction;

public class ERDiagramActionBarContributor extends ActionBarContributor {

	public ERDiagramActionBarContributor() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildActions() {
		this.addRetargetAction(new RetargetAction(ActionFactory.SELECT_ALL
				.getId(), "selectAll"));
		this.addRetargetAction(new RetargetAction(ActionFactory.PRINT.getId(),
				"print"));

		this.addRetargetAction(new DeleteRetargetAction());
		this.addRetargetAction(new RetargetAction(ActionFactory.COPY.getId(),
				"copy"));
		this.addRetargetAction(new RetargetAction(ActionFactory.PASTE.getId(),
				"paste"));

		this.addRetargetAction(new UndoRetargetAction());
		this.addRetargetAction(new RedoRetargetAction());

		ZoomInRetargetAction zoomInAction = new ZoomInRetargetAction();
		zoomInAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ZOOM_IN));
		ZoomOutRetargetAction zoomOutAction = new ZoomOutRetargetAction();
		zoomOutAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ZOOM_OUT));
		this.addRetargetAction(zoomInAction);
		this.addRetargetAction(zoomOutAction);
		this.addRetargetAction(new ZoomAdjustRetargetAction());

		RetargetAction gridAction = new RetargetAction(
				GEFActionConstants.TOGGLE_GRID_VISIBILITY,
				ResourceString.getResourceString("action.title.grid"),
				IAction.AS_CHECK_BOX);
		gridAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.GRID));

		this.addRetargetAction(gridAction);

		RetargetAction gridSnapAction = new RetargetAction(
				GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY,
				ResourceString.getResourceString("action.title.grid.snap"),
				IAction.AS_CHECK_BOX);
		gridSnapAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.GRID_SNAP));

		this.addRetargetAction(gridSnapAction);

		RetargetAction tooltipAction = new RetargetAction(TooltipAction.ID,
				ResourceString.getResourceString("action.title.tooltip"),
				IAction.AS_CHECK_BOX);
		tooltipAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.TOOLTIP));
		this.addRetargetAction(tooltipAction);

		RetargetAction lockEditAction = new RetargetAction(LockEditAction.ID,
				ResourceString.getResourceString("action.title.lock.edit"),
				IAction.AS_CHECK_BOX);
		lockEditAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.LOCK_EDIT));
		this.addRetargetAction(lockEditAction);

		this.addRetargetAction(new ExportToDBRetargetAction());

		AlignmentRetargetAction alignLeftAction = new AlignmentRetargetAction(
				PositionConstants.LEFT);
		alignLeftAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_LEFT));
		alignLeftAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignLeftAction);
		AlignmentRetargetAction alignCenterAction = new AlignmentRetargetAction(
				PositionConstants.CENTER);
		alignCenterAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_CENTER));
		alignCenterAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignCenterAction);
		AlignmentRetargetAction alignRightAction = new AlignmentRetargetAction(
				PositionConstants.RIGHT);
		alignRightAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_RIGHT));
		alignRightAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignRightAction);
		AlignmentRetargetAction alignTopAction = new AlignmentRetargetAction(
				PositionConstants.TOP);
		alignTopAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_TOP));
		alignTopAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignTopAction);
		AlignmentRetargetAction alignMiddleAction = new AlignmentRetargetAction(
				PositionConstants.MIDDLE);
		alignMiddleAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_MIDDLE));
		alignMiddleAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignMiddleAction);
		AlignmentRetargetAction alignBottomAction = new AlignmentRetargetAction(
				PositionConstants.BOTTOM);
		alignBottomAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.ALIGN_BOTTOM));
		alignBottomAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignBottomAction);

		MatchWidthRetargetAction matchWidthAction = new MatchWidthRetargetAction();
		matchWidthAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.MATCH_WIDTH));
		matchWidthAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(matchWidthAction);
		MatchHeightRetargetAction matchHeightAction = new MatchHeightRetargetAction();
		matchHeightAction.setImageDescriptor(ERDiagramActivator
				.getImageDescriptor(ImageKey.MATCH_HEIGHT));
		matchHeightAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(matchHeightAction);

		this.addRetargetAction(new HorizontalLineRetargetAction());
		this.addRetargetAction(new VerticalLineRetargetAction());

		this.addRetargetAction(new ChangeBackgroundColorRetargetAction());
	}

	public void contributeToToolBar(ERDiagram diagram,
			IToolBarManager toolBarManager,
			ZoomComboContributionItem zoomComboContributionItem) {
		toolBarManager.add(this.getAction(ActionFactory.DELETE.getId()));
		toolBarManager.add(this.getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(this.getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(getActionRegistry().getAction(ZoomAdjustAction.ID));

		toolBarManager.add(zoomComboContributionItem);

		toolBarManager.add(new Separator());

		toolBarManager.add(this
				.getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		toolBarManager.add(this
				.getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		toolBarManager.add(this.getAction(TooltipAction.ID));
		toolBarManager.add(this.getAction(LockEditAction.ID));

		toolBarManager.add(new Separator());

		toolBarManager.add(this.getAction(ExportToDBAction.ID));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_LEFT));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_CENTER));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_RIGHT));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_TOP));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_MIDDLE));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_BOTTOM));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.MATCH_WIDTH));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.MATCH_HEIGHT));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				HorizontalLineAction.ID));
		toolBarManager
				.add(getActionRegistry().getAction(VerticalLineAction.ID));

		toolBarManager.add(getActionRegistry().getAction(
				ChangeBackgroundColorAction.ID));

		toolBarManager.add(new Separator());

		final FontNameContributionItem fontNameContributionItem = new FontNameContributionItem(
				getPage());
		final FontSizeContributionItem fontSizeContributionItem = new FontSizeContributionItem(
				getPage());

		toolBarManager.add(fontNameContributionItem);
		toolBarManager.add(fontSizeContributionItem);

		this.getPage().addSelectionListener(new ISelectionListener() {

			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {

				if (selection instanceof IStructuredSelection) {
					List selectedEditParts = ((IStructuredSelection) selection)
							.toList();

					if (!selectedEditParts.isEmpty()) {
						if (selectedEditParts.get(0) instanceof EditPart) {
							Object model = ((EditPart) selectedEditParts.get(0))
									.getModel();

							if (model instanceof ViewableModel) {
								ViewableModel viewableModel = (ViewableModel) model;

								String fontName = viewableModel.getFontName();
								int fontSize = viewableModel.getFontSize();

								if (fontName != null) {
									fontNameContributionItem.setText(fontName);

								} else {
									FontData fontData = Display.getCurrent()
											.getSystemFont().getFontData()[0];
									fontNameContributionItem.setText(fontData
											.getName());
									viewableModel.setFontName(fontData
											.getName());
								}

								if (fontSize > 0) {
									fontSizeContributionItem.setText(String
											.valueOf(fontSize));

								} else {
									fontSizeContributionItem.setText(String
											.valueOf(ViewableModel.DEFAULT_FONT_SIZE));
									viewableModel.setFontSize(fontSize);
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(IWorkbenchActionConstants.PRINT_EXT);
	}

	public void initRetargetActions(EditorPart newEditor) {
		Iterator iter = this.getActionRegistry().getActions();

		while (iter.hasNext()) {
			IAction action = (IAction) iter.next();
			if (action instanceof RetargetAction) {
				((RetargetAction) action).partActivated(newEditor);
			}
		}
	}
}
