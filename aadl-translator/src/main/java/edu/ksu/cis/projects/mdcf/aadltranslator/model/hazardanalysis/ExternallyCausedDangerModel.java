package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

public class ExternallyCausedDangerModel extends CausedDangerModel {
	
	ErrorTypeModel manifestation;
	
	public ExternallyCausedDangerModel(ErrorTypeModel succDanger, ErrorTypeModel name, String interp, Set<ErrorTypeModel> cooccurringDangers) {
		super(succDanger, interp, cooccurringDangers);
		this.manifestation = name;
	}

}
