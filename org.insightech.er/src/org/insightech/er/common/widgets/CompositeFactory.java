package org.insightech.er.common.widgets;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.editor.view.dialog.dbimport.ViewLabelProvider;

public class CompositeFactory {

	public static Composite createComposite(Composite parent, int numColumns,
			boolean withMargin) {
		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = numColumns;

		if (withMargin) {
			gridLayout.marginTop = Resources.MARGIN;
			gridLayout.marginBottom = Resources.MARGIN;
			gridLayout.marginRight = Resources.MARGIN;
			gridLayout.marginLeft = Resources.MARGIN;

		} else {
			gridLayout.marginTop = 0;
			gridLayout.marginBottom = 0;
			gridLayout.marginWidth = 0;
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		composite.setLayoutData(gridData);

		return composite;
	}

	public static Composite createChildComposite(Composite parent, int span,
			int numColumns) {
		return createChildComposite(parent, -1, span, numColumns);
	}

	public static Composite createChildComposite(Composite parent, int height,
			int span, int numColumns) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalSpan = span;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		if (height >= 0) {
			gridData.heightHint = height;
		}

		composite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = numColumns;

		composite.setLayout(gridLayout);

		return composite;
	}

	public static SpinnerWithScale createSpinnerWithScale(
			AbstractDialog dialog, Composite composite, String title,
			int minimum, int maximum) {
		return createSpinnerWithScale(dialog, composite, title, "%", minimum,
				maximum);
	}

	public static SpinnerWithScale createSpinnerWithScale(
			AbstractDialog dialog, Composite composite, String title,
			String unit, int minimum, int maximum) {
		if (title != null) {
			Label label = new Label(composite, SWT.LEFT);
			label.setText(ResourceString.getResourceString(title));
		}

		GridData scaleGridData = new GridData();
		scaleGridData.horizontalAlignment = GridData.FILL;
		scaleGridData.grabExcessHorizontalSpace = true;

		final Scale scale = new Scale(composite, SWT.NONE);
		scale.setLayoutData(scaleGridData);

		int diff = 0;

		if (minimum < 0) {
			scale.setMinimum(0);
			scale.setMaximum(-minimum + maximum);
			diff = minimum;

		} else {
			scale.setMinimum(minimum);
			scale.setMaximum(maximum);

		}

		scale.setPageIncrement((maximum - minimum) / 10);

		GridData spinnerGridData = new GridData();

		Spinner spinner = new Spinner(composite, SWT.RIGHT | SWT.BORDER);
		spinner.setLayoutData(spinnerGridData);
		spinner.setMinimum(minimum);
		spinner.setMaximum(maximum);

		Label label = new Label(composite, SWT.NONE);
		label.setText(unit);

		ListenerAppender.addModifyListener(scale, spinner, diff, dialog);

		return new SpinnerWithScale(spinner, scale, diff);
	}

	public static Combo createReadOnlyCombo(AbstractDialog dialog,
			Composite composite, String title) {
		return createReadOnlyCombo(dialog, composite, title, 1);
	}

	public static Combo createReadOnlyCombo(AbstractDialog dialog,
			Composite composite, String title, int span) {
		return createReadOnlyCombo(dialog, composite, title, span, -1);
	}

	public static Combo createReadOnlyCombo(AbstractDialog dialog,
			Composite composite, String title, int span, int width) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = span;

		if (title != null) {
			gridData.horizontalIndent = Resources.INDENT;

			Label label = new Label(composite, SWT.LEFT);

			GridData labelGridData = new GridData();
			labelGridData.horizontalAlignment = SWT.LEFT;
			label.setLayoutData(labelGridData);
			label.setText(ResourceString.getResourceString(title));
		}

		if (width > 0) {
			gridData.widthHint = width;

		} else {
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
		}

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		ListenerAppender.addComboListener(combo, dialog, false);

