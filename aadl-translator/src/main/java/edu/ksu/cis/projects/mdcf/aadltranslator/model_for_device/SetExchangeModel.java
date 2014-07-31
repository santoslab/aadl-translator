package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class SetExchangeModel extends ExchangeModel {

	private String parameterName;
	
	public SetExchangeModel(String parameterName, String deviceType, String vmdType, String exchangeName) {
		super(deviceType, vmdType, exchangeName, ExchangeKind.SET);
		this.parameterName = parameterName;
	}
	
	public String getParameterName(){
		return this.parameterName;
	}
	
	public PortInfoModel getOutPortInfo() {
		return outPortInfo;
	}

	public void setOutPortInfo(PortInfoModel outPortInfo) {
		this.outPortInfo = outPortInfo;
	}

	public PortInfoModel getInPortInfo() {
		return inPortInfo;
	}

	public void setInPortInfo(PortInfoModel inPortInfo) {
		this.inPortInfo = inPortInfo;
	}
	
	public String getSendMessageType(){
		return this.inPortInfo.getMessageType();
	}
	
	public int getMinSeparationInterval(){
		return this.inPortInfo.getMinSeparationInterval();
	}
	
	public int getMaxSeparationInterval(){
		return this.inPortInfo.getMaxSeparationInterval();
	}
}
