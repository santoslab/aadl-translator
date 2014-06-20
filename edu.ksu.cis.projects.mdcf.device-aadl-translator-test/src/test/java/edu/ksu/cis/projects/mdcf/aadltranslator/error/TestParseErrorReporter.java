package edu.ksu.cis.projects.mdcf.aadltranslator.error;

import java.util.ArrayList;

import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.parsesupport.LocationReference;

public class TestParseErrorReporter implements ParseErrorReporter {

	private ArrayList<String> errors = new ArrayList<>();

	@Override
	public void deleteMessages() {
		errors.clear();
	}

	@Override
	public int getNumErrors() {
		return errors.size();
	}

	@Override
	public int getNumWarnings() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumInfos() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumMessages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void error(LocationReference loc, String message) {
		errors.add("Error at ");
		errors.add(loc.getFilename());
		errors.add(":");
		errors.add(String.valueOf(loc.getLine()));
		errors.add(": ");
		errors.add(message);
		errors.add("\n");
	}

	@Override
	public void error(String filename, int line, String message) {
		errors.add("Error at ");
		errors.add(filename);
		errors.add(":");
		errors.add(String.valueOf(line));
		errors.add(": ");
		errors.add(message);
		errors.add("\n");
	}

	@Override
	public void warning(LocationReference loc, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warning(String filename, int line, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void info(LocationReference loc, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void info(String filename, int line, String message) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String error : errors){
			sb.append(error);
		}
		return sb.toString();
	}
}
