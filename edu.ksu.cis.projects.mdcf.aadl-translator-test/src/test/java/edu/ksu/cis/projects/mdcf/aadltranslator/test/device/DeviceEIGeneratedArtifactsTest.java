package edu.ksu.cis.projects.mdcf.aadltranslator.test.device;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.errorSB;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.DoTranslationOfflineTest;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceEIGeneratedArtifactsTest {
	private static DeviceComponentModel deviceComponentModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("ICE_Types");
		usedProperties.add("MDCF_Types");

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
	
//	@Test
//	public void testCapnographSuperType() {
//		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
//		DoTranslationOfflineTest doTranslationTest = new DoTranslationOfflineTest();
//		System.err.println(doTranslationTest.buildDeviceSuperType(deviceComponentModel));
//		assertTrue(true);
//	}
//	
//	@Test
//	public void testCapnographSuperImplAPI() {
//		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
//		DoTranslationOfflineTest doTranslationTest = new DoTranslationOfflineTest();
//		System.err.println(doTranslationTest.buildDeviceUserImpleAPI(deviceComponentModel));
//		assertTrue(true);
//	}
//	
//	@Test
//	public void testCapnographCompSig() {
//		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
//		DoTranslationOfflineTest doTranslationTest = new DoTranslationOfflineTest();
//		System.err.println(doTranslationTest.buildDeviceCompSig(deviceComponentModel));
//		assertTrue(true);
//	}
//	
//	@Test
//	public void testPCA() {
//		deviceComponentModel = AllTests.runDeviceTransTest("SimpleCapnograph", "SimpleCapnograph");
//		assertTrue(true);
//	}
	
	@Test
	public void testPulseOx() {
		deviceComponentModel = AllTests.runDeviceTransTest("APulseOx", "APulseOx");
		DoTranslationOfflineTest doTranslationTest = new DoTranslationOfflineTest();
		System.err.println(doTranslationTest.buildDeviceSuperType(deviceComponentModel));
		assertTrue(true);
	}
}
