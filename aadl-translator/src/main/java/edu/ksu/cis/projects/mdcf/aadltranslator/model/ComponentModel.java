package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;

public abstract class ComponentModel <ChildType extends ComponentModel, ConnectionType extends ConnectionModel> {

	/**
	 * The type name of this component
	 */
	protected String name;

	/**
	 * The name of the (super)component this (sub)component is contained in
	 */
	protected String parentName;
	
	/**
	 * Maps port name -> port model
	 */
	protected HashMap<String, PortModel> ports;
	
	/**
	 * Maps task name -> task model
	 */
	protected HashMap<String, ChildType> children;
	
	/**
	 * The role this component plays in its parent's decomposition
	 */
	protected ComponentType componentType;
	
	/**
	 * Error propagations entering and leaving this component
	 */
	protected HashSet<PropagationModel> propagations;
	
	/**
	 * Error flows starting, ending, or moving through this component
	 */
	protected HashSet<ErrorFlowModel> errorFlows;
	
	/**
	 * Connections between this component's (immediate) children
	 */
	protected HashMap<String, ConnectionType> channels;

	/**
	 * Maps element name -> element model
	 */
	protected HashMap<String, StpaPreliminaryModel> stpaPreliminaries;
	
	public ComponentModel(){
		ports = new HashMap<>();
		children = new HashMap<>();
		channels = new HashMap<>();
		stpaPreliminaries = new HashMap<String, StpaPreliminaryModel>();
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
	
	public void addChild(String name, ChildType childModel) throws DuplicateElementException {
		children.put(name, childModel);
	}
	
	public ChildType getChild(String name) {
		return children.get(name);
	}
	
	public HashMap<String, ChildType> getChildren() {
		return children;
	}
	
	public ConnectionType getChannelByName(String connectionName){
		return channels.get(connectionName);
	}
	
	public HashMap<String, ConnectionType> getChannels() {
		return channels;
	}

	public void addConnection(String name, ConnectionType cm){
		channels.put(name, cm);
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
	
	public void addAccidentLevel(AccidentLevelModel alm)
			throws DuplicateElementException {
		addStpaPreliminary(alm);
	}

	public void addAccident(AccidentModel am) throws DuplicateElementException {
		addStpaPreliminary(am);
	}

	public void addHazard(HazardModel hm) throws DuplicateElementException {
		addStpaPreliminary(hm);
	}

	public void addConstraint(ConstraintModel cm)
			throws DuplicateElementException {
		addStpaPreliminary(cm);
	}

	public AccidentLevelModel getAccidentLevelByName(String name) {
		return (AccidentLevelModel) getStpaPreliminary(name);
	}

	public Map<String, StpaPreliminaryModel> getAccidentLevels() {
		return Maps.filterValues(stpaPreliminaries,
				ModelUtil.accidentLevelFilter);
	}

	public Map<String, StpaPreliminaryModel> getAccidents() {
		return Maps.filterValues(stpaPreliminaries, ModelUtil.accidentFilter);
	}

	public Map<String, StpaPreliminaryModel> getHazards() {
		return Maps.filterValues(stpaPreliminaries, ModelUtil.hazardFilter);
	}

	public Map<String, StpaPreliminaryModel> getConstraints() {
		return Maps.filterValues(stpaPreliminaries, ModelUtil.constraintFilter);
	}

	public AccidentModel getAccidentByName(String name) {
		return (AccidentModel) getStpaPreliminary(name);
	}

	public HazardModel getHazardByName(String name) {
		return (HazardModel) getStpaPreliminary(name);
	}

	public ConstraintModel getConstraintByName(String name) {
		return (ConstraintModel) getStpaPreliminary(name);
	}

	private StpaPreliminaryModel getStpaPreliminary(String name) {
		return stpaPreliminaries.get(name);
	}

	private void addStpaPreliminary(StpaPreliminaryModel prelim)
			throws DuplicateElementException {
		if (stpaPreliminaries.containsKey(prelim.getName()))
			throw new DuplicateElementException(
					"STPA Preliminaries cannot share names or be redefined");
		stpaPreliminaries.put(prelim.getName(), prelim);
	}
	
	public Map<String, ConnectionType> getControlActions() {
		return Maps.filterValues(channels, ModelUtil.controlActionFilter);
	}

	public Map<String, ConnectionType> getRangedControlActions() {
		Map<String, ConnectionType> controlActions = Maps.filterValues(
				channels, ModelUtil.controlActionFilter);
		return Maps.filterValues(controlActions, ModelUtil.rangedChannelFilter);
	}

	public Map<String, ChildType> getSensors() {
		return Maps.filterValues(children, ModelUtil.sensorFilter);
	}

	public Map<String, ChildType> getControllers() {
		return Maps.filterValues(children, ModelUtil.controllerFilter);
	}

	public Map<String, ChildType> getActuators() {
		return Maps.filterValues(children, ModelUtil.actuatorFilter);
	}

	public Map<String, ChildType> getControlledProcesses() {
		return Maps.filterValues(children, ModelUtil.controlledProcessFilter);
	}
	
	public void generateBoundaryDiagram() {
		StringBuffer diagramSchematic = new StringBuffer();
		diagramSchematic.append("Sensors:\n");
		for(ChildType child : getSensors().values()){
			diagramSchematic.append("\t");
			diagramSchematic.append(child.getName());
			diagramSchematic.append("\n");
			for(ConnectionType conn : channels.values()){
				if(conn.subscriber == child)
					diagramSchematic.append("lol");
				else if(conn.publisher == child){
					diagramSchematic.append("\t\t(");
					diagramSchematic.append(conn.getPubName());
					diagramSchematic.append(".");
					diagramSchematic.append(conn.getPubPortName());
					diagramSchematic.append(")-");
					diagramSchematic.append(conn.getName());
					diagramSchematic.append("->(");
					diagramSchematic.append(conn.getSubName());
					diagramSchematic.append(".");
					diagramSchematic.append(conn.getSubPortName());
					diagramSchematic.append(")\n");
				}
			}
		}
		diagramSchematic.append("Controllers:\n");
		for(ChildType child : getControllers().values()){
			diagramSchematic.append("\t");
			diagramSchematic.append(child.getName());
			diagramSchematic.append("\n");
		}
		diagramSchematic.append("Actuators:\n");
		for(ChildType child : getActuators().values()){
			diagramSchematic.append("\t");
			diagramSchematic.append(child.getName());
			diagramSchematic.append("\n");
		}
		diagramSchematic.append("Controlled Processes:\n");
		for(ChildType child : getControlledProcesses().values()){
			diagramSchematic.append("\t");
			diagramSchematic.append(child.getName());
			diagramSchematic.append("\n");
		}
		System.out.println(diagramSchematic.toString());
	}
}
