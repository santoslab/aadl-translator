package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class SystemModel extends ComponentModel<DevOrProcModel, SystemConnectionModel>{
	private String timestamp;
	private HashMap<String, DevOrProcModel> children;
//	private HashMap<String, SystemConnectionModel> channels;

	// Type name -> Child name Model
	private HashMap<String, DevOrProcModel> typeToComponent;

	// Element name -> Element model
	private HashMap<String, StpaPreliminaryModel> stpaPreliminaries;

	private String hazardReportContext;
	private HashSet<AbbreviationModel> hazardReportAbbreviations;
	private HashSet<String> hazardReportAssumptions;
	private HashMap<String, String> hazardReportDiagrams;

	public SystemModel() {
		super();
		children = new HashMap<>();
		typeToComponent = new HashMap<>();
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

	@Override
	public SystemConnectionModel getChannelByName(String connectionName) {
		return channels.get(connectionName);
	}
	
	@Override
	public void addChild(String childName, DevOrProcModel childModel) throws DuplicateElementException {
		if(typeToComponent.containsKey(childName))
			throw new DuplicateElementException(childName + " already exists");
		if(children.containsKey(childName))
			throw new DuplicateElementException(childName + " already exists");
		
		children.put(childName, childModel);
		typeToComponent.put(childModel.getName(), childModel);
	}
	
	@Override
	public DevOrProcModel getChild(String childName) {
		return children.get(childName);		
	}
	
	@Override
	public HashMap<String, DevOrProcModel> getChildren() {
		return children;
	}

	@Override
	public void addConnection(String name, SystemConnectionModel cm) {
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
		HashMap<String, DevOrProcModel> preCast = new HashMap<>(Maps.filterValues(children, ModelUtil.logicComponentFilter));
		HashMap<String, ProcessModel> ret = new HashMap<>();
		for(String elemName : preCast.keySet()){
			ret.put(elemName, (ProcessModel) preCast.get(elemName));
		}
		return ret;
	}

	@Override
	public HashMap<String, SystemConnectionModel> getChannels() {
		return channels;
	}

	public Map<String, SystemConnectionModel> getControlActions() {
		return Maps.filterValues(channels, ModelUtil.controlActionFilter);
	}

	public Map<String, SystemConnectionModel> getRangedControlActions() {
		Map<String, SystemConnectionModel> controlActions = Maps.filterValues(
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
}
