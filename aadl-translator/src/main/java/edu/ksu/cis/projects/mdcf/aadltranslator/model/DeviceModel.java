package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class DeviceModel implements IComponentModel {

	/**
	 * The type name of this device
	 */
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
