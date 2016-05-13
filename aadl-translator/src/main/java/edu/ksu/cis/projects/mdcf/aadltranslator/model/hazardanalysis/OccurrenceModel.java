package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;

public class OccurrenceModel {
	private HazardModel hazard;
	private ConstraintModel constraint;
	private String title;
	private String cause;
	private String compensation;
//	private ErrorTypeModel errorType;
	private String connErrorName;
	private ComponentModel parent;
	private ComponentModel child;
	
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
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
//	public ErrorTypeModel getErrorType() {
//		return errorType;
//	}
//	
//	public void setErrorType(ErrorTypeModel impact) {
//		this.errorType = impact;
//	}
	
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
