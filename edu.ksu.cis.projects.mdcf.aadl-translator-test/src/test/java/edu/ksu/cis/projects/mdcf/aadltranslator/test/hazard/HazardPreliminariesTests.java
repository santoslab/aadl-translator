package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class HazardPreliminariesTests {

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
	public void testAccidentLevel() {
		assertEquals(1, systemModel.getAccidentLevels().size());
		assertEquals("DeathOrInjury", systemModel.getAccidentLevelByName("DeathOrInjury").getName());
		assertEquals("A human is killed or seriously injured.", systemModel
				.getAccidentLevelByName("DeathOrInjury").getDescription());
		assertEquals(1, systemModel.getAccidentLevelByName("DeathOrInjury").getNumber());
	}

	@Test
	public void testAccident() {
		assertEquals(1, systemModel.getAccidents().size());
		assertEquals("PatientHarmed", systemModel.getAccidentByName("PatientHarmed").getName());
		assertEquals("Patient is killed or seriously injured.", systemModel
				.getAccidentByName("PatientHarmed").getDescription());
	}

	@Test
	public void testHazard() {
		assertEquals(2, systemModel.getHazards().size());
		assertEquals("BadInfoDisplayed", systemModel.getHazardByName("BadInfoDisplayed").getName());
		assertEquals("Incorrect information is sent to the display.",
				systemModel.getHazardByName("BadInfoDisplayed").getDescription());
	}

	@Test
	public void testConstraint() {
		assertEquals(2, systemModel.getConstraints().size());
		assertEquals("ShowGoodInfo", systemModel.getConstraintByName("ShowGoodInfo").getName());
		assertEquals(
				"The app must accurately inform the display of the status of the patient's"
						+ " vital signs.", systemModel
						.getConstraintByName("ShowGoodInfo").getDescription());
	}
}
