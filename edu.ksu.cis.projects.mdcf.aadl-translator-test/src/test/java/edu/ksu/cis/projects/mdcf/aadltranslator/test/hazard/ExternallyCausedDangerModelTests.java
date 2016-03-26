package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ExternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class ExternallyCausedDangerModelTests {

	private static Map<String, ExternallyCausedDangerModel> pmDangers;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		pmDangers = processModel.getExternallyCausedDangers();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testECDMExist() {
		assertFalse(pmDangers.isEmpty());
		assertEquals(4, pmDangers.size());
	}

	@Test
	public void testECDMNames() {
		assertTrue(pmDangers.keySet().contains("HighSpO2LeadsToMissedAlarm"));
		assertTrue(pmDangers.keySet().contains("MultipleInputs"));
		assertTrue(pmDangers.keySet().contains("MultipleOutputs"));
		assertTrue(pmDangers.keySet().contains("MtoN"));
	}
	
	@Test
	public void testInterpretation() {
		assertEquals("The SpO2 value is too high, leading the app to fail to issue an alarm when it should",
				pmDangers.get("HighSpO2LeadsToMissedAlarm").getInterp());
	}

	@Test
	public void testECDMOutPort() {
		assertEquals("DerivedAlarm", pmDangers.get("HighSpO2LeadsToMissedAlarm").getSuccessorDanger().getPort().getName());
		assertEquals("DerivedAlarm", pmDangers.get("MultipleInputs").getSuccessorDanger().getPort().getName());
	}

	@Test
	public void testECDMSingleSuccDangerExists() {
		assertEquals(1, pmDangers.get("HighSpO2LeadsToMissedAlarm").getSuccessorDanger().getErrors().size());
		assertEquals(1, pmDangers.get("MultipleInputs").getSuccessorDanger().getErrors().size());
	}

	@Test
	public void testECDMSingleSuccDangerManifestation() {
		assertEquals("VIOLATEDCONSTRAINT", pmDangers.get("HighSpO2LeadsToMissedAlarm").getSuccessorDanger().getErrors()
				.iterator().next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmDangers.get("MultipleInputs").getSuccessorDanger().getErrors()
				.iterator().next().getManifestationName());
	}

	@Test
	public void testECDMSingleSuccDangerName() {
		assertEquals("MissedAlarm",
				pmDangers.get("HighSpO2LeadsToMissedAlarm").getSuccessorDanger().getErrors().iterator().next().getName());
		assertEquals("MissedAlarm",
				pmDangers.get("MultipleInputs").getSuccessorDanger().getErrors().iterator().next().getName());
	}

	@Test
	public void testECDMMultipleSuccDangerExists() {
		assertEquals(2, pmDangers.get("MultipleOutputs").getSuccessorDanger().getErrors().size());
		assertEquals(2, pmDangers.get("MtoN").getSuccessorDanger().getErrors().size());
	}

	@Test
	public void testECDMMultipleSuccDangerManifestation() {
		Iterator<ErrorTypeModel> multOutIter = pmDangers.get("MultipleOutputs").getSuccessorDanger().getErrors()
				.iterator();
		Iterator<ErrorTypeModel> mtoNIter = pmDangers.get("MtoN").getSuccessorDanger().getErrors().iterator();

		assertEquals("VIOLATEDCONSTRAINT", multOutIter.next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", multOutIter.next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", mtoNIter.next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", mtoNIter.next().getManifestationName());
	}

	@Test
	public void testECDMMultipleSuccDangerName() {
		Iterator<ErrorTypeModel> multOutIter = pmDangers.get("MultipleOutputs").getSuccessorDanger().getErrors()
				.iterator();
		Iterator<ErrorTypeModel> mtoNIter = pmDangers.get("MtoN").getSuccessorDanger().getErrors().iterator();

		assertEquals("MissedAlarm", multOutIter.next().getName());
		assertEquals("BogusAlarm", multOutIter.next().getName());
		assertEquals("BogusAlarm", mtoNIter.next().getName());
		assertEquals("MissedAlarm", mtoNIter.next().getName());
	}

	@Test
	public void testECDMInPort() {
		assertEquals("SpO2", pmDangers.get("HighSpO2LeadsToMissedAlarm").getDanger().getPort().getName());
		assertEquals("SpO2", pmDangers.get("MultipleOutputs").getDanger().getPort().getName());
	}

	@Test
	public void testECDMSingleDangerExists() {
		assertEquals(1, pmDangers.get("HighSpO2LeadsToMissedAlarm").getDanger().getErrors().size());
		assertEquals(1, pmDangers.get("MultipleOutputs").getDanger().getErrors().size());
	}

	@Test
	public void testECDMSingleDangerManifestation() {
		assertEquals("HIGH",
				pmDangers.get("HighSpO2LeadsToMissedAlarm").getDanger().getErrors().iterator().next().getManifestationName());
		assertEquals("HIGH",
				pmDangers.get("MultipleOutputs").getDanger().getErrors().iterator().next().getManifestationName());
	}

	@Test
	public void testECDMSingleDangerName() {
		assertEquals("SpO2ValueHigh",
				pmDangers.get("HighSpO2LeadsToMissedAlarm").getDanger().getErrors().iterator().next().getName());
		assertEquals("SpO2ValueHigh",
				pmDangers.get("MultipleOutputs").getDanger().getErrors().iterator().next().getName());
	}

	@Test
	public void testECDMMultipleDangerExists() {
		assertEquals(2, pmDangers.get("MultipleInputs").getDanger().getErrors().size());
		assertEquals(2, pmDangers.get("MtoN").getDanger().getErrors().size());
	}

	@Test
	public void testECDMMultipleDangerManifestation() {
		Iterator<ErrorTypeModel> multInIter = pmDangers.get("MultipleInputs").getDanger().getErrors().iterator();
		Iterator<ErrorTypeModel> mtoNIter = pmDangers.get("MtoN").getDanger().getErrors().iterator();

		assertEquals("HIGH", multInIter.next().getManifestationName());
		assertEquals("LOW", multInIter.next().getManifestationName());
		assertEquals("LOW", mtoNIter.next().getManifestationName());
		assertEquals("HIGH", mtoNIter.next().getManifestationName());
	}

	@Test
	public void testECDMMultipleDangerName() {
		Iterator<ErrorTypeModel> multInIter = pmDangers.get("MultipleInputs").getDanger().getErrors().iterator();
		Iterator<ErrorTypeModel> mtoNIter = pmDangers.get("MtoN").getDanger().getErrors().iterator();

		assertEquals("SpO2ValueHigh", multInIter.next().getName());
		assertEquals("SpO2ValueLow", multInIter.next().getName());
		assertEquals("SpO2ValueLow", mtoNIter.next().getName());
		assertEquals("SpO2ValueHigh", mtoNIter.next().getName());
	}
}
