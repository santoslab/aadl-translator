package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AbbreviationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;

public class SystemModel extends ComponentModel<DevOrProcModel, SystemConnectionModel>{

	// Type name -> Child name Model
	private HashMap<String, DevOrProcModel> typeToComponent;

	private String timestamp;
	private String hazardReportContext;
	private HashSet<AbbreviationModel> hazardReportAbbreviations;
	private HashSet<String> hazardReportAssumptions;
	
	// Fault name -> Fault model
//	private HashMap<String, ErrorTypeModel> faultClasses;

	/**
	 * Error type name -> model
	 */
	private Map<String, ManifestationTypeModel> errorTypeModels;

	public SystemModel() {
		super();
		typeToComponent = new HashMap<>();
		hazardReportAbbreviations = new HashSet<>();
		hazardReportAssumptions = new HashSet<>();
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
	public void addChild(String childName, DevOrProcModel childModel) throws DuplicateElementException {
		super.addChild(childName, childModel);
		if(typeToComponent.containsKey(childName))
			throw new DuplicateElementException(childName + " already exists");
		typeToComponent.put(childModel.getName(), childModel);
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		Map<String, DevOrProcModel> preCast = children.entrySet()
				.stream()
				.filter(p -> p.getValue() instanceof ProcessModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
		HashMap<String, ProcessModel> ret = new HashMap<>();
		for(String elemName : preCast.keySet()){
			ret.put(elemName, (ProcessModel) preCast.get(elemName));
		}
		return ret;
	}
	
	public HashMap<String, DevOrProcModel> getLogicAndDevices() {
		return children;
	}
	
	public boolean hasProcessType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof ProcessModel));
	}

	public boolean hasDeviceType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof DeviceModel));
	}

	public void addAbbreviation(AbbreviationModel am) {
		hazardReportAbbreviations.add(am);
	}

	public void addAssumption(String assumption) {
		hazardReportAssumptions.add(assumption);
	}
	
	public HashMap<String, ConnectionModel> getUniqueDevicePublishedChannels(){
		Set<SystemConnectionModel> chanSet = channels.values()
				.stream()
				.filter(cs -> cs.publisher instanceof DeviceModel)
				.collect(Collectors.toSet());
		
		// Get a set that's distinct based on publishing identity 
		// (publisher component name + publisher port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getPubName().concat(cm.getPubPortName()), cm);
		}
		return chanMap;
	}
	
	public HashMap<String, ConnectionModel> getUniqueDeviceSubscribedChannels(){
		Set<SystemConnectionModel> chanSet = channels.values()
				.stream()
				.filter(cs -> cs.subscriber instanceof DeviceModel)
				.collect(Collectors.toSet());
		
		// Get a set that's distinct based on subscriber identity 
		// (subscriber component name + subscriber port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getSubName().concat(cm.getSubPortName()), cm);
		}
		return chanMap;
	}

	public void setErrorTypes(Map<String, ManifestationTypeModel> errorTypeModels) {
		this.errorTypeModels = errorTypeModels;
	}
	
	public Map<String, ManifestationTypeModel> getErrorTypes(){
		return errorTypeModels;
	}
	
	public ManifestationTypeModel getErrorTypeModelByName(String name){
		if(errorTypeModels == null){
			// This will happen if there is no error type information at all
			// We don't want to require that, so we just return null
			return null;
		}
		return errorTypeModels.get(name);
	}
}
