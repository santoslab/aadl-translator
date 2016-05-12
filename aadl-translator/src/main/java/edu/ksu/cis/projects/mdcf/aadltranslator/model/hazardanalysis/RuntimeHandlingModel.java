package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorDetectionApproach;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorHandlingApproach;

public class RuntimeHandlingModel {
	private RuntimeErrorHandlingApproach approach;
	private String explanation;
	private String name;
	
	public RuntimeHandlingModel(String name, String approachStr, String explanation) {
		this.name = name;
		this.explanation = explanation;
		this.approach = RuntimeErrorHandlingApproach.valueOf(approachStr.toUpperCase());
	}

	public RuntimeErrorHandlingApproach getApproach() {
		return approach;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public String getName() {
		return name;
	}
}
