package edu.ksu.cis.projects.mdcf.aadltranslator.writer;

import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.initComplete;
import static edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests.usedProperties;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.test.AllTests;

public class AppWriterTests {

	public final static String MAIN_PLUGIN_BUNDLE_ID = "edu.ksu.cis.projects.mdcf.aadl-translator";
	public final static String TEMPLATE_DIR = "src/main/resources/templates/";

	public static SystemModel systemModel;
	public static ProcessModel processModel;
	public static STGroup appSuperClassSTG;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		
		URL appSuperClassStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID).getEntry(TEMPLATE_DIR + "java-superclass.stg");
		appSuperClassSTG = new STGroupFile(appSuperClassStgUrl.getFile());
		
	}
	
	@Test
	public void testWholeProcess() {
		System.out.println(appSuperClassSTG.getInstanceOf("class").add("model", processModel).render());
	}
	
}
