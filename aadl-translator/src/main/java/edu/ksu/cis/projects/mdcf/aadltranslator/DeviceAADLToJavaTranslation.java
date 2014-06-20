package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
import org.osate.aadl2.Classifier;
import org.osate.aadl2.Element;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.errorreporting.LogParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.MarkerParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.TraverseWorkspace;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.core.OsateCorePlugin;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ActionExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.GetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PeriodicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SporadicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.preference.PreferenceConstants;

public final class DeviceAADLToJavaTranslation implements IHandler, IRunnableWithProgress {

	private final STGroup java_superclassSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-superclass.stg");
	private final STGroup java_userimplSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-userimpl.stg");
	private final STGroup midas_compsigSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-compsig.stg");
	
	
	
	private final STGroup java_device_supertypeSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-device-supertype.stg");
	private final STGroup java_device_userimplSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-device-userimpl.stg");
	private final STGroup device_compsigSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/device-compsig.stg");
	
	
	private ExecutionEvent triggeringEvent;

	public HashSet<IFile> getUsedFiles() {
		IncludesCalculator ic = new IncludesCalculator(
				new NullProgressMonitor());

		// 1) Get the current selection, convert it to an IFile
		IFile file = getIFileFromSelection();

		// 2) Get the target element (hopefully a system)
		Element target = getTargetElement(file);

		// 3) Verify that the target is a system. If it isn't, abort the
		// translation
		if (!isSystem(target))
			return null;

		// 4) Recursively traverse includes to build up IFile list
		ic.process(target);

		// 5) Return the used files.
		return ic.getUsedFiles();
	}

	private boolean isSystem(Element target) {
		AadlPackage pack = (AadlPackage) target;
		PublicPackageSection sect = pack.getPublicSection();
		Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
		return ownedClassifier instanceof org.osate.aadl2.System;
	}

	private Element getTargetElement(IFile file) {
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		Resource res = rs.getResource(
				OsateResourceUtil.getResourceURI((IResource) file), true);
		Element target = (Element) res.getContents().get(0);
		return target;
	}

	private IFile getIFileFromSelection() {
		ISelection selection = HandlerUtil.getCurrentSelection(triggeringEvent);
		IFile file = null;
		if (selection instanceof TextSelection) {
			file = ((FileEditorInput) ((ITextEditor) HandlerUtil
					.getActiveEditor(triggeringEvent)).getEditorInput())
					.getFile();
		} else if (selection instanceof IStructuredSelection) {
			file = (IFile) ((IAdaptable) ((IStructuredSelection) selection)
					.getFirstElement()).getAdapter(IFile.class);
		}
		return file;
	}

	public void doTranslation(IProgressMonitor monitor) {
		// 1) Initialize the progress monitor and translator
		ResourceSet rs = initProgressMonitor(monitor);
		DeviceTranslator stats = new DeviceTranslator(monitor);
		
		// 2) Get the list of files used in the model we're translating
		HashSet<IFile> usedFiles = this.getUsedFiles();

		// 3) Identify which file contains the AADL system
		IFile systemFile = getSystemFile(rs, stats, usedFiles);
		
		// 4) Initialize the error reporter
		ParseErrorReporterManager parseErrManager = initErrManager(stats);
		
		// 5) Build the in-memory system model
		processFiles(monitor, rs, stats, usedFiles, systemFile, parseErrManager);
		
		// 6) Write the generated files
		writeOutput(stats);

		// 7) Shut down the progress monitor
		wrapUpProgressMonitor(monitor);
	}

	private void wrapUpProgressMonitor(IProgressMonitor monitor) {
		monitor.worked(1);
		monitor.done();
	}

	private ResourceSet initProgressMonitor(IProgressMonitor monitor) {
		OsateResourceUtil.refreshResourceSet();
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		HashSet<IFile> files = TraverseWorkspace
				.getAadlandInstanceFilesInWorkspace();

		monitor.beginTask("Translating AADL to Java / MIDAS", files.size() + 1);
		return rs;
	}

