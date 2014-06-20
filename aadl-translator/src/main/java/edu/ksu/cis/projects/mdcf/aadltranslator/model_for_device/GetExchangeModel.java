package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

public class GetExchangeModel extends ExchangeModel {
	public enum OutPortProperty{
		MSG_TYPE("SEND_MESSAGE_TYPE");
		
		private final String propName;
		
		private OutPortProperty(final String propName){
			this.propName = propName;
		}
		
		public String toString(){
			return this.propName;
		}
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
	
	private String parameterName;
	
	public GetExchangeModel(String parameterName, String deviceType, String exchangeName,
			ExchangeKind exchangekind) {
		super(deviceType, exchangeName, exchangekind);
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
		return this.outPortInfo.getPortProperty(OutPortProperty.MSG_TYPE.toString());
	}
	
	public String getMinSeparationInterval(){
		return this.inPortInfo.getPortProperty(InPortProperty.MIN_SEPARATION_INTERVAL.toString());
	}
	
	public String getMaxSeparationInterval(){
		return this.inPortInfo.getPortProperty(InPortProperty.MAX_SEPARATION_INTERVAL.toString());
	}
	public String getMessageTypeDefault(){
		return ModelUtil.getDefaultValueString(getSendMessageType());
	}
}
