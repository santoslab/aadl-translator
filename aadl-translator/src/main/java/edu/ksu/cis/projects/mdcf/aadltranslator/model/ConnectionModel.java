package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;

public class ConnectionModel {
	private ComponentModel publisher;
	private ComponentModel subscriber;
	
	/**
	 * Maps an STPA guideword to any associated occurrences
	 */
	private HashMap<String, HashSet<OccurrenceModel>> occurrenceMap = new HashMap<>();
	
	private boolean devicePublished;
	private boolean deviceSubscribed;
	
	/**
	 * The name the app developer uses to refer to this channel
	 */
	private String name;
	
	/**
	 * The name the app developer uses to refer to the publishing component
	 */
	private String pubName;
	
	/**
	 * The name the app developer uses to refer to the subscribing component
	 */
	private String subName;
	
	/**
	 * The name of the port the publisher uses to listen to this channel
	 */
	private String pubPortName;

	/**
	 * The name of the port the subscriber uses to publish to this channel
	 */
	private String subPortName;
	
	private int channelDelay;
	
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

	public boolean isDevicePublished() {
		return devicePublished;
	}

	public void setDevicePublished(boolean devicePublished) {
		this.devicePublished = devicePublished;
	}

	public boolean isDeviceSubscribed() {
		return deviceSubscribed;
	}

	public void setDeviceSubscribed(boolean deviceSubscribed) {
		this.deviceSubscribed = deviceSubscribed;
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

	public int getChannelDelay() {
		return channelDelay;
	}

	public void setChannelDelay(int channelDelay) {
		this.channelDelay = channelDelay;
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
	
	public void addOccurrence(OccurrenceModel occurrence) {
		String key = occurrence.getKeyword().toString().toUpperCase();
		if(occurrenceMap.containsKey(key)){
			occurrenceMap.get(key).add(occurrence);
		} else {
			HashSet<OccurrenceModel> set = new HashSet<>();
			set.add(occurrence);
			occurrenceMap.put(key, set);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getExchangeName() {
		// Note: This method assumes device <-> device connections are disallowed
		if(devicePublished)
			return this.publisher.getPortByName(pubPortName+"Out").getExchangeName();
		else if(deviceSubscribed)
			return this.subscriber.getPortByName(subPortName+"In").getExchangeName();
		else
			return null;
	}
}
