package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.OccurrenceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class HazardPreliminariesTests {
	
	private static SystemModel systemModel;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testAccidentLevel(){
		assertEquals(1, systemModel.getAccidentLevels().size());
		assertEquals("AL1", systemModel.getAccidentLevelByName("AL1").getName());
		assertEquals(1, systemModel.getAccidentLevelByName("AL1").getNumber());
	}
	
	@Test
	public void testAccident(){
		assertEquals(1, systemModel.getAccidents().size());
		assertEquals("A1", systemModel.getAccidentByName("A1").getName());
		assertEquals(1, systemModel.getAccidentByName("A1").getNumber());
	}
	
	@Test
	public void testHazard(){
		assertEquals(1, systemModel.getHazards().size());
		assertEquals("H1", systemModel.getHazardByName("H1").getName());
		assertEquals(1, systemModel.getHazardByName("H1").getNumber());
	}
	
	@Test
	public void testConstraint(){
		assertEquals(1, systemModel.getConstraints().size());
		assertEquals("C1", systemModel.getConstraintByName("C1").getName());
		assertEquals(1, systemModel.getConstraintByName("C1").getNumber());
	}
}
