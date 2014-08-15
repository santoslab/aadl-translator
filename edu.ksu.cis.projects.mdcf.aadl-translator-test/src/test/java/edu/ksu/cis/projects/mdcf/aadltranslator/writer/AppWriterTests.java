package edu.ksu.cis.projects.mdcf.aadltranslator.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class AppWriterTests {

	public final static String MAIN_PLUGIN_BUNDLE_ID = "edu.ksu.cis.projects.mdcf.aadl-translator";
	public final static String TEMPLATE_DIR = "src/main/resources/templates/";

	public final static String TEST_PLUGIN_BUNDLE_ID = "edu.ksu.cis.projects.mdcf.aadl-translator-test";
	public final static String TEST_DIR = "src/test/resources/edu/ksu/cis/projects/mdcf/aadltranslator/test/";
	
	public static SystemModel systemModel;
	public static STGroup appSuperClassSTG;
	
	@BeforeClass
	public static void initialize() {
		
		URL appSuperClassStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID).getEntry(TEMPLATE_DIR + "java-superclass.stg");
		appSuperClassSTG = new STGroupFile(appSuperClassStgUrl.getFile());
		URL systemModelXmlUrl = Platform.getBundle(TEST_PLUGIN_BUNDLE_ID).getEntry(TEST_DIR + "model/SystemModel.serial");
		ObjectInputStream in;
		try { //new File(FileLocator.toFileURL(aadlDirUrl).getPath())
			in = new ObjectInputStream(new FileInputStream(new File(FileLocator.toFileURL(systemModelXmlUrl).getPath())));
			systemModel = (SystemModel) in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHelloWorld() {
		System.out.println(appSuperClassSTG.getInstanceOf("class").add("model", systemModel).render());
	}
	
}
