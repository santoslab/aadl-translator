package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

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
	
	// method name -> method model
	private HashMap<String, MethodModel> methods;

	public ProcessModel() {
		receivePorts = new HashMap<>();
		sendPorts = new HashMap<>();
		tasks = new HashMap<>();
		methods = new HashMap<>();
		globals = new HashMap<>();
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

	public HashMap<String, String> getGlobals() {
		return globals;
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
		tasks.put(name, new TaskModel(name));
	}
	
	public void addGlobal(String name, String type) {
		globals.put(name, type);
	}

	public HashMap<String, MethodModel> getMethods() {
		return methods;
	}

	public void addMethod(String methodName, MethodModel method) {
		methods.put(methodName, method);
	}
	
	public void addParameterToMethod(String methodName, String parameterName, String parameterType) throws DuplicateElementException{
		if(!methods.containsKey(methodName))
			methods.put(methodName, new MethodModel(methodName));
		methods.get(methodName).addParameter(parameterName, parameterType);
	}

	public void addReturnToMethod(String methodName, String returnType) {
		if(!methods.containsKey(methodName))
			methods.put(methodName, new MethodModel(methodName));
		methods.get(methodName).setRetType(returnType);
	}
}
