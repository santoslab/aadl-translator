package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

public class DeviceModel implements IComponentModel{
	/**
	 * The name that the app developer uses to refer to this device
	 */
	private String name;
	
	/**
	 * The type of this device
	 */
	private String type;
	
	private HashMap<String, PortModel> ports;
	
	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}
}
