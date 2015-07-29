package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class SystemModel {
	private String name;
	private String timestamp;
	private HashMap<String, ProcessModel> logicComponents;
	private HashMap<String, DeviceModel> devices;
	private HashMap<String, ConnectionModel> channels;

	// Type name -> Process Model
	private HashMap<String, ComponentModel> typeToComponent;

	// Element name -> Element model
	private HashMap<String, StpaPreliminaryModel> stpaPreliminaries;

	private String hazardReportContext;
	private HashSet<AbbreviationModel> hazardReportAbbreviations;
	private HashSet<String> hazardReportAssumptions;
	private HashMap<String, String> hazardReportDiagrams;

	public SystemModel() {
		logicComponents = new HashMap<>();
		typeToComponent = new HashMap<>();
		channels = new HashMap<>();
		devices = new HashMap<>();
		stpaPreliminaries = new HashMap<>();
		hazardReportAbbreviations = new HashSet<>();
		hazardReportAssumptions = new HashSet<>();
		initHazardReportDiagrams();
	}

	private void initHazardReportDiagrams() {
		hazardReportDiagrams = new HashMap<>();
		URL imagesDirUrl = Platform.getBundle(
				"edu.ksu.cis.projects.mdcf.aadl-translator").getEntry(
				"src/main/resources/images/");
		try {
			File imagesDir = new File(FileLocator.toFileURL(imagesDirUrl)
					.getPath());
			File appBoundaryPH = new File(imagesDir,
					"AppBoundary-Placeholder.png");
			File procModelPH = new File(imagesDir, "ProcModel-Placeholder.png");
			hazardReportDiagrams.put("SystemBoundary",
					appBoundaryPH.getAbsolutePath());
			hazardReportDiagrams.put("ProcessModel",
					procModelPH.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getHazardReportContext() {
		return hazardReportContext;
	}

	public void setHazardReportContext(String hazardReportContext) {
		this.hazardReportContext = hazardReportContext;
	}

	public HashSet<AbbreviationModel> getHazardReportAbbreviations() {
		return hazardReportAbbreviations;
	}

	public HashSet<String> getHazardReportAssumptions() {
		return hazardReportAssumptions;
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

	public ConnectionModel getChannelByName(String connectionName) {
		return channels.get(connectionName);
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

	public void addConnection(String name, ConnectionModel cm) {
		channels.put(name, cm);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		return logicComponents;
	}

	public HashMap<String, ComponentModel> getLogicAndDevices() {
		HashMap<String, ComponentModel> ret = new HashMap<>();
		HashSet<String> logicComponentNames = new HashSet<>(
				logicComponents.keySet());
		if(logicComponentNames.isEmpty() && devices.isEmpty()) {
			// TODO: Handle this more gracefully?
			System.err.println("No components (logic or devices) to write");
		} else if (logicComponentNames.isEmpty()) {
			ret.putAll(devices);
		} else if (devices.isEmpty()) {
			ret.putAll(logicComponents);
		} else if (logicComponentNames.retainAll(devices.keySet())) {
			ret.putAll(devices);
			ret.putAll(logicComponents);
		} else {
			// TODO: Handle this more gracefully?
			System.err.println("Device and Logic components can't have the same name");
		}
		return ret;
	}

	public HashMap<String, ConnectionModel> getChannels() {
		return channels;
	}

	public Map<String, ConnectionModel> getControlActions() {
		return Maps.filterValues(channels, ModelUtil.controlActionFilter);
	}

	public Map<String, ConnectionModel> getRangedControlActions() {
		Map<String, ConnectionModel> controlActions = Maps.filterValues(
				channels, ModelUtil.controlActionFilter);
		return Maps.filterValues(controlActions, ModelUtil.rangedChannelFilter);
	}

	public boolean hasProcessType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof ProcessModel));
	}

	public boolean hasDeviceType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof DeviceModel));
	}

	public void setContext(String value) {
		hazardReportContext = value;
	}

	public void addAbbreviation(AbbreviationModel am) {
		hazardReportAbbreviations.add(am);
	}

	public void addAssumption(String assumption) {
		hazardReportAssumptions.add(assumption);
	}

	public HashMap<String, String> getHazardReportDiagrams() {
		return hazardReportDiagrams;
	}
	
	public Collection<ConnectionModel> getUniqueDevicePublishedChannels(){
		Set<ConnectionModel> chanSet = new HashSet<>(channels.values());
		chanSet = Sets.filter(chanSet, ModelUtil.devicePublishedFilter);
		
		// Get a set that's distinct based on publishing identity 
		// (publisher component name + publisher port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getPubName().concat(cm.getPubPortName()), cm);
		}
		return chanMap.values();
	}
	
	public Collection<ConnectionModel> getUniqueDeviceSubscribedChannels(){
		Set<ConnectionModel> chanSet = new HashSet<>(channels.values());
		chanSet = Sets.filter(chanSet, ModelUtil.deviceSubscribedFilter);
		
		// Get a set that's distinct based on subscriber identity 
		// (subscriber component name + subscriber port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getSubName().concat(cm.getSubPortName()), cm);
		}
		return chanMap.values();
	}
}
