package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;

public class TaskModel {
	private String taskName;
	private String trigPortName;
	private String trigPortType;

	/**
	 * This stores a mapping from global name -> global type
	 */
	private HashMap<String, String> incomingGlobals;
	private ArrayList<MethodModel> callSequence;

	public TaskModel() {
		incomingGlobals = new HashMap<>();
		callSequence = new ArrayList<>();
		trigPortName = null;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTrigPortName(String trigPortName) throws NotImplementedException {
		if (this.trigPortName == null){
			this.trigPortName = trigPortName;
		} else{
			throw new NotImplementedException("A task with triggering port "
					+ this.trigPortName + " tried to add a second port named "
					+ trigPortName);
		}
	}
}
