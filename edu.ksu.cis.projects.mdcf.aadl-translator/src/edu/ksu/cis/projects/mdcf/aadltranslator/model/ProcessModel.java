package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessModel {

	private String objectName;

	// port name -> port type
	private HashMap<String, String> receivePorts;

	// port name -> port type
	private HashMap<String, String> sendPorts;

	// task name -> task model
	private HashMap<String, TaskModel> tasks;
	
	// variable name -> type
	private HashMap<String, String> globals;
	
	public ProcessModel() {
		receivePorts = new HashMap<>();
		sendPorts = new HashMap<>();
		tasks = new HashMap<>();
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

	public HashMap<String, TaskModel> getTasks() {
		return tasks;
	}
	
	/**
	 * This will return the type of a global variable
	 * @param name The name of the global variable
	 * @return The type of the global variable
	 */
	public String getGlobalType(String name){
		return globals.get(name);
	}
	
	public TaskModel getTask(String name){
		return tasks.get(name);
	}
	
	public boolean isDisplay(){
		return false;
	}

	public TaskModel getLastThread(){
		return tasks.get(tasks.size() - 1);
	}
	
	public void addTask(String name) {
		tasks.put(name, new TaskModel());
	}
}
