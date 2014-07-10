package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class ActionExchangeModel extends ExchangeModel {
	public enum OutPortProperty{
		
	};
	
	public enum InPortProperty{
		MIN_SEPARATION_INTERVAL("MIN_SEPARATION_TIME"),
		MAX_SEPARATION_INTERVAL("MAX_SEPARATION_TIME");
		
		private final String propName;
		
		private InPortProperty(final String propName){
			this.propName = propName;
		}
		
		public String toString(){
			return this.propName;
		}
	};
	
	public String actionName;
	
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
	
	public String getMinSeparationInterval(){
		return this.inPortInfo.getPortProperty(InPortProperty.MIN_SEPARATION_INTERVAL.toString());
	}
	
	public String getMaxSeparationInterval(){
		return this.inPortInfo.getPortProperty(InPortProperty.MAX_SEPARATION_INTERVAL.toString());
	}
}
