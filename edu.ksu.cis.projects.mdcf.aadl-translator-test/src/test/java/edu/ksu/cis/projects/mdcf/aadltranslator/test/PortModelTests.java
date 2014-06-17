package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osate.aadl2.PortCategory;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class PortModelTests {


	private static PortModel devSendPort;
	private static PortModel procSendPort;
	private static PortModel procRecvPort;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runTest("PulseOx", "PulseOx_Forwarding_System");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		devSendPort = deviceModel.getSendPorts().get("SpO2");
		procSendPort = processModel.getSendPorts().get("DerivedAlarm");
		procRecvPort = processModel.getReceivePorts().get("SpO2");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPortCategory() {
		assertEquals(PortCategory.EVENT_DATA, devSendPort.getCategory());
		assertEquals(PortCategory.EVENT, procSendPort.getCategory());
		assertEquals(PortCategory.DATA, procRecvPort.getCategory());
	}

	@Test
	public void testPortType() {
		assertEquals("Integer", devSendPort.getType());
		assertEquals("Object", procSendPort.getType());
	}

	@Test
	public void testDefaultPortPeriod() {
		assertEquals(300, devSendPort.getMaxPeriod());
		assertEquals(100, devSendPort.getMinPeriod());
		assertEquals(300, procRecvPort.getMaxPeriod());
		assertEquals(100, procRecvPort.getMinPeriod());
	}
	
	@Test
	public void testOverridePortPeriod() {
		assertEquals(400, procSendPort.getMaxPeriod());
		assertEquals(200, procSendPort.getMinPeriod());
	}

	@Test
	public void testPortName() {
		assertEquals("SpO2", devSendPort.getName());
		assertEquals("DerivedAlarm", procSendPort.getName());
		assertEquals("SpO2", procRecvPort.getName());
	}
}
