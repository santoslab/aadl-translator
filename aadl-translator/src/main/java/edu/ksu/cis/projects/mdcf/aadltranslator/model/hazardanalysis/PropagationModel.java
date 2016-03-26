package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Collection;
import java.util.LinkedHashSet;
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
	private Set<ErrorTypeModel> errors;
	private PortModel port;

	public PropagationModel(String name, Collection<ErrorTypeModel> errors, PortModel portModel) {
		this.name = name;
		this.errors = new LinkedHashSet<>(errors);
		this.port = portModel;
	}

	public Set<ErrorTypeModel> getErrors() {
		return errors;
	}

	public void setError(Set<ErrorTypeModel> errors) {
		this.errors = new LinkedHashSet<>(errors);
	}

	public PortModel getPort() {
		return port;
	}

	public String getName() {
		return name;
	}
}
