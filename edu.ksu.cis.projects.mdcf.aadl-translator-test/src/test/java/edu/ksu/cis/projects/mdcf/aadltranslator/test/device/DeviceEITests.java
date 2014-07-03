package edu.ksu.cis.projects.mdcf.aadltranslator.test.device;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceEITests {
	private static DeviceComponentModel deviceComponentModel;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MDCF_Comm_Props");
		usedProperties.add("MDCF_Data_Props");
		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "Capnograph");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testRunCheck(){
		assertEquals(true, true);
	}
	
}
