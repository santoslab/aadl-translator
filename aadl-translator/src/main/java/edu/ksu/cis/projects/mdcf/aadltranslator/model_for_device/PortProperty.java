package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import java.util.HashMap;

public class PortProperty {
	private HashMap<String, String> propertyMap;
	
	public PortProperty(){
		this.propertyMap = new HashMap<String, String>();
	}
	
	public void setProperty(String propertyName, String value){
		this.propertyMap.put(propertyName, value);
	}
	
	public String getProperty(String propertyName){
		return this.propertyMap.get(propertyName);
	}
}
