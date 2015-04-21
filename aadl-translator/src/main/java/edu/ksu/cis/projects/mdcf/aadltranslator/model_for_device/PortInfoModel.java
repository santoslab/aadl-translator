package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class PortInfoModel {

	public static enum PortDirection {Out, In};
	
	private PortDirection inOut;
	private String portName;
	
	private String messageType;
	private String messageTypeDefault;
	private int minSeparationInterval;
	private int maxSeparationInterval;
	private int separationInterval;
	
	private String port_model_path;
	
	public PortInfoModel(PortDirection inOut, String portName, String port_model_path) {
		super();
		this.inOut = inOut;
		this.portName = portName;
		this.port_model_path = port_model_path;
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

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
		this.messageTypeDefault = PortDataTypeMap.getJavaTypeDefaultValueString(messageType);
	}

	/**
	 * @return the messageTypeDefault
	 */
	public String getMessageTypeDefault() {
		return messageTypeDefault;
	}

	/**
	 * @return the minSeparationInterval
	 */
	public int getMinSeparationInterval() {
		return minSeparationInterval;
	}

	/**
	 * @param minSeparationInterval the minSeparationInterval to set
	 */
	public void setMinSeparationInterval(int minSeparationInterval) {
		this.minSeparationInterval = minSeparationInterval;
	}

	/**
	 * @return the maxSeparationInterval
	 */
	public int getMaxSeparationInterval() {
		return maxSeparationInterval;
	}

	/**
	 * @param maxSeparationInterval the maxSeparationInterval to set
	 */
	public void setMaxSeparationInterval(int maxSeparationInterval) {
		this.maxSeparationInterval = maxSeparationInterval;
	}

	/**
	 * @return the separationInterval
	 */
	public int getSeparationInterval() {
		return separationInterval;
	}

	/**
	 * @param separationInterval the separationInterval to set
	 */
	public void setSeparationInterval(int separationInterval) {
		this.separationInterval = separationInterval;
	}
	
	public String getPortModelPath(){
		return this.port_model_path;
	}

	public String printPortProperties() {
		return "";
	}
}