	private void writeOutput(DeviceTranslator stats) {
		IPreferencesService service = Platform.getPreferencesService();
		
		// Get user preferences
		boolean generateShells = service.getBoolean(
				"edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_USERSHELLS, true, null);
		String appDevDirectory = service.getString(
				"edu.ksu.cis.projects.mdcf.aadl-translator",
				PreferenceConstants.P_APPDEVPATH, null, null);

		// Configure stringtemplate delimeters
		midas_compsigSTG.delimiterStartChar = '$';
		midas_compsigSTG.delimiterStopChar = '$';
		
		DeviceComponentModel dcm = null;

		String supertype = buildDeviceSuperType(dcm);
		
		String userImplAPI = buildDeviceUserImpleAPI(dcm);
		
		String compsig = buildDeviceCompSig(dcm);

		// Write the files
		if (stats.notCancelled()) {
			WriteOutputFiles.writeDeviceFiles(
					supertype,
					userImplAPI,
					compsig, dcm.getName(),
					appDevDirectory);
		}

	}

	private String buildDeviceSuperType(DeviceComponentModel dcm){
		java_device_supertypeSTG.delimiterStartChar = '<';
		java_device_supertypeSTG.delimiterStopChar = '>';
		return java_device_supertypeSTG.
				getInstanceOf("class").
				add("class", dcm).
				render();
	}
	
	private String buildDeviceUserImpleAPI(DeviceComponentModel dcm){
		java_device_userimplSTG.delimiterStartChar = '<';
		java_device_userimplSTG.delimiterStopChar = '>';
		return java_device_userimplSTG.
				getInstanceOf("userimpl").
				add("model", dcm).
				render();
	}
	
	private String buildDeviceCompSig(DeviceComponentModel dcm){

		ArrayList<String> receivePorts = new ArrayList<String>();
		ArrayList<String> sendPorts = new ArrayList<String>();
		device_compsigSTG.delimiterStartChar = '$';
		device_compsigSTG.delimiterStopChar = '$';
		for (ExchangeModel em : dcm.exchangeModels.values()) {

			if (em instanceof GetExchangeModel) {
				GetExchangeModel gem = (GetExchangeModel) em;

				receivePorts.add(device_compsigSTG
						.getInstanceOf("get_recv_port")
						.add("RECEIVE_PORT_NAME",
								gem.getInPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								gem.getMinSeparationInterval())
						.add("MAX_SEPARATION_TIME",
								gem.getMaxSeparationInterval())
						.add("RECV_MESSAGE_TYPE",
								gem.getInPortInfo().getPortProperty(
										"RECV_MESSAGE_TYPE")).render());

				sendPorts.add(device_compsigSTG
						.getInstanceOf("get_send_port")
						.add("SEND_PORT_NAME",
								gem.getOutPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								gem.getOutPortInfo().getPortProperty(
										"MIN_SEPARATION_TIME"))
						.add("MAX_SEPARATION_TIME",
								gem.getOutPortInfo().getPortProperty(
										"MAX_SEPARATION_TIME"))
						.render());

			} else if (em instanceof SetExchangeModel) {
				SetExchangeModel sem = (SetExchangeModel) em;

				receivePorts.add(device_compsigSTG
						.getInstanceOf("set_recv_port")
						.add("RECEIVE_PORT_NAME",
								sem.getInPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								sem.getInPortInfo().getPortProperty(
										"MIN_SEPARATION_TIME"))
						.add("MAX_SEPARATION_TIME",
								sem.getInPortInfo().getPortProperty(
										"MAX_SEPARATION_TIME"))
						.render());

				sendPorts.add(device_compsigSTG
						.getInstanceOf("set_send_port")
						.add("SEND_PORT_NAME",
								sem.getOutPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								sem.getOutPortInfo().getPortProperty(
										"MIN_SEPARATION_TIME"))
						.add("MAX_SEPARATION_TIME",
								sem.getOutPortInfo().getPortProperty(
										"MAX_SEPARATION_TIME"))
						.add("SEND_MESSAGE_TYPE",
								sem.getOutPortInfo().getPortProperty(
										"SEND_MESSAGE_TYPE")).render());

			} else if (em instanceof ActionExchangeModel) {
				ActionExchangeModel aem = (ActionExchangeModel) em;

				receivePorts.add(device_compsigSTG
						.getInstanceOf("action_recv_port")
						.add("RECEIVE_PORT_NAME",
								aem.getInPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								aem.getInPortInfo().getPortProperty(
										"MIN_SEPARATION_TIME"))
						.add("MAX_SEPARATION_TIME",
								aem.getInPortInfo().getPortProperty(
										"MAX_SEPARATION_TIME"))
						.render());

				sendPorts.add(device_compsigSTG
						.getInstanceOf("action_send_port")
						.add("SEND_PORT_NAME",
								aem.getOutPortInfo().getPortName())
						.add("MIN_SEPARATION_TIME",
								aem.getOutPortInfo().getPortProperty(
										"MIN_SEPARATION_TIME"))
						.add("MAX_SEPARATION_TIME",
								aem.getOutPortInfo().getPortProperty(
										"MAX_SEPARATION_TIME"))
						.render());
			} else if (em instanceof PeriodicExchangeModel) {
				PeriodicExchangeModel pem = (PeriodicExchangeModel) em;

				sendPorts.add(device_compsigSTG
						.getInstanceOf("send_provider_initiated_port")
						.add("SEND_PORT_NAME",
								pem.getOutPortInfo().getPortName())
						.add("SEND_MESSAGE_TYPE",
								pem.getOutPortInfo().getPortProperty(
										"SEND_MESSAGE_TYPE")).render());
			} else if (em instanceof SporadicExchangeModel) {
				SporadicExchangeModel sem = (SporadicExchangeModel) em;

				sendPorts.add(device_compsigSTG
						.getInstanceOf("send_provider_initiated_port")
						.add("SEND_PORT_NAME",
								sem.getOutPortInfo().getPortName())
						.add("SEND_MESSAGE_TYPE",
								sem.getOutPortInfo().getPortProperty(
										"SEND_MESSAGE_TYPE")).render());
			}

		}

		return device_compsigSTG.getInstanceOf("compsig")
				.add("RECV_PORTS", receivePorts).add("SEND_PORTS", sendPorts)
				.add("DEVICE_TYPE", dcm.getDeviceType()).render();
	}

	private void processFiles(IProgressMonitor monitor, ResourceSet rs,
			DeviceTranslator stats, HashSet<IFile> usedFiles, IFile systemFile,
			ParseErrorReporterManager parseErrManager) {

		// Process the system first...
		processFile(monitor, rs, stats, systemFile, parseErrManager);

		// Now process all the other files
		for (IFile f : usedFiles) {
			processFile(monitor, rs, stats, f, parseErrManager);
		}
	}

	private void processFile(IProgressMonitor monitor, ResourceSet rs,
			DeviceTranslator stats, IFile systemFile,
			ParseErrorReporterManager parseErrManager) {
		Resource res = rs.getResource(
				OsateResourceUtil.getResourceURI((IResource) systemFile), true);

		// Delete any existing error markers in the system file
		IResource file = OsateResourceUtil.convertToIResource(res);
		ParseErrorReporter errReporter = parseErrManager.getReporter(file);
		((MarkerParseErrorReporter) errReporter).setContextResource(res);
		errReporter.deleteMessages();

		Element target = (Element) res.getContents().get(0);
		stats.process(target);
		monitor.worked(1);
	}

	private ParseErrorReporterManager initErrManager(DeviceTranslator stats) {
		// The ParseErrorReporter provided by the OSATE model support is nearly
		// perfect here, we only change the marker id (much of the code is
		// directly lifted from elsewhere in the OSATE codebase
		ParseErrorReporterFactory parseErrorLoggerFactory = new LogParseErrorReporter.Factory(
				OsateCorePlugin.getDefault().getBundle());
		ParseErrorReporterManager parseErrManager = new ParseErrorReporterManager(
				new MarkerParseErrorReporter.Factory(
						"edu.ksu.cis.projects.mdcf.aadl-translator.TranslatorErrorMarker",
						parseErrorLoggerFactory));

		stats.setErrorManager(parseErrManager);
		return parseErrManager;
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
	 *            The set of files used in the architecture description. Note
	 *            that this parameter is modified (to not include the AADL file
	 *            containing the system) by this method.
	 * @return The file containing the AADL system.
	 */
	private IFile getSystemFile(ResourceSet rs, DeviceTranslator stats,
			HashSet<IFile> usedFiles) {
		IFile systemFile = null;
		for (IFile f : usedFiles) {
			Resource res = rs.getResource(
					OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)) {
				String propertySetName = ((PropertySet) target).getName();
				if (!AadlUtil.getPredeclaredPropertySetNames().contains(
						propertySetName))
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

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// We don't track handlers, so we do nothing here
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DeviceAADLToJavaTranslation runnable = new DeviceAADLToJavaTranslation();
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
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		doTranslation(monitor);
	}

	public void setTriggeringEvent(ExecutionEvent event) {
		triggeringEvent = event;
	}
}
