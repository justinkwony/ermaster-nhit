package org.insightech.er.editor.model.dbexport.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.OverviewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.CategoryHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.GroupHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.IndexHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.SequenceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TableHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TablespaceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TriggerHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.ViewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.WordHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.model.dbexport.image.ImageInfoSet;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportHtmlSetting;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToHtmlManager extends AbstractExportManager {

	private static final String OUTPUT_DIR = "dbdocs";

	public static final String IMAGE_DIR = "image";

	private static final Map PROPERTIES = ResourceString
			.getResources("html.report.");

	private static final String[] FIX_FILES = { "help-doc.html", "index.html",
			"stylesheet.css" };

	public static final String[] ICON_FILES = { "icons/pkey.gif",
			"icons/foreign_key.gif" };

	private static final String TEMPLATE_DIR = "html/";

	protected List<HtmlReportPageGenerator> htmlReportPageGeneratorList = new ArrayList<HtmlReportPageGenerator>();

	protected OverviewHtmlReportPageGenerator overviewPageGenerator;

	private ExportHtmlSetting exportHtmlSetting;

	private File outputDir;

	public ExportToHtmlManager(ExportHtmlSetting exportHtmlSetting) {
		super("dialog.message.export.html");

		this.exportHtmlSetting = exportHtmlSetting;

		Map<Object, Integer> idMap = new HashMap<Object, Integer>();

		this.overviewPageGenerator = new OverviewHtmlReportPageGenerator(idMap);
		htmlReportPageGeneratorList
				.add(new TableHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList
				.add(new IndexHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new SequenceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add(new ViewHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new TriggerHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList
				.add(new GroupHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new TablespaceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add(new WordHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add(new CategoryHtmlReportPageGenerator(
				idMap));
	}

	@Override
	protected int getTotalTaskCount() {
		int count = 2;

		count += overviewPageGenerator.countAllClasses(this.diagram,
				this.htmlReportPageGeneratorList);

		if (this.exportHtmlSetting.isWithImage()) {
			count += ExportToImageManager.countTask(this.diagram,
					this.exportHtmlSetting.isWithCategoryImage(), true);
		}

		return count;
	}

	private ImageInfoSet createImageInfoSet(ProgressMonitor monitor)
			throws Exception {
		File imageDir = new File(this.outputDir, IMAGE_DIR);

		return ExportToImageManager.outputImage(imageDir, monitor,
				this.diagram, this.exportHtmlSetting.isWithCategoryImage());
	}

	@Override
	protected void doProcess(ProgressMonitor monitor) throws Exception {
		this.outputDir = new File(FileUtils.getFile(this.projectDir,
				this.exportHtmlSetting.getOutputDir()), OUTPUT_DIR);

		monitor.subTaskWithCounter("delete dir : "
				+ this.outputDir.getAbsolutePath());

		FileUtils.deleteDirectory(this.outputDir);
		monitor.worked(1);

		monitor.subTaskWithCounter("  make dir : "
				+ this.outputDir.getAbsolutePath());
		this.outputDir.mkdirs();
		monitor.worked(1);

		ImageInfoSet imageInfoSet = null;

		if (this.exportHtmlSetting.isWithImage()) {
			imageInfoSet = createImageInfoSet(monitor);
		}

		for (int i = 0; i < FIX_FILES.length; i++) {
			this.copyOut(FIX_FILES[i], FIX_FILES[i]);
		}

		String template = null;

		for (String iconFile : ICON_FILES) {
			this.copyOutResource("image/" + iconFile, iconFile);
		}

		String allclasses = this.overviewPageGenerator.generateAllClasses(
				this.diagram, this.htmlReportPageGeneratorList);
		this.writeOut("allclasses.html", allclasses);

		String overviewFrame = this.overviewPageGenerator
				.generateFrame(this.htmlReportPageGeneratorList);
		this.writeOut("overview-frame.html", overviewFrame);

		String overviewSummary = overviewPageGenerator.generateSummary(
				imageInfoSet, this.htmlReportPageGeneratorList);
		this.writeOut("overview-summary.html", overviewSummary);

		for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {

			HtmlReportPageGenerator pageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
					.get(i);
			pageGenerator.setImageInfoSet(imageInfoSet);

			try {
				HtmlReportPageGenerator prevPageGenerator = null;
				if (i != 0) {
					prevPageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
							.get(i - 1);
				}
				HtmlReportPageGenerator nextPageGenerator = null;
				if (i != htmlReportPageGeneratorList.size() - 1) {
					nextPageGenerator = (HtmlReportPageGenerator) htmlReportPageGeneratorList
							.get(i + 1);
				}

				String type = pageGenerator.getType();

				template = pageGenerator.generatePackageFrame(diagram);
				this.writeOut(type + "/package-frame.html", template);

				template = pageGenerator.generatePackageSummary(
						prevPageGenerator, nextPageGenerator, diagram);
				this.writeOut(type + "/package-summary.html", template);

				List<Object> objectList = pageGenerator.getObjectList(diagram);
				for (int j = 0; j < objectList.size(); j++) {
					Object object = objectList.get(j);

					monitor.subTaskWithCounter("writing : ["
							+ pageGenerator.getType() + "] "
							+ pageGenerator.getObjectName(object));

					Object prevObject = null;
					if (j != 0) {
						prevObject = (Object) objectList.get(j - 1);
					}
					Object nextObject = null;
					if (j != objectList.size() - 1) {
						nextObject = (Object) objectList.get(j + 1);
					}

					template = pageGenerator.generateContent(diagram, object,
							prevObject, nextObject);

					String objectId = pageGenerator.getObjectId(object);
					this.writeOut(type + "/" + objectId + ".html", template);

					monitor.worked(1);
				}

			} catch (RuntimeException e) {
				throw new IllegalStateException(pageGenerator.getClass()
						.getName(), e);
			}
		}
	}

	public static String getTemplate(String key) throws IOException {
		InputStream in = ERDiagramActivator.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + key);

		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + key);
		}

		try {
			String content = IOUtils.toString(in);
			content = replaceProperties(content);

			return content;

		} finally {
			in.close();
		}
	}

	private void writeOut(String dstPath, String content) throws IOException {
		File file = new File(this.outputDir, dstPath);
		file.getParentFile().mkdirs();

		FileUtils.writeStringToFile(file, content, "UTF-8");
	}

	private void copyOut(String dstPath, String key)
			throws FileNotFoundException, IOException {
		String content = getTemplate(key);
		this.writeOut(dstPath, content);
	}

	private static String replaceProperties(String content) {
		for (Object key : PROPERTIES.keySet()) {
			content = content.replaceAll(String.valueOf(key), Matcher
					.quoteReplacement(String.valueOf(PROPERTIES.get(key))));
		}

		return content;
	}

	private void copyOutResource(String dstPath, String srcPath)
			throws FileNotFoundException, IOException {
		InputStream in = null;

		try {
			in = ERDiagramActivator.getClassLoader().getResourceAsStream(
					srcPath);
			this.copyOutResource(dstPath, in);

		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void copyOutResource(String dstPath, InputStream in)
			throws FileNotFoundException, IOException {
		FileOutputStream out = null;

		try {
			File file = new File(this.outputDir, dstPath);
			file.getParentFile().mkdirs();

			out = new FileOutputStream(file);

			IOUtils.copy(in, out);

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public File getOutputFileOrDir() {
		return this.outputDir;
	}

	public static File getIndexHtml(File outputDir) {
		return new File(outputDir, OUTPUT_DIR + File.separator + "index.html");
	}

}
