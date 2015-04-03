package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class HazardBackgroundTests {

	private static SystemModel systemModel;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		systemModel = AllTests.runHazardTransTest("PulseOx",
				"PulseOx_Forwarding_System");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testContext() {
		assertEquals(
				"Clinicians want to view physiological parameters on a display"
				+ " not physically connected to a physiological monitor.",
				systemModel.getHazardReportContext());
	}

	@Test
	public void testAssumption() {
		assertEquals(1, systemModel.getHazardReportAssumptions().size());
		assertEquals("There are no alarms that need forwarding.",
				systemModel.getHazardReportAssumptions().iterator().next());
	}

	@Test
	public void testAbbreviation() {
		assertEquals(1, systemModel.getHazardReportAbbreviations().size());
		assertEquals("KVO", systemModel.getHazardReportAbbreviations()
				.iterator().next().getName());
		assertEquals("Keep Vein Open", systemModel
				.getHazardReportAbbreviations().iterator().next().getFull());
		assertEquals("A minimal rate (of drug administration)", systemModel
				.getHazardReportAbbreviations().iterator().next()
				.getDefinition());
	}
}
