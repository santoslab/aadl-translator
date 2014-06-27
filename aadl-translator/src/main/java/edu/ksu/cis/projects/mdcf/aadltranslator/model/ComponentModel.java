package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentKind;

public class ComponentModel {

	/**
	 * The type name of this component
	 */
	protected String name;

	/**
	 * The name of the system this component is used in
	 */
	protected String systemName;
	
	// port name -> port model
	protected HashMap<String, PortModel> ports;
	
	// task name -> task model
	protected HashMap<String, TaskModel> tasks;
	
	protected ComponentKind kind;
	
	protected HashSet<PropagationModel> propagations;
	protected HashSet<ErrorFlowModel> errorFlows;
	
	public ComponentModel(){
		ports = new HashMap<>();
		tasks = new HashMap<>();
	}
	
	public void addPropagation(PropagationModel propagation) throws DuplicateElementException {
		if(propagations.contains(propagation))
			throw new DuplicateElementException("Component model propagations must be unique");
		propagations.add(propagation);
	}
	
	public void addErrorFlow(ErrorFlowModel errorFlow) throws DuplicateElementException {
		if(errorFlows.contains(errorFlow))
			throw new DuplicateElementException("Error flows propagations must be unique");
		errorFlows.add(errorFlow);
	}
	
	public void addTask(String name) throws DuplicateElementException {
		if(tasks.containsKey(name))
			throw new DuplicateElementException("Tasks cannot have the same name");
		tasks.put(name, new TaskModel(name));
	}
	
	public TaskModel getTask(String name){
		return tasks.get(name);
	}
	
	public HashMap<String, TaskModel> getTasks() {
		return tasks;
	}
	
	public Map<String, TaskModel> getSporadicTasks() {
		return Maps.filterValues(tasks, ModelUtil.sporadicTaskFilter);
	}
	
	public Map<String, TaskModel> getPeriodicTasks() {
		return Maps.filterValues(tasks, ModelUtil.periodicTaskFilter);
	}
	
	public boolean isDisplay(){
		return kind == ComponentKind.DISPLAY;
	}
	
	public boolean isPseudoDevice(){
		return kind == ComponentKind.PSEUDODEVICE;
	}
	
	public boolean isLogic(){
		return kind == ComponentKind.LOGIC;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void addPort(PortModel pm) throws DuplicateElementException {
		if(ports.containsKey(pm.getName()))
			throw new DuplicateElementException("Ports cannot share names");
		ports.put(pm.getName(), pm);
	}
	
	public PortModel getPortByName(String portName){
		return ports.get(portName);
	}

	public Map<String, PortModel> getPorts() {
		return ports;
	}
	
	public Map<String, PortModel> getReceivePorts() {
		return Maps.filterValues(ports, ModelUtil.receivePortFilter);
	}
	
	public Map<String, PortModel> getReceiveEventDataPorts() {
		return Maps.filterValues(ports, ModelUtil.receiveEventDataPortFilter);
	}
	
	public Map<String, PortModel> getReceiveEventPorts() {
		return Maps.filterValues(ports, ModelUtil.receiveEventPortFilter);
	}
	
	public Map<String, PortModel> getReceiveDataPorts() {
		return Maps.filterValues(ports, ModelUtil.receiveDataPortFilter);
	}
	
	public Map<String, PortModel> getSendPorts() {
		return Maps.filterValues(ports, ModelUtil.sendPortFilter);
	}
	
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
}
