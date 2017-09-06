package org.insightech.er.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.controller.editpart.element.PagableFreeformRootEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.ERDiagramPopupMenuManager;
import org.insightech.er.editor.view.action.category.CategoryManageAction;
import org.insightech.er.editor.view.action.category.ChangeFreeLayoutAction;
import org.insightech.er.editor.view.action.category.ChangeShowReferredTablesAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDDLAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDictionaryAction;
import org.insightech.er.editor.view.action.dbexport.ExportToExcelAction;
import org.insightech.er.editor.view.action.dbexport.ExportToHtmlAction;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.editor.view.action.dbexport.ExportToJavaAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTestDataAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTranslationDictionaryAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromDBAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromFileAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction;
import org.insightech.er.editor.view.action.edit.CopyAction;
import org.insightech.er.editor.view.action.edit.DeleteWithoutUpdateAction;
import org.insightech.er.editor.view.action.edit.EditAllAttributesAction;
import org.insightech.er.editor.view.action.edit.PasteAction;
import org.insightech.er.editor.view.action.edit.SelectAllContentsAction;
import org.insightech.er.editor.view.action.group.GroupManageAction;
import org.insightech.er.editor.view.action.line.AutoResizeModelAction;
import org.insightech.er.editor.view.action.line.DefaultLineAction;
import org.insightech.er.editor.view.action.line.ERDiagramAlignmentAction;
import org.insightech.er.editor.view.action.line.ERDiagramMatchHeightAction;
import org.insightech.er.editor.view.action.line.ERDiagramMatchWidthAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction;
import org.insightech.er.editor.view.action.line.RightAngleLineAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction;
import org.insightech.er.editor.view.action.option.OptionSettingAction;
import org.insightech.er.editor.view.action.option.notation.ChangeCapitalAction;
import org.insightech.er.editor.view.action.option.notation.ChangeNotationExpandGroupAction;
import org.insightech.er.editor.view.action.option.notation.ChangeStampAction;
import org.insightech.er.editor.view.action.option.notation.LockEditAction;
import org.insightech.er.editor.view.action.option.notation.TooltipAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFunnyAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToColumnAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToDetailAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToExcludeTypeAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToNameAndKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyTitleAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIDEF1XNotationAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIENotationAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToBothAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToLogicalAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToPhysicalAction;
import org.insightech.er.editor.view.action.printer.PageSettingAction;
import org.insightech.er.editor.view.action.printer.PrintImageAction;
import org.insightech.er.editor.view.action.search.SearchAction;
import org.insightech.er.editor.view.action.testdata.TestDataCreateAction;
import org.insightech.er.editor.view.action.tracking.ChangeTrackingAction;
import org.insightech.er.editor.view.action.translation.TranslationManageAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustAction;
import org.insightech.er.editor.view.contributor.ERDiagramActionBarContributor;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDropTargetListener;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePage;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePopupMenuManager;
import org.insightech.er.editor.view.tool.ERDiagramPaletteRoot;
import org.insightech.er.extention.ExtensionLoader;

/**
 * TODO ON UPDATE、ON DELETE のプルダウンを設定できるものだけに制限する<br>
 * TODO デフォルト値に型の制限を適用する<br>
 * 
 */
public class ERDiagramEditor extends GraphicalEditorWithPalette {

	private ERDiagram diagram;

	private ERDiagramEditPartFactory editPartFactory;

	private ERDiagramOutlinePage outlinePage;

	private MenuManager outlineMenuMgr;

	private ERDiagramActionBarContributor actionBarContributor;

	private ERDiagramPaletteRoot palette;

	private ExtensionLoader extensionLoader;

	private boolean isDirty;

