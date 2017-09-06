package org.insightech.er.preference.page.classpath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.view.dialog.common.FileOverrideConfirmDialog;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.io.IOUtils;

public class ExtClassPathPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private DirectoryFieldEditor extDir;

	private Button downloadButton;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		composite.setLayout(gridLayout);

		this.extDir = new DirectoryFieldEditor("",
				ResourceString.getResourceString("label.ext.classpath"),
				composite);

		CompositeFactory.filler(composite, 2);
		this.downloadButton = this.createButton(composite, "Download");

		this.downloadButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				download();
			}
		});

		this.extDir.setFocus();

		this.setData();

		return composite;
	}

	private void download() {
		try {
			String path = PreferenceInitializer.getExtendedClasspath();

			List<URL> urls = ERDiagramActivator.getURLList("html");
			urls.addAll(ERDiagramActivator.getURLList("javasource"));

			boolean overrideYesToAll = false;
			boolean overrideNoToAll = false;

			for (URL url : urls) {
				String inputFile = url.getFile()
						.substring("template/".length());

				InputStream in = null;
				Writer out = null;

				try {
					if (!inputFile.endsWith("/")) {
						in = url.openStream();

						File outputFile = new File(path, inputFile);

						if (outputFile.exists()) {
							boolean override = false;

							if (!overrideYesToAll && !overrideNoToAll) {
								FileOverrideConfirmDialog fileOverrideConfirmDialog = new FileOverrideConfirmDialog(
										outputFile.getCanonicalPath());
								int result = fileOverrideConfirmDialog.open();

								if (result == 2) {
									overrideYesToAll = true;

								} else if (result == 3) {
									overrideNoToAll = true;

								} else if (result == 0) {
									override = true;

								} else if (result == 4) {
									break;
								}
							}

							if (overrideYesToAll || override) {
								outputFile.getParentFile().mkdirs();
								out = new OutputStreamWriter(
										new FileOutputStream(outputFile),
										"UTF-8");

								IOUtils.copy(in, out);
							}

						} else {
							outputFile.getParentFile().mkdirs();
							out = new OutputStreamWriter(new FileOutputStream(
									outputFile), "UTF-8");

							IOUtils.copy(in, out);
						}

					}

				} finally {
					IOUtils.closeQuietly(in);
					IOUtils.closeQuietly(out);
				}

			}

		} catch (Exception e) {
			ERDiagramActivator.showExceptionDialog(e);
		}
	}

	/*
	 * private List<URL> getFileList(String rootPath) throws URISyntaxException,
	 * IOException { List<URL> urlList = new ArrayList<URL>(); ClassLoader
	 * classLoader = ERDiagramActivator.class.getClassLoader();
	 * 
	 * URL dirURL = classLoader.getResource(rootPath);
	 * 
	 * classLoader. if (dirURL.getProtocol().equals("file")) { List<File>
	 * fileList = FileUtils .getChildren(new File(dirURL.toURI()));
	 * 
	 * for (File file : fileList) { urlList.add(file.toURI().toURL()); } }
	 * ERDiagramActivator.
	 * ResourcesPlugin.getPlugin().getBundle().findEntries(arg0, arg1, arg2) if
	 * (dirURL.getProtocol().equals("jar")) { String jarPath =
	 * dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
	 * 
	 * JarFile jar = new JarFile(jarPath);
	 * 
	 * try { Enumeration<JarEntry> entries = jar.entries(); List<String>
	 * fileList = new ArrayList<String>();
	 * 
	 * while (entries.hasMoreElements()) { String name =
	 * entries.nextElement().getName(); if (name.startsWith(rootPath)) { String
	 * entry = name.substring(rootPath.length()); int checkSubdir =
	 * entry.indexOf("/"); if (checkSubdir >= 0) { entry = entry.substring(0,
	 * checkSubdir); } fileList.add(entry); } }
	 * 
	 * for (String file : fileList) {
	 * urlList.add(classLoader.getResource(file)); }
	 * 
	 * } finally { jar.close(); } }
	 * 
	 * return urlList; }
	 */
	private void setData() {
		String path = PreferenceInitializer.getExtendedClasspath();
		this.extDir.setStringValue(path);
	}

	@Override
	protected void performDefaults() {
		PreferenceInitializer.saveExtendedClasspath(null);

		setData();

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		PreferenceInitializer.saveExtendedClasspath(this.extDir
				.getStringValue());

		return super.performOk();
	}

	private Button createButton(Composite parent, String label) {
		int widthHint = convertHorizontalDLUsToPixels(61);

		Button button = new Button(parent, 8);
		button.setText(label);

		Dialog.applyDialogFont(button);
		GridData data = new GridData(256);
		Point minButtonSize = button.computeSize(-1, -1, true);
		data.widthHint = Math.max(widthHint, minButtonSize.x);
		button.setLayoutData(data);

		return button;
	}
}
