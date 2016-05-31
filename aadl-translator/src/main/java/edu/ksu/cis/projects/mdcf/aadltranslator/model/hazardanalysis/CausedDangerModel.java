package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CausedDangerModel {

	private PropagationModel successorDanger;
	private String interp;
	private String name; // The name should be set by the concrete extensions of this class
	private Set<RuntimeDetectionModel> runtimeDetections = new LinkedHashSet<>();
	private Set<RuntimeHandlingModel> runtimeHandlings = new LinkedHashSet<>();
	
	public CausedDangerModel(PropagationModel succDanger, String interp) {
		this.successorDanger = succDanger;
		this.interp = interp;
	}
	
	public String getName(){
		return name;
	}
	
	public PropagationModel getSuccessorDanger() {
		return successorDanger;
	}

	public String getInterp() {
		return interp;
	}

	public Set<RuntimeDetectionModel> getRuntimeDetections() {
		return runtimeDetections;
	}

	public void addRuntimeDetection(RuntimeDetectionModel runtimeDetection) {
		this.runtimeDetections.add(runtimeDetection);
	}

	public Set<RuntimeHandlingModel> getRuntimeHandlings() {
		return runtimeHandlings;
	}

	public void addRuntimeHandling(RuntimeHandlingModel runtimeHandling) {
		this.runtimeHandlings.add(runtimeHandling);
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
