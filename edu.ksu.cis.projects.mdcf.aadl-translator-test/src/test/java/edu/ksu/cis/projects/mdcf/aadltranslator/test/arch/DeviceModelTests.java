package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceModelTests {

	private static DeviceModel deviceModelFromSystem;
	private static DeviceModel deviceModelStandalone;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel fullSystemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
		SystemModel deviceOnlySystemModel = AllTests.runArchTransTest("PulseOxDevOnly", "PulseOx_Interface");
		deviceModelFromSystem = fullSystemModel.getDeviceByType("ICEpoInterface");
		deviceModelStandalone = deviceOnlySystemModel.getDeviceByType("ICEpoInterface");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testDeviceExists() {
		assertNotNull(deviceModelFromSystem);
		assertNotNull(deviceModelStandalone);
	}
	
	@Test
	public void testDevicesComponentType() {
		assertEquals(ComponentType.SENSOR, deviceModelFromSystem.getComponentType());
		assertEquals(ComponentType.SENSOR, deviceModelStandalone.getComponentType());
	}

	@Test
	public void testDeviceSendPortExists() {
		assertEquals(1, deviceModelStandalone.getSendPorts().size());
		assertEquals(1, deviceModelFromSystem.getSendPorts().size());
		assertNotNull(deviceModelFromSystem.getSendPorts().get("SpO2Out"));
		assertNotNull(deviceModelStandalone.getSendPorts().get("SpO2Out"));
	}
	
	@Test
	public void testImplicitTasks() {
		assertEquals(1, deviceModelStandalone.getChildren().size());
		assertEquals(1, deviceModelFromSystem.getChildren().size());
		assertNotNull(deviceModelFromSystem.getChildren().get("SpO2Task"));
		assertNotNull(deviceModelStandalone.getChildren().get("SpO2Task"));
	}
	
	@Test
	public void testProcessSystemName() {
		assertEquals("PulseOx_Forwarding_System", deviceModelFromSystem.getParentName());
		assertEquals("Device_Stub_System", deviceModelStandalone.getParentName());
	}
	
}
