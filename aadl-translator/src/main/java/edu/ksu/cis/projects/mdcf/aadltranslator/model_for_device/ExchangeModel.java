package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public abstract class ExchangeModel {
	public static enum ExchangeKind {GET, SET, ACTION, PERIODIC, SPORADIC};
	
	private String deviceType;
	
	private String vmdType;
	
	private String exchangeName;

	protected PortInfoModel outPortInfo;
	protected PortInfoModel inPortInfo;
	
	public ExchangeModel(String deviceType, String vmdType, String exchangeName,
			ExchangeKind exchangekind) {
		super();
		this.deviceType = deviceType;
		this.vmdType = vmdType;
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

	public String getVmdType() {
		return vmdType;
	}

	public void setVmdType(String vmdType) {
		this.vmdType = vmdType;
	}

	public String toString(){
		String outPortInfo = null;
		String inPortInfo = null;
		if(this.getInPortInfo() != null)
			inPortInfo = "  Input Port:" + this.getInPortInfo().getPortName() 
					+ this.getInPortInfo().printPortProperties()
					+ "\n";
		
		if(this.getOutPortInfo() != null)
			outPortInfo = "  Output Port:" + this.getOutPortInfo().getPortName() 
					+ this.getOutPortInfo().printPortProperties()
					+ "\n\n";
		return ((outPortInfo != null) ? outPortInfo : "") 
			   + ((inPortInfo != null) ? inPortInfo : "");
	}
}
