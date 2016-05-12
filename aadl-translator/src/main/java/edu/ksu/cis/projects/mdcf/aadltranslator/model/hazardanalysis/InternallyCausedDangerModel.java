package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
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
	private Map<String, ErrorTypeModel> faultClasses = new LinkedHashMap<>();

	public InternallyCausedDangerModel(PropagationModel succDanger, String interp,
			Set<ManifestationTypeModel> cooccurringDangers) {
		super(succDanger, interp, cooccurringDangers);
		this.name = succDanger.getName();
	}
	
	public void addFaultClass(ErrorTypeModel etm) {
		faultClasses.put(etm.getName(), etm);
	}

	public Collection<ErrorTypeModel> getFaultClasses() {
		return faultClasses.values();
	}
}
