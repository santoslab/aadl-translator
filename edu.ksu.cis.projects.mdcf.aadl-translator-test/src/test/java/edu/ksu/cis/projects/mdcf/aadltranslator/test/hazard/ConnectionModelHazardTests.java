package edu.ksu.cis.projects.mdcf.aadltranslator.test.hazard;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.Keyword;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.OccurrenceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class ConnectionModelHazardTests {

	private static OccurrenceModel occurrence;
	private static ConnectionModel channel;
	private static SystemModel systemModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		channel = systemModel.getChannelByName("spo2_to_logic");
		occurrence = channel.getOccurrences().iterator().next();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testOccurrenceExist(){
		assertNotNull(occurrence);
	}
	
	@Test
	public void testOccurrenceCause() {
		assertEquals("Incorrect values are gathered from the physiological sensors", occurrence.getCause());
	}
	
	@Test
	public void testOccurrenceConnectionErrorName() {
		assertEquals("spo2_high_err", occurrence.getConnErrorName());
	}
	
	@Test
	public void testOccurrenceConstraint() {
		assertEquals(systemModel.getConstraintByName("C1"), occurrence.getConstraint());
	}
	
	@Test
	public void testOccurrenceDescription() {
		assertEquals("Wrong values (Undetected)", occurrence.getDescription());
	}
	
	@Test
	public void testOccurrenceHazard() {
		assertEquals(systemModel.getHazardByName("H1"), occurrence.getHazard());
	}
	
	@Test
	public void testOccurrenceImpactName() {
		assertEquals("SpO2ValueHigh", occurrence.getImpact().getName());
	}
	
	@Test
	public void testOccurrenceKeyword() {
		assertEquals(Keyword.PROVIDING, occurrence.getKeyword());
	}
}
