package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class ProcessModel implements IComponentModel{

	/**
	 * The name of this process -- note that this is a type name, rather than
	 * an instance name (since there could be several instances of this
	 * process, all named different things)
	 */
	private String name;
	
	private boolean display;

//	// port name -> port type
//	private HashMap<String, String> receivePorts;
//
//	// port name -> port type
//	private HashMap<String, String> sendPorts;
	
	// port name -> port model
	private HashMap<String, PortModel> ports;

	// task name -> task model
	private HashMap<String, TaskModel> tasks;
	
	// variable name -> type
	private HashMap<String, String> globals;
	
	// method name -> method model
	private HashMap<String, MethodModel> methods;

	public ProcessModel() {
//		receivePorts = new HashMap<>();
//		sendPorts = new HashMap<>();
		ports = new HashMap<>();
		tasks = new HashMap<>();
		methods = new HashMap<>();
		globals = new HashMap<>();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void addPort(PortModel pm) throws DuplicateElementException {
		if(ports.containsKey(pm.getName()))
			throw new DuplicateElementException("Ports cannot share names");
		ports.put(pm.getName(), pm);
	}
	
	public PortModel getPortByName(String portName){
		return ports.get(portName);
	}

	public String getName() {
		return name;
	}

	public Map<String, PortModel> getPorts() {
		return ports;
	}
	
	public Map<String, PortModel> getReceiveEventDataPorts() {
		return Maps.filterValues(ports, receiveEventDataPortFilter);
	}
	
	public Map<String, PortModel> getReceiveDataPorts() {
		return Maps.filterValues(ports, receiveDataPortFilter);
	}
	
	public Map<String, PortModel> getSendPorts() {
		return Maps.filterValues(ports, sendPortFilter);
	}

	public HashMap<String, TaskModel> getTasks() {
		return tasks;
	}
	
	public Map<String, TaskModel> getSporadicTasks() {
		return Maps.filterValues(tasks, sporadicTaskFilter);
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
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public TaskModel getLastThread(){
		return tasks.get(tasks.size() - 1);
	}
	
	public void addTask(String name) throws DuplicateElementException {
		if(tasks.containsKey(name))
			throw new DuplicateElementException("Tasks cannot have the same name");
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
	
	Predicate<PortModel> receiveEventDataPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe() && pm.isEventData();
		}
	};
	
	Predicate<PortModel> receiveDataPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe() && pm.isData();
		}
	};
	
	Predicate<PortModel> sendPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return !pm.isSubscribe();
		}
	};
	
	Predicate<TaskModel> sporadicTaskFilter = new Predicate<TaskModel>() {
		public boolean apply(TaskModel tm) {
			return tm.isSporadic();
		}
	};
}
