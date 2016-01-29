package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.Map;
import java.util.stream.Collectors;

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
		return children.entrySet()
				.stream()
				.filter(p -> p.getValue().isSporadic())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}
	
	public Map<String, TaskModel> getPeriodicTasks() {
		return children.entrySet()
				.stream()
				.filter(p -> !p.getValue().isSporadic())
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}
}
