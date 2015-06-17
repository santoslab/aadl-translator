package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class ActionExchangeModel extends ExchangeModel {

	private String actionName;
	
	public ActionExchangeModel(String actionName, String deviceType, String vmdType, String exchangeName) {
		super(deviceType, vmdType, exchangeName, ExchangeKind.ACTION);
		this.actionName = actionName;
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
	
	public String getActionName(){
		return this.actionName;
	}
	
	public int getMinSeparationInterval(){
		return this.inPortInfo.getMinSeparationInterval();
	}
	
	public int getMaxSeparationInterval(){
		return this.inPortInfo.getMaxSeparationInterval();
	}
}
