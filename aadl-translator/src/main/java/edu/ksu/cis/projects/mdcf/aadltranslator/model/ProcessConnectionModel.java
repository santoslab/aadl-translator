package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class ProcessConnectionModel extends ConnectionModel {

	/**
	 * True if this connection's source is the process itself (ie, incoming
	 * messages arrive from outside of the process)
	 */
	private boolean processToThread;

	public boolean isProcessToThread() {
		return processToThread;
	}

	public void setProcessToThread(boolean processToThread) {
		this.processToThread = processToThread;
	}
}
