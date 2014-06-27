package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.Keyword;

public class OccurrenceModel {
	private Keyword keyword;
	private HazardModel hazard;
	private ConstraintModel constraint;
	private String description;
	private String cause;
	private String compensation;
	private ImpactModel impact;
	private String connErrorName;
	private ComponentModel parent;
	private ComponentModel child;
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	
	public HazardModel getHazard() {
		return hazard;
	}
	
	public void setHazard(HazardModel hazard) {
		this.hazard = hazard;
	}
	
	public ConstraintModel getConstraint() {
		return constraint;
	}
	
	public void setConstraint(ConstraintModel constraint) {
		this.constraint = constraint;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCause() {
		return cause;
	}
	
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public String getCompensation() {
		return compensation;
	}
	
	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}
	
	public ImpactModel getImpact() {
		return impact;
	}
	
	public void setImpact(ImpactModel impact) {
		this.impact = impact;
	}
	
	public String getConnErrorName() {
		return connErrorName;
	}
	
	public void setConnErrorName(String connErrorName) {
		this.connErrorName = connErrorName;
	}
	
	public ComponentModel getParent() {
		return parent;
	}
	
	public void setParent(ComponentModel parent) {
		this.parent = parent;
	}
	
	public ComponentModel getChild() {
		return child;
	}
	
	public void setChild(ComponentModel child) {
		this.child = child;
	}
}
