package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

/**
 * This class is used to model faults and errors that have been considered but are deemed to be safe.
 * q 
 * @author sam
 *
 */
public class NotDangerousDangerModel extends CausedDangerModel {

	public NotDangerousDangerModel(PropagationModel succDanger, String interp, Set<ErrorTypeModel> cooccurringDangers) {
		super(succDanger, interp, cooccurringDangers);
		// TODO Auto-generated constructor stub
	}

}
