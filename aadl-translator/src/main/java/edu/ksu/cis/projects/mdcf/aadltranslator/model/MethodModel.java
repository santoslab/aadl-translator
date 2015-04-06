package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class MethodModel extends ComponentModel{
	/**
	 * This stores a mapping from param name -> param type
	 */
	private HashMap<String, String> parameters;
	private String name;
	private String retType;

//	/**
//	 * This is the name of the parameter at the task level, so the code
//	 * generator knows what value to set.
//	 */
//	private String retName;

	public MethodModel(String methodName) {
		super();
		this.name = methodName;
		parameters = new HashMap<>();
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

//	public String getRetName() {
//		return retName;
//	}
//
//	public void setRetName(String retName) {
//		this.retName = retName;
//	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void addParameter(String paramName, String type)
			throws DuplicateElementException {
		if (parameters.containsKey(paramName))
			throw new DuplicateElementException("Method " + name
					+ " is trying to add a parameter called " + paramName
					+ " but it already has one by that name.");
		parameters.put(paramName, type);
	}
}
