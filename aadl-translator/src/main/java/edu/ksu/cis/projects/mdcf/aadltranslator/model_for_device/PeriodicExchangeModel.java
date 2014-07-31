package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class PeriodicExchangeModel extends ExchangeModel {

	public String parameterName;
	
	public PeriodicExchangeModel(String parameterName, String deviceType, String vmdType, String exchangeName) {
		super(deviceType, vmdType, exchangeName, ExchangeKind.PERIODIC);
		this.parameterName = parameterName;
	}
	
	public String getParmeterName(){
		return this.parameterName;
	}
	
	public PortInfoModel getOutPortInfo() {
		return outPortInfo;
	}

	public void setOutPortInfo(PortInfoModel outPortInfo) {
		this.outPortInfo = outPortInfo;
	}
	
	public String getSendMessageType(){
		return this.outPortInfo.getMessageType();
	}
}
