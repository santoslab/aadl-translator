package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;

public class ProcessModel {

	private String objectName;

	// port name -> port type
	private HashMap<String, String> receivePorts;

	// port name -> port type
	private HashMap<String, String> sendPorts;

	// This is a placeholder until I get actual tasks implemented
	private HashMap<String, String> tasks;
	
	public ProcessModel() {
		receivePorts = new HashMap<>();
		sendPorts = new HashMap<>();
		tasks = new HashMap<>();
		tasks.put("PlaceholderTaskName", "PlaceholderTaskType");
	}
	public void setObjectName(String name) {
		objectName = name;
	}

	public void addReceivePort(String name, String representation) {
		// TODO: Throw exception if we already have a port with the given name?
		receivePorts.put(name, representation);
	}

	public void addSendPort(String name, String representation) {
		// TODO: Throw exception if we already have a port with the given name?
		sendPorts.put(name, representation);
	}

	public String getObjectName() {
		return objectName;
	}

	public HashMap<String, String> getReceivePorts() {
		return receivePorts;
	}

	public HashMap<String, String> getSendPorts() {
		return sendPorts;
	}

	public HashMap<String, String> getTasks() {
		return tasks;
	}
	
	public boolean isDisplay(){
		return false;
	}
}
