package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

public class DeviceModel implements IComponentModel {

	/**
	 * The type name of this device
	 */
	private String name;

	private HashMap<String, PortModel> ports;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
