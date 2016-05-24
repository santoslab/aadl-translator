package edu.ksu.cis.projects.mdcf.aadltranslator.test.arch;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

	private static TaskModel checkSpO2Thread, updateSpO2Thread,
			handleAlarmThread, pseudoDevThread;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");
		ProcessModel logicProcess = systemModel
				.getProcessByType("PulseOx_Logic_Process");
		ProcessModel displayProcess = systemModel
				.getProcessByType("PulseOx_Display_Process");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		checkSpO2Thread = logicProcess.getChild("CheckSpO2Thread");
		updateSpO2Thread = displayProcess.getChild("UpdateSpO2Thread");
		handleAlarmThread = displayProcess.getChild("HandleAlarmThread");
		pseudoDevThread = deviceModel.getChild("SpO2Task");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testTasksExist() {
		assertNotNull(checkSpO2Thread);
		assertNotNull(updateSpO2Thread);
		assertNotNull(handleAlarmThread);
		assertNotNull(pseudoDevThread);
	}
	
	@Test
	public void testTaskDefaultDeadline() {
		assertEquals(50, checkSpO2Thread.getDeadline());
		assertEquals(50, updateSpO2Thread.getDeadline());
		assertEquals(50, pseudoDevThread.getDeadline());
	}

	@Test
	public void testTaskOverrideDeadline() {
		assertEquals(75, handleAlarmThread.getDeadline());
	}

	@Test
	public void testDefaultPeriod() {
		assertEquals(50, checkSpO2Thread.getPeriod());
		assertEquals(50, updateSpO2Thread.getPeriod());
		assertEquals(-1, pseudoDevThread.getPeriod());
	}

	@Test
	public void testTaskOverridePeriod() {
		assertEquals(95, handleAlarmThread.getPeriod());
	}

	@Test
	public void testTaskDefaultWCET() {
		assertEquals(5, checkSpO2Thread.getWcet());
		assertEquals(5, updateSpO2Thread.getWcet());
		assertEquals(5, pseudoDevThread.getWcet());
	}

	@Test
	public void testTaskOverrideWCET() {
		assertEquals(7, handleAlarmThread.getWcet());
	}

	@Test
	public void testTaskTrigPortInfo() {
		assertEquals("Double", updateSpO2Thread.getTrigPortType());
		assertEquals("SpO2", updateSpO2Thread.getTrigPortName());
		assertEquals("SpO2", updateSpO2Thread.getTrigPortLocalName());
	}

	@Test
	public void testTaskIncomingGlobals() {
		assertEquals(0, checkSpO2Thread.getIncomingGlobals().size());
		assertEquals(0, handleAlarmThread.getIncomingGlobals().size());
		assertEquals(0, updateSpO2Thread.getIncomingGlobals().size());
	}

	@Test
	public void testTaskOutgoingGlobals() {
		assertEquals(0, checkSpO2Thread.getOutgoingGlobals().size());
		assertEquals(0, handleAlarmThread.getOutgoingGlobals().size());
		assertEquals(0, updateSpO2Thread.getOutgoingGlobals().size());
	}

	@Test
	public void testTaskTriggerIsEvent() {
		assertTrue(handleAlarmThread.getTrigPort().isEvent());
		assertFalse(updateSpO2Thread.getTrigPort().isEvent());
		assertFalse(pseudoDevThread.getTrigPort().isEvent());
	}
	
	@Test
	public void testTaskTriggerIsEventData() {
		assertFalse(handleAlarmThread.getTrigPort().isEventData());
		assertTrue(updateSpO2Thread.getTrigPort().isEventData());
		assertTrue(pseudoDevThread.getTrigPort().isEventData());
	}
	
	@Test
	public void testTaskTriggerIsData() {
		assertFalse(handleAlarmThread.getTrigPort().isData());
		assertFalse(updateSpO2Thread.getTrigPort().isData());
		assertFalse(pseudoDevThread.getTrigPort().isData());
	}

	@Test
	public void testNoTriggerPort(){
		assertNull(checkSpO2Thread.getTrigPort());
	}
	
	@Test
	public void testTaskIsSporadic() {
		assertFalse(checkSpO2Thread.isSporadic());
		assertTrue(handleAlarmThread.isSporadic());
		assertTrue(updateSpO2Thread.isSporadic());
		assertTrue(pseudoDevThread.isSporadic());
	}
}
