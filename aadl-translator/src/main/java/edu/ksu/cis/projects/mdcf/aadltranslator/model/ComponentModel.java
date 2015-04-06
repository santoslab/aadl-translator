package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ProcessType;

public abstract class ComponentModel <ChildType extends ComponentModel> {

	/**
	 * The type name of this component
	 */
	protected String name;

	/**
	 * The name of the (super)component this (sub)component is contained in
	 */
	protected String parentName;
	
	// port name -> port model
	protected HashMap<String, PortModel> ports;
	
	// task name -> task model
	protected HashMap<String, ChildType> children;
	
	protected ProcessType processType;
	protected ComponentType componentType;
	
	protected HashSet<PropagationModel> propagations;
	protected HashSet<ErrorFlowModel> errorFlows;
	
	public ComponentModel(){
		ports = new HashMap<>();
		children = new HashMap<>();
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
	
	public abstract void addChild(String name, ChildType childModel) throws DuplicateElementException;
	
	public abstract ChildType getChild(String name);
	
	public abstract HashMap<String, ChildType> getChildren();

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
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void getParentName(String parentName) {
		this.parentName = parentName;
	}

	public ComponentType getComponentType() {
		return this.componentType;
	}
}
