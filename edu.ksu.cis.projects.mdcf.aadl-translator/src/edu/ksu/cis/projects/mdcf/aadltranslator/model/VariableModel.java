package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class VariableModel {
	private String type;
	private String outerName;
	private String innerName;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setOuterName(String outerName) {
		this.outerName = outerName;
	}
	
	public void setInnerName(String innerName) {
		this.innerName = innerName;
	}
	
	public String getType() {
		return type;
	}

	public String getOuterName() {
		return outerName;
	}

	public String getInnerName() {
		return innerName;
	}
}
