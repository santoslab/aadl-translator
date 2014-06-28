package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class TaskModelTests {

	private static TaskModel CheckSpO2Thread, UpdateSpO2Thread,
			HandleAlarmThread, PseudoDevThread;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");
		ProcessModel logicProcess = systemModel
				.getProcessByType("PulseOx_Logic_Process");
		ProcessModel displayProcess = systemModel
				.getProcessByType("PulseOx_Display_Process");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		CheckSpO2Thread = logicProcess.getTask("CheckSpO2Thread");
		UpdateSpO2Thread = displayProcess.getTask("UpdateSpO2Thread");
		HandleAlarmThread = displayProcess.getTask("HandleAlarmThread");
		PseudoDevThread = deviceModel.getTask("SpO2Task");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testTasksExist() {
		assertNotNull(CheckSpO2Thread);
		assertNotNull(UpdateSpO2Thread);
		assertNotNull(HandleAlarmThread);
		assertNotNull(PseudoDevThread);
	}

	@Test
	public void testTaskCallSequence() {
		assertEquals(0, CheckSpO2Thread.getCallSequence().size());
		assertEquals(0, UpdateSpO2Thread.getCallSequence().size());
		assertEquals(0, HandleAlarmThread.getCallSequence().size());
		assertEquals(0, PseudoDevThread.getCallSequence().size());
	}

	@Test
	public void testTaskDefaultDeadline() {
		assertEquals(50, CheckSpO2Thread.getDeadline());
		assertEquals(50, UpdateSpO2Thread.getDeadline());
		assertEquals(50, PseudoDevThread.getDeadline());
	}

	@Test
	public void testTaskOverrideDeadline() {
		assertEquals(75, HandleAlarmThread.getDeadline());
	}

	@Test
	public void testDefaultPeriod() {
		assertEquals(50, CheckSpO2Thread.getPeriod());
		assertEquals(50, UpdateSpO2Thread.getPeriod());
		assertEquals(-1, PseudoDevThread.getPeriod());
	}

	@Test
	public void testTaskOverridePeriod() {
		assertEquals(95, HandleAlarmThread.getPeriod());
	}

	@Test
	public void testTaskDefaultWCET() {
		assertEquals(5, CheckSpO2Thread.getWcet());
		assertEquals(5, UpdateSpO2Thread.getWcet());
		assertEquals(5, PseudoDevThread.getWcet());
	}

	@Test
	public void testTaskOverrideWCET() {
		assertEquals(7, HandleAlarmThread.getWcet());
	}

	@Test
	public void testTaskTrigPortInfo() {
		assertEquals("Integer", UpdateSpO2Thread.getTrigPortType());
		assertEquals("SpO2", UpdateSpO2Thread.getTrigPortName());
		assertEquals("SpO2", UpdateSpO2Thread.getTrigPortLocalName());
	}

	@Test
	public void testTaskIncomingGlobals() {
		assertEquals(0, CheckSpO2Thread.getIncomingGlobals().size());
		assertEquals(0, HandleAlarmThread.getIncomingGlobals().size());
		assertEquals(0, UpdateSpO2Thread.getIncomingGlobals().size());
	}

	@Test
	public void testTaskOutgoingGlobals() {
		assertEquals(0, CheckSpO2Thread.getOutgoingGlobals().size());
		assertEquals(0, HandleAlarmThread.getOutgoingGlobals().size());
		assertEquals(0, UpdateSpO2Thread.getOutgoingGlobals().size());
	}

	@Test
	public void testTaskIsEventTriggered() {
		assertFalse(CheckSpO2Thread.isEventTriggered());
		assertTrue(HandleAlarmThread.isEventTriggered());
		assertFalse(UpdateSpO2Thread.isEventTriggered());
		assertFalse(PseudoDevThread.isEventTriggered());
	}

	@Test
	public void testTaskIsSporadic() {
		assertFalse(CheckSpO2Thread.isSporadic());
		assertTrue(HandleAlarmThread.isSporadic());
		assertTrue(UpdateSpO2Thread.isSporadic());
		assertTrue(PseudoDevThread.isSporadic());
	}
}
