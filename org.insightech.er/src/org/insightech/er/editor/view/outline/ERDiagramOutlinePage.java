package org.insightech.er.editor.view.outline;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.editor.controller.editpart.outline.ERDiagramOutlineEditPartFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.outline.index.CreateIndexAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.insightech.er.editor.view.action.outline.sequence.CreateSequenceAction;
import org.insightech.er.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.insightech.er.editor.view.action.outline.trigger.CreateTriggerAction;
import org.insightech.er.editor.view.drag_drop.ERDiagramOutlineTransferDropTargetListener;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ERDiagramOutlinePage extends ContentOutlinePage {

	// ページをアウトラインとサムネイルに分離するコンポジット
	private SashForm sash;

	private TreeViewer viewer;

	private ERDiagram diagram;

	private LightweightSystem lws;

	private ScrollableThumbnail thumbnail;

	private GraphicalViewer graphicalViewer;

	private ActionRegistry outlineActionRegistory;

	private ActionRegistry registry;

	public ERDiagramOutlinePage(ERDiagram diagram) {
		// GEFツリービューワを使用する
		super(new TreeViewer());

		this.viewer = (TreeViewer) this.getViewer();
		this.diagram = diagram;

		this.outlineActionRegistory = new ActionRegistry();
		this.registerAction(this.viewer, outlineActionRegistory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		this.sash = new SashForm(parent, SWT.VERTICAL);

		// コンストラクタで指定したビューワの作成
		this.viewer.createControl(this.sash);

		// EditPartFactory の設定
		ERDiagramOutlineEditPartFactory editPartFactory = new ERDiagramOutlineEditPartFactory();
		this.viewer.setEditPartFactory(editPartFactory);

		// グラフィカル・エディタのルート・モデルをツリー・ビューワにも設定
		this.viewer.setContents(this.diagram);

		Canvas canvas = new Canvas(this.sash, SWT.BORDER);
		// サムネイル・フィギュアを配置する為の LightweightSystem
		this.lws = new LightweightSystem(canvas);

		this.resetView(this.registry);

		AbstractTransferDragSourceListener dragSourceListener = new ERDiagramTransferDragSourceListener(
				this.viewer, TemplateTransfer.getInstance());
		this.viewer.addDragSourceListener(dragSourceListener);
		
		this.diagram.refreshOutline();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control getControl() {
		// アウトライン・ビューをアクティブにした時にフォーカスが設定されるコントロールを返す
		return sash;
	}

	private void showThumbnail() {
		// RootEditPartのビューをソースとしてサムネイルを作成
		ScalableFreeformRootEditPart editPart = (ScalableFreeformRootEditPart) this.graphicalViewer
				.getRootEditPart();

		if (this.thumbnail != null) {
			this.thumbnail.deactivate();
		}

		this.thumbnail = new ScrollableThumbnail((Viewport) editPart
				.getFigure());
		this.thumbnail.setSource(editPart
				.getLayer(LayerConstants.PRINTABLE_LAYERS));

		this.lws.setContents(this.thumbnail);

	}

	private void initDropTarget() {
		AbstractTransferDropTargetListener dropTargetListener = new ERDiagramOutlineTransferDropTargetListener(
				this.graphicalViewer, TemplateTransfer.getInstance());

		this.graphicalViewer.addDropTargetListener(dropTargetListener);
	}

	public void setCategory(EditDomain editDomain,
			GraphicalViewer graphicalViewer, MenuManager outlineMenuMgr,
			ActionRegistry registry) {
		this.graphicalViewer = graphicalViewer;
		this.viewer.setContextMenu(outlineMenuMgr);

		// エディット・ドメインの設定
		this.viewer.setEditDomain(editDomain);
		this.registry = registry;

		if (this.getSite() != null) {
			this.resetView(registry);
		}
	}

	private void resetAction(ActionRegistry registry) {
		// アウトライン・ページで有効にするアクション
		IActionBars bars = this.getSite().getActionBars();

		String id = ActionFactory.UNDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.REDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.DELETE.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		bars.updateActionBars();
	}

	private void resetView(ActionRegistry registry) {
		this.showThumbnail();
		this.initDropTarget();
		this.resetAction(registry);
	}

	private void registerAction(TreeViewer treeViewer,
			ActionRegistry actionRegistry) {
		IAction[] actions = { new CreateIndexAction(treeViewer),
				new CreateSequenceAction(treeViewer),
				new CreateTriggerAction(treeViewer),
				new CreateTablespaceAction(treeViewer),
				new ChangeOutlineViewToPhysicalAction(treeViewer),
				new ChangeOutlineViewToLogicalAction(treeViewer),
				new ChangeOutlineViewToBothAction(treeViewer),
				new ChangeOutlineViewOrderByPhysicalNameAction(treeViewer),
				new ChangeOutlineViewOrderByLogicalNameAction(treeViewer) };

		for (IAction action : actions) {
			actionRegistry.registerAction(action);
		}
	}

	public ActionRegistry getOutlineActionRegistory() {
		return outlineActionRegistory;
	}

	@Override
	public EditPartViewer getViewer() {
		return super.getViewer();
	}

}
