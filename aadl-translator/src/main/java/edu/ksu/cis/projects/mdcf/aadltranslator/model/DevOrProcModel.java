package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ProcessType;

public class DevOrProcModel extends ComponentModel<TaskModel, ProcessConnectionModel>{
	
	public DevOrProcModel(){
		super();
	}
	
	public boolean isDisplay(){
		return processType == ProcessType.DISPLAY;
	}
	
	public boolean isPseudoDevice(){
		return processType == ProcessType.PSEUDODEVICE;
	}
	
	public boolean isLogic(){
		return processType == ProcessType.LOGIC;
	}
	
	@Override
	public void addChild(String name, TaskModel cm) throws DuplicateElementException {
		if (children.containsKey(name))
			throw new DuplicateElementException(
					"Tasks cannot have the same name");
		children.put(name, cm);
	}
	
	@Override
	public TaskModel getChild(String name) {
		return (TaskModel) children.get(name);
	}
	
	@Override
	public HashMap<String, TaskModel> getChildren() {
		HashMap<String, TaskModel> ret = new HashMap<String, TaskModel>();
		for(String childName : children.keySet()){
			ret.put(childName, (TaskModel)children.get(childName));
		}
		return ret;
	}
	
	public Map<String, TaskModel> getSporadicTasks() {
		return Maps.filterValues(this.getChildren(), ModelUtil.sporadicTaskFilter);
	}
	
	public Map<String, TaskModel> getPeriodicTasks() {
		return Maps.filterValues(this.getChildren(), ModelUtil.periodicTaskFilter);
	}

	@Override
	public ProcessConnectionModel getChannelByName(String connectionName) {
		return channels.get(connectionName);
	}

	@Override
	public HashMap<String, ProcessConnectionModel> getChannels() {
		return channels;
	}

	@Override
	public void addConnection(String name, ProcessConnectionModel cm) {
		channels.put(name, cm);
	}
	
}
