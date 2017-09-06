package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.h2.H2DBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Format;

public class SequenceHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public SequenceHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "sequence";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List list = diagram.getDiagramContents().getSequenceSet()
				.getObjectList();

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object) {
		Sequence sequence = (Sequence) object;

		String cache = Format.toString(sequence.getCache());

		if (DBManagerFactory.getDBManager(diagram).isSupported(
				DBManager.SUPPORT_SEQUENCE_NOCACHE)) {
			if (sequence.isNocache()) {
				cache = "NO CACHE";
			}
		}

		String min = Format.toString(sequence.getMinValue());
		String max = Format.toString(sequence.getMaxValue());
		String start = Format.toString(sequence.getStart());
		String cycle = String.valueOf(sequence.isCycle()).toUpperCase();

		if (H2DBManager.ID.equals(diagram.getDatabase())) {
			min = "-";
			max = "-";
			start = "-";
			cycle = "-";
		}

		return new String[] { Format.null2blank(sequence.getDescription()),
				Format.toString(sequence.getIncrement()), min, max, start, cache,
				cycle };
	}

	public String getObjectName(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getDescription();
	}
}
