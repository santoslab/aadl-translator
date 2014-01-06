package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SystemModel {
	private String name;
	private HashMap<String, ProcessModel> logicComponents;
	private HashMap<String, DeviceModel> devices;
	private ArrayList<ConnectionModel> channels;
	
	// Type name -> Process Model
	private HashMap<String, IComponentModel> typeToComponent;
	
	public SystemModel(){
		logicComponents = new HashMap<>();
		typeToComponent = new HashMap<>();
		channels = new ArrayList<>();
		devices = new HashMap<>();
	}
	
	public ProcessModel getProcessByType(String processTypeName) {
		if(typeToComponent.get(processTypeName) instanceof ProcessModel)
			return (ProcessModel)typeToComponent.get(processTypeName);
		else
			return null;
	}
	
	public DeviceModel getDeviceByType(String deviceTypeName) {
		if(typeToComponent.get(deviceTypeName) instanceof DeviceModel)
			return (DeviceModel)typeToComponent.get(deviceTypeName);
		else
			return null;
	}
	
	public void addProcess(String instanceName, ProcessModel pm){
		logicComponents.put(instanceName, pm);
		typeToComponent.put(pm.getName(), pm);
	}
	
	public void addDevice(String deviceName, DeviceModel dm){
		devices.put(deviceName, dm);
		typeToComponent.put(dm.getName(), dm);
	}

	public void addConnection(ConnectionModel cm){
		channels.add(cm);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		return logicComponents;
	}

	public ArrayList<ConnectionModel> getChannels() {
		return channels;
	}
	
	public boolean hasProcessType(String typeName){
		return typeToComponent.containsKey(typeName);				
	}
}
