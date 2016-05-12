package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.InternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class InternallyCausedDangerModelTests {

	private static Map<String, InternallyCausedDangerModel> pmDangers;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		pmDangers = processModel.getInternallyCausedDangers();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testICDMExist() {
		assertFalse(pmDangers.isEmpty());
		assertEquals(1, pmDangers.size());
	}

	@Test
	public void testICDMName() {
		assertTrue(pmDangers.keySet().contains("BogusAlarmsArePossible"));
	}

	@Test
	public void testICDMCausedErrorExists() {
		assertEquals(1, pmDangers.values().iterator().next().getSuccessorDanger().getErrors().size());
	}
	
	@Test
	public void testICDMCausedErrorName() {
		assertEquals("BogusAlarm", pmDangers.values().iterator().next().getSuccessorDanger().getErrors().iterator().next().getName());
	}
	
	@Test
	public void testICDMCausedErrorPortName() {
		assertEquals("DerivedAlarm", pmDangers.values().iterator().next().getSuccessorDanger().getPort().getName());
	}
	
	@Test
	public void testICDMFaultClass() {
		assertTrue(pmDangers.values().iterator().next().getFaultClasses().iterator().next().getName().equals("SoftwareBug"));
		//TODO: Add multiple fault classes
	}
}
