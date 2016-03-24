package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;

/**
 * This class models a single EMv2 error type.
 * 
 * @author Sam
 *
 */
public class ErrorTypeModel {
	private String name;
	private ManifestationType manifestation;

	public ErrorTypeModel(String name, ErrorType parentType) {
		this.name = name;
	}

	public void setManifestation(ManifestationType manifestation) {
		this.manifestation = manifestation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getManifestationName() {
		return manifestation.toString();
	}
}
