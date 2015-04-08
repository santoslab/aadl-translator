package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.errorSB;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.runArchTransTest;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedDevices;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

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
		runArchTransTest("PulseOxNoChannelDelay", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_System.aadl:20: Missing required property 'Default_Channel_Delay'",
				errorSB.toString().trim());
	}

	@Test
	public void testNoOutputRate() {
		usedProperties.add("PulseOx_ForwardingNoOutputRate_Properties");
		runArchTransTest("PulseOxNoOutputRate", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:7: Missing the required output rate specification.",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDeadline() {
		usedProperties.add("PulseOx_ForwardingNoThreadDeadline_Properties");
		runArchTransTest("PulseOxNoThreadDeadline", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:31: Thread deadline must either be set with Default_Thread_Deadline (at package level) or with Timing_Properties::Deadline (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDispatch() {
		usedProperties.add("PulseOx_ForwardingNoThreadDispatch_Properties");
		runArchTransTest("PulseOxNoThreadDispatch", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Display.aadl:31: Thread dispatch type must either be set with Default_Thread_Dispatch (at package level) or with Thread_Properties::Dispatch_Protocol (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadPeriod() {
		usedProperties.add("PulseOx_ForwardingNoThreadPeriod_Properties");
		runArchTransTest("PulseOxNoThreadPeriod", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:31: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoWCET() {
		usedProperties.add("PulseOx_ForwardingNoWCET_Properties");
		runArchTransTest("PulseOxNoWCET", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:31: Thread WCET must either be set with Default_Thread_WCET (at package level) or with Timing_Properties::Compute_Execution_Time (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testDuplicateSystem() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		runArchTransTest("PulseOxDuplicateSystem", "PulseOx_Forwarding_Duplicate_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Duplicate_System.aadl:29: Got a system called Duplicate_System but I already have one called PulseOx_Forwarding_Duplicate_System",
				errorSB.toString().trim());
	}

	@Test
	public void testIntegerOverflow() {
		usedProperties.add("PulseOx_ForwardingIntegerOverflow_Properties");
		runArchTransTest("PulseOxIntegerOverflow", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:31: Property Default_Thread_Period on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:31: Property Default_Thread_Deadline on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:31: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testBidirectionalPort() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		runArchTransTest("PulseOxBidirectionalPortConnection",
				"PulseOx_Forwarding_Bidirectional_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Bidirectional_System.aadl:18: Bidirectional ports are not yet allowed.",
				errorSB.toString().trim());
	}

	@Test
	public void testDevToDevConnection() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedDevices.add("PulseOx_UseSpO2_Interface");
		runArchTransTest("PulseOxDevToDevConnection",
				"PulseOx_Forwarding_DevToDev_System");
		assertEquals(
				"Error at PulseOx_Forwarding_DevToDev_System.aadl:28: Device to device connections are not yet allowed.",
				errorSB.toString().trim());
	}

	@Test
	public void testTypelessDev() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedDevices.add("Typeless_PulseOx_Interface");
		runArchTransTest("TypelessPulseOx",
				"PulseOx_Forwarding_TypelessPO_System");
		assertEquals(
				"Error at PulseOx_Forwarding_TypelessPO_System.aadl:13: Devices must declare their role with MAP_Properties::Component_Type",
				errorSB.toString().trim());
	}
}
