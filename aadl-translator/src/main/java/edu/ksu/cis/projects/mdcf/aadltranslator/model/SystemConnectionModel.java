package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class SystemConnectionModel extends ConnectionModel {

	/**
	 * True if this connection has a device as its publisher
	 */
	private boolean devicePublished;

	/**
	 * True if this connection has a device as its subscriber
	 */
	private boolean deviceSubscribed;

	/**
	 * The maximum tolerable latency of the channel, minimum latency is assumed
	 * to be zero
	 */
	private int channelDelay;

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

	public int getChannelDelay() {
		return channelDelay;
	}

	public void setChannelDelay(int channelDelay) {
		this.channelDelay = channelDelay;
	}
}
