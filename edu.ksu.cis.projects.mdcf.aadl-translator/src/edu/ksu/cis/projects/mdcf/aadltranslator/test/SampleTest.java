package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Before;
import org.junit.Test;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;

import com.google.common.collect.ImmutableMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.Translator;

public class SampleTest {

	private HashMap<String, IFile> systemFiles;
	private ResourceSet resourceSet;
	private final ImmutableMap<String, String> SYSTEM_FILE_TO_FILE_PATH = new ImmutableMap.Builder<String, String>()
			.put("systemFile1",	"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_System.aadl")
			.put("systemFile2",	"/Users/Sam/git/aadl-medical/pulseox-smartalarm/packages/PulseOx_SmartAlarm_System.aadl")
			.build();

	@Before
	public void setUp() {
		IProject testProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("TestProject");
		resourceSet = OsateResourceUtil.createResourceSet();
		systemFiles = new HashMap<>();
		try {
			testProject.create(null);
			testProject.open(null);
			IFolder packagesFolder = testProject.getFolder("packages");
			packagesFolder.create(true, true, null);
			IFolder propertySetsFolder = testProject.getFolder("propertysets");
			propertySetsFolder.create(true, true, null);
			initSystemFiles(packagesFolder);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void initSystemFiles(IFolder packagesFolder) {
		try {
			for (String systemName : SYSTEM_FILE_TO_FILE_PATH.keySet()) {
				systemFiles.put(systemName,
						packagesFolder.getFile(systemName + ".aadl"));
				systemFiles.get(systemName).create(
						new FileInputStream(SYSTEM_FILE_TO_FILE_PATH.get(systemName)), true, null);
			}
		} catch (FileNotFoundException | CoreException e) {
			e.printStackTrace();
		}
	}

	private void runTest(IFile inputFile) {
		Resource res = resourceSet.getResource(
				OsateResourceUtil.getResourceURI((IResource) inputFile), true);
		Element target = (Element) res.getContents().get(0);
		Translator stats = new Translator(new NullProgressMonitor());
		stats.process(target);
	}

	@Test
	public void testDummyTest1() {
		runTest(systemFiles.get("systemFile1"));
	}
}
