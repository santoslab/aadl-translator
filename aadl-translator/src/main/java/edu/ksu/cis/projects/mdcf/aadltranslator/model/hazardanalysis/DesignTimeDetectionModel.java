package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.DesignTimeFaultDetectionApproach;

public class DesignTimeDetectionModel extends DetectionModel {

	public DesignTimeDetectionModel(String explanation, String name, String approachStr) {
		super(explanation, name);
		this.approach = DesignTimeFaultDetectionApproach.valueOf(approachStr.toUpperCase());
	}

	public DesignTimeFaultDetectionApproach getApproach(){
		return (DesignTimeFaultDetectionApproach) approach;
	}
}
