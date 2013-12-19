package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;

public class TaskModel {

	/**
	 * The name of this task
	 */
	private String taskName;

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
	 * The names (in order) of methods that will be called when this task is
	 * executed
	 */
	private ArrayList<MethodModel> callSequence;

	public TaskModel(String name) {
		incomingGlobals = new ArrayList<>();
		outgoingGlobals = new ArrayList<>();
		callSequence = new ArrayList<>();
		trigPortName = null;
		taskName = name;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTrigPortInfo(String portName, String portType,
			String localName) throws NotImplementedException {
		if (this.trigPortName == null) {
			trigPortName = portName;
			trigPortType = portType;
			trigPortLocalName = localName;
		} else {
			throw new NotImplementedException("The task" + taskName
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

	public String getTaskName() {
		return taskName;
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

	public ArrayList<MethodModel> getCallSequence() {
		return callSequence;
	}
}
