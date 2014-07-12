package edu.ksu.cis.projects.mdcf.aadltranslator.test.device;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.errorSB;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class DeviceEIAADLSystemErrorTest {
	private static DeviceComponentModel deviceComponentModel;
	
	@Before
	public void setUp() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MDCF_Comm_Props");
		usedProperties.add("MDCF_Data_Props");
		usedProperties.add("MDCF_ICE_Props");
		errorSB.setLength(0);
		errorSB.trimToSize();

	}

	@After
	public void tearDown() {
		usedProperties.clear();
	}
	
	@Test
	public void testMissingSystemImplementation(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Missing", "Device_AADL_Test_System_Impl_Missing");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing System Implementation"));
	}
	
	@Test
	public void testMismatchingNameBetweenSystemAndPackage(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_File_Name_Mismatch", "Device_AADL_Test_System_File_Name_Mismatch");
		assertEquals(true, AllTests.errorSB.toString().contains("Mismatching name between the system and the package:"));
	}
	

	@Test
	public void testMissingAbstractDevice(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_Abstract_Device_Missing", "Device_AADL_Test_Abstract_Device_Missing");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing VMD type definition:"));
	}

}
