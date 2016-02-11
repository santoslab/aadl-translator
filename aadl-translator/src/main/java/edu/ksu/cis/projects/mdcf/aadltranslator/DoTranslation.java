package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.Element;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.errorreporting.LogParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.MarkerParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.modelsupport.modeltraversal.TraverseWorkspace;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.core.OsateCorePlugin;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.errorModel.impl.ErrorModelLibraryImpl;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.WriteOutputFiles.OutputFormat;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.DevOrProcModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.preference.PreferenceConstants;
import edu.ksu.cis.projects.mdcf.aadltranslator.util.MarkdownLinkRenderer;

public final class DoTranslation implements IHandler, IRunnableWithProgress {

	public static enum Mode {
		APP_ARCH, HAZARD_ANALYSIS, DEVICEDRIVER
	};

	private final STGroup java_superclassSTG = new STGroupFile("src/main/resources/templates/java-superclass.stg");
	private final STGroup java_userimplSTG = new STGroupFile("src/main/resources/templates/java-userimpl.stg");
	private final STGroup midas_compsigSTG = new STGroupFile("src/main/resources/templates/midas-compsig.stg");
	private final STGroup midas_appspecSTG = new STGroupFile("src/main/resources/templates/midas-appspec.stg");
	private final STGroup report_overview = new STGroupFile("src/main/resources/templates/report-overview.stg");
	private final STGroup report_element = new STGroupFile("src/main/resources/templates/report-element.stg");

	private final STGroup java_device_supertypeSTG = new STGroupFile(
			"src/main/resources/templates/java-device-supertype.stg");
	private final STGroup java_device_userimplSTG = new STGroupFile(
			"src/main/resources/templates/java-device-userimpl.stg");
	private final STGroup device_compsigSTG = new STGroupFile("src/main/resources/templates/device-compsig.stg");

	private IFile targetFile;
	private ExecutionEvent triggeringEvent;
	private final String TRANSLATE_ARCH_COMMAND_ID = "edu.ksu.cis.projects.mdcf.aadl-translator.translate";
	private final String TRANSLATE_HAZARDS_COMMAND_ID = "edu.ksu.cis.projects.mdcf.aadl-translator.translate-hazards";
	private final String TRANSLATE_DEVICE_COMMAND_ID = "edu.ksu.cis.projects.mdcf.aadl-translator.device-aadl-translate";

	public HashSet<IFile> getUsedFiles() {
		IncludesCalculator ic = new IncludesCalculator(new NullProgressMonitor());

		// 1) Get the current selection, convert it to an IFile
		targetFile = getIFileFromSelection();

		// 2) Get the target element
		Element target = getTargetElement(targetFile);

		// 3) Verify that the target is a valid type. If it isn't, abort the
		// translation
		if (!isValidTarget(target)) {
			// TODO: Handle this more gracefully
			return null;
		}

		// 4) Recursively traverse includes to build up IFile list
		ic.process(target);

		// 5) Return the used files.
		return ic.getUsedFiles();
	}

	/**
	 * This checks to see if the intended translation target is valid, ie, a
	 * device, process, or system.
	 * 
	 * @param target
	 *            The AADL element that was selected
	 * @return True if the target is valid for translation, false otherwise
	 */
	private boolean isValidTarget(Element target) {
		AadlPackage pack = (AadlPackage) target;
		PublicPackageSection sect = pack.getPublicSection();
		Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
		if (ownedClassifier instanceof org.osate.aadl2.System || ownedClassifier instanceof org.osate.aadl2.Device
				|| ownedClassifier instanceof org.osate.aadl2.Process) {
			return true;
		} else {
			return false;
		}
	}

	private Element getTargetElement(IFile file) {
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		Resource res = rs.getResource(OsateResourceUtil.getResourceURI((IResource) file), true);
		Element target = (Element) res.getContents().get(0);
		return target;
	}

	private IFile getIFileFromSelection() {
		ISelection selection = HandlerUtil.getCurrentSelection(triggeringEvent);
		IFile file = null;
		if (selection instanceof TextSelection) {
			file = ((FileEditorInput) ((ITextEditor) HandlerUtil.getActiveEditor(triggeringEvent)).getEditorInput())
					.getFile();
		} else if (selection instanceof IStructuredSelection) {
			file = (IFile) ((IAdaptable) ((IStructuredSelection) selection).getFirstElement()).getAdapter(IFile.class);
		}

		return file;
	}

