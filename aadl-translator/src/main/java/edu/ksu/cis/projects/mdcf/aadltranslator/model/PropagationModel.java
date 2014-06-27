package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class PropagationModel {
	private boolean in;
	private ImpactModel error;
	private ConnectionModel conn;
	
	public PropagationModel(boolean in, ImpactModel error, ConnectionModel conn) {
		super();
		this.in = in;
		this.error = error;
		this.conn = conn;
	}

	public boolean isIn() {
		return in;
	}

	public void setIn() {
		this.in = true;
	}

	public boolean isOut() {
		return !in;
	}

	public void setOut() {
		this.in = false;
	}

	public ImpactModel getError() {
		return error;
	}

	public void setError(ImpactModel error) {
		this.error = error;
	}

	public ConnectionModel getConn() {
		return conn;
	}

	public void setConn(ConnectionModel conn) {
		this.conn = conn;
	}
	
}
