package org.insightech.er.editor.view.dialog.outline.sequence;

import java.math.BigDecimal;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.h2.H2DBManager;
import org.insightech.er.db.impl.hsqldb.HSQLDBDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class SequenceDialog extends AbstractDialog {

	private int NUMBER_TEXT_SIZE = -1;

	private Text nameText;

	private Text schemaText;

	private Text incrementText;

	private Text minValueText;

	private Text maxValueText;

	private Text startText;

	private Text cacheText;

	private Button nocacheCheckBox;

	private Button cycleCheckBox;

	private Button orderCheckBox;

	private Text descriptionText;

	private Combo dataTypeCombo;

	private Text decimalSizeText;

	private Sequence sequence;

	private Sequence result;

	private ERDiagram diagram;

	public SequenceDialog(Shell parentShell, Sequence sequence,
			ERDiagram diagram) {
		super(parentShell);

		this.sequence = sequence;
		this.diagram = diagram;
	}

	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.numColumns = 5;
	}

	@Override
	protected void initialize(Composite composite) {
		String database = this.diagram.getDatabase();

		this.nameText = CompositeFactory.createText(this, composite,
				"label.sequence.name", 4, false, true);
		this.schemaText = CompositeFactory.createText(this, composite,
				"label.schema", 4, false, true);

		if (DB2DBManager.ID.equals(diagram.getDatabase())) {
			this.dataTypeCombo = CompositeFactory.createReadOnlyCombo(this,
					composite, "Data Type", 2);
			this.dataTypeCombo.add("BIGINT");
			this.dataTypeCombo.add("INTEGER");
			this.dataTypeCombo.add("SMALLINT");
			this.dataTypeCombo.add("DECIMAL(p)");

			this.decimalSizeText = CompositeFactory.createNumText(this,
					composite, "Size", 1, 30, false);
			this.decimalSizeText.setEnabled(false);

		} else if (HSQLDBDBManager.ID.equals(database)) {
			this.dataTypeCombo = CompositeFactory.createReadOnlyCombo(this,
					composite, "Data Type", 4);
			this.dataTypeCombo.add("BIGINT");
			this.dataTypeCombo.add("INTEGER");

		}

		this.incrementText = CompositeFactory.createNumText(this, composite,
				"Increment", 4, NUMBER_TEXT_SIZE, true);

		if (!H2DBManager.ID.equals(database)) {
			this.startText = CompositeFactory.createNumText(this, composite,
					"Start", 4, NUMBER_TEXT_SIZE, true);

			this.minValueText = CompositeFactory.createNumText(this, composite,
					"MinValue", 4, NUMBER_TEXT_SIZE, true);

			this.maxValueText = CompositeFactory.createNumText(this, composite,
					"MaxValue", 4, NUMBER_TEXT_SIZE, true);
		}

		if (!HSQLDBDBManager.ID.equals(diagram.getDatabase())) {
			if (DB2DBManager.ID.equals(diagram.getDatabase())) {
				this.cacheText = CompositeFactory.createNumText(this,
						composite, "Cache", 1, NUMBER_TEXT_SIZE, true);
				this.nocacheCheckBox = CompositeFactory.createCheckbox(this,
						composite, "nocache", false, 3);
			} else {
				this.cacheText = CompositeFactory.createNumText(this,
						composite, "Cache", 4, NUMBER_TEXT_SIZE, true);

			}
		}

		if (!H2DBManager.ID.equals(database)) {
			this.cycleCheckBox = CompositeFactory.createCheckbox(this,
					composite, "Cycle", false, 5);
		}

		if (DB2DBManager.ID.equals(diagram.getDatabase())) {
			this.orderCheckBox = CompositeFactory.createCheckbox(this,
					composite, "Order", false, 5);
		}

		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.description", -1, 100, 4, true);
	}

	@Override
	protected String getErrorMessage() {
		if (!DBManagerFactory.getDBManager(this.diagram).isSupported(
				DBManager.SUPPORT_SEQUENCE)) {
			return "error.sequence.not.supported";
		}

		String text = nameText.getText().trim();
		if (text.equals("")) {
			return "error.sequence.name.empty";
		}

		if (!Check.isAlphabet(text)) {
			if (this.diagram.getDiagramContents().getSettings()
					.isValidatePhysicalName()) {
				return "error.sequence.name.not.alphabet";
			}
		}

		text = schemaText.getText();

		if (!Check.isAlphabet(text)) {
			return "error.schema.not.alphabet";
		}

		text = incrementText.getText();

		if (!text.equals("")) {
			try {
				Integer.parseInt(text);

			} catch (NumberFormatException e) {
				return "error.sequence.increment.degit";
			}
		}

		if (this.minValueText != null) {
			text = minValueText.getText();

			if (!text.equals("")) {
				try {
					Long.parseLong(text);

				} catch (NumberFormatException e) {
					return "error.sequence.minValue.degit";
				}
			}
		}

		if (this.maxValueText != null) {
			text = maxValueText.getText();

			if (!text.equals("")) {
				try {
					new BigDecimal(text);

				} catch (NumberFormatException e) {
					return "error.sequence.maxValue.degit";
				}
			}
		}

		if (this.startText != null) {
			text = this.startText.getText();

			if (!text.equals("")) {
				try {
					Long.parseLong(text);

				} catch (NumberFormatException e) {
					return "error.sequence.start.degit";
				}
			}
		}

		if (this.cacheText != null) {
			text = cacheText.getText();

			if (!text.equals("")) {
				try {
					int cache = Integer.parseInt(text);
					if (DB2DBManager.ID.equals(this.diagram.getDatabase())) {
						if (cache < 2) {
							return "error.sequence.cache.min2";
						}
					} else {
						if (cache < 1) {
							return "error.sequence.cache.min1";
						}
					}
				} catch (NumberFormatException e) {
					return "error.sequence.cache.degit";
				}
			}
		}

		if (this.decimalSizeText != null) {
			text = this.decimalSizeText.getText();

			if (!text.equals("")) {

				try {
					int size = Integer.parseInt(text);
					if (size < 0) {
						return "error.sequence.size.zero";
					}

				} catch (NumberFormatException e) {
					return "error.sequence.size.degit";
				}
			}
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.sequence";
	}

	@Override
	protected void perfomeOK() throws InputException {
		this.result = new Sequence();

		this.result.setName(this.nameText.getText().trim());
		this.result.setSchema(this.schemaText.getText().trim());

		Integer increment = null;
		Long minValue = null;
		BigDecimal maxValue = null;
		Long start = null;
		Integer cache = null;

		String text = incrementText.getText();
		if (!text.equals("")) {
			increment = Integer.valueOf(text);
		}

		if (this.minValueText != null) {
			text = minValueText.getText();
			if (!text.equals("")) {
				minValue = Long.valueOf(text);
			}
		}

		if (this.maxValueText != null) {
			text = maxValueText.getText();
			if (!text.equals("")) {
				maxValue = new BigDecimal(text);
			}
		}

		text = startText.getText();
		if (!text.equals("")) {
			start = Long.valueOf(text);
		}

		if (this.cacheText != null) {
			text = cacheText.getText();
			if (!text.equals("")) {
				cache = Integer.valueOf(text);
			}
		}

		this.result.setIncrement(increment);
		this.result.setMinValue(minValue);
		this.result.setMaxValue(maxValue);
		this.result.setStart(start);
		this.result.setCache(cache);

		if (this.nocacheCheckBox != null) {
			this.result.setNocache(this.nocacheCheckBox.getSelection());
		}

		if (this.cycleCheckBox != null) {
			this.result.setCycle(this.cycleCheckBox.getSelection());
		}

		if (this.orderCheckBox != null) {
			this.result.setOrder(this.orderCheckBox.getSelection());
		}

		this.result.setDescription(this.descriptionText.getText().trim());

		if (this.dataTypeCombo != null) {
			this.result.setDataType(this.dataTypeCombo.getText());
			int decimalSize = 0;
			try {
				decimalSize = Integer.parseInt(this.decimalSizeText.getText()
						.trim());
			} catch (NumberFormatException e) {
			}
			this.result.setDecimalSize(decimalSize);
		}
	}

	@Override
	protected void setData() {
		if (this.sequence != null) {
			this.nameText.setText(Format.toString(this.sequence.getName()));
			this.schemaText.setText(Format.toString(this.sequence.getSchema()));
			this.incrementText.setText(Format.toString(this.sequence
					.getIncrement()));
			if (this.minValueText != null) {
				this.minValueText.setText(Format.toString(this.sequence
						.getMinValue()));
			}
			if (this.maxValueText != null) {
				this.maxValueText.setText(Format.toString(this.sequence
						.getMaxValue()));
			}
			if (this.startText != null) {
				this.startText
						.setText(Format.toString(this.sequence.getStart()));
			}
			if (this.cacheText != null) {
				this.cacheText
						.setText(Format.toString(this.sequence.getCache()));
			}
			if (this.nocacheCheckBox != null) {
				this.nocacheCheckBox.setSelection(this.sequence.isNocache());
			}
			if (this.cycleCheckBox != null) {
				this.cycleCheckBox.setSelection(this.sequence.isCycle());
			}
			if (this.orderCheckBox != null) {
				this.orderCheckBox.setSelection(this.sequence.isOrder());
			}

			this.descriptionText.setText(Format.toString(this.sequence
					.getDescription()));

			if (this.dataTypeCombo != null) {
				String dataType = Format.toString(this.sequence.getDataType());
				this.dataTypeCombo.setText(dataType);
				if (dataType.equals("DECIMAL(p)")
						&& this.decimalSizeText != null) {
					this.decimalSizeText.setEnabled(true);
					this.decimalSizeText.setText(Format.toString(this.sequence
							.getDecimalSize()));
				}
			}
		}
	}

	public Sequence getResult() {
		return result;
	}

	@Override
	protected void addListener() {
		super.addListener();

		if (this.dataTypeCombo != null && this.decimalSizeText != null) {
			this.dataTypeCombo.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					String dataType = dataTypeCombo.getText();

					if (dataType.equals("DECIMAL(p)")) {
						decimalSizeText.setEnabled(true);

					} else {
						decimalSizeText.setEnabled(false);
					}
				}

			});
		}

		if (this.nocacheCheckBox != null) {
			this.nocacheCheckBox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (nocacheCheckBox.getSelection()) {
						cacheText.setEnabled(false);

					} else {
						cacheText.setEnabled(true);
					}

				}

			});
		}
	}

}
