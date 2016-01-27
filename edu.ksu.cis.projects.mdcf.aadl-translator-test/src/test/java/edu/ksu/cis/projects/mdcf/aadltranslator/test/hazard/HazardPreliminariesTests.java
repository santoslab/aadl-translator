package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class HazardPreliminariesTests {

	private static final String CONSTRAINT_NAME = "ShowGoodInfo";
	private static final String HAZARD_NAME = "BadInfoDisplayed";
	private static final String ACCIDENT_NAME = "PatientHarmed";
	private static final String ACCIDENT_LEVEL_NAME = "DeathOrInjury";
	private static ArrayList<String> explanations;
	private static SystemModel systemModel;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		
		explanations = new ArrayList<>();
		explanations.add("First Explanation");
		explanations.add("Second Explanation");
		explanations.add("Third Explanation");
		
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
		assertEquals(ACCIDENT_LEVEL_NAME, systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getName());
		assertEquals("A human is killed or seriously injured.", systemModel
				.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getDescription());
		assertEquals(1, systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getNumber());
		assertArrayEquals(explanations.toArray(), systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getExplanations().toArray());
	}

	@Test
	public void testAccident() {
		assertEquals(1, systemModel.getAccidents().size());
		assertEquals(ACCIDENT_NAME, systemModel.getAccidentByName(ACCIDENT_NAME).getName());
		assertEquals("Patient is killed or seriously injured.", systemModel
				.getAccidentByName(ACCIDENT_NAME).getDescription());
		assertArrayEquals(explanations.toArray(), systemModel.getAccidentByName(ACCIDENT_NAME).getExplanations().toArray());
	}

	@Test
	public void testHazard() {
		assertEquals(2, systemModel.getHazards().size());
		assertEquals(HAZARD_NAME, systemModel.getHazardByName(HAZARD_NAME).getName());
		assertEquals("Incorrect information is sent to the display.",
				systemModel.getHazardByName(HAZARD_NAME).getDescription());
	}

	@Test
	public void testConstraint() {
		assertEquals(2, systemModel.getConstraints().size());
		assertEquals(CONSTRAINT_NAME, systemModel.getConstraintByName(CONSTRAINT_NAME).getName());
		assertEquals(
				"The app must accurately inform the display of the status of the patient's"
						+ " vital signs.", systemModel
						.getConstraintByName(CONSTRAINT_NAME).getDescription());
		assertArrayEquals(explanations.toArray(), systemModel.getConstraintByName(CONSTRAINT_NAME).getExplanations().toArray());
	}
}
