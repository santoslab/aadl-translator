package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osate.aadl2.PortCategory;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class DeviceModelTests {

	private static DeviceModel deviceModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runTest("PulseOx", "PulseOx_Forwarding_System");
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
		assertEquals(deviceModel.getSendPorts().size(), 1);
	}

	@Test
	public void testDeviceSendPortCategory() {
		assertEquals(deviceModel.getSendPorts().get("SpO2").getCategory(), PortCategory.EVENT_DATA);
	}

	@Test
	public void testDeviceSendPortType() {
		assertEquals(deviceModel.getSendPorts().get("SpO2").getType(), "Integer");
	}

	@Test
	public void testDeviceSendPortPeriod() {
		assertEquals(deviceModel.getSendPorts().get("SpO2").getMaxPeriod(), 300);
		assertEquals(deviceModel.getSendPorts().get("SpO2").getMinPeriod(), 100);
	}

	@Test
	public void testDeviceSendPortName() {
		assertEquals(deviceModel.getSendPorts().get("SpO2").getName(), "SpO2");
	}
	
	@Test
	public void testImplicitTasks() {
		assertEquals(deviceModel.getTasks().size(), 1);
	}
}
