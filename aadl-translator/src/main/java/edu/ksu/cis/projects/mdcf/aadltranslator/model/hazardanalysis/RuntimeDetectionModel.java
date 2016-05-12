package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorDetectionApproach;

public class RuntimeDetectionModel {
	private RuntimeErrorDetectionApproach approach;
	private String explanation;
	private String name;
	
	public RuntimeDetectionModel(String approachStr, String explanation, String name) {
		this.explanation = explanation;
		this.name = name;
		this.approach = RuntimeErrorDetectionApproach.valueOf(approachStr.toUpperCase());
	}

	public RuntimeErrorDetectionApproach getApproach() {
		return approach;
	}

	public String getExplanation() {
		return explanation;
	}

	public String getName() {
		return name;
	}
}
