package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;

public class TaskModel {

	/**
	 * The name of this task
	 */
	private String name;

	/**
	 * The name of the triggering port
	 */
	private String trigPortName;

	/**
	 * The type of the triggering port
	 */
	private String trigPortType;

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
	 * Whether or not this task is triggered by an event (ie, a message without
	 * a payload)
	 */
	private boolean eventTriggered;

	/**
	 * This task's worst case execution time
	 */
	private int wcet;

	// /**
	// * The names of called methods mapped to the variables that make up the
	// * parameter list
	// */
	// private HashMap<String, ArrayList<String>> methodParameters;
	//
	// /**
	// * This maps the task's name for a method to its real name
	// */
	// private HashMap<String, String> methodNames;
	//
	private ArrayList<CallModel> callSequence;

	public TaskModel(String name) {
		incomingGlobals = new ArrayList<>();
		outgoingGlobals = new ArrayList<>();
		callSequence = new ArrayList<>();
		// methodParameters = new HashMap<>();
		// methodNames = new HashMap<>();
		trigPortName = null;
		this.name = name;
	}

	public void setName(String taskName) {
		this.name = taskName;
	}

	public void setTrigPortInfo(String portName, String portType,
			String localName, boolean isEventTriggered)
			throws NotImplementedException {
		if (this.trigPortName == null) {
			trigPortName = portName;
			trigPortType = portType;
			trigPortLocalName = localName;
			eventTriggered = isEventTriggered;
		} else {
			throw new NotImplementedException("The task" + name
					+ " with triggering port " + this.trigPortName
					+ " tried to add a second port named " + trigPortName);
		}
	}

	public void addIncGlobal(VariableModel vm) {
		incomingGlobals.add(vm);
	}

	public void addOutGlobal(VariableModel vm) {
		outgoingGlobals.add(vm);
	}

	public void addCalledMethod(String internalName, String externalName) {
		// TODO: Handle pre-existing method
		// methodParameters.put(internalName, new ArrayList<String>());
		// methodNames.put(internalName, externalName);
		callSequence.add(new CallModel(internalName, externalName));
	}

	/**
	 * This gets the name of a method from a task's local name for it
	 * 
	 * @param internalName
	 *            The task's name for a method
	 * @return The method's actual name
	 */
	public String getMethodProcessName(String internalName) {
		for (CallModel call : callSequence) {
			if (call.getInternalName().equals(internalName)) {
				return call.getExternalName();
			}
		}
		return null;
	}

	public void addParameterToCalledMethod(String internalName, String formal,
			String actual) {
		// TODO: Handle wrong method name
		// methodParameters.get(methodName).add(parameter);
		for (CallModel call : callSequence) {
			if (call.getInternalName().equals(internalName)) {
				call.addParam(formal, actual);
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getTrigPortName() {
		return trigPortName;
	}

	public String getTrigPortType() {
		return trigPortType;
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

	public ArrayList<CallModel> getCallSequence() {
		return callSequence;
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

	/*-
	public void addReturnToCalledMethod(String internalName, String methodName,
			String taskName) {

	}
	 */

	public boolean isEventTriggered() {
		return eventTriggered;
	}

	public void setEventTriggered(boolean eventTriggered) {
		this.eventTriggered = eventTriggered;
	}

	public boolean isSporadic() {
		return sporadic;
	}

	public void setSporadic(boolean sporadic) {
		this.sporadic = sporadic;
	}

	/*-
	 public HashMap<String, ArrayList<String>> getMethodParameters() {
		return methodParameters;
	}
	 */
}
