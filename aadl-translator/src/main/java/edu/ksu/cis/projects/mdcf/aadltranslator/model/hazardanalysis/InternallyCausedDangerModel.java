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
	private Set<DesignTimeDetectionModel> designTimeDetections = new LinkedHashSet<>();

	public InternallyCausedDangerModel(PropagationModel succDanger, String interp) {
		super(succDanger, interp);
		super.setName(succDanger.getName());
	}
	
	public void addFaultClass(String etm) {
		faultClasses.add(etm);
	}

	public Collection<String> getFaultClasses() {
		return faultClasses;
	}

	public void addDesignTimeDetection(DesignTimeDetectionModel designTimeDetection) {
		this.designTimeDetections.add(designTimeDetection);
	}

	public Set<DesignTimeDetectionModel> getDesignTimeDetections() {
		return designTimeDetections;
	}
}
