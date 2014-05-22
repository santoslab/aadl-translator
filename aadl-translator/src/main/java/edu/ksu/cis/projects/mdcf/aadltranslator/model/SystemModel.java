package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class SystemModel {
	private String name;
	private HashMap<String, ProcessModel> logicComponents;
	private HashMap<String, DeviceModel> devices;
	private ArrayList<ConnectionModel> channels;

	// Type name -> Process Model
	private HashMap<String, ComponentModel> typeToComponent;

	public SystemModel() {
		logicComponents = new HashMap<>();
		typeToComponent = new HashMap<>();
		channels = new ArrayList<>();
		devices = new HashMap<>();
	}

	public ProcessModel getProcessByType(String processTypeName) {
		if (typeToComponent.get(processTypeName) instanceof ProcessModel)
			return (ProcessModel) typeToComponent.get(processTypeName);
		else
			return null;
	}

	public DeviceModel getDeviceByType(String deviceTypeName) {
		if (typeToComponent.get(deviceTypeName) instanceof DeviceModel)
			return (DeviceModel) typeToComponent.get(deviceTypeName);
		else
			return null;
	}

	public void addProcess(String instanceName, ProcessModel pm)
			throws DuplicateElementException {
		if (logicComponents.containsKey(instanceName))
			throw new DuplicateElementException(instanceName
					+ " already exists");
		logicComponents.put(instanceName, pm);
		typeToComponent.put(pm.getName(), pm);
	}

	public void addDevice(String deviceName, DeviceModel dm)
			throws DuplicateElementException {
		if (devices.containsKey(deviceName))
			throw new DuplicateElementException(deviceName + " already exists");
		devices.put(deviceName, dm);
		typeToComponent.put(dm.getName(), dm);
	}

	public void addConnection(ConnectionModel cm) {
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

	public HashMap<String, ComponentModel> getLogicAndDevices() {
		HashMap<String, ComponentModel> ret = new HashMap<>();
		HashSet<String> logicComponentNames = new HashSet<>(
				logicComponents.keySet());
		if (logicComponentNames.retainAll(devices.keySet())) {
			ret.putAll(devices);
			ret.putAll(logicComponents);
		} else {
			// TODO: Handle this more gracefully?
			System.err
					.println("Device and Logic components can't have the same name");
		}
		return ret;
	}

	public ArrayList<ConnectionModel> getChannels() {
		return channels;
	}

	public boolean hasProcessType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof ProcessModel));
	}

	public boolean hasDeviceType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof DeviceModel));
	}
}
