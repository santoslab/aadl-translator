package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.net.URI;

public class WriteOutputFiles {

	public enum OutputFormat {
		HTML, PDF, MARKDOWN
	};

	public static void writeHazardReport(String reportText,
			String reportDirectory, String reportName, OutputFormat fmt,
			String pandocPath, java.net.URI uri) {
		createDir(reportDirectory + "/reports");
		writeStrToFile(reportText, reportDirectory + "/reports/" + reportName + ".md");
		ArrayList<String> args = new ArrayList<>();
		args.add(pandocPath);
		args.add("--from=markdown");
		switch (fmt) {
		case MARKDOWN:
			break;
		case PDF:
			// Disabled for now -- the unsafe control actions table is totally messed up in latex.
			args.add("--to=latex");
			args.add("--output=" + reportDirectory + "/reports/" + reportName + ".pdf");
			break;
		case HTML:
			args.add("--to=html5");
			args.add("--include-in-header=" + uri.getPath());
			args.add("--self-contained");
			args.add("--smart");
			args.add("--output=" + reportDirectory + "/reports/" + reportName + ".html");
			break;
		}
		if(fmt != OutputFormat.MARKDOWN){
			args.add(reportDirectory + "/reports/" + reportName + ".md");
			ProcessBuilder pb = new ProcessBuilder().command(args);
			try {
				pb.start();
			} catch (IOException e) {
				// TODO Handle this
				e.printStackTrace();
			}
		}
	}

	private static void writeStrToFile(String contents, String path) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			fw.write(contents);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createDir(String dirName) {
		File theDir = new File(dirName);

		// TODO: Handle failure / permission problems
		theDir.mkdirs();
	}

	public static void writeFiles(HashMap<String, String> compsigs,
			HashMap<String, String> javaClasses, String appName,
			String appSpecContents, String appDevDirectory) {

		if (appDevDirectory == null) {
			// TODO: Handle this
			System.err.println("Error: AppDev Directory not set");
			return;
		}

		// Make sure we have a trailing slash
		if (appDevDirectory.charAt(appDevDirectory.length() - 1) != '/')
			appDevDirectory += "/";

		// Create the package structure
		appDevDirectory += appName + "/mdcf/app/";

		createDir(appDevDirectory + appName + "/appcfg");
		createDir(appDevDirectory + appName + "/appcomp");

		for (String fileName : javaClasses.keySet()) {
			writeStrToFile(javaClasses.get(fileName), appDevDirectory + appName
					+ "/" + fileName + ".java");
		}

		for (String fileName : compsigs.keySet()) {
			writeStrToFile(compsigs.get(fileName), appDevDirectory + appName
					+ "/appcomp/" + fileName + ".compsig.xml");
		}

		writeStrToFile(appSpecContents, appDevDirectory + appName + "/appcfg/"
				+ appName + ".cfg.xml");

	}

	public static void writeDeviceFiles(String superType, String userAPI,
			String compsigs, String devName, String devDirectory) {

		if (devDirectory == null) {
			System.err.println("Error: Dev Directory not set");
			return;
		}

		// Make sure we have a trailing slash
		if (devDirectory.charAt(devDirectory.length() - 1) != '/')
			devDirectory += "/";

		// Create the package structure
		devDirectory += devName + "/mdcf/device/";

		File dir = new File(devDirectory + devName);

		FileWriter fw = null;

		try {
			// TODO: Handle failure / permission problems
			dir.mkdirs();

			fw = new FileWriter(devDirectory + devName + "/" + devName
					+ ".compsig.xml");
			fw.write(compsigs);
			fw.close();
			
			fw = new FileWriter(devDirectory + devName + "/" + devName + "SuperType.java");
			fw.write(superType);
			fw.close();
			
			fw = new FileWriter(devDirectory + devName + "/" + devName + ".java");
			fw.write(userAPI);
			fw.close();
		} catch (IOException e) {
			// TODO: handle this
			e.printStackTrace();
		}
	}
}
