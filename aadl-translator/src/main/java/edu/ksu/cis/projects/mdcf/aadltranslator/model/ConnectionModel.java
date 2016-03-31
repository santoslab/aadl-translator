package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.OccurrenceModel;

public class ConnectionModel {
	protected ComponentModel publisher;
	protected ComponentModel subscriber;
	
	/**
	 * Maps an STPA guideword to any associated occurrences
	 */
	protected HashMap<String, HashSet<OccurrenceModel>> occurrenceMap = new HashMap<>();
	
	/**
	 * The name the app developer uses to refer to this connection
	 */
	protected String name;
	
	/**
	 * The name the app developer uses to refer to the publishing component
	 */
	protected String pubName;
	
	/**
	 * The name the app developer uses to refer to the subscribing component
	 */
	protected String subName;
	
	/**
	 * The name of the port the publisher uses to listen to this channel
	 */
	protected String pubPortName;

	/**
	 * The name of the port the subscriber uses to publish to this channel
	 */
	protected String subPortName;
	
	public void setPublisher(ComponentModel publisher) {
		this.publisher = publisher;
	}

	public void setSubscriber(ComponentModel subscriber) {
		this.subscriber = subscriber;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public void setPubPortName(String pubPortName) {
		this.pubPortName = pubPortName;
	}

	public void setSubPortName(String subPortName) {
		this.subPortName = subPortName;
	}

	public ComponentModel getPublisher() {
		return publisher;
	}

	public ComponentModel getSubscriber() {
		return subscriber;
	}

	public String getPubName() {
		return pubName;
	}

	public String getSubName() {
		return subName;
	}

	public String getPubPortName() {
		return pubPortName;
	}

	public String getSubPortName() {
		return subPortName;
	}
	
	public HashSet<OccurrenceModel> getOccurrences() {
		HashSet<OccurrenceModel> allOccurrences = new HashSet<>();
		for(String k : occurrenceMap.keySet())
			allOccurrences.addAll(occurrenceMap.get(k));
		return allOccurrences;
	}
	
	public HashMap<String, HashSet<OccurrenceModel>> getOccurrenceMap() {
		return occurrenceMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExchangeName() {
		// Note: This method assumes device <-> device connections are disallowed
		if(this.publisher instanceof DeviceModel)
			return this.publisher.getPortByName(pubPortName+"Out").getExchangeName();
		else if(this.subscriber instanceof DeviceModel)
			return this.subscriber.getPortByName(subPortName+"In").getExchangeName();
		else
			return null;
	}
}
