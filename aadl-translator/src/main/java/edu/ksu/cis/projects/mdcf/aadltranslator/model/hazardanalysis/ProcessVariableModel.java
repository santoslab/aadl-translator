package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

public class ProcessVariableModel {
	private enum typeType {BOOLEAN, CHARACTER, FLOAT, INTEGER, STRING};
	private String units;
	private typeType type;
	private double minVal;
	private double maxVal;
	private String name;
	
	public ProcessVariableModel(String type, String units, String name, String minVal, String maxVal){
		this.type = typeType.valueOf(type.toUpperCase());
		this.units = units;
		this.name = name;
		this.minVal = Double.valueOf(minVal);
		this.maxVal = Double.valueOf(maxVal);
	}
	
	public ProcessVariableModel(String type, String units, String name){
		this.type = typeType.valueOf(type.toUpperCase());
		this.units = units;
		this.name = name;
		this.minVal = Double.NaN;
		this.maxVal = Double.NaN;
	}
	
	public boolean isNumeric() {
		if(type == typeType.FLOAT || type == typeType.INTEGER){
			return true;
		}
		return false;
	}

	public String getUnits() {
		return units;
	}

	public String getMinVal() {
		if(type == typeType.FLOAT){
			return String.valueOf(minVal);
		} else if (type == typeType.INTEGER){
			return String.valueOf((int) minVal);
		} else {
			return null;
		}
	}

	public String getMaxVal() {
		if(type == typeType.FLOAT){
			return String.valueOf(maxVal);
		} else if (type == typeType.INTEGER){
			return String.valueOf((int) maxVal);
		} else {
			return null;
		}
	}
	
	public String getType() {
		return type.toString();
	}

	public String getName() {
		return name;
	}
}
