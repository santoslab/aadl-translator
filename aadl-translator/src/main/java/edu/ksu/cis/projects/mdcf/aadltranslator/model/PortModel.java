package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class PortModel {
	private String name;
	private boolean subscribe;
	private String type;
	private int minPeriod;
	private int maxPeriod;

	public String getName() {
		return name;
	}
	
	public boolean isSubscribe() {
		return subscribe;
	}
	
	public String getType() {
		return type;
	}
	
	public int getMinPeriod() {
		return minPeriod;
	}
	
	public int getMaxPeriod() {
		return maxPeriod;
	}
	
	public void setName(String portName) {
		this.name = portName;
	}

	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setMinPeriod(int minPeriod) {
		this.minPeriod = minPeriod;
	}

	public void setMaxPeriod(int maxPeriod) {
		this.maxPeriod = maxPeriod;
	}
}
