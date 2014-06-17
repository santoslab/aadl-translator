package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class ConnectionModelTests {
	private static SystemModel systemModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runTest("PulseOx", "PulseOx_Forwarding_System");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testPortConnectionsExist(){
		assertEquals(systemModel.getChannels().size(), 3);
	}
	
	@Test
	public void testDefaultPortConnectionChannelDelay() {
		assertEquals(100, systemModel.getChannels().get(0).getChannelDelay());
	}
	
	@Test
	public void testOverridePortConnectionChannelDelay() {
		assertEquals(150, systemModel.getChannels().get(1).getChannelDelay());
	}
	
	@Test
	public void testPortConnectionPublisherInfo() {
		assertEquals(systemModel.getDeviceByType("ICEpoInterface"), systemModel.getChannels().get(0).getPublisher());
		assertEquals("pulseOx", systemModel.getChannels().get(0).getPubName());
		assertEquals("SpO2", systemModel.getChannels().get(0).getPubPortName());
	}
	
	@Test
	public void testPortConnectionSubscriberInfo() {
		assertEquals(systemModel.getChannels().get(0).getSubscriber(), systemModel.getProcessByType("PulseOx_Logic_Process"));
		assertEquals(systemModel.getChannels().get(0).getSubName(), "appLogic");
		assertEquals(systemModel.getChannels().get(0).getSubPortName(), "SpO2");
	}
	
	@Test
	public void testPortConnectionDirections() {
		assertTrue(systemModel.getChannels().get(0).isDevicePublished());
		assertFalse(systemModel.getChannels().get(0).isDeviceSubscribed());
		assertFalse(systemModel.getChannels().get(2).isDevicePublished());
		assertFalse(systemModel.getChannels().get(2).isDeviceSubscribed());
	}
}
