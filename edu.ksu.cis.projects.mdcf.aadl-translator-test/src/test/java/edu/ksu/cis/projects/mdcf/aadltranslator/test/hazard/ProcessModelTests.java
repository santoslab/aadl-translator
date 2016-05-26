package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ProcessVariableModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class ProcessModelTests {

	private static Map<String, ProcessVariableModel> processModel;
	
	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		usedProperties.add("MAP_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process").getProcessModel();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testProcessModelExists() {
		assertNotNull(processModel);
	}

	@Test
	public void testProcessVariableExists() {
		assertEquals(1, processModel.size());
	}
	
	@Test
	public void testProcessVariableUnits() {
		assertEquals("Percent", processModel.get("SpO2Val").getUnits());
	}
	
	@Test
	public void testProcessVariableType() {
		assertEquals("FLOAT", processModel.get("SpO2Val").getType());
	}
	
	@Test
	public void testProcessVariableMinVal() {
		assertEquals("0.0", processModel.get("SpO2Val").getMinVal());
	}
	
	@Test
	public void testProcessVariableMaxVal() {
		assertEquals("100.0", processModel.get("SpO2Val").getMaxVal());
	}
	
	@Test
	public void testProcessModelIsNumeric() {
		assertTrue(processModel.get("SpO2Val").isNumeric());
	}
}
