package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class PropagationModelTests {
	
	private static Set<PropagationModel> pmInProps;
	private static Set<PropagationModel> pmOutProps;
	private static Set<PropagationModel> dmProps;
	private static Iterator<PropagationModel> pmInIter;
	private static Iterator<PropagationModel> pmOutIter;

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
		
		dmProps = new LinkedHashSet<>(deviceModel.getPortByName("SpO2Out").getPropagations());
		pmInProps = new LinkedHashSet<>(processModel.getPortByName("SpO2").getPropagations());
		pmOutProps = new LinkedHashSet<>(processModel.getPortByName("DerivedAlarm").getPropagations());
	}
	
	@Before
	public void setup(){
		pmInIter = pmInProps.iterator();
		pmOutIter = pmOutProps.iterator();
	}
	
	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPropagationExists() {
		assertNotNull(dmProps);
		assertEquals(1, dmProps.size());
		assertNotNull(pmInProps);
		assertEquals(2, pmInProps.size());
		assertNotNull(pmOutProps);
		assertEquals(2, pmOutProps.size());
	}
	
	@Test
	public void testManifestationKinds() {
		assertEquals("HIGH", pmInIter.next().getError().getManifestationName());
		assertEquals("LOW", pmInIter.next().getError().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmOutIter.next().getError().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmOutIter.next().getError().getManifestationName());
		assertEquals("HIGH", dmProps.iterator().next().getError().getManifestationName());
	}
	
	@Test
	public void testManifestations() {
		assertEquals("SpO2ValueHigh", pmInIter.next().getError().getName());
		assertEquals("SpO2ValueLow", pmInIter.next().getError().getName());
		assertEquals("MissedAlarm", pmOutIter.next().getError().getName());
		assertEquals("BogusAlarm", pmOutIter.next().getError().getName());
		assertEquals("SpO2ValueHigh", dmProps.iterator().next().getError().getName());
	}
}
