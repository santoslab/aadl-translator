package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class MethodModel {
	/**
	 * This stores a mapping from param name -> param type
	 */
	private HashMap<String, String> parameters;
	private String methodName;
	private String retType;

//	/**
//	 * This is the name of the parameter at the task level, so the code
//	 * generator knows what value to set.
//	 */
//	private String retName;

	public MethodModel(String methodName) {
		this.methodName = methodName;
		parameters = new HashMap<>();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
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

	public void addParameter(String name, String type)
			throws DuplicateElementException {
		if (parameters.containsKey(name))
			throw new DuplicateElementException("Method " + methodName
					+ " is trying to add a parameter called " + name
					+ " but it already has one by that name.");
		parameters.put(name, type);
	}
}
