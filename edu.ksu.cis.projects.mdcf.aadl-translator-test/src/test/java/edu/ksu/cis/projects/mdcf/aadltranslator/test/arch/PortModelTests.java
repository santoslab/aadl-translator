package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osate.aadl2.PortCategory;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

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
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		devSendPort = deviceModel.getSendPorts().get("SpO2Out");
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
		assertEquals("SpO2Out", devSendPort.getName());
		assertEquals("DerivedAlarm", procSendPort.getName());
		assertEquals("SpO2", procRecvPort.getName());
	}
	
	@Test
	public void testExchangeName(){
		assertEquals("spo2_per", devSendPort.getExchangeName());
		
		// This is hacked in here because OSATE's string property util method
		// doesn't behave as its documentation says it should. I've submitted a
		// pull request, but until it's accepted, this has to stay
		// See https://github.com/osate/osate2-core/pull/615
		assertTrue(procSendPort.getExchangeName() == null ||
				   procSendPort.getExchangeName().isEmpty());
		assertTrue(procRecvPort.getExchangeName() == null ||
				   procRecvPort.getExchangeName().isEmpty());
		
		// These can be reenabled when OSATE is fixed...
		//assertEquals(null, procSendPort.getExchangeName());
		//assertEquals(null, procRecvPort.getExchangeName());
	}
	
	@Test
	public void testContainingComponentTypeName(){
		assertEquals("ICEpoInterface", devSendPort.getContainingComponentName());
		assertEquals("PulseOx_Logic_Process", procSendPort.getContainingComponentName());
		assertEquals("PulseOx_Logic_Process", procRecvPort.getContainingComponentName());
	}
}
