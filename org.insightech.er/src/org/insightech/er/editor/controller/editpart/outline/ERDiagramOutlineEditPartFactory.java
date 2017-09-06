package org.insightech.er.editor.controller.editpart.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.insightech.er.editor.controller.editpart.outline.dictionary.DictionaryOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.dictionary.WordOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.group.GroupOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.group.GroupSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.index.IndexOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.index.IndexSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.sequence.SequenceOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.sequence.SequenceSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.table.RelationOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.table.TableSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.tablespace.TablespaceOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.tablespace.TablespaceSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.trigger.TriggerOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.trigger.TriggerSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.view.ViewOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.view.ViewSetOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class ERDiagramOutlineEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart editPart = null;

		if (model instanceof ERTable) {
			editPart = new TableOutlineEditPart();

		} else if (model instanceof ERDiagram) {
			editPart = new ERDiagramOutlineEditPart();

		} else if (model instanceof Relation) {
			editPart = new RelationOutlineEditPart();

		} else if (model instanceof Word) {
			editPart = new WordOutlineEditPart();

		} else if (model instanceof Dictionary) {
			editPart = new DictionaryOutlineEditPart();

		} else if (model instanceof ColumnGroup) {
			editPart = new GroupOutlineEditPart();

		} else if (model instanceof GroupSet) {
			editPart = new GroupSetOutlineEditPart();

		} else if (model instanceof SequenceSet) {
			editPart = new SequenceSetOutlineEditPart();

		} else if (model instanceof Sequence) {
			editPart = new SequenceOutlineEditPart();

		} else if (model instanceof ViewSet) {
			editPart = new ViewSetOutlineEditPart();

		} else if (model instanceof View) {
			editPart = new ViewOutlineEditPart();

		} else if (model instanceof TriggerSet) {
			editPart = new TriggerSetOutlineEditPart();

		} else if (model instanceof Trigger) {
			editPart = new TriggerOutlineEditPart();

		} else if (model instanceof TablespaceSet) {
			editPart = new TablespaceSetOutlineEditPart();

		} else if (model instanceof Tablespace) {
			editPart = new TablespaceOutlineEditPart();

		} else if (model instanceof TableSet) {
			editPart = new TableSetOutlineEditPart();

		} else if (model instanceof IndexSet) {
			editPart = new IndexSetOutlineEditPart();

		} else if (model instanceof Index) {
			editPart = new IndexOutlineEditPart();

		}

		if (editPart != null) {
			editPart.setModel(model);
		}

		return editPart;
	}
}
