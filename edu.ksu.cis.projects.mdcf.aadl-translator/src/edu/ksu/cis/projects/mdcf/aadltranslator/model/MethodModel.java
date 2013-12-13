package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

public class MethodModel {
	/**
	 * This stores a mapping from param name -> param type
	 */
	private HashMap<String, String> parameters;
	private String methodName;
	private String retType;

	/**
	 * This is the name of the parameter at the task level, so the code
	 * generator knows what value to set.
	 */
	private String retName;
}
