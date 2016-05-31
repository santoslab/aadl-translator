package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

/**
 * This class is used to model faults and errors that have been considered but are deemed to be safe.
 *  
 * @author Sam
 *
 */
public class NotDangerousDangerModel extends CausedDangerModel {

	public NotDangerousDangerModel(PropagationModel succDanger, String interp) {
		super(succDanger, interp);
		super.setName(succDanger.getName());
	}	
}
