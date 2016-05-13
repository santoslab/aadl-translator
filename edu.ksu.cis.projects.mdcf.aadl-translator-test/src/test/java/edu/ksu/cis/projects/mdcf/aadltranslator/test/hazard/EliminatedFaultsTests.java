package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class EliminatedFaultsTests {

	private static Map<String, Set<String>> eliminatedFaults;
	private static ProcessModel processModel;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		eliminatedFaults = processModel.getEliminatedFaults();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testEliminatedFaultsExist() {
		assertNotNull(eliminatedFaults);
		assertEquals(3, eliminatedFaults.size());
	}

	@Test
	public void testEliminatedFaultsExplanations() {
		assertTrue(eliminatedFaults.keySet().contains("The hospital has physical security measures in place"));
		assertTrue(eliminatedFaults.keySet().contains("We're using a 'proven in use' app"));
		assertTrue(eliminatedFaults.keySet().contains("The app logic isn't a connection between two components"));
	}

	@Test
	public void testEliminatedFaultsLists() {
		assertTrue(eliminatedFaults.get("The hospital has physical security measures in place")
				.contains("AdversaryAccessesHardware"));
		assertTrue(eliminatedFaults.get("The hospital has physical security measures in place")
				.contains("AdversaryAccessesSoftware"));

		assertTrue(eliminatedFaults.get("The app logic isn't a connection between two components")
				.contains("SyntaxMismatch"));
		assertTrue(eliminatedFaults.get("The app logic isn't a connection between two components")
				.contains("RateMismatch"));
		assertTrue(eliminatedFaults.get("The app logic isn't a connection between two components")
				.contains("SemanticMismatch"));
	}

	@Test
	public void testMissedFaultClassesExist() {
		assertEquals(5, processModel.getMissedFaultClasses().size());
	}

	@Test
	public void testMissedFaultClassNames() {
		Set<String> missedFaults = Stream.of("OperatorSWWrongChoice", "CosmicRay", "OperatorSWMistake",
				"OperatorHWMistake", "OperatorHWWrongChoice").collect(Collectors.toSet());
		assertTrue(processModel.getMissedFaultClasses().containsAll(missedFaults));
	}
}
