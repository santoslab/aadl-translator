package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentLevelModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.CausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorFlowModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ExternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.HazardModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.InternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.NotDangerousDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.StpaPreliminaryModel;

public abstract class ComponentModel<ChildType extends ComponentModel<?, ?>, ConnectionType extends ConnectionModel> {

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
	protected HashMap<String, PortModel> ports = new HashMap<>();

	/**
	 * Maps child name -> model
	 */
	protected HashMap<String, ChildType> children = new HashMap<>();

	/**
	 * The role this component plays in its parent's decomposition
	 */
	protected ComponentType componentType;

	/**
	 * Error flows starting, ending, or moving through this component
	 */
	protected Set<ErrorFlowModel> errorFlows = new HashSet<>();

	/**
	 * Connections between this component's (immediate) children
	 */
	protected HashMap<String, ConnectionType> channels = new HashMap<>();

	/**
	 * Maps element name -> element model
	 */
	protected HashMap<String, StpaPreliminaryModel> stpaPreliminaries = new HashMap<>();

	/**
	 * Maps diagram name (eg: SystemBoundary) to file path
	 */
	protected HashMap<String, String> hazardReportDiagrams;

	private Map<String, CausedDangerModel> causedDangers = new LinkedHashMap<>();

	/**
	 * Explanation -> Names of faults that have been eliminated
	 */
	private Map<String, Set<String>> eliminatedFaults = new LinkedHashMap<>();

	/**
	 * The set of fault classes that should be accounted for. Each component has
	 * their own copy because it's modified when calculating missed fault classes.
	 */
	private Set<String> faultClasses = new LinkedHashSet<>();

	public ComponentModel() {
		initHazardReportDiagrams();
	}

