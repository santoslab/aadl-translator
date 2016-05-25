package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

		systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	///////////////////////////////////////////////////////////////////////////
	// ACCIDENT LEVEL TESTS ///////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	@Test
	public void testAccidentLevelExists() {
		assertEquals(1, systemModel.getAccidentLevels().size());
	}

	@Test
	public void testAccidentLevelName() {
		assertEquals(ACCIDENT_LEVEL_NAME, systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getName());
	}

	@Test
	public void testAccidentLevelDescription() {
		assertEquals("A human is killed or seriously injured.",
				systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getDescription());
	}

	@Test
	public void testAccidentLevelExplanations() {
		assertArrayEquals(explanations.toArray(),
				systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getExplanations().toArray());
	}

	@Test
	public void testAccidentLevelNumber() {
		assertEquals(1, systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getNumber());
	}

	@Test
	public void testAccidentLevelParent() {
		assertNull(systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME).getParent());
	}

	///////////////////////////////////////////////////////////////////////////
	// ACCIDENT TESTS /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testAccidentExists() {
		assertEquals(1, systemModel.getAccidents().size());
	}

	@Test
	public void testAccidentName() {
		assertEquals(ACCIDENT_NAME, systemModel.getAccidentByName(ACCIDENT_NAME).getName());
	}

	@Test
	public void testAccidentDescription() {
		assertEquals("Patient is killed or seriously injured.",
				systemModel.getAccidentByName(ACCIDENT_NAME).getDescription());
	}

	@Test
	public void testAccidentExplanations() {
		assertArrayEquals(explanations.toArray(),
				systemModel.getAccidentByName(ACCIDENT_NAME).getExplanations().toArray());
	}

	@Test
	public void testAccidentParent() {
		assertEquals(systemModel.getAccidentLevelByName(ACCIDENT_LEVEL_NAME),
				systemModel.getAccidentByName(ACCIDENT_NAME).getParent());
	}

	///////////////////////////////////////////////////////////////////////////
	// HAZARD TESTS ///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testHazardExists() {
		assertEquals(2, systemModel.getHazards().size());
	}

	@Test
	public void testHazardName() {
		assertEquals(HAZARD_NAME, systemModel.getHazardByName(HAZARD_NAME).getName());
	}

	@Test
	public void testHazardDescription() {
		assertEquals("Incorrect information is sent to the display.",
				systemModel.getHazardByName(HAZARD_NAME).getDescription());
	}

	@Test
	public void testHazardParent() {
		assertEquals(systemModel.getAccidentByName(ACCIDENT_NAME),
				systemModel.getHazardByName(HAZARD_NAME).getParent());
	}

	@Test
	public void testHazardSystemElement() {
		assertEquals("pulseOx", systemModel.getHazardByName(HAZARD_NAME).getSystemElement());
	}

	@Test
	public void testHazardEnvironmentElement() {
		assertEquals("patient", systemModel.getHazardByName(HAZARD_NAME).getEnvironmentElement());
	}

	///////////////////////////////////////////////////////////////////////////
	// CONSTRAINT TESTS ///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testConstraintExists() {
		assertEquals(2, systemModel.getConstraints().size());
	}

	@Test
	public void testConstraintName() {
		assertEquals(CONSTRAINT_NAME, systemModel.getConstraintByName(CONSTRAINT_NAME).getName());
	}

	@Test
	public void testConstraintDescription() {
		assertEquals("The app must accurately inform the display of the status of the patient's" + " vital signs.",
				systemModel.getConstraintByName(CONSTRAINT_NAME).getDescription());
	}

	@Test
	public void testConstraintExplanations() {
		assertArrayEquals(explanations.toArray(),
				systemModel.getConstraintByName(CONSTRAINT_NAME).getExplanations().toArray());
	}

	@Test
	public void testConstraintParent() {
		assertEquals(systemModel.getHazardByName(HAZARD_NAME),
				systemModel.getConstraintByName(CONSTRAINT_NAME).getParent());
	}

	@Test
	public void testConstraintErrorType() {
		assertEquals("MissedAlarm", systemModel.getConstraintByName(CONSTRAINT_NAME).getErrorType().getName());
		assertEquals("VIOLATEDCONSTRAINT",
				systemModel.getConstraintByName(CONSTRAINT_NAME).getErrorType().getManifestationName());
	}
}
