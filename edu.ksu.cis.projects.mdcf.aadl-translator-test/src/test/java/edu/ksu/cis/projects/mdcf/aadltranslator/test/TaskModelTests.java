package edu.ksu.cis.projects.mdcf.aadltranslator.test;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;

public class TaskModelTests {
	
	private static TaskModel CheckSpO2Thread, UpdateSpO2Thread, HandleAlarmThread;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runTest("PulseOx", "PulseOx_Forwarding_System");
		ProcessModel logicProcess = systemModel.getProcessByType("PulseOx_Logic_Process");
		ProcessModel displayProcess = systemModel.getProcessByType("PulseOx_Display_Process");
		CheckSpO2Thread = logicProcess.getTask("CheckSpO2Thread");
		UpdateSpO2Thread = displayProcess.getTask("UpdateSpO2Thread");
		HandleAlarmThread = displayProcess.getTask("HandleAlarmThread");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void tasksExist() {
		assertNotNull(CheckSpO2Thread);
		assertNotNull(UpdateSpO2Thread);
		assertNotNull(HandleAlarmThread);
	}
}
