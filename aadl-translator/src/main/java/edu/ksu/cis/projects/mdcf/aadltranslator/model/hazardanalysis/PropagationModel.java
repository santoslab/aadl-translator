package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;

/**
 * This class models EMv2 propagations.
 * 
 * @author Sam
 *
 */
public class PropagationModel{
	private ErrorTypeModel error;
	private PortModel port;

	public PropagationModel(ErrorTypeModel error, PortModel portModel) {
		this.error = error;
		this.port = portModel;
	}

	public ErrorTypeModel getError() {
		return error;
	}

	public void setError(ErrorTypeModel errors) {
		this.error = errors;
	}

	public PortModel getPort() {
		return port;
	}
}
