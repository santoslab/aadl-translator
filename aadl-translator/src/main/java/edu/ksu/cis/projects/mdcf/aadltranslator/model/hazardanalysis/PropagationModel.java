package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

/**
 * This class models EMv2 propagations.
 * 
 * @author Sam
 *
 */
public class PropagationModel{
	private ErrorTypeModel error;

	public PropagationModel(ErrorTypeModel error) {
		this.error = error;
	}

	public ErrorTypeModel getError() {
		return error;
	}

	public void setError(ErrorTypeModel errors) {
		this.error = errors;
	}	
}
