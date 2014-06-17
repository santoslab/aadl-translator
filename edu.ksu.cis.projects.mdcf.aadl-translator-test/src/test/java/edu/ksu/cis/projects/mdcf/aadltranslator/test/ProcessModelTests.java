package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class ProcessModelTests {
	private static ProcessModel processModel;

	//TODO: Add PseudoDevice process tests
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runTest("PulseOx", "PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testProcessExists() {
		assertNotNull(processModel);
	}
	
	@Test
	public void testProcessComponentType() {
		assertTrue(processModel.isLogic());
	}
	
	@Test
	public void testProcessContainedPorts() {
		assertEquals(2, processModel.getPorts().size());
	}
	
	@Test
	public void testProcessContainedTasks() {
		assertEquals(1, processModel.getTasks().size());
		assertNotNull(processModel.getTasks().get("CheckSpO2Thread"));
	}
	
	@Test
	public void testProcessSporadicTasks() {
		assertEquals(0, processModel.getSporadicTasks().size());
	}
	
	@Test
	public void testProcessPeriodicTasks() {
		assertEquals(1, processModel.getPeriodicTasks().size());
		assertNotNull(processModel.getPeriodicTasks().get("CheckSpO2Thread"));
	}
	
	@Test
	public void testProcessInDataPorts() {
		assertEquals(1, processModel.getReceiveDataPorts().size());
		assertNotNull(processModel.getReceiveDataPorts().get("SpO2"));
	}
	
	@Test
	public void testProcessInEventPorts() {
		assertEquals(0, processModel.getReceiveEventPorts().size());
	}
	
	@Test
	public void testProcessInEventDataPorts() {
		assertEquals(0, processModel.getReceiveEventDataPorts().size());
	}
	
	@Test
	public void testProcessOutPorts() {
		assertEquals(1, processModel.getSendPorts().size());
		assertNotNull(processModel.getSendPorts().get("DerivedAlarm"));
	}
	
	@Test
	public void testProcessIsPseudoDevice() {
		assertFalse(processModel.isPseudoDevice());
	}
	
	@Test
	public void testProcessSystemName() {
		assertEquals("PulseOx_Forwarding_System", processModel.getSystemName());
	}
	
	@Test
	public void testProcessGlobals() {
		assertEquals(0, processModel.getGlobals().size());
	}
}
