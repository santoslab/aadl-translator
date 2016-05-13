package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is used to model faults that occur due to some internal failure.
 *  
 * @author Sam
 *
 */
public class InternallyCausedDangerModel extends CausedDangerModel {
	
	/**
	 * Fault class name -> Fault Class Model
	 */
	private Set<String> faultClasses = new LinkedHashSet<>();

	public InternallyCausedDangerModel(PropagationModel succDanger, String interp,
			Set<ManifestationTypeModel> cooccurringDangers) {
		super(succDanger, interp, cooccurringDangers);
		super.setName(succDanger.getName());
	}
	
	public void addFaultClass(String etm) {
		faultClasses.add(etm);
	}

	public Collection<String> getFaultClasses() {
		return faultClasses;
	}
}
