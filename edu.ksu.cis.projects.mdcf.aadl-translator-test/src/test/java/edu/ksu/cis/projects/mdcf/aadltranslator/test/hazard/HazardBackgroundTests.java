package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.OccurrenceModel;
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
				"A simple app that forwards information from a pulse oximeter "
						+ "to an app display.",
				systemModel.getHazardReportContext());
	}

	@Test
	public void testAssumption() {
		assertEquals(1, systemModel.getHazardReportAssumptions().size());
		assertEquals("We assume there are no alarms that need forwarding.",
				systemModel.getHazardReportAssumptions().iterator().next());
	}

	@Test
	public void testAbbreviation() {
		assertEquals(1, systemModel.getHazardReportAbbreviations().size());
		assertEquals("SpO2", systemModel.getHazardReportAbbreviations()
				.iterator().next().getName());
		assertEquals("Blood-oxygen saturation", systemModel
				.getHazardReportAbbreviations().iterator().next().getFull());
		assertEquals("The ratio of SpO2 in the patient's blood", systemModel
				.getHazardReportAbbreviations().iterator().next()
				.getDefinition());
	}
}
