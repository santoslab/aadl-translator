package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.ArrayList;
import java.util.List;

public class StpaPreliminaryModel {
	protected String name;
	protected String description;
	protected StpaPreliminaryModel parent;
	protected List<String> explanations;
	
	public StpaPreliminaryModel(){
		explanations = new ArrayList<>();
	}
	
	public void addExplanation(String explanation){
		explanations.add(explanation);
	}
	
	public List<String> getExplanations(){
		return explanations;
	}
	
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
