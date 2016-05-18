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

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class AwasTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String EXPECTED_DIR = "awas/";

	private static SystemModel systemModel;
	private static STGroup awasSTG;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");

		URL awasStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "awas.stg");
		awasSTG = new STGroupFile(awasStgUrl.getFile());
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testTypes() {
		runTest("types", systemModel);
		/*
		 * for each subcomponent of the system
		 *   for each port
		 *     get the manifestationtype
		 */
	}

	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, Object model) {
		runWriterTest(testName, model, "model", awasSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
