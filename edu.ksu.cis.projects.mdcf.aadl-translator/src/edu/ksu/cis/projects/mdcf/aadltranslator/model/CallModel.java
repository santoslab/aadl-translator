package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

public class CallModel {
	private String internalName;
	private String externalName;

	/**
	 * Maps the formal parameter (that is, the method's name for the parameter)
	 * to the actual parameter (the task's name for the value that will be
	 * supplied to the method)
	 */
	private HashMap<String, String> parameters;

	public CallModel(String internalName, String externalName) {
		this.internalName = internalName;
		this.externalName = externalName;
		parameters = new HashMap<>();
	}

	public void addParam(String formal, String actual) {
		parameters.put(formal, actual);
	}

	public String getInternalName() {
		return internalName;
	}

	public String getExternalName() {
		return externalName;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}
}
