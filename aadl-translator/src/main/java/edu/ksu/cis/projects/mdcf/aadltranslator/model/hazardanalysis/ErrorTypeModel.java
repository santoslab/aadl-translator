package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;

/**
 * This class models a single EMv2 error type.
 * 
 * It implements comparable so that unordered sets can be sorted for testing
 * purposes
 * 
 * @author Sam
 *
 */
public class ErrorTypeModel implements Comparable<ErrorTypeModel>{
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

	@Override
	public int compareTo(ErrorTypeModel o) {
		return name.compareTo(o.getName());
	}
}
