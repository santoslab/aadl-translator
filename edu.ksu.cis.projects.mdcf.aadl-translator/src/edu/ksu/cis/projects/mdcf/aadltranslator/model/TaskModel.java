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

	public TaskModel() {
		incomingGlobals = new ArrayList<>();
		outgoingGlobals = new ArrayList<>();
		callSequence = new ArrayList<>();
		trigPortName = null;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTrigPortName(String trigPortName)
			throws NotImplementedException {
		if (this.trigPortName == null) {
			this.trigPortName = trigPortName;
		} else {
			throw new NotImplementedException("A task with triggering port "
					+ this.trigPortName + " tried to add a second port named "
					+ trigPortName);
		}
	}

	public void addIncGlobal(VariableModel vm) {
		incomingGlobals.add(vm);
	}

	public void addOutGlobal(VariableModel vm) {
		outgoingGlobals.add(vm);
	}
}
