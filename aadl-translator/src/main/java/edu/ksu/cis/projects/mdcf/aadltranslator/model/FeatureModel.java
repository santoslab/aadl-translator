package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;

public class FeatureModel {

	private String name;
	protected boolean subscribe;

	/**
	 * Error types entering or leaving this port
	 */
	private Map<String, ManifestationTypeModel> propagatableErrors = new LinkedHashMap<>();

	/**
	 * The actual propagations that describe how the incoming or outgoing errors
	 * are grouped
	 */
	private Map<String, PropagationModel> propagations = new LinkedHashMap<>();

	public FeatureModel() {
		super();
	}

	public String getName() {
		return name;
	}

	public boolean isSubscribe() {
		return subscribe;
	}

	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}

	public void setName(String portName) {
		this.name = portName;
	}

	public void addPropagatableErrors(Collection<ManifestationTypeModel> propagations) {
		for (ManifestationTypeModel errType : propagations) {
			propagatableErrors.put(errType.getName(), errType);
		}
	}
	
	public Map<String, ManifestationTypeModel> getPropagatableErrors() {
		return propagatableErrors;
	}

	public void addPropagation(PropagationModel propModel) {
		propagations.put(propModel.getName(), propModel);
	}

	public ManifestationTypeModel getPropagatableErrorByName(String name) {
		return propagatableErrors.get(name);
	}
	
	public PropagationModel getPropagationByName(String name){
		return propagations.get(name);
	}

}