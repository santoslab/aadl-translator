package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import org.osate.aadl2.PortCategory;

public class PortModel extends FeatureModel {
	private PortCategory category;
	private String type;
	private int minPeriod;
	private int maxPeriod;
	private String exchangeName;
	private String containingComponentName;

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
}
