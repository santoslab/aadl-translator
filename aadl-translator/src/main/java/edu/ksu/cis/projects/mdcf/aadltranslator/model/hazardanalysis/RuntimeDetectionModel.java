package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.RuntimeErrorDetectionApproach;

public class RuntimeDetectionModel extends DetectionModel {
	
	public RuntimeDetectionModel(String explanation, String name, String approachStr) {
		super(explanation, name);
		this.approach = RuntimeErrorDetectionApproach.valueOf(approachStr.toUpperCase());
	}

	@Override
	public RuntimeErrorDetectionApproach getApproach() {
		return (RuntimeErrorDetectionApproach) approach;
	}

}