	/**
	 * コンストラクタ.
	 * 
	 * @param diagram
	 *            ERDiagram
	 * @param editPartFactory
	 *            ERDiagramEditPartFactory
	 * @param outlinePage
	 *            ERDiagramOutlinePage
	 * @param editDomain
	 *            DefaultEditDomain
	 */
	public ERDiagramEditor(ERDiagram diagram,
			ERDiagramEditPartFactory editPartFactory,
			ERDiagramOutlinePage outlinePage, DefaultEditDomain editDomain,
			ERDiagramPaletteRoot palette) {
		this.diagram = diagram;
		this.editPartFactory = editPartFactory;
		this.outlinePage = outlinePage;
		this.palette = palette;

		this.setEditDomain(editDomain);

		try {
			this.extensionLoader = new ExtensionLoader(this);
		} catch (CoreException e) {
			ERDiagramActivator.showExceptionDialog(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.getSelectionSynchronizer().removeViewer(
				this.outlinePage.getViewer());
		super.dispose();
	}

	/**
	 * <pre>
	 * 保存時の処理
	 * ファイルの保存自体は、{@link ERDiagramMultiPageEditor} で行うため
	 * 各ページの {@link ERDiagramEditor} では、コマンドスタックのクリアのみを行う
	 * </pre>
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		this.getCommandStack().markSaveLocation();
		this.isDirty = false;
	}

	public void resetCommandStack() {
		this.getCommandStack().flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commandStackChanged(EventObject eventObject) {
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(eventObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = this.getGraphicalViewer();
		viewer.setEditPartFactory(editPartFactory);

		this.initViewerAction(viewer);
		this.initDragAndDrop(viewer);

		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true);
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);
		viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, true);

		MenuManager menuMgr = new ERDiagramPopupMenuManager(
				this.getActionRegistry(), this.diagram);

		this.extensionLoader.addERDiagramPopupMenu(menuMgr,
				this.getActionRegistry());

		viewer.setContextMenu(menuMgr);

		viewer.setContents(diagram);

		this.outlineMenuMgr = new ERDiagramOutlinePopupMenuManager(
				this.diagram, this.getActionRegistry(),
				this.outlinePage.getOutlineActionRegistory(),
				this.outlinePage.getViewer());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return this.palette;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();

		} else if (type == IContentOutlinePage.class) {
			return this.outlinePage;
		}

		return super.getAdapter(type);
	}

	/**
	 * <pre>
	 * このページが選択された際の処理
	 * </pre>
	 */
	public void changeCategory() {
		this.outlinePage.setCategory(this.getEditDomain(),
				this.getGraphicalViewer(), this.outlineMenuMgr,
				this.getActionRegistry());

		this.getSelectionSynchronizer().addViewer(this.outlinePage.getViewer());

		this.getEditDomain().setPaletteViewer(this.getPaletteViewer());

		this.getActionRegistry().getAction(TooltipAction.ID)
				.setChecked(this.diagram.isTooltip());
		this.getActionRegistry().getAction(LockEditAction.ID)
				.setChecked(this.diagram.isDisableSelectColumn());

		((ChangeBackgroundColorAction) this.getActionRegistry().getAction(
				ChangeBackgroundColorAction.ID)).setRGB();

	}

	public void removeSelection() {
		this.getGraphicalViewer().deselectAll();
		this.getSelectionSynchronizer().removeViewer(
				this.outlinePage.getViewer());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void createActions() {
		super.createActions();

		ActionRegistry registry = this.getActionRegistry();
		List<String> selectionActionList = this.getSelectionActions();

		List<IAction> actionList = new ArrayList<IAction>(
				Arrays.asList(new IAction[] {
						new ChangeViewToLogicalAction(this),
						new ChangeViewToPhysicalAction(this),
						new ChangeViewToBothAction(this),
						new ChangeToIENotationAction(this),
						new ChangeToIDEF1XNotationAction(this),
						new ChangeNotationLevelToColumnAction(this),
						new ChangeNotationLevelToExcludeTypeAction(this),
						new ChangeNotationLevelToDetailAction(this),
						new ChangeNotationLevelToOnlyTitleAction(this),
						new ChangeNotationLevelToOnlyKeyAction(this),
						new ChangeNotationLevelToNameAndKeyAction(this),
						new ChangeNotationExpandGroupAction(this),
						new ChangeDesignToFunnyAction(this),
						new ChangeDesignToFrameAction(this),
						new ChangeDesignToSimpleAction(this),
						new ChangeCapitalAction(this),
						new ChangeStampAction(this),
						new GroupManageAction(this),
						new ChangeTrackingAction(this),
						new OptionSettingAction(this),
						new CategoryManageAction(this),
						new ChangeFreeLayoutAction(this),
						new ChangeShowReferredTablesAction(this),
						new TranslationManageAction(this),
						new TestDataCreateAction(this),
						new ImportFromDBAction(this),
						new ImportFromFileAction(this),
						new ExportToImageAction(this),
						new ExportToExcelAction(this),
						new ExportToHtmlAction(this),
						new ExportToJavaAction(this),
						new ExportToDDLAction(this),
						new ExportToDictionaryAction(this),
						new ExportToTranslationDictionaryAction(this),
						new ExportToTestDataAction(this),
						new PageSettingAction(this),
						new EditAllAttributesAction(this),
						new DirectEditAction((IWorkbenchPart) this),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.LEFT),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.CENTER),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.RIGHT),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.TOP),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.MIDDLE),
						new ERDiagramAlignmentAction((IWorkbenchPart) this,
								PositionConstants.BOTTOM),
						new ERDiagramMatchWidthAction(this),
						new ERDiagramMatchHeightAction(this),
						new HorizontalLineAction(this),
						new VerticalLineAction(this),
						new RightAngleLineAction(this),
						new DefaultLineAction(this), new CopyAction(this),
						new PasteAction(this), new SearchAction(this),
						new AutoResizeModelAction(this),
						new PrintImageAction(this),
						new DeleteWithoutUpdateAction(this),
						new SelectAllContentsAction(this) }));

		actionList.addAll(this.extensionLoader.createExtendedActions());

		for (IAction action : actionList) {
			if (action instanceof SelectionAction) {
				IAction originalAction = registry.getAction(action.getId());

				if (originalAction != null) {
					selectionActionList.remove(originalAction);
				}
				selectionActionList.add(action.getId());
			}

			registry.registerAction(action);
		}

		IAction action = registry.getAction(SearchAction.ID);
		this.addKeyHandler(action);
	}

	@SuppressWarnings("unchecked")
	private void initViewerAction(GraphicalViewer viewer) {
		ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(
				this.diagram);
		viewer.setRootEditPart(rootEditPart);

		ZoomManager manager = rootEditPart.getZoomManager();

		double[] zoomLevels = new double[] { 0.1, 0.25, 0.5, 0.75, 0.8, 1.0,
				1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);

		List<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		ZoomInAction zoomInAction = new ZoomInAction(manager);
		ZoomOutAction zoomOutAction = new ZoomOutAction(manager);
		ZoomAdjustAction zoomAdjustAction = new ZoomAdjustAction(manager);

		this.getActionRegistry().registerAction(zoomInAction);
		this.getActionRegistry().registerAction(zoomOutAction);
		this.getActionRegistry().registerAction(zoomAdjustAction);

		this.addKeyHandler(zoomInAction);
		this.addKeyHandler(zoomOutAction);

		IFigure gridLayer = rootEditPart.getLayer(LayerConstants.GRID_LAYER);
		gridLayer.setForegroundColor(Resources.GRID_COLOR);

		IAction action = new ToggleGridAction(viewer);
		this.getActionRegistry().registerAction(action);

		action = new ToggleSnapToGeometryAction(viewer);
		this.getActionRegistry().registerAction(action);

		action = new ChangeBackgroundColorAction(this, this.diagram);
		this.getActionRegistry().registerAction(action);
		this.getSelectionActions().add(action.getId());

		action = new TooltipAction(this);
		this.getActionRegistry().registerAction(action);

		action = new LockEditAction(this);
		this.getActionRegistry().registerAction(action);

		action = new ExportToDBAction(this);
		this.getActionRegistry().registerAction(action);

		this.actionBarContributor = new ERDiagramActionBarContributor();
		this.actionBarContributor.init(this.getEditorSite().getActionBars(),
				this.getSite().getPage());
		// action = new ToggleRulerVisibilityAction(viewer);
		// this.getActionRegistry().registerAction(action);
	}

	private void initDragAndDrop(GraphicalViewer viewer) {
		AbstractTransferDragSourceListener dragSourceListener = new ERDiagramTransferDragSourceListener(
				viewer, TemplateTransfer.getInstance());
		viewer.addDragSourceListener(dragSourceListener);

		AbstractTransferDropTargetListener dropTargetListener = new ERDiagramTransferDropTargetListener(
				viewer, TemplateTransfer.getInstance());

		viewer.addDropTargetListener(dropTargetListener);
	}

	private void addKeyHandler(IAction action) {
		IHandlerService service = (IHandlerService) this.getSite().getService(
				IHandlerService.class);
		service.activateHandler(action.getActionDefinitionId(),
				new ActionHandler(action));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	public void resetEditDomain() {
		this.getEditDomain().setPaletteViewer(this.getPaletteViewer());
	}

	public ERDiagramActionBarContributor getActionBarContributor() {
		return actionBarContributor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IEditorPart editorPart = getSite().getPage().getActiveEditor();

		if (editorPart instanceof ERDiagramMultiPageEditor) {
			ERDiagramMultiPageEditor multiPageEditorPart = (ERDiagramMultiPageEditor) editorPart;

			if (this.equals(multiPageEditorPart.getActiveEditor())) {
				updateActions(this.getSelectionActions());
			}

		} else {
			super.selectionChanged(part, selection);
		}
	}

	public Point getLocation() {
		FigureCanvas canvas = (FigureCanvas) this.getGraphicalViewer()
				.getControl();
		return canvas.getViewport().getViewLocation();
	}

	public void setLocation(int x, int y) {
		FigureCanvas canvas = (FigureCanvas) this.getGraphicalViewer()
				.getControl();
		canvas.scrollTo(x, y);
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public boolean isDirty() {
		if (this.isDirty) {
			return true;
		}

		return super.isDirty();
	}

	public String getProjectFilePath(String extention) {
		IFile file = ((IFileEditorInput) this.getEditorInput()).getFile();
		String filePath = file.getLocation().toOSString();
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + extention;

		return filePath;
	}

}
