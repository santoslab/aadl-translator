package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ModelUtil;

public class DeviceComponentModel {

	private String name;
	private String deviceType;
	
	private String manufacturerName;
	private String modelNumber;
	
	private ArrayList<String> credentials;
	
	public HashMap<String, ExchangeModel> exchangeModels;

	public Map<String, GetExchangeModel> getExchangeModels;
	public Map<String, SetExchangeModel> setExchangeModels;
	public Map<String, ActionExchangeModel> actionExchangeModels;
	public Map<String, PeriodicExchangeModel> periodicExchangeModels;
	public Map<String, SporadicExchangeModel> sporadicExchangeModels;
	
	public ArrayList<String> receivePortNames;
	public ArrayList<String> sendPortNames;

	public DeviceComponentModel(String deviceName, String deviceType) {
		super();
		this.name = deviceName;
		this.deviceType = deviceType;
		this.exchangeModels = new HashMap<String, ExchangeModel>();
		this.receivePortNames = new ArrayList<String>();
		this.sendPortNames = new ArrayList<String>();
	}
	
	public DeviceComponentModel() {
		super();
		this.exchangeModels = new HashMap<String, ExchangeModel>();
		this.receivePortNames = new ArrayList<String>();
		this.sendPortNames = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public void putExchangeModel(String exchangeName, ExchangeModel exchangeModel){
		this.exchangeModels.put(exchangeName, exchangeModel);
	}
	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	
	public ArrayList<String> getCredentials(){
		if(this.credentials == null)
			this.credentials = new ArrayList<String>();
		return this.credentials;
	}
	
	public void addCredential(String credential){
		if(this.credentials == null)
			this.credentials = new ArrayList<String>();
		this.credentials.add(credential);
	}
	
	public void distributeExchanges(){
		getExchangeModels = (Map<String, GetExchangeModel>) Maps.transformValues(
				(Map<String, ExchangeModel>) Maps.filterValues(exchangeModels, ModelUtil.getExchangeFilter), 
				ModelUtil.transformToGetExchangeModel);
		setExchangeModels = (Map<String, SetExchangeModel>) Maps.transformValues(
				(Map<String, ExchangeModel>) Maps.filterValues(exchangeModels, ModelUtil.setExchangeFilter), 
				ModelUtil.transformToSetExchangeModel);
		actionExchangeModels = (Map<String, ActionExchangeModel>) Maps.transformValues(
				(Map<String, ExchangeModel>) Maps.filterValues(exchangeModels, ModelUtil.actionExchangeFilter), 
				ModelUtil.transformToActionExchangeModel);
		periodicExchangeModels = (Map<String, PeriodicExchangeModel>) Maps.transformValues(
				(Map<String, ExchangeModel>) Maps.filterValues(exchangeModels, ModelUtil.periodicExchangeFilter), 
				ModelUtil.transformToPeriodicExchangeModel);
		sporadicExchangeModels = (Map<String, SporadicExchangeModel>) Maps.transformValues(
				(Map<String, ExchangeModel>) Maps.filterValues(exchangeModels, ModelUtil.sporadicExchangeFilter), 
				ModelUtil.transformToSporadicExchangeModel);
		
		for(ExchangeModel em : getExchangeModels.values()){
			if(em.inPortInfo != null)
				this.receivePortNames.add(em.getExchangeName());
			if(em.outPortInfo != null)
				this.sendPortNames.add(em.getExchangeName());
		}
	}
	
	public String toString(){
		return "IEEE11073_MDC_ATTR_SYS_TYPE:" + this.deviceType + "\n" 
				+ "Manufacturer:" + this.manufacturerName + "\n"
				+ "Model Number:" + this.modelNumber + "\n"
//				+ "Credential:" + this.credentials.toString() + "\n"
				+ printExchangeMap();
	}
	
	public String printExchangeMap(){
		StringBuffer sb = new StringBuffer("ExchangeMap of " + this.deviceType + "\n");
		for(ExchangeModel em : this.exchangeModels.values()){
			sb.append(em.getExchangeName() + "=>" + em.toString());
		}
		return sb.toString();
	}

	public String sanityCheckExchanges(String vmdTypeName) {
		//Checking whether get/set/action exchanges has pairs
		for(ExchangeModel em : this.exchangeModels.values()){
			if(em.getVmdType().equals(vmdTypeName)){
				if(em instanceof GetExchangeModel || em instanceof SetExchangeModel || em instanceof ActionExchangeModel){
					if(em.inPortInfo == null || em.outPortInfo == null){
						return "Missing Matching Port Pair:" + em.getExchangeName() + " in " + em.getVmdType();
					}
				}
			}
		}
		return "";
	}
}
