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
		channel = systemModel.getChannelByName("alarm_to_display");
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
		assertEquals("The SpO2 values from the pulse oximeter are too high, so the alarm is missed", occurrence.getCause());
	}
	
	@Test
	public void testOccurrenceConnectionErrorName() {
		assertEquals("alarm_to_display", occurrence.getConnErrorName());
	}
	
	@Test
	public void testOccurrenceConstraint() {
		assertEquals(systemModel.getConstraintByName("ShowAllAlarms"), occurrence.getConstraint());
	}
	
	@Test
	public void testOccurrenceTitle() {
		assertEquals("Bad SpO2", occurrence.getTitle());
	}
	
	@Test
	public void testOccurrenceHazard() {
		assertEquals(systemModel.getHazardByName("MissedAlarm"), occurrence.getHazard());
	}
	
	@Test
	public void testOccurrenceImpactName() {
		assertEquals("SpO2ValueHigh", occurrence.getImpact().getName());
	}
	
	@Test
	public void testOccurrenceKeyword() {
		assertEquals(Keyword.NOTPROVIDING, occurrence.getKeyword());
	}
}
