package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;

/**
 * This class models a single manifestation.
 * 
 * @author Sam
 *
 */
public class ManifestationTypeModel {
	private String name;
	private ManifestationType manifestation;
	
	public ManifestationTypeModel(String name, ErrorType parentType) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public void setManifestation(ManifestationType manifestation) {
		this.manifestation = manifestation;
	}

	public String getManifestationName() {
		return manifestation.toString();
	}
}