	public void addErrorFlow(ErrorFlowModel errorFlow) throws DuplicateElementException {
		if (errorFlows.contains(errorFlow))
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

	public ConnectionType getChannelByName(String connectionName) {
		return channels.get(connectionName);
	}

	public HashMap<String, ConnectionType> getChannels() {
		return channels;
	}

	public void addConnection(String name, ConnectionType cm) {
		channels.put(name, cm);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addPort(PortModel pm) throws DuplicateElementException {
		if (ports.containsKey(pm.getName()))
			throw new DuplicateElementException("Ports cannot share names");
		ports.put(pm.getName(), pm);
	}

	public PortModel getPortByName(String portName) {
		return ports.get(portName);
	}

	public Map<String, PortModel> getPorts() {
		return ports;
	}

	public Map<String, PortModel> getReceivePorts() {
		return ports.entrySet().stream().filter(p -> p.getValue().isSubscribe())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, PortModel> getReceiveEventDataPorts() {
		return ports.entrySet().stream().filter(p -> p.getValue().isSubscribe() && p.getValue().isEventData())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, PortModel> getReceiveEventPorts() {
		return ports.entrySet().stream().filter(p -> p.getValue().isSubscribe() && p.getValue().isEvent())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, PortModel> getReceiveDataPorts() {
		return ports.entrySet().stream().filter(p -> p.getValue().isSubscribe() && p.getValue().isData())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, PortModel> getSendPorts() {
		return ports.entrySet().stream().filter(p -> !p.getValue().isSubscribe())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
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

	public String getComponentTypeAsString() {
		return WordUtils.capitalize(this.componentType.toString().toLowerCase());
	}

	public void addAccidentLevel(AccidentLevelModel alm) throws DuplicateElementException {
		addStpaPreliminary(alm);
	}

	public void addAccident(AccidentModel am) throws DuplicateElementException {
		addStpaPreliminary(am);
	}

	public void addHazard(HazardModel hm) throws DuplicateElementException {
		addStpaPreliminary(hm);
	}

	public void addConstraint(ConstraintModel cm) throws DuplicateElementException {
		addStpaPreliminary(cm);
	}

	public AccidentLevelModel getAccidentLevelByName(String name) {
		return (AccidentLevelModel) getStpaPreliminary(name);
	}

	public Map<String, StpaPreliminaryModel> getAccidentLevels() {
		return stpaPreliminaries.entrySet().stream().filter(p -> p.getValue() instanceof AccidentLevelModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, StpaPreliminaryModel> getAccidents() {
		return stpaPreliminaries.entrySet().stream().filter(p -> p.getValue() instanceof AccidentModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, StpaPreliminaryModel> getHazards() {
		return stpaPreliminaries.entrySet().stream().filter(p -> p.getValue() instanceof HazardModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	public Map<String, StpaPreliminaryModel> getConstraints() {
		return stpaPreliminaries.entrySet().stream().filter(p -> p.getValue() instanceof ConstraintModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
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

	private void addStpaPreliminary(StpaPreliminaryModel prelim) throws DuplicateElementException {
		if (stpaPreliminaries.containsKey(prelim.getName()))
			throw new DuplicateElementException("STPA Preliminaries cannot share names or be redefined");
		stpaPreliminaries.put(prelim.getName(), prelim);
	}

	public Map<String, ConnectionType> getControlActions() {
		// TODO: We need to think harder about what's a control action --
		// since threads (controllers) talk to their containing processes
		// (controllers in the system view, actuators in the thread view) we
		// may even need multi-role components, or per-view component roles.
		// return channels.entrySet()
		// .stream()
		// .filter(p -> true)
		// .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		return channels;
	}

	public void setComponentType(String componentType) {
		this.componentType = ComponentType.valueOf(componentType.toUpperCase());
	}

	public Map<String, ConnectionType> getRangedControlActions() {
		return getControlActions().entrySet().stream().filter(p -> isRangedControlAction(p))
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	private boolean isRangedControlAction(Entry<String, ConnectionType> p) {
		ConnectionType connection = p.getValue();
		PortModel pubPort = connection.getPublisher().getPortByName(connection.getPubPortName() + "Out");
		if (pubPort == null)
			pubPort = connection.getPublisher().getPortByName(connection.getPubPortName());
		String type = pubPort.getType();

		return !(type.equals("Object") || type.equals("Boolean"));
	}

	private void initHazardReportDiagrams() {
		hazardReportDiagrams = new HashMap<>();
		URL imagesDirUrl = Platform.getBundle("edu.ksu.cis.projects.mdcf.aadl-translator")
				.getEntry("src/main/resources/images/");
		try {
			File imagesDir = new File(FileLocator.toFileURL(imagesDirUrl).toURI().normalize());
			File appBoundaryPH = new File(imagesDir, "AppBoundary-Placeholder.png");
			hazardReportDiagrams.put("SystemBoundary", appBoundaryPH.getAbsolutePath());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> getHazardReportDiagrams() {
		return hazardReportDiagrams;
	}

	public void addCausedDanger(CausedDangerModel cdm) {
		causedDangers.put(cdm.getName(), cdm);
	}

	public Map<String, CausedDangerModel> getCausedDangers() {
		return causedDangers;
	}

	public Map<String, ExternallyCausedDangerModel> getExternallyCausedDangers() {
		return causedDangers.entrySet().stream().filter(m -> m.getValue() instanceof ExternallyCausedDangerModel)
				.collect(Collectors.toMap(m -> m.getKey(), m -> (ExternallyCausedDangerModel) m.getValue()));
	}

	public Map<String, InternallyCausedDangerModel> getInternallyCausedDangers() {
		return causedDangers.entrySet().stream().filter(m -> m.getValue() instanceof InternallyCausedDangerModel)
				.collect(Collectors.toMap(m -> m.getKey(), m -> (InternallyCausedDangerModel) m.getValue()));
	}

	public Map<String, NotDangerousDangerModel> getSunkDangers() {
		return causedDangers.entrySet().stream().filter(m -> m.getValue() instanceof NotDangerousDangerModel)
				.collect(Collectors.toMap(m -> m.getKey(), m -> (NotDangerousDangerModel) m.getValue()));
	}

	public Map<String, Set<String>> getEliminatedFaults() {
		return eliminatedFaults;
	}

	public void addEliminatedFaults(String explanation, Set<String> faults) {
		eliminatedFaults.put(explanation, faults);
	}

	/**
	 * Note that as this method computes missing fault classes, it is more
	 * expensive than the standard O(1) getter.
	 */
	public Set<String> getMissedFaultClasses() {
		for (Set<String> elimFaults : eliminatedFaults.values()) {
			faultClasses.removeAll(elimFaults);
		}
		for (InternallyCausedDangerModel icdm : getInternallyCausedDangers().values()) {
			faultClasses.removeAll(icdm.getFaultClasses());
		}
		return faultClasses;
	}

	public void setFaultClasses(HashMap<String, ManifestationTypeModel> faultClasses) {
		this.faultClasses = faultClasses.keySet();
	}
}
