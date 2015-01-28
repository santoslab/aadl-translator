package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class StpaPreliminaryModel {
	protected String name;
	protected String description;
	protected StpaPreliminaryModel parent;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public StpaPreliminaryModel getParent() {
		return parent;
	}
	
	public void setParent(StpaPreliminaryModel parent) {
		this.parent = parent;
	}
}
