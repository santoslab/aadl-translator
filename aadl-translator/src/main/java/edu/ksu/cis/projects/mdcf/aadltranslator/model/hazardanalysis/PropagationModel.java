package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;

/**
 * This class models EMv2 propagations.
 * 
 * @author Sam
 *
 */
public class PropagationModel{
	private String name;
	private Map<String, ErrorTypeModel> errors;
	private PortModel port;

	public PropagationModel(String name, Collection<ErrorTypeModel> errors, PortModel portModel) {
		this.name = name;
		setError(errors);
		this.port = portModel;
	}

	public Set<ErrorTypeModel> getErrors() {
		return new LinkedHashSet<>(errors.values());
	}

	public void setError(Collection<ErrorTypeModel> errors) {
		this.errors = new LinkedHashMap<>();
		for(ErrorTypeModel error : errors){
			this.errors.put(error.getName(), error);
		}
	}
	
	public ErrorTypeModel getErrorByName(String name){
		return errors.get(name);
	}

	public PortModel getPort() {
		return port;
	}

	public String getName() {
		return name;
	}
}
