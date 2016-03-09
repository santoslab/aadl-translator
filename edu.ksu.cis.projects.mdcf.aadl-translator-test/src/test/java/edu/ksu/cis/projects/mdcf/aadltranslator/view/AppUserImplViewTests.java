package edu.ksu.cis.projects.mdcf.aadltranslator.view;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.MAIN_PLUGIN_BUNDLE_ID;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.TEMPLATE_DIR;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.runWriterTest;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class AppUserImplViewTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = true;
	private final static String EXPECTED_DIR = "java/app/process/";
	
	private static ProcessModel processModel;
	private static STGroup appUserImplSTG;
	private static TaskModel taskModel;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Display_Process");
		taskModel = processModel.getChild("UpdateSpO2Thread");

		URL appSuperClassStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "java-userimpl.stg");
		appUserImplSTG = new STGroupFile(appSuperClassStgUrl.getFile());
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testUserImplOnMessageStub() {
		runTest("userImplOnMessageStub", "taskModel", taskModel);
	}

	@Test
	public void testUserImplStub() {
		runTest("userImplStub", "taskName", taskModel.getName());
	}

	@Test
	public void testFullUserSkeleton() {
		runTest("userimpl", "model", processModel);
	}

	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, String varName, Object var) {
		runWriterTest(testName, var, varName, appUserImplSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