	public void doTranslation(IProgressMonitor monitor, Mode mode) {
		// 1) Initialize the progress monitor and translator
		ResourceSet rs = initProgressMonitor(monitor);
		Translator archTranslator = new Translator(monitor);

		// 2) Get the list of files used in the model we're translating
		HashSet<IFile> usedFiles = this.getUsedFiles();

		// 3) Filter out the property sets, initialize the list of error types
		filterPropertySets(rs, archTranslator, usedFiles);
		archTranslator.setErrorTypes(getErrorTypes(rs, usedFiles));

		// 4) Initialize the error reporter
		ParseErrorReporterManager parseErrManager = initErrManager(archTranslator);

		// 5) Build the in-memory system model
		processFiles(monitor, rs, archTranslator, usedFiles, targetFile, parseErrManager);

		// 6) Set the system name to the package name
		updatePackageName(archTranslator.getSystemModel(), targetFile.getProject().getName());

		// 6.1) If selected, build the in-memory hazard analysis model
		if (mode == Mode.HAZARD_ANALYSIS) {
//			ErrorTranslator hazardAnalysis = new ErrorTranslator();
//			HashSet<ErrorType> errors = getErrorTypes(rs, usedFiles);

//			hazardAnalysis.setErrorType(errors);
//			hazardAnalysis.setSystemModel(archTranslator.getSystemModel());
//			hazardAnalysis.parseEMV2(archTranslator.getSystemModel(), archTranslator.getSystemImplementation());
//			archTranslator.getChildren().forEach((model, classifier) -> hazardAnalysis.parseEMV2(model, classifier));

			IProject proj = targetFile.getProject();
			if (proj.getFolder("diagrams").exists()) {
				IFile procModel = proj.getFolder("diagrams").getFile("ProcessModel.png");
				IFile sysBoundary = proj.getFolder("diagrams").getFile("SystemBoundary.png");
				if (procModel.exists()) {
					archTranslator.getSystemModel().getHazardReportDiagrams().put("ProcessModel",
							procModel.getRawLocation().toString());
				}
				if (sysBoundary.exists()) {
					archTranslator.getSystemModel().getHazardReportDiagrams().put("SystemBoundary",
							sysBoundary.getRawLocation().toString());
				}
			}

			writeHazardReport(archTranslator.getSystemModel());
		}

		// 7) Write the generated files
		writeOutput(archTranslator);

		// 8) Shut down the progress monitor
		wrapUpProgressMonitor(monitor);
	}

	private void updatePackageName(SystemModel systemModel, String name) {
		systemModel.setName(name);
		for (DevOrProcModel dopm : systemModel.getChildren().values()) {
			dopm.setParentName(name);
		}
	}

	public void doDeviceTranslation(IProgressMonitor monitor) {
		// 1) Initialize the progress monitor and translator
		ResourceSet rs = initProgressMonitor(monitor);
		DeviceTranslator stats = new DeviceTranslator(monitor);

		// 2) Get the list of files used in the model we're translating
		HashSet<IFile> usedFiles = this.getUsedFiles();

		// 3) Identify which file contains the AADL system
		IFile systemFile = getDeviceSystemFile(rs, stats, usedFiles);

		// 4) Initialize the error reporter
		ParseErrorReporterManager parseErrManager = initDeviceErrManager(stats);

		// 5) Build the in-memory system model
		processFiles(monitor, rs, stats, usedFiles, systemFile, parseErrManager);

		// 6) Write the generated files
		writeDeviceOutput(stats);

		// 7) Shut down the progress monitor
		wrapUpProgressMonitor(monitor);
	}

