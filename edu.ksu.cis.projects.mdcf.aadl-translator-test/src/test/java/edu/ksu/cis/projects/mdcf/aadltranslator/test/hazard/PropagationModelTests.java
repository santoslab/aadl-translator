package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypesModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class PropagationModelTests {

	private static DeviceModel deviceModel;
	private static ProcessModel processModel;
	private static PropagationModel pmProp1;
	private static PropagationModel pmProp2;
	private static PropagationModel pmProp3;
	private static PropagationModel dmProp;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		
		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		

		dmProp = deviceModel.getPropagations().iterator().next();
		
		// We need a predictable ordering for testing, so we sort the set
		TreeSet<PropagationModel> procPropSet = new TreeSet<>();
		procPropSet.addAll(processModel.getPropagations());
		Iterator<PropagationModel> iter = procPropSet.iterator();
		pmProp1 = iter.next();
		if(pmProp1 != null){
			pmProp2 = iter.next();
		}
		if(pmProp2 != null){
			pmProp3 = iter.next();
		}
	}
	
	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPropagationExists() {
		assertEquals(1, deviceModel.getPropagations().size());
		assertEquals(3, processModel.getPropagations().size());
	}
	
	@Test
	public void testPropagationDirection() {
		assertTrue(dmProp.isOut());
		assertTrue(pmProp1.isIn());
		assertTrue(pmProp2.isOut());
	}
	
	@Test
	public void testPropagationPort() {
		assertEquals("SpO2Out", dmProp.getPort().getName());
		assertEquals("SpO2", pmProp1.getPort().getName());
		assertEquals("DerivedAlarm", pmProp2.getPort().getName());
	}
	
	@Test
	public void testPropagationErrorTypes() {
		assertEquals(1, dmProp.getErrors().size());
		assertEquals(1, dmProp.getErrors().iterator().next().getTypes().size());
		assertEquals("SpO2ValueHigh", dmProp.getErrors().iterator().next().getTypes().iterator().next().getName());

		assertEquals(1, pmProp1.getErrors().size());
		assertEquals(1, pmProp1.getErrors().iterator().next().getTypes().size());
		assertEquals("SpO2ValueHigh", pmProp1.getErrors().iterator().next().getTypes().iterator().next().getName());
		
		assertEquals(1, pmProp2.getErrors().size());
		assertEquals(1, pmProp2.getErrors().iterator().next().getTypes().size());
		assertEquals("MissedAlarm", pmProp2.getErrors().iterator().next().getTypes().iterator().next().getName());
		
		TreeSet<ErrorTypesModel> p3Set = new TreeSet<>(pmProp3.getErrors());
		Iterator<ErrorTypesModel> p3Iter = p3Set.iterator();		
		
		assertEquals(2, pmProp3.getErrors().size());
		assertEquals("ExtraTypeOne", p3Iter.next().getTypes().iterator().next().getName());
		assertEquals("ExtraTypeTwo", p3Iter.next().getTypes().iterator().next().getName());
		
	}
}
