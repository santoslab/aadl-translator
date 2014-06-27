package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class ConnectionModel {
	private ComponentModel publisher;
	private ComponentModel subscriber;
	
	/**
	 * Maps a connection error name to its associated occurrence
	 */
	private HashMap<String, OccurrenceModel> occurrenceMap = new HashMap<>();
	
	private boolean devicePublished;
	private boolean deviceSubscribed;
	
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
	
	public HashMap<String, OccurrenceModel> getOccurrences() {
		return occurrenceMap;
	}
	
	public void addOccurrence(String connErrName, OccurrenceModel occurrence) throws DuplicateElementException {
		if(!occurrenceMap.containsKey(occurrence)){
			occurrenceMap.put(connErrName, occurrence);
	 	} else {
	 		throw new DuplicateElementException("Each connection error must have exactly one occurrence");
	 	}
	}
	
	public OccurrenceModel getOccurrence(String connErrName) {
		return occurrenceMap.get(connErrName);
	}
}
