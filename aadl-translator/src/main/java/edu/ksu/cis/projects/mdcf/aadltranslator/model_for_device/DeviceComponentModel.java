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

//	public HashMap<String, GetExchangeModel> getExchangeModels;
//	public HashMap<String, SetExchangeModel> setExchangeModels;
//	public HashMap<String, ActionExchangeModel> actionExchangeModels;
//	public HashMap<String, PeriodicExchangeModel> periodicExchangeModels;
//	public HashMap<String, SporadicExchangeModel> sporadicExchangeModels;
	
	public ArrayList<String> receivePortNames;
	public ArrayList<String> sendPortNames;

	public DeviceComponentModel(String deviceName, String deviceType) {
		super();
		this.name = deviceName;
		this.deviceType = deviceType;
		this.exchangeModels = new HashMap<String, ExchangeModel>();
	}
	
	public DeviceComponentModel() {
		super();
		this.exchangeModels = new HashMap<String, ExchangeModel>();
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
	
	public String toString(){
		return "IEEE11073_MDC_ATTR_SYS_TYPE:" + this.deviceType + "\n" 
				+ "Manufacturer:" + this.manufacturerName + "\n"
				+ "Model Number:" + this.modelNumber + "\n"
				+ "Credential:" + this.credentials.toString() + "\n"
				+ printExchangeMap();
	}
	
	public String printExchangeMap(){
		StringBuffer sb = new StringBuffer("ExchangeMap of " + this.deviceType + "\n");
		for(ExchangeModel em : this.exchangeModels.values()){
			sb.append(em.getExchangeName() + "=>" + em.toString());
		}
		return sb.toString();
	}
}
