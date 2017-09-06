package org.insightech.er.editor.model.diagram_contents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.testdata.TestData;

public class DiagramContents {

	private Settings settings;

	private NodeSet contents;

	private GroupSet groups;

	private Dictionary dictionary;

	private SequenceSet sequenceSet;

	private TriggerSet triggerSet;

	private IndexSet indexSet;

	private TablespaceSet tablespaceSet;

	private List<TestData> testDataList;

	public DiagramContents() {
		this.settings = new Settings();
		this.contents = new NodeSet();
		this.groups = new GroupSet();
		this.dictionary = new Dictionary();
		this.sequenceSet = new SequenceSet();
		this.triggerSet = new TriggerSet();
		this.indexSet = new IndexSet();
		this.tablespaceSet = new TablespaceSet();

		this.testDataList = new ArrayList<TestData>();
	}

	public void clear() {
		this.contents.clear();
		this.groups.clear();
		this.dictionary.clear();
		this.sequenceSet.clear();
		this.triggerSet.clear();
		this.tablespaceSet.clear();
		this.testDataList.clear();
	}

	public void sort() {
		this.contents.sort();
		this.groups.sort();
		this.sequenceSet.sort();
		this.triggerSet.sort();
		this.tablespaceSet.sort();
		Collections.sort(this.testDataList);	
	}
	
	public NodeSet getContents() {
		return this.contents;
	}

	public void setContents(NodeSet contents) {
		this.contents = contents;
	}

	public GroupSet getGroups() {
		return this.groups;
	}

	public void setColumnGroups(GroupSet groups) {
		this.groups = groups;
		for (ColumnGroup group : groups) {
			for (NormalColumn normalColumn : group.getColumns()) {
				this.dictionary.add(normalColumn);
			}
		}
	}

	public Dictionary getDictionary() {
		return this.dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public SequenceSet getSequenceSet() {
		return sequenceSet;
	}

	public void setSequenceSet(SequenceSet sequenceSet) {
		this.sequenceSet = sequenceSet;
	}

	public TriggerSet getTriggerSet() {
		return triggerSet;
	}

	public void setTriggerSet(TriggerSet triggerSet) {
		this.triggerSet = triggerSet;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public IndexSet getIndexSet() {
		return indexSet;
	}

	public TablespaceSet getTablespaceSet() {
		return tablespaceSet;
	}

	public List<TestData> getTestDataList() {
		return testDataList;
	}

	public void setTestDataList(List<TestData> testDataList) {
		this.testDataList = testDataList;
	}

}
