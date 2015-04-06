package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ProcessType;

public class DevOrProcModel extends ComponentModel<TaskModel, ProcessConnectionModel>{
	
	protected ProcessType processType;
	
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
	
	public Map<String, TaskModel> getSporadicTasks() {
		return Maps.filterValues(this.getChildren(), ModelUtil.sporadicTaskFilter);
	}
	
	public Map<String, TaskModel> getPeriodicTasks() {
		return Maps.filterValues(this.getChildren(), ModelUtil.periodicTaskFilter);
	}	
}
