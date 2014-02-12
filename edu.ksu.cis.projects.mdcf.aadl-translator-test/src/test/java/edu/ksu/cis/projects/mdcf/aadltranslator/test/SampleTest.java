package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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
import org.junit.Before;
import org.junit.Test;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.thoughtworks.xstream.XStream;

import edu.ksu.cis.projects.mdcf.aadltranslator.Translator;

public class SampleTest {

	private HashMap<String, IFile> systemFiles;
	private ResourceSet resourceSet;
	private Translator stats;

	ImmutableMap<String, String> supportingFileMap = getSupportingFiles();
	ImmutableMap<String, String> propertyFileMap = getPropertyFiles();
	private final boolean GENERATE_EXPECTED = false;

	@Before
	public void setUp() {
		IProject testProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("TestProject");
		resourceSet = OsateResourceUtil.createResourceSet();
		systemFiles = new HashMap<>();
		ImmutableMap<String, String> systemFileMap = getSystemFiles();
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
			testProject.create(null);
			testProject.open(null);

			IProjectDescription prpd = pluginResources.getDescription();
			prpd.setNatureIds(natureIDs);
			pluginResources.setDescription(prpd, null);

			IProjectDescription testpd = testProject.getDescription();
			testpd.setNatureIds(natureIDs);
			testpd.setReferencedProjects(referencedProjects);
			testProject.setDescription(testpd, null);

			IFolder packagesFolder = testProject.getFolder("packages");
			packagesFolder.create(true, true, null);
			IFolder propertySetsFolder = testProject.getFolder("propertysets");
			propertySetsFolder.create(true, true, null);
			initFiles(propertySetsFolder, propertyFileMap);
			initFiles(packagesFolder, systemFileMap);
			initFiles(packagesFolder, supportingFileMap);
			// May be optional?
			testProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
		} catch (CoreException | ExecutionException | NotDefinedException
				| NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
	}

	private ImmutableMap<String, String> getSupportingFiles() {
		return new ImmutableMap.Builder<String, String>()
				.put("PulseOx_Interface",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_Interface.aadl")
				.put("PulseOx_SmartAlarm_Display",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_Display.aadl")
				.put("PulseOx_SmartAlarm_Logic",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_Logic.aadl")
				.put("PulseOx_SmartAlarm_Types",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_Types.aadl")
				.build();
	}

	private ImmutableMap<String, String> getPropertyFiles() {
		return new ImmutableMap.Builder<String, String>()
				.put("PulseOx_SmartAlarm_Properties",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/propertysets/PulseOx_SmartAlarm_Properties.aadl")
				.put("MAP_Properties",
						"/Users/Sam/git/aadl-medical/map-globals/propertysets/MAP_Properties.aadl")
				.build();
	}

	private ImmutableMap<String, String> getSystemFiles() {
		return new ImmutableMap.Builder<String, String>()
				.put("PulseOx_SmartAlarm_System",
						"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_System.aadl")
				.build();
	}

	private void initFiles(IFolder packagesFolder,
			ImmutableMap<String, String> nameToFilePath) {
		try {
			for (String systemName : nameToFilePath.keySet()) {
				systemFiles.put(systemName,
						packagesFolder.getFile(systemName + ".aadl"));
				systemFiles.get(systemName).create(
						new FileInputStream(nameToFilePath.get(systemName)),
						true, null);
				resourceSet // May be optional?
						.createResource(OsateResourceUtil
								.getResourceURI((IResource) systemFiles
										.get(systemName)));
			}
		} catch (FileNotFoundException | CoreException e) {
			e.printStackTrace();
		}
	}

	private void runTest(String systemName) {
		IFile inputFile = systemFiles.get(systemName);
		stats = new Translator(new NullProgressMonitor());
		for (String propSetName : propertyFileMap.keySet()) {
			stats.addPropertySetName(propSetName);
		}
		Resource res = resourceSet.getResource(
				OsateResourceUtil.getResourceURI((IResource) inputFile), true);
		Element target = (Element) res.getContents().get(0);
		stats.process(target);

		for (String supportingFileName : supportingFileMap.keySet()) {
			IFile supportingFile = systemFiles.get(supportingFileName);
			res = resourceSet.getResource(OsateResourceUtil
					.getResourceURI((IResource) supportingFile), true);
			target = (Element) res.getContents().get(0);
			stats.process(target);
		}
		XStream xs = new XStream();

		try {
			testExpectedResult(systemName, xs.toXML(stats.getSystemModel()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void testExpectedResult(final String name, final String content)
			throws URISyntaxException, IOException, Exception {		

		URL testDirUrl = Platform
				.getBundle("edu.ksu.cis.projects.mdcf.aadl-translator-test")
				.getEntry("src/test/resources/edu/ksu/cis/projects/mdcf/"
						+ "aadltranslator/test/");
		File testDir = new File(FileLocator.toFileURL(testDirUrl).getPath());

		final File expected = new File(testDir, "expected/" + name + ".xml");
		final File result = new File(testDir, "actual/" + name + ".xml");
		if (GENERATE_EXPECTED) {
			expected.getParentFile().mkdirs();
			Files.write(content, expected, StandardCharsets.US_ASCII);
		} else {
			result.getParentFile().mkdirs();
			Files.write(content, result, StandardCharsets.US_ASCII);
			assertFilesEqual(expected, content);
		}
	}

	void assertFilesEqual(final File expectedFile, final String resultString)
			throws Exception {
		final String expectedFileAsString = Files.readLines(expectedFile,
				StandardCharsets.US_ASCII, new ReadFileIntoString());
		assertEquals(expectedFileAsString, resultString);
	}

	@Test
	public void testPulseOxSystem() {
		runTest("PulseOx_SmartAlarm_System");
	}

	private class ReadFileIntoString implements LineProcessor<String> {
		StringBuilder theFileAsAString = new StringBuilder();

		@Override
		public String getResult() {
			return theFileAsAString.deleteCharAt(theFileAsAString.length() - 1)
					.toString();
		}

		@Override
		public boolean processLine(String line) throws IOException {
			theFileAsAString.append(line);
			theFileAsAString.append("\n");
			return true;
		}

	}
}
