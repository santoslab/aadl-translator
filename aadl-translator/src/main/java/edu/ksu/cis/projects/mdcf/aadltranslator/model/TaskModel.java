package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;

public class TaskModel extends ComponentModel<MethodModel, ConnectionModel>{
	
	private PortModel trigPort = null;

	/**
	 * The name that the task uses to refer to the value that arrives via the
	 * triggering port
	 */
	private String trigPortLocalName;

	/**
	 * This is a list of globals read by this task.
	 */
	private ArrayList<VariableModel> incomingGlobals;

	/**
	 * This is a list of globals written by this task.
	 */
	private ArrayList<VariableModel> outgoingGlobals;

	/**
	 * This task's period in milliseconds
	 */
	private int period;

	/**
	 * This task's deadline in milliseconds
	 */
	private int deadline;

	/**
	 * The type of this task's trigger -- periodic, sporadic, etc.
	 */
	private boolean sporadic;

	/**
	 * This task's worst case execution time
	 */
	private int wcet;

	public TaskModel(String name) {
		super();
		incomingGlobals = new ArrayList<>();
		outgoingGlobals = new ArrayList<>();
		this.name = name;
	}

	public void setTrigPort(PortModel port) throws NotImplementedException{
		if(this.trigPort == null){
			this.trigPort = port;
		} else {
			throw new NotImplementedException("The task" + name
					+ " with triggering port " + this.trigPort.getName()
					+ " tried to add a second port named " + port.getName());
		}
	}
	
//	public void setTrigPortInfo(String portName, String portType,
//			String localName, boolean isEventTriggered)
//			throws NotImplementedException {
//		if (this.trigPortName == null) {
//			trigPortName = portName;
//			trigPortType = portType;
//			trigPortLocalName = localName;
//			eventTriggered = isEventTriggered;
//		} else {
//			throw new NotImplementedException("The task" + name
//					+ " with triggering port " + this.trigPortName
//					+ " tried to add a second port named " + trigPortName);
//		}
//	}

	public void addIncGlobal(VariableModel vm) {
		incomingGlobals.add(vm);
	}

	public void addOutGlobal(VariableModel vm) {
		outgoingGlobals.add(vm);
	}
	
	public String getTrigPortName() {
		return trigPort.getName();
	}

	public String getTrigPortType() {
		return trigPort.getType();
	}

	public String getTrigPortLocalName() {
		return trigPortLocalName;
	}

	public ArrayList<VariableModel> getIncomingGlobals() {
		return incomingGlobals;
	}

	public ArrayList<VariableModel> getOutgoingGlobals() {
		return outgoingGlobals;
	}

	public int getPeriod() {
		return period;
	}

	public int getDeadline() {
		return deadline;
	}

	public int getWcet() {
		return wcet;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}

	public void setWcet(int wcet) {
		this.wcet = wcet;
	}
	
	public PortModel getTrigPort(){
		return trigPort;
	}

	public boolean isSporadic() {
		return sporadic;
	}
	
	public boolean isPeriodic() {
		return !sporadic;
	}

	public void setSporadic(boolean sporadic) {
		this.sporadic = sporadic;
	}
	
	public void setTrigPortLocalName(String name) {
		trigPortLocalName = name;
	}
}
