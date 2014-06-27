package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceModelTests {

	private static DeviceModel deviceModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
		deviceModel = systemModel.getDeviceByType("ICEpoInterface");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testDeviceExists() {
		assertNotNull(deviceModel);
	}

	@Test
	public void testDeviceSendPortExists() {
		assertEquals(1, deviceModel.getSendPorts().size());
		assertNotNull(deviceModel.getSendPorts().get("SpO2"));
	}
	
	@Test
	public void testImplicitTasks() {
		assertEquals(1, deviceModel.getTasks().size());
		assertNotNull(deviceModel.getTasks().get("SpO2Task"));
	}
}
