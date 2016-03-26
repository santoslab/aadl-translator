package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

public abstract class CausedDangerModel {
	
	ErrorTypeModel successorDanger;
	String interp;
	Set<ErrorTypeModel> cooccurringDangers;
	//RuntimeDetectionModel
	//RuntimeHandlingModel
	
	public CausedDangerModel(ErrorTypeModel succDanger, String interp,
			Set<ErrorTypeModel> cooccurringDangers) {
		this.successorDanger = succDanger;
		this.interp = interp;
		this.cooccurringDangers = cooccurringDangers;
	}
}
