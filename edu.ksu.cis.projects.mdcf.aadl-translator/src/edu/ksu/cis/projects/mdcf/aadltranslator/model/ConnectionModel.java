package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class ConnectionModel {
	private IComponentModel publisher;
	private IComponentModel subscriber;
	
	/**
	 * The name of the port the publisher uses to listen to this channel
	 */
	private String pubPortName;
	
	/**
	 * The name of the port the subscriber uses to publish to this channel
	 */
	private String subPortName;
}
