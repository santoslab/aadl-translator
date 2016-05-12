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

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorDetectionApproach;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorHandlingApproach;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.NotDangerousDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.RuntimeDetectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ExternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DetectionAndHandlingTests {

	private static Map<String, NotDangerousDangerModel> pmDangers;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		pmDangers = processModel.getSunkDangers();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testRuntimeErrorsExist() {
		assertEquals("TimestampViolation",
				pmDangers.get("LateSpO2DoesNothing").getRuntimeDetection().iterator().next().getName());
	}

	@Test
	public void testRuntimeHandlingsExist() {
		assertEquals("SwitchToNoOutput",
				pmDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getName());
		assertEquals("SwitchToNoOutput",
				pmDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getName());
	}

	@Test
	public void testRuntimeErrorDetectionExplanation() {
		assertEquals("Messages should be timestamped so latency violations can be detected",
				pmDangers.get("LateSpO2DoesNothing").getRuntimeDetection().iterator().next().getExplanation());
	}

	@Test
	public void testRuntimeErrorHandlingExplanation() {
		assertEquals("The pump switches into a fail-safe mode, ie, it runs at a minimal (KVO) rate",
				pmDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getExplanation());
		assertEquals("The pump switches into a fail-safe mode, ie, it runs at a minimal (KVO) rate",
				pmDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getExplanation());
	}

	@Test
	public void testRuntimeErrorDetectionApproach() {
		assertEquals(RuntimeErrorDetectionApproach.CONCURRENT,
				pmDangers.get("LateSpO2DoesNothing").getRuntimeDetection().iterator().next().getApproach());
	}

	@Test
	public void testRuntimeErrorHandlingApproach() {
		assertEquals(RuntimeErrorHandlingApproach.ROLLFORWARD,
				pmDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getApproach());
		assertEquals(RuntimeErrorHandlingApproach.ROLLFORWARD,
				pmDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getApproach());
	}
}
