package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.errorSB;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.runTest;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedDevices;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ControllerErrorTests {

	@Before
	public void setUp() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		// See http://stackoverflow.com/a/18766889/2001755
		errorSB.setLength(0);
		errorSB.trimToSize();
	}

	@After
	public void tearDown() {
		usedProperties.clear();
		usedDevices.clear();
	}

	@Test
	public void testNoChannelDelay() {
		usedProperties.add("PulseOx_ForwardingNoChannelDelay_Properties");
		runTest("PulseOxNoChannelDelay", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_System.aadl:18: Missing required property 'Default_Channel_Delay'",
				errorSB.toString().trim());
	}

	@Test
	public void testNoOutputRate() {
		usedProperties.add("PulseOx_ForwardingNoOutputRate_Properties");
		runTest("PulseOxNoOutputRate", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:7: Missing the required output rate specification.",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDeadline() {
		usedProperties.add("PulseOx_ForwardingNoThreadDeadline_Properties");
		runTest("PulseOxNoThreadDeadline", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:20: Thread deadline must either be set with Default_Thread_Deadline (at package level) or with Timing_Properties::Deadline (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDispatch() {
		usedProperties.add("PulseOx_ForwardingNoThreadDispatch_Properties");
		runTest("PulseOxNoThreadDispatch", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Display.aadl:22: Thread dispatch type must either be set with Default_Thread_Dispatch (at package level) or with Thread_Properties::Dispatch_Protocol (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadPeriod() {
		usedProperties.add("PulseOx_ForwardingNoThreadPeriod_Properties");
		runTest("PulseOxNoThreadPeriod", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:20: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoWCET() {
		usedProperties.add("PulseOx_ForwardingNoWCET_Properties");
		runTest("PulseOxNoWCET", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:20: Thread WCET must either be set with Default_Thread_WCET (at package level) or with Timing_Properties::Compute_Execution_Time (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testDuplicateSystem() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		runTest("PulseOxDuplicateSystem", "PulseOx_Forwarding_Duplicate_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Duplicate_System.aadl:27: Got a system called Duplicate_System but I already have one called PulseOx_Forwarding_Duplicate_System",
				errorSB.toString().trim());
	}

	@Test
	public void testIntegerOverflow() {
		usedProperties.add("PulseOx_ForwardingIntegerOverflow_Properties");
		runTest("PulseOxIntegerOverflow", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:20: Property Default_Thread_Period on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:20: Property Default_Thread_Deadline on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:20: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testBidirectionalPort() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		runTest("PulseOxBidirectionalPortConnection",
				"PulseOx_Forwarding_Bidirectional_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Bidirectional_System.aadl:18: Bidirectional ports are not yet allowed.",
				errorSB.toString().trim());
	}

	@Test
	public void testDevToDevConnection() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedDevices.add("PulseOx_UseSpO2_Interface");
		runTest("PulseOxDevToDevConnection",
				"PulseOx_Forwarding_DevToDev_System");
		assertEquals(
				"Error at PulseOx_Forwarding_DevToDev_System.aadl:28: Device to device connections are not yet allowed.",
				errorSB.toString().trim());
	}
}
