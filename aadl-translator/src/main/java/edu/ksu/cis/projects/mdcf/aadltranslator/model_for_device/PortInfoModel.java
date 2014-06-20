package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class PortInfoModel {

	public static enum PortDirection {Out, In};
	
	private PortDirection inOut;
	private String portName;
	private PortProperty portProperty;
	
	public PortInfoModel(PortDirection inOut, String portName) {
		super();
		this.inOut = inOut;
		this.portName = portName;
	}
	
	public PortDirection getInOut() {
		return inOut;
	}
	public void setInOut(PortDirection inOut) {
		this.inOut = inOut;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getPortProperty(String propertyName) {
		return this.portProperty.getProperty(propertyName);
	}
	public void setPortProperty(String propertyName, String propertyValue) {
		this.portProperty.setProperty(propertyName, propertyValue);
	}
}
