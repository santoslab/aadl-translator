package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

public class ProcessVariableModel {
	private enum typeType {BOOLEAN, CHARACTER, FLOAT, INTEGER, STRING};
	private String units;
	private typeType type;
	private double minVal;
	private double maxVal;
	
	public ProcessVariableModel(String type, String units, String minVal, String maxVal){
		this.type = typeType.valueOf(type.toUpperCase());
		this.units = units;
		this.minVal = Double.valueOf(minVal);
		this.maxVal = Double.valueOf(maxVal);
	}
	
	public ProcessVariableModel(String type, String units){
		this.type = typeType.valueOf(type.toUpperCase());
		this.units = units;
		this.minVal = Double.NaN;
		this.maxVal = Double.NaN;
	}

	public String getUnits() {
		return units;
	}

	public double getMinVal() {
		return minVal;
	}

	public double getMaxVal() {
		return maxVal;
	}
	
	public String getType() {
		return type.toString();
	}
}
