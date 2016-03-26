package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

public class ExternallyCausedDangerModel extends CausedDangerModel {
	
	PropagationModel danger;
	
	public ExternallyCausedDangerModel(PropagationModel succDanger, PropagationModel manifestation, String interp, Set<ErrorTypeModel> cooccurringDangers) {
		super(succDanger, interp, cooccurringDangers);
		this.danger = manifestation;
	}

	public PropagationModel getDanger() {
		return danger;
	}

}
