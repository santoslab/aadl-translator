package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public abstract class ExchangeModel {
	public static enum ExchangeKind {GET, SET, ACTION, PERIODIC, SPORADIC};
	
	private String deviceType;
	
	private String exchangeName;

	protected PortInfoModel outPortInfo;
	protected PortInfoModel inPortInfo;
	
	public ExchangeModel(String deviceType, String exchangeName,
			ExchangeKind exchangekind) {
		super();
		this.deviceType = deviceType;
		this.exchangeName = exchangeName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	
	public PortInfoModel getOutPortInfo() {
		return null;
	}

	public void setOutPortInfo(PortInfoModel outPortInfo) {
	}

	public PortInfoModel getInPortInfo() {
		return null;
	}

	public void setInPortInfo(PortInfoModel inPortInfo) {
	}

}