		return combo;
	}

	public static Combo createCombo(AbstractDialog dialog, Composite composite,
			String title) {
		return createCombo(dialog, composite, title, 1);
	}

	public static Combo createCombo(AbstractDialog dialog, Composite composite,
			String title, int span) {
		if (title != null) {
			Label label = new Label(composite, SWT.LEFT);
			label.setText(ResourceString.getResourceString(title));
		}

		GridData gridData = new GridData();
		gridData.horizontalSpan = span;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = Resources.INDENT;
		gridData.grabExcessHorizontalSpace = true;

		Combo combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(gridData);

		ListenerAppender.addComboListener(combo, dialog, false);

		return combo;
	}

	public static Combo createFileEncodingCombo(String defaultCharset,
			AbstractDialog dialog, Composite composite, String title, int span) {
		Combo fileEncodingCombo = createReadOnlyCombo(dialog, composite, title,
				span, -1);

		for (Charset charset : Charset.availableCharsets().values()) {
			fileEncodingCombo.add(charset.displayName());
		}

		fileEncodingCombo.setText(defaultCharset);

		return fileEncodingCombo;
	}

	public static Text createText(AbstractDialog dialog, Composite composite,
			String title, boolean imeOn, boolean indent) {
		return createText(dialog, composite, title, 1, imeOn, indent);
	}

	public static Text createText(AbstractDialog dialog, Composite composite,
			String title, int span, boolean imeOn, boolean indent) {
		return createText(dialog, composite, title, span, -1, imeOn, indent);
	}

	public static Text createText(AbstractDialog dialog, Composite composite,
			String title, int span, int width, boolean imeOn, boolean indent) {
		return createText(dialog, composite, title, span, width, SWT.BORDER,
				imeOn, indent);
	}

	public static Text createNumText(AbstractDialog dialog,
			Composite composite, String title) {
		return createNumText(dialog, composite, title, -1);
	}

	public static Text createNumText(AbstractDialog dialog,
			Composite composite, String title, boolean indent) {
		return createNumText(dialog, composite, title, 1, -1, indent);
	}

	public static Text createNumText(AbstractDialog dialog,
			Composite composite, String title, int width) {
		return createNumText(dialog, composite, title, 1, width);
	}

	public static Text createNumText(AbstractDialog dialog,
			Composite composite, String title, int span, int width) {
		return createNumText(dialog, composite, title, span, width, false);
	}

	public static Text createNumText(AbstractDialog dialog,
			Composite composite, String title, int span, int width,
			boolean indent) {
		return createText(dialog, composite, title, span, width, SWT.BORDER
				| SWT.RIGHT, false, indent);
	}

	public static Text createText(AbstractDialog dialog, Composite composite,
			String title, int span, int width, int style, boolean imeOn,
			boolean indent) {
		if (title != null) {
			Label label = new Label(composite, SWT.NONE);
			if (indent) {
				GridData labelGridData = new GridData();
				labelGridData.horizontalAlignment = SWT.LEFT;
				label.setLayoutData(labelGridData);
			}

			label.setText(ResourceString.getResourceString(title));
		}

		GridData textGridData = new GridData();
		textGridData.horizontalSpan = span;
		if (indent) {
			textGridData.horizontalIndent = Resources.INDENT;
		}

		if (width > 0) {
			textGridData.widthHint = width;

		} else {
			textGridData.horizontalAlignment = GridData.FILL;
			textGridData.grabExcessHorizontalSpace = true;
		}

		Text text = new Text(composite, style);
		text.setLayoutData(textGridData);

		ListenerAppender.addTextListener(text, dialog, imeOn);

		return text;
	}

	public static Label createExampleLabel(Composite composite, String title) {
		return createExampleLabel(composite, title, -1);
	}

	public static Label createExampleLabel(Composite composite, String title,
			int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString.getResourceString(title));

		if (span > 0) {
			GridData gridData = new GridData();
			gridData.horizontalSpan = span;
			label.setLayoutData(gridData);
		}

		FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
		Font font = Resources.getFont(fontData.getName(), 8);
		label.setFont(font);

		return label;
	}

	public static void filler(Composite composite, int span) {
		filler(composite, span, -1);
	}

	public static void filler(Composite composite, int span, int width) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = span;
		gridData.heightHint = 1;

		if (width > 0) {
			gridData.widthHint = width;
		}

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData);
	}

	public static void fillLine(Composite composite) {
		fillLine(composite, -1);
	}

	public static void fillLine(Composite composite, int height) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns;
		if (height != -1) {
			gridData.heightHint = height;
		}

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData);
	}

	public static Label separater(Composite composite) {
		return separater(composite, -1);
	}

	public static Label separater(Composite composite, int span) {
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 1;

		// gridData.horizontalIndent = Resources.INDENT;

		if (span > 0) {
			gridData.horizontalSpan = span;

		} else {
			gridData.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns;
		}

		label.setLayoutData(gridData);

		return label;
	}

	public static Label createLabelAsValue(Composite composite, String title,
			int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString.getResourceString(title));

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Resources.INDENT;
		gridData.horizontalSpan = span;

		label.setLayoutData(gridData);

		return label;
	}

	public static Label createLeftLabel(Composite composite, String title,
			int span) {
		return createLabel(composite, title, span, -1, true, false);
	}

	public static Label createLabel(Composite composite, String title) {
		return createLabel(composite, title, -1);
	}

	public static Label createLabel(Composite composite, String title, int span) {
		return createLabel(composite, title, span, -1);
	}

	public static Label createLabel(Composite composite, String title,
			int span, int width) {
		return createLabel(composite, title, span, width, true, false);
	}

	public static Label createLabel(Composite composite, String title,
			int span, int width, boolean leftAlign, boolean indent) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString.getResourceString(title));

		GridData gridData = new GridData();
		if (indent) {
			gridData.horizontalIndent = Resources.INDENT;
		}
		if (leftAlign) {
			gridData.horizontalAlignment = SWT.LEFT;

		} else {
			gridData.horizontalAlignment = SWT.RIGHT;
		}

		if (span > 0 || width > 0) {
			if (span > 0) {
				gridData.horizontalSpan = span;
			}
			if (width > 0) {
				gridData.widthHint = width;
			}
		}

		label.setLayoutData(gridData);

		return label;
	}

	public static Button createCheckbox(AbstractDialog dialog,
			Composite composite, String title, boolean indent) {
		return createCheckbox(dialog, composite, title, indent, -1);
	}

	public static Button createCheckbox(AbstractDialog dialog,
			Composite composite, String title, boolean indent, int span) {
		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText(ResourceString.getResourceString(title));

		GridData gridData = new GridData();

		if (span != -1) {
			gridData.horizontalSpan = span;
		}
		if (indent) {
			gridData.horizontalIndent = Resources.INDENT;
		}

		checkbox.setLayoutData(gridData);

		ListenerAppender.addCheckBoxListener(checkbox, dialog);

		return checkbox;
	}

	public static MultiLineCheckbox createMultiLineCheckbox(
			final AbstractDialog dialog, Composite composite, String title,
			boolean indent, int span) {
		return new MultiLineCheckbox(dialog, composite, title, indent, span);
	}

	public static Button createRadio(AbstractDialog dialog,
			Composite composite, String title) {
		return createRadio(dialog, composite, title, -1);
	}

	public static Button createRadio(AbstractDialog dialog,
			Composite composite, String title, int span) {
		return createRadio(dialog, composite, title, span, false);
	}

	public static Button createRadio(AbstractDialog dialog,
			Composite composite, String title, int span, boolean indent) {
		Button radio = new Button(composite, SWT.RADIO);
		radio.setText(ResourceString.getResourceString(title));

		GridData gridData = new GridData();

		if (span != -1) {
			gridData.horizontalSpan = span;
		}

		if (indent) {
			gridData.horizontalIndent = Resources.INDENT;
		}

		radio.setLayoutData(gridData);

		ListenerAppender.addCheckBoxListener(radio, dialog);

		return radio;
	}

	public static Text createTextArea(AbstractDialog dialog,
			Composite composite, String title, int width, int height, int span,
			boolean imeOn) {
		return createTextArea(dialog, composite, title, width, height, span,
				true, imeOn, true);
	}

	public static Text createTextArea(AbstractDialog dialog,
			Composite composite, String title, int width, int height, int span,
			boolean imeOn, boolean indent) {
		return createTextArea(dialog, composite, title, width, height, span,
				true, imeOn, indent);
	}

	public static Text createTextArea(AbstractDialog dialog,
			Composite composite, String title, int width, int height, int span,
			boolean selectAll, boolean imeOn, boolean indent) {
		if (title != null) {
			Label label = new Label(composite, SWT.NONE);

			GridData labelGridData = new GridData();
			labelGridData.verticalAlignment = SWT.TOP;
			labelGridData.horizontalAlignment = SWT.LEFT;

			label.setLayoutData(labelGridData);

			label.setText(ResourceString.getResourceString(title));
		}

		GridData textAreaGridData = new GridData();
		textAreaGridData.heightHint = height;
		textAreaGridData.horizontalSpan = span;

		if (width > 0) {
			textAreaGridData.widthHint = width;
		} else {
			textAreaGridData.horizontalAlignment = GridData.FILL;
			textAreaGridData.grabExcessHorizontalSpace = true;
		}

		if (title != null && indent) {
			textAreaGridData.horizontalIndent = Resources.INDENT;
		}

		Text text = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL
				| SWT.BORDER);
		text.setLayoutData(textAreaGridData);

		ListenerAppender.addTextAreaListener(text, dialog, selectAll, imeOn);

		return text;
	}

	public static Table createTable(Composite composite, int height, int span) {
		return createTable(composite, height, span, false);
	}

	public static Table createTable(Composite composite, int height, int span,
			boolean multi) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = span;
		gridData.heightHint = height;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		int style = SWT.SINGLE;
		if (multi) {
			style = SWT.MULTI;
		}

		Table table = new Table(composite, style | SWT.BORDER
				| SWT.FULL_SELECTION);
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return table;
	}

	public static Button createSmallButton(Composite composite, String text) {
		return createButton(composite, text, -1, Resources.SMALL_BUTTON_WIDTH);
	}

	public static Button createMiddleButton(Composite composite, String text) {
		return createButton(composite, text, -1, Resources.MIDDLE_BUTTON_WIDTH);
	}

	public static Button createLargeButton(Composite composite, String text) {
		return createButton(composite, text, -1, Resources.LARGE_BUTTON_WIDTH);
	}

	public static Button createLargeButton(Composite composite, String text,
			int span) {
		return createButton(composite, text, span, Resources.LARGE_BUTTON_WIDTH);
	}

	public static Button createButton(Composite composite, String text,
			int span, int width) {
		GridData gridData = new GridData();
		gridData.widthHint = width;

		if (span != -1) {
			gridData.horizontalSpan = span;
		}

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString(text));
		button.setLayoutData(gridData);

		return button;
	}

	public static Button createFillButton(Composite composite, String text) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString(text));
		button.setLayoutData(gridData);

		return button;
	}

	public static Button createAddButton(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.END;
		gridData.widthHint = Resources.BUTTON_ADD_REMOVE_WIDTH;

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString("label.right.arrow"));
		button.setLayoutData(gridData);

		return button;
	}

	public static Button createRemoveButton(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.widthHint = Resources.BUTTON_ADD_REMOVE_WIDTH;

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString("label.left.arrow"));
		button.setLayoutData(gridData);

		return button;
	}

	public static Button createUpButton(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.END;
		gridData.grabExcessVerticalSpace = true;
		gridData.widthHint = Resources.SMALL_BUTTON_WIDTH;

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString("label.up.arrow"));
		button.setLayoutData(gridData);

		return button;
	}

	public static Button createDownButton(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.widthHint = Resources.SMALL_BUTTON_WIDTH;

		Button button = new Button(composite, SWT.NONE);
		button.setText(ResourceString.getResourceString("label.down.arrow"));
		button.setLayoutData(gridData);

		return button;
	}

	public static TableEditor createCheckBoxTableEditor(TableItem tableItem,
			boolean selection, int column) {
		Table table = tableItem.getParent();

		final Button checkBox = new Button(table, SWT.CHECK);
		checkBox.pack();

		TableEditor editor = new TableEditor(table);

		editor.minimumWidth = checkBox.getSize().x;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(checkBox, tableItem, column);

		checkBox.setSelection(selection);

		return editor;
	}

	public static RowHeaderTable createRowHeaderTable(Composite parent,
			int width, int height, int rowHeaderWidth, int rowHeight, int span,
			boolean iconEnable, boolean editable) {
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		GridData gridData = new GridData();
		gridData.horizontalSpan = span;
		gridData.widthHint = width;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = height;
		composite.setLayoutData(gridData);

		return createRowHeaderTable(composite, width, height, rowHeaderWidth,
				rowHeight, iconEnable, editable);
	}

	private static RowHeaderTable createRowHeaderTable(Composite composite,
			int width, int height, int rowHeaderWidth, int rowHeight,
			boolean iconEnable, boolean editable) {
		Frame frame = SWT_AWT.new_Frame(composite);
		FlowLayout frameLayout = new FlowLayout();
		frameLayout.setVgap(0);
		frame.setLayout(frameLayout);

		Panel panel = new Panel();
		FlowLayout panelLayout = new FlowLayout();
		panelLayout.setVgap(0);
		panel.setLayout(panelLayout);
		frame.add(panel);

		RowHeaderTable table = new RowHeaderTable(width, height,
				rowHeaderWidth, rowHeight, iconEnable, editable);
		panel.add(table);

		return table;
	}

	public static Group createGroup(Composite parent, String title, int span,
			int numColumns) {
		return createGroup(parent, title, span, numColumns, 15);
	}

	public static Group createGroup(Composite parent, String title, int span,
			int numColumns, int margin) {
		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		groupGridData.verticalAlignment = GridData.FILL;
		groupGridData.grabExcessVerticalSpace = true;
		groupGridData.horizontalSpan = span;

		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = margin;
		groupLayout.marginHeight = margin;
		groupLayout.numColumns = numColumns;

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString(title));
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		return group;
	}

	public static FileText createFileText(boolean save, AbstractDialog dialog,
			Composite parent, String title, File projectDir,
			String defaultFileName, String filterExtension) {
		return createFileText(save, dialog, parent, title, projectDir,
				defaultFileName, filterExtension, true);
	}

	public static FileText createFileText(boolean save, AbstractDialog dialog,
			Composite parent, String title, File projectDir,
			String defaultFileName, String filterExtension, boolean indent) {
		return createFileText(save, dialog, parent, title, projectDir,
				defaultFileName, new String[] { filterExtension }, indent);
	}

	public static FileText createFileText(boolean save, AbstractDialog dialog,
			Composite parent, String title, File projectDir,
			String defaultFileName, String[] filterExtensions) {
		return createFileText(save, dialog, parent, title, projectDir,
				defaultFileName, filterExtensions, true);
	}

	public static FileText createFileText(boolean save, AbstractDialog dialog,
			Composite parent, String title, File projectDir,
			String defaultFileName, String[] filterExtensions, boolean indent) {
		if (title != null) {
			Label label = new Label(parent, SWT.NONE);
			if (indent) {
				GridData labelGridData = new GridData();
				labelGridData.horizontalAlignment = SWT.LEFT;
				label.setLayoutData(labelGridData);
			}

			label.setText(ResourceString.getResourceString(title));
		}

		FileText fileText = new FileText(save, parent, projectDir,
				defaultFileName, filterExtensions, indent);

		ListenerAppender.addPathTextListener(fileText, dialog);

		return fileText;
	}

	public static DirectoryText createDirectoryText(AbstractDialog dialog,
			Composite parent, String title, final File projectDir,
			final String message) {
		return createDirectoryText(dialog, parent, title, projectDir, message,
				true);
	}

	public static DirectoryText createDirectoryText(AbstractDialog dialog,
			Composite parent, String title, final File projectDir,
			final String message, boolean indent) {

		if (title != null) {
			Label label = new Label(parent, SWT.NONE);
			if (indent) {
				GridData labelGridData = new GridData();
				labelGridData.horizontalAlignment = SWT.LEFT;
				label.setLayoutData(labelGridData);
			}

			label.setText(ResourceString.getResourceString(title));
		}

		DirectoryText directoryText = new DirectoryText(parent, projectDir,
				message, indent);

		ListenerAppender.addPathTextListener(directoryText, dialog);

		return directoryText;
	}

	public static ContainerCheckedTreeViewer createCheckedTreeViewer(
			final AbstractDialog dialog, Composite parent, int height, int span) {
		GridData gridData = new GridData();
		gridData.heightHint = height;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = span;

		ContainerCheckedTreeViewer viewer = new ContainerCheckedTreeViewer(
				parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Tree tree = viewer.getTree();
		tree.setLayoutData(gridData);

		viewer.setContentProvider(new TreeNodeContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		if (dialog != null) {
			viewer.addCheckStateListener(new ICheckStateListener() {

				public void checkStateChanged(CheckStateChangedEvent event) {
					dialog.validate();
				}

			});
		}

		return viewer;
	}

	public static Table createTable(Composite parent, int height) {
		GridData gridData = new GridData();
		gridData.heightHint = height;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Table table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData);
		table.setLinesVisible(false);

		return table;
	}

	public static TableColumn createTableColumn(Table table, String title) {
		return createTableColumn(table, title, -1);
	}

	public static TableColumn createTableColumn(Table table, String title,
			int width) {
		return createTableColumn(table, title, width, SWT.LEFT);
	}

	public static TableColumn createTableColumn(Table table, String title,
			int width, int align) {
		TableColumn column = new TableColumn(table, align);

		column.setText(ResourceString.getResourceString(title));

		if (width >= 0) {
			column.setWidth(width);
		} else {
			column.pack();
		}

		return column;
	}
}
