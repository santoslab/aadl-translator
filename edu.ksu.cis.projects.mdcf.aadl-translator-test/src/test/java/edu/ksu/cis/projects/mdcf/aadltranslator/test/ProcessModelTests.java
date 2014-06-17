package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class ProcessModelTests {
	private static ProcessModel processModel;

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
		assertEquals(processModel.getPorts().size(), 2);
	}
	
	@Test
	public void testProcessContainedTasks() {
		assertEquals(processModel.getTasks().size(), 1);
	}
}
