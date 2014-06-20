package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;

import edu.ksu.cis.projects.mdcf.aadltranslator.Translator;
import edu.ksu.cis.projects.mdcf.aadltranslator.error.TestParseErrorReporterFactory;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Model Tests
	SystemModelTests.class,
	DeviceModelTests.class,
	ProcessModelTests.class,
	TaskModelTests.class,
	PortModelTests.class,
	ConnectionModelTests.class,

	// Error-handling tests
	ControllerErrorTests.class,
})
public class AllTests {
	public static HashMap<String, IFile> systemFiles = new HashMap<>();
	public static ResourceSet resourceSet = null;
	public static Translator stats;

	// This may need to turn into a map from system name -> the set of
	// supporting files
	public static HashSet<String> supportingFiles = new HashSet<>();
	public static HashSet<String> propertyFiles = new HashSet<>();
	public static HashSet<String> deviceFiles = new HashSet<>();
	public final boolean GENERATE_EXPECTED = false;

	public final static String BUNDLE_ID = "edu.ksu.cis.projects.mdcf.aadl-translator-test";
	public final static String TEST_DIR = "src/test/resources/edu/ksu/cis/projects/mdcf/aadltranslator/test/";

	public static IProject testProject = null;
	
	public static HashSet<String> usedDevices = new HashSet<>();
	public static HashSet<String> usedProperties = new HashSet<>();
	public static ParseErrorReporterFactory parseErrorReporterFactory = TestParseErrorReporterFactory.INSTANCE;
	public static ParseErrorReporterManager parseErrManager;
	
	public static boolean initComplete = false;
	public static StringBuilder errorSB = new StringBuilder();

	@BeforeClass
	public static void initialize() {
		testProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("TestProject");
		try {

			IHandlerService handlerService = (IHandlerService) PlatformUI
					.getWorkbench().getService(IHandlerService.class);
			handlerService
					.executeCommand(
							"org.osate.xtext.aadl2.ui.resetpredeclaredproperties",
							null);

			String[] natureIDs = new String[] { "org.osate.core.aadlnature",
					"org.eclipse.xtext.ui.shared.xtextNature" };

			IProject pluginResources = ResourcesPlugin.getWorkspace().getRoot()
					.getProject("Plugin_Resources");
			
			IProject[] referencedProjects = new IProject[] { pluginResources };
			if (!testProject.isAccessible()) {
				testProject.create(null);
				testProject.open(null);
			}

			IProjectDescription prpd = pluginResources.getDescription();
			prpd.setNatureIds(natureIDs);
			pluginResources.setDescription(prpd, null);

			IProjectDescription testpd = testProject.getDescription();
			testpd.setNatureIds(natureIDs);
			testpd.setReferencedProjects(referencedProjects);
			testProject.setDescription(testpd, null);

			IFolder packagesFolder = testProject.getFolder("packages");
			if (!packagesFolder.isAccessible()) {
				packagesFolder.create(true, true, null);
			}

			IFolder propertySetsFolder = testProject.getFolder("propertysets");
			if (!propertySetsFolder.isAccessible()) {
				propertySetsFolder.create(true, true, null);
			}

			resourceSet = OsateResourceUtil.createResourceSet();
			
			initFiles(packagesFolder, propertySetsFolder);
			
			testProject
					.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		} catch (CoreException | ExecutionException | NotDefinedException
				| NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		initComplete = true;
	}
	
	private static void initFiles(IFolder packagesFolder,
			IFolder propertySetsFolder) {
		URL aadlDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/");
		URL aadlPropertysetsDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/propertyset/");
		URL aadlSystemDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/system/");
		URL aadlDeviceDirUrl = Platform.getBundle(BUNDLE_ID).getEntry(
				TEST_DIR + "aadl/device/");
		File aadlDir = null;
		File aadlPropertysetsDir = null;
		File aadlSystemDir = null;
		File aadlDeviceDir = null;
		try {
			aadlDir = new File(FileLocator.toFileURL(aadlDirUrl).getPath());
			aadlPropertysetsDir = new File(FileLocator.toFileURL(
					aadlPropertysetsDirUrl).getPath());
			aadlSystemDir = new File(FileLocator.toFileURL(aadlSystemDirUrl)
					.getPath());
			aadlDeviceDir = new File(FileLocator.toFileURL(aadlDeviceDirUrl)
					.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		initFiles(packagesFolder, propertySetsFolder, aadlDir, systemFiles,
				supportingFiles);
		// We don't actually maintain a set of system definitions, since
		// they are each only used by a single test case, so we sort of
		// kludge this in with an unused hashset...
		initFiles(packagesFolder, propertySetsFolder, aadlSystemDir,
				systemFiles, new HashSet<String>());
		initFiles(packagesFolder, propertySetsFolder, aadlDeviceDir,
				systemFiles, deviceFiles);
		initFiles(packagesFolder, propertySetsFolder, aadlPropertysetsDir,
				systemFiles, propertyFiles);
	}

	private static void initFiles(IFolder packagesFolder,
			IFolder propertySetsFolder, File dir,
			HashMap<String, IFile> fileMap, HashSet<String> fileNameMap) {
		String fileName = null;
		try {
			for (File f : dir.listFiles()) {
				if (f.isHidden() || f.isDirectory())
					continue;
				fileName = f.getName().substring(0, f.getName().length() - 5);
				fileNameMap.add(fileName);
				fileMap.put(fileName,
						packagesFolder.getFile(fileName + ".aadl"));
				if (!packagesFolder.getFile(fileName + ".aadl").exists()) {
					fileMap.get(fileName).create(new FileInputStream(f), true,
							null);
				}
				resourceSet.createResource(OsateResourceUtil
						.getResourceURI((IResource) fileMap.get(fileName)));
			}
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static SystemModel runTest(final String testName, final String systemName) {
		IFile inputFile = systemFiles.get(systemName);
		stats = new Translator(new NullProgressMonitor());

		parseErrManager = new ParseErrorReporterManager(parseErrorReporterFactory);

		stats.setErrorManager(parseErrManager);
		for (String propSetName : usedProperties) {
			stats.addPropertySetName(propSetName);
		}
		Resource res = resourceSet.getResource(
				OsateResourceUtil.getResourceURI((IResource) inputFile), true);
		Element target = (Element) res.getContents().get(0);
		stats.process(target);
		errorSB.append(parseErrManager.getReporter((IResource) inputFile)
				.toString());

		supportingFiles.addAll(usedDevices);
		
		for (String supportingFileName : supportingFiles) {
			IFile supportingFile = systemFiles.get(supportingFileName);
			res = resourceSet.getResource(OsateResourceUtil
					.getResourceURI((IResource) supportingFile), true);
			target = (Element) res.getContents().get(0);
			stats.process(target);
			errorSB.append(parseErrManager.getReporter(
					(IResource) supportingFile).toString());
		}
		
		supportingFiles.removeAll(usedDevices);

		return stats.getSystemModel();
	}
}
