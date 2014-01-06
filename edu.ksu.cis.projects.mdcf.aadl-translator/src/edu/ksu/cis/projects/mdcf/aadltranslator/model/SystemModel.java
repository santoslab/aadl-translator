package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SystemModel {
	private String name;
	private HashMap<String, ProcessModel> logicComponents;
	private HashMap<String, DeviceModel> devices;
	private ArrayList<ConnectionModel> channels;
	
	public SystemModel(){
		logicComponents = new HashMap<>();
		channels = new ArrayList<>();
		devices = new HashMap<>();
	}
	
	public ProcessModel getProcessByName(String processName) {
		return logicComponents.get(processName);
	}
	
	public void addProcess(ProcessModel pm){
		logicComponents.put(pm.getName(), pm);
	}
	
	public void addDevice(DeviceModel dm){
		devices.put(dm.getName(), dm);
	}

	public String getName() {
		return name;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		return logicComponents;
	}

	public ArrayList<ConnectionModel> getChannels() {
		return channels;
	}
}
