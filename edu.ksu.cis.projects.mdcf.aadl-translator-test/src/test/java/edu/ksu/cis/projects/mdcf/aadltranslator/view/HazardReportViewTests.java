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

public class HazardReportViewTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String EXPECTED_DIR = "markdown/app/stpareport/";

	private static SystemModel systemModel;
	private static STGroup appSuperClassSTG;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runHazardTransTest("PulseOx",
				"PulseOx_Forwarding_System");
		
		URL stpaReportStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "stpa-markdown.stg");
		appSuperClassSTG = new STGroupFile(stpaReportStgUrl.getFile());
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	// We don't test the header because it's like 1 line, and it has a timestamp
	// in it, so the comparison would fail anyway.

	@Test
	public void testBackground() {
		runTest("background", systemModel);
	}
	
	@Test
	public void testTableOfContents() {
		runTest("tblOfContents", systemModel);
	}
	
	@Test
	public void testCtrlActTbl() {
		runTest("ctrlActTbl", systemModel);
	}
	
	@Test
	public void testHazCtrlActs() {
		runTest("hazCtrlActs", systemModel);
	}

	@Test
	public void testPreliminaries() {
		runTest("preliminaries", systemModel);
	}
	
	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, Object model) {
		runWriterTest(testName, model, appSuperClassSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
