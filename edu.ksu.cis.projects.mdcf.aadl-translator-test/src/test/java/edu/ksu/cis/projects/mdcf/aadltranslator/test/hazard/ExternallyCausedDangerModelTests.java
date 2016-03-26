package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.xtext.junit4.serializer.AssertStructureAcceptor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ExternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class ExternallyCausedDangerModelTests {
	
	private static ExternallyCausedDangerModel pmDanger;
	private static ExternallyCausedDangerModel dmDanger;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		
		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");

//		dmDanger = deviceModel.getExternallyCausedDangers().iterator().next();
		pmDanger = processModel.getExternallyCausedDangers().iterator().next();
	}
	
	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testECDMSuccDangerManifestation() {
		assertEquals("VIOLATEDCONSTRAINT", pmDanger.getSuccessorDanger().getError().getManifestationName());
	}
	
	@Test
	public void testECDMSuccDangerName() {
		assertEquals("MissedAlarm", pmDanger.getSuccessorDanger().getError().getName());
	}
	
	@Test
	public void testECDMOutPort() {
		assertEquals("DerivedAlarm", pmDanger.getSuccessorDanger().getPort().getName());
	}
	
	@Test
	public void testECDMInPort() {
		assertEquals("SpO2", pmDanger.getDanger().getPort().getName());
	}
	
	@Test
	public void testECDMDangerManifestation() {
		assertEquals("HIGH", pmDanger.getDanger().getError().getManifestationName());
	}
	
	@Test
	public void testECDMDangerName() {
		assertEquals("SpO2ValueHigh", pmDanger.getDanger().getError().getName());
	}
}
