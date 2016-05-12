package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.osate.aadl2.PortCategory;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;

public class PortModel {
	private String name;
	private boolean subscribe;
	private PortCategory category;
	private String type;
	private int minPeriod;
	private int maxPeriod;
	private String exchangeName;
	private String containingComponentName;

	/**
	 * Error types entering or leaving this port
	 */
	private Map<String, ManifestationTypeModel> propagatableErrors = new LinkedHashMap<>();

	/**
	 * The actual propagations that describe how the incoming or outgoing errors
	 * are grouped
	 */
	private Map<String, PropagationModel> propagations = new LinkedHashMap<>();

	public String getName() {
		return name;
	}

	public boolean isSubscribe() {
		return subscribe;
	}

	public boolean isData() {
		return category == PortCategory.DATA;
	}

	public boolean isEventData() {
		return category == PortCategory.EVENT_DATA;
	}

	public boolean isEvent() {
		return category == PortCategory.EVENT;
	}

	public void setEvent() {
		this.category = PortCategory.EVENT;
	}

	public void setData() {
		this.category = PortCategory.DATA;
	}

	public void setEventData() {
		this.category = PortCategory.EVENT_DATA;
	}

	public String getType() {
		return type;
	}

	public int getMinPeriod() {
		return minPeriod;
	}

	public int getMaxPeriod() {
		return maxPeriod;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setName(String portName) {
		this.name = portName;
	}

	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setMinPeriod(int minPeriod) {
		this.minPeriod = minPeriod;
	}

	public void setMaxPeriod(int maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	public PortCategory getCategory() {
		return category;
	}

	public void setCategory(PortCategory category) {
		this.category = category;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public void setContainingComponentName(String containingComponentName) {
		this.containingComponentName = containingComponentName;
	}

	public String getContainingComponentName() {
		return containingComponentName;
	}

	public void addPropagatableErrors(Collection<ManifestationTypeModel> propagations) {
		for (ManifestationTypeModel errType : propagations) {
			propagatableErrors.put(errType.getName(), errType);
		}
	}
	
	public Map<String, ManifestationTypeModel> getPropagatableErrors() {
		return propagatableErrors;
	}

	public void addPropagation(PropagationModel propModel) {
		propagations.put(propModel.getName(), propModel);
	}

	public ManifestationTypeModel getPropagatableErrorByName(String name) {
		return propagatableErrors.get(name);
	}
	
	public PropagationModel getPropagationByName(String name){
		return propagations.get(name);
	}
}
