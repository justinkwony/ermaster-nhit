package org.insightech.er.extentions.sample;

import org.eclipse.swt.widgets.Event;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class SampleAction extends AbstractBaseAction {

	public SampleAction(ERDiagramEditor editor) {
		super(SampleAction.class.getName(), "Greeting", editor);
	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagramActivator.showMessageDialog("Hello World!");
	}

}
