package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class SporadicExchangeModel extends ExchangeModel {
	public enum OutPortProperty{
		MSG_TYPE("SEND_MESSAGE_TYPE"),
		SEPARATION_INTERVAL("SEPARATION_TIME");
		
		private final String propName;
		
		private OutPortProperty(final String propName){
			this.propName = propName;
		}
		
		public String toString(){
			return this.propName;
		}
	};

	private String parameterName;
	
	public SporadicExchangeModel(String parameterName, String deviceType, String vmdType, String exchangeName) {
		super(deviceType, vmdType, exchangeName, ExchangeKind.SPORADIC);
		this.parameterName = parameterName;
	}
	
	public PortInfoModel getOutPortInfo() {
		return outPortInfo;
	}

	public void setOutPortInfo(PortInfoModel outPortInfo) {
		this.outPortInfo = outPortInfo;
	}
	
	public String getParmeterName(){
		return this.parameterName;
	}
	
	public String getSendMessageType(){
		return this.outPortInfo.getPortProperty(OutPortProperty.MSG_TYPE.toString());
	}
}
