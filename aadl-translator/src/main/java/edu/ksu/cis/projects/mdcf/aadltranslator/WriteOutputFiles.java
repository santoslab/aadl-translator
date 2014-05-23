package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class WriteOutputFiles {

	public static void writeFiles(HashMap<String, String> compsigs,
			HashMap<String, String> javaClasses, String appName,
			String appSpecContents, String appDevDirectory) {
		
		if (appDevDirectory == null){
			//TODO: Handle this
			System.err.println("Error: AppDev Directory not set");
			return;
		}
		
		// Make sure we have a trailing slash
		if(appDevDirectory.charAt(appDevDirectory.length() - 1) != '/')
			appDevDirectory += "/";
		
		// Create the package structure
		appDevDirectory += appName + "/mdcf/app/"; 
		
		File cfgDir = new File(appDevDirectory + appName + "/appcfg");
		File compDir = new File(appDevDirectory + appName + "/appcomp");

		FileWriter fw = null;

		try {
			// TODO: Handle failure / permission problems
			cfgDir.mkdirs();
			compDir.mkdirs();			
			
			for (String fileName : javaClasses.keySet()) {
				fw = new FileWriter(appDevDirectory + appName + "/" + fileName
						+ ".java");
				fw.write(javaClasses.get(fileName));
				fw.close();
			}

			for (String fileName : compsigs.keySet()) {
				fw = new FileWriter(appDevDirectory + appName + "/appcomp/"
						+ fileName + ".compsig.xml");
				fw.write(compsigs.get(fileName));
				fw.close();
			}

			fw = new FileWriter(appDevDirectory + appName + "/appcfg/" + appName
					+ ".cfg.xml");
			fw.write(appSpecContents);
			fw.close();
		} catch (IOException e) {
			// TODO: handle this
			e.printStackTrace();
		}
	}
}