	private void writeDeviceOutput(DeviceTranslator stats) {
		IPreferencesService service = Platform.getPreferencesService();

		// Get user preferences
		String devDevDirectory = service.getString("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_DEVDEVPATH, null, null);

		DeviceComponentModel dcm = stats.getDeviceComponentModel();

		String supertype = buildDeviceSuperType(dcm);

		String userImplAPI = buildDeviceUserImpleAPI(dcm);

		String compsig = buildDeviceCompSig(dcm);

		// Write the files
		if (stats.notCancelled()) {
			WriteOutputFiles.writeDeviceFiles(supertype, userImplAPI, compsig, dcm.getName(), devDevDirectory);
		}

	}

	private String buildDeviceSuperType(DeviceComponentModel dcm) {
		java_device_supertypeSTG.delimiterStartChar = '<';
		java_device_supertypeSTG.delimiterStopChar = '>';
		return java_device_supertypeSTG.getInstanceOf("class").add("model", dcm).render();
	}

	private String buildDeviceUserImpleAPI(DeviceComponentModel dcm) {
		java_device_userimplSTG.delimiterStartChar = '$';
		java_device_userimplSTG.delimiterStopChar = '$';
		return java_device_userimplSTG.getInstanceOf("userimpl").add("model", dcm).render();
	}

	private String buildDeviceCompSig(DeviceComponentModel dcm) {
		device_compsigSTG.delimiterStartChar = '$';
		device_compsigSTG.delimiterStopChar = '$';

		return device_compsigSTG.getInstanceOf("compsig").add("model", dcm).render();
	}

	private void wrapUpProgressMonitor(IProgressMonitor monitor) {
		monitor.worked(1);
		monitor.done();
	}

	private ResourceSet initProgressMonitor(IProgressMonitor monitor) {
		OsateResourceUtil.refreshResourceSet();
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		HashSet<IFile> files = TraverseWorkspace.getAadlandInstanceFilesInWorkspace();

		monitor.beginTask("Translating AADL to Java / MIDAS", files.size() + 1);
		return rs;
	}

	private void writeHazardReport(SystemModel sysModel) {
		// Filename -> file contents
		HashMap<String, String> elementReports = new HashMap<>();

		IPreferencesService service = Platform.getPreferencesService();
		String appDevDirectory = service.getString("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_APPDEVPATH, null, null);
		String fmtStr = service.getString("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_REPORTFORMAT, null, null);
		String pandocPath = service.getString("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_PANDOCPATH, null, null);

		report_overview.registerRenderer(String.class, MarkdownLinkRenderer.getInstance());
		report_element.registerRenderer(String.class, MarkdownLinkRenderer.getInstance());

		String overview = report_overview.getInstanceOf("report").add("model", sysModel).render();

		// We just use one element for testing, this will need to be expanded to
		// a full DFS later
		ComponentModel<?, ?> cm = sysModel.getChild("appLogic");
		String elem_report = report_element.getInstanceOf("report").add("model", cm)
				.add("name", "appLogic")
				.add("timestamp", sysModel.getTimestamp()).render();
		elementReports.put("appLogic", elem_report);

		try {
			URI reportHeaderURI = FileLocator.toFileURL(Platform.getBundle("edu.ksu.cis.projects.mdcf.aadl-translator")
					.getEntry("src/main/resources/styles/default.html")).toURI();
			WriteOutputFiles.writeHazardReport(overview, appDevDirectory, sysModel.getName(),
					OutputFormat.valueOf(fmtStr), pandocPath, reportHeaderURI);
			for (String elemName : elementReports.keySet()) {
				WriteOutputFiles.writeHazardReport(elementReports.get(elemName), appDevDirectory, elemName,
						OutputFormat.valueOf(fmtStr), pandocPath, reportHeaderURI);
			}
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeOutput(Translator stats) {
		// Filename -> file contents
		HashMap<String, String> compsigs = new HashMap<>();

		// Filename -> file contents
		HashMap<String, String> javaClasses = new HashMap<>();

		String appName;
		String appSpecContents;
		IPreferencesService service = Platform.getPreferencesService();

		// Get user preferences
		boolean generateShells = service.getBoolean("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_USERSHELLS, true, null);
		String appDevDirectory = service.getString("edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_APPDEVPATH, null, null);

		// Configure stringtemplate delimeters
		midas_compsigSTG.delimiterStartChar = '$';
		midas_compsigSTG.delimiterStopChar = '$';
		midas_appspecSTG.delimiterStartChar = '#';
		midas_appspecSTG.delimiterStopChar = '#';

		// Give the model to the string templates
		for (DevOrProcModel cm : stats.getSystemModel().getChildren().values()) {
			DevOrProcModel dpcm = (DevOrProcModel) cm;
			if (!dpcm.isPseudoDevice()) {
				javaClasses.put(dpcm.getName() + "SuperType",
						java_superclassSTG.getInstanceOf("class").add("model", dpcm).render());
			} else {
				javaClasses.put(dpcm.getName(), java_superclassSTG.getInstanceOf("class").add("model", dpcm).render());
			}
			if (generateShells && !dpcm.isPseudoDevice()) {
				javaClasses.put(dpcm.getName(), java_userimplSTG.getInstanceOf("userimpl").add("model", dpcm).render());
			}
			compsigs.put(cm.getName(), midas_compsigSTG.getInstanceOf("compsig").add("model", dpcm).render());
		}

		appName = stats.getSystemModel().getName();
		appSpecContents = midas_appspecSTG.getInstanceOf("appspec").add("model", stats.getSystemModel()).render();

		// Write the files
		if (stats.notCancelled()) {
			WriteOutputFiles.writeFiles(compsigs, javaClasses, appName, appSpecContents, appDevDirectory);
		}
	}

	private void processFiles(IProgressMonitor monitor, ResourceSet rs, AadlProcessingSwitchWithProgress stats,
			HashSet<IFile> usedFiles, IFile systemFile, ParseErrorReporterManager parseErrManager) {

		// Configure the translator for our target
		AadlPackage pack = (AadlPackage) getTargetElement(targetFile);
		PublicPackageSection sect = pack.getPublicSection();
		Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
		if (ownedClassifier instanceof org.osate.aadl2.System) {
			((Translator) stats).setTarget("System");
		} else if (ownedClassifier instanceof org.osate.aadl2.Device) {
			((Translator) stats).setTarget("Device");
		} else if (ownedClassifier instanceof org.osate.aadl2.Process) {
			((Translator) stats).setTarget("Process");
		}

		// Process the target first...
		processFile(monitor, rs, stats, targetFile, parseErrManager);

		// Remove the system from the used files so we don't double-translate it
		usedFiles.remove(targetFile);

		// Now process all the other files
		for (IFile f : usedFiles) {
			processFile(monitor, rs, stats, f, parseErrManager);
		}
	}

	private void processFile(IProgressMonitor monitor, ResourceSet rs, AadlProcessingSwitchWithProgress stats,
			IFile currentFile, ParseErrorReporterManager parseErrManager) {
		Resource res = rs.getResource(OsateResourceUtil.getResourceURI((IResource) currentFile), true);

		// Delete any existing error markers in the system file
		IResource file = OsateResourceUtil.convertToIResource(res);
		ParseErrorReporter errReporter = parseErrManager.getReporter(file);
		((MarkerParseErrorReporter) errReporter).setContextResource(res);
		errReporter.deleteMessages();

		Element target = (Element) res.getContents().get(0);
		stats.process(target);
		monitor.worked(1);
	}

	private ParseErrorReporterManager initErrManager(Translator stats) {
		// The ParseErrorReporter provided by the OSATE model support is nearly
		// perfect here, we only change the marker id (much of the code is
		// directly lifted from elsewhere in the OSATE codebase
		ParseErrorReporterFactory parseErrorLoggerFactory = new LogParseErrorReporter.Factory(
				OsateCorePlugin.getDefault().getBundle());
		ParseErrorReporterManager parseErrManager = new ParseErrorReporterManager(new MarkerParseErrorReporter.Factory(
				"edu.ksu.cis.projects.mdcf.aadl-translator.TranslatorErrorMarker", parseErrorLoggerFactory));

		stats.setErrorManager(parseErrManager);
		return parseErrManager;
	}

	private ParseErrorReporterManager initDeviceErrManager(DeviceTranslator stats) {
		// The ParseErrorReporter provided by the OSATE model support is nearly
		// perfect here, we only change the marker id (much of the code is
		// directly lifted from elsewhere in the OSATE codebase
		ParseErrorReporterFactory parseErrorLoggerFactory = new LogParseErrorReporter.Factory(
				OsateCorePlugin.getDefault().getBundle());
		ParseErrorReporterManager parseErrManager = new ParseErrorReporterManager(new MarkerParseErrorReporter.Factory(
				"edu.ksu.cis.projects.mdcf.aadl-translator.TranslatorErrorMarker", parseErrorLoggerFactory));

		stats.setErrorManager(parseErrManager);
		return parseErrManager;
	}

	private IFile filterPropertySets(ResourceSet rs, Translator stats, HashSet<IFile> usedFiles) {
		IFile systemFile = null;
		for (IFile f : usedFiles) {
			Resource res = rs.getResource(OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)) {
				String propertySetName = ((PropertySet) target).getName();
				if (!AadlUtil.getPredeclaredPropertySetNames().contains(propertySetName))
					stats.addPropertySetName(propertySetName);
				continue;
			}
			// AadlPackage pack = (AadlPackage) target;
			// PublicPackageSection sect = pack.getPublicSection();
			// for (Classifier ownedClassifier : sect.getOwnedClassifiers()) {
			// if (ownedClassifier instanceof org.osate.aadl2.SystemType) {
			// // Can't return f directly, may have more propertysets to
			// // add
			// systemFile = f;
			// }
			// }
		}
		return systemFile;
	}

	/**
	 * This method does two things. One, it returns the (first) file containing
	 * an AADL system, and two, it removes that file from the set of usedFiles.
	 * 
	 * @param rs
	 *            The set of resources in the workspace
	 * @param stats
	 *            The translator implementation
	 * @param usedFiles
	 *            The set of files used in the architecture description.
	 * @return The file containing the AADL system.
	 */
	private IFile getDeviceSystemFile(ResourceSet rs, DeviceTranslator stats, HashSet<IFile> usedFiles) {
		IFile systemFile = null;
		for (IFile f : usedFiles) {
			Resource res = rs.getResource(OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)) {
				String propertySetName = ((PropertySet) target).getName();
				if (!AadlUtil.getPredeclaredPropertySetNames().contains(propertySetName))
					stats.addPropertySetName(propertySetName);
				continue;
			}
			AadlPackage pack = (AadlPackage) target;
			PublicPackageSection sect = pack.getPublicSection();
			for (Classifier ownedClassifier : sect.getOwnedClassifiers()) {
				if ((ownedClassifier instanceof org.osate.aadl2.SystemType)) {
					systemFile = f;
					usedFiles.remove(f);
					break;
				}
			}
		}
		return systemFile;
	}

	private HashSet<ErrorType> getErrorTypes(ResourceSet rs, HashSet<IFile> usedFiles) {
		HashSet<ErrorType> retSet = new HashSet<>();
		for (IFile f : usedFiles) {
			Resource res = rs.getResource(OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)) {
				continue;
			}
			AadlPackage pack = (AadlPackage) target;
			PublicPackageSection sect = pack.getPublicSection();
			if (sect.getOwnedAnnexLibraries().size() > 0
					&& sect.getOwnedAnnexLibraries().get(0).getName().equals("EMV2")) {
				AnnexLibrary annexLibrary = sect.getOwnedAnnexLibraries().get(0);
				DefaultAnnexLibrary defaultAnnexLibrary = (DefaultAnnexLibrary) annexLibrary;
				ErrorModelLibraryImpl emImpl = (ErrorModelLibraryImpl) defaultAnnexLibrary.getParsedAnnexLibrary();
				retSet.addAll(emImpl.getTypes());
				if(!emImpl.getTypesets().isEmpty()){
					System.err.println("Sets of error types are not supported");
				}
			}
		}
		return retSet;
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// We don't track handlers, so we do nothing here
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DoTranslation runnable = new DoTranslation();
		runnable.setTriggeringEvent(event);
		try {
			new ProgressMonitorDialog(new Shell()).run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO: Figure out how to check for enablement
		return true;
	}

	@Override
	public boolean isHandled() {
		// If we're enabled, then we can handle input
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// We don't track handlers, so we do nothing here
	}

	@Override
	public void dispose() {
		// We have nothing to dispose of, so we do nothing here
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		String commandId = triggeringEvent.getCommand().getId();
		Mode mode;
		if (commandId.equals(TRANSLATE_ARCH_COMMAND_ID)) {
			mode = Mode.APP_ARCH;
		} else if (commandId.equals(TRANSLATE_HAZARDS_COMMAND_ID)) {
			mode = Mode.HAZARD_ANALYSIS;
		} else if (commandId.equals(TRANSLATE_DEVICE_COMMAND_ID)) {
			mode = Mode.DEVICEDRIVER;
		} else {
			System.err.println("Bad command received, id was " + commandId);
			return;
		}

		if (mode == Mode.APP_ARCH || mode == Mode.HAZARD_ANALYSIS) {
			doTranslation(monitor, mode);
		} else if (mode == Mode.DEVICEDRIVER) {
			doDeviceTranslation(monitor);
		}
	}

	public void setTriggeringEvent(ExecutionEvent event) {
		triggeringEvent = event;
	}
}
