package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

/**
 * 変更履歴削除コマンド
 */
public class DeleteChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	private ChangeTracking changeTracking;

	private int index;

	private ChangeTrackingList changeTrackingList;

	/**
	 * 変更履歴削除コマンドを作成します。
	 * 
	 * @param diagram
	 * @param index
	 */
	public DeleteChangeTrackingCommand(ERDiagram diagram, int index) {
		this.diagram = diagram;
		this.changeTrackingList = this.diagram.getChangeTrackingList();

		this.index = index;
		this.changeTracking = this.changeTrackingList.get(index);
	}

	/**
	 * 変更履歴削除処理を実行する
	 */
	@Override
	protected void doExecute() {
		this.changeTrackingList.removeChangeTracking(this.index);

		if (this.changeTrackingList.isCalculated()) {
			this.changeTrackingList.setCalculated(false);
			this.diagram.refresh();
		}
	}

	/**
	 * 変更履歴削除処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		this.changeTrackingList.addChangeTracking(this.index,
				this.changeTracking);
	}

}
