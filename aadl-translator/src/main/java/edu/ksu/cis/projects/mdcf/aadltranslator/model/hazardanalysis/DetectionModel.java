package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.DetectionApproach;

public abstract class DetectionModel {

	protected DetectionApproach approach;
	protected String explanation;
	protected String name;

	public DetectionModel(String explanation, String name) {
		this.explanation = explanation;
		this.name = name;
	}

	public String getExplanation() {
		return explanation;
	}

	public String getName() {
		return name;
	}
	
	public abstract DetectionApproach getApproach(); 

	public String getApproachStr() {
		return approach.toString();
	}

}