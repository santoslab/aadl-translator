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

	// Type name -> Child name Model
	private HashMap<String, DevOrProcModel> typeToComponent;

	private String timestamp;
	private String hazardReportContext;
	private HashSet<AbbreviationModel> hazardReportAbbreviations;
	private HashSet<String> hazardReportAssumptions;
	private HashMap<String, String> hazardReportDiagrams;

	public SystemModel() {
		super();
		typeToComponent = new HashMap<>();
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
		HashMap<String, DevOrProcModel> preCast = new HashMap<>(Maps.filterValues(children, ModelUtil.logicComponentFilter));
		HashMap<String, ProcessModel> ret = new HashMap<>();
		for(String elemName : preCast.keySet()){
			ret.put(elemName, (ProcessModel) preCast.get(elemName));
		}
		return ret;
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

	public HashMap<String, String> getHazardReportDiagrams() {
		return hazardReportDiagrams;
	}
}
