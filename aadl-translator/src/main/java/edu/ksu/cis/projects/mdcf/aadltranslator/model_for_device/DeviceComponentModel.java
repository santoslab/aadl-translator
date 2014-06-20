package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ModelUtil;

public class DeviceComponentModel {

	private String name;
	private String deviceType;
	
	public HashMap<String, ExchangeModel> exchangeModels;
	
	public HashMap<String, GetExchangeModel> getExchangeModels;
	public HashMap<String, SetExchangeModel> setExchangeModels;
	public HashMap<String, ActionExchangeModel> actionExchangeModels;
	public HashMap<String, PeriodicExchangeModel> periodicExchangeModels;
	public HashMap<String, SporadicExchangeModel> sporadicExchangeModels;
	
	public ArrayList<String> receivePortNames;
	public ArrayList<String> sendPortNames;

	public DeviceComponentModel(String deviceName, String deviceType) {
		super();
		this.name = deviceName;
		this.deviceType = deviceType;
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
	
	public ExchangeModel getExchangeModel(String exchangeName){
		return this.exchangeModels.get(exchangeName);
	}
	
	public Map<String, ExchangeModel> getGetExchanges(){
		return Maps.filterValues(exchangeModels, ModelUtil.getExchangeFilter);
	}
	
}
