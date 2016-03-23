package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class PropagationModelTests {
	
	private static Set<PropagationModel> pmInProps;
	private static PropagationModel pmOutProp;
	private static PropagationModel dmProp;

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
		
		dmProp = deviceModel.getPortByName("SpO2Out").getOutPropagation();
		
		pmInProps = processModel.getPortByName("SpO2").getInPropagations();
		pmOutProp = processModel.getPortByName("DerivedAlarm").getOutPropagation();
	}
	
	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPropagationExists() {
		assertNotNull(dmProp);
		assertNotNull(pmInProps);
		assertEquals(1, pmInProps.size());
		assertNotNull(pmOutProp);
	}
	
	@Test
	public void testManifestationKinds() {
		assertEquals("HIGH", pmInProps.iterator().next().getError().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmOutProp.getError().getManifestationName());
		assertEquals("HIGH", dmProp.getError().getManifestationName());
	}
	
	@Test
	public void testManifestations() {
		assertEquals("SpO2ValueHigh", dmProp.getError().getName());
		assertEquals("SpO2ValueHigh", pmInProps.iterator().next().getError().getName());
		assertEquals("MissedAlarm", pmOutProp.getError().getName());		
	}
}
