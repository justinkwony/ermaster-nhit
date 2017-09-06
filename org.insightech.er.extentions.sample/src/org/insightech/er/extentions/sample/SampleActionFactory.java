package org.insightech.er.extentions.sample;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.extention.IERDiagramActionFactory;

public class SampleActionFactory implements IERDiagramActionFactory {

	@Override
	public IAction createIAction(ERDiagramEditor editor) {
		return new SampleAction(editor);
	}

}
