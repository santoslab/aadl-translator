package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

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

	public ErrorTypeModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(ErrorTypeModel o) {
		return name.compareTo(o.getName());
	}
}
