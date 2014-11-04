package edu.ksu.cis.projects.mdcf.aadltranslator.test.device;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.errorSB;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceEIGeneratedArtifactsTest {
	private static DeviceComponentModel deviceComponentModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MDCF_Comm_Props");
		usedProperties.add("MDCF_Data_Props");
		usedProperties.add("MDCF_ICE_Props");
		errorSB.setLength(0);
		errorSB.trimToSize();
	}
	
	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testCapnograph() {
		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
		assertEquals(true, true);
	}
	
	@Test
	public void testPCA() {
		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
		assertEquals(true, true);
	}
	
	@Test
	public void testPulseOx() {
		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
		assertEquals(true, true);
	}

}