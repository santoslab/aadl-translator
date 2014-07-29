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

	@Test
	public void testAbstractDeviceIllegalPortName(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_Abstract_Device_Illegal_Port_Name", "Device_AADL_Test_Abstract_Device_Illegal_Port_Name");
		assertEquals(true, AllTests.errorSB.toString().contains("Unknown Communication Pattern:"));
	}
	@Test
	public void testAbstractDeviceMissingPorts(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_Abstract_Device_Missing_Ports", "Device_AADL_Test_Abstract_Device_Missing_Ports");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing Matching Port Pair:"));
	}
	@Test
	public void testAbstractDeviceMissingDataTypeResponder(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_Abstract_Device_Missing_Data_Type_Responder", "Device_AADL_Test_Abstract_Device_Missing_Data_Type_Responder");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing Data Type for the Port:"));
	}
	@Test
	public void testSystemImplementationPorpertyMissingCredential(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Property_Missing_Credentials", "Device_AADL_Test_System_Impl_Property_Missing_Credentials");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing Credential:"));
	}
	@Test
	public void testSystemImplementationManufacturerName(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Property_Missing_Manufacturer", "Device_AADL_Test_System_Impl_Property_Missing_Manufacturer");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing Manufacturer Name in Manufacturer Model:"));
	}
	@Test
	public void testSystemImplementationManufactureModel(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Property_Missing_ManufacturerModel", "Device_AADL_Test_System_Impl_Property_Missing_ManufacturerModel");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing ManufacturerModel Property:"));
	}
	@Test
	public void testSystemImplementationModelNumber(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Property_Missing_ModelNumber", "Device_AADL_Test_System_Impl_Property_Missing_ModelNumber");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing Model Number in Manufacturer Model:"));
	}
	@Test
	public void testSystemImplementationMissingSystemType(){
		deviceComponentModel = AllTests.runDeviceTransTest("Device_AADL_Test_System_Impl_Property_Missing_SYSTYPE", "Device_AADL_Test_System_Impl_Property_Missing_SYSTYPE");
		assertEquals(true, AllTests.errorSB.toString().contains("Missing System Type Property:"));
	}
	
}
