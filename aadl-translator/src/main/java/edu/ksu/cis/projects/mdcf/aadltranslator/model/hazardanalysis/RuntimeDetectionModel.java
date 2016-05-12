package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorDetectionApproach;

public class RuntimeDetectionModel {
	private RuntimeErrorDetectionApproach approach;
	private String explanation;
	private String name;
	
	public RuntimeDetectionModel(RuntimeErrorDetectionApproach approach, String explanation, String name) {
		this.approach = approach;
		this.explanation = explanation;
		this.name = name;
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
