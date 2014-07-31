package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class GetExchangeModel extends ExchangeModel {

	private String parameterName;
	
	public GetExchangeModel(String parameterName, String deviceType, String vmdType, String exchangeName) {
		super(deviceType, vmdType, exchangeName, ExchangeModel.ExchangeKind.GET);
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
		return this.outPortInfo.getMessageType();
	}
	
	public int getMinSeparationInterval(){
		return this.inPortInfo.getMinSeparationInterval();
	}
	
	public int getMaxSeparationInterval(){
		return this.inPortInfo.getMaxSeparationInterval();
	}
	public String getMessageTypeDefault(){
		return this.outPortInfo.getMessageTypeDefault();
	}
}
