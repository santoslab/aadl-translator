package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class ConnectionModel {
	private IComponentModel publisher;
	private IComponentModel subscriber;
	
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
	
	public void setPublisher(IComponentModel publisher) {
		this.publisher = publisher;
	}

	public void setSubscriber(IComponentModel subscriber) {
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

	public IComponentModel getPublisher() {
		return publisher;
	}

	public IComponentModel getSubscriber() {
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
}
