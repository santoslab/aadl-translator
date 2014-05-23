package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentKind;

public class DeviceModel extends ComponentModel {
	
	private HashBiMap<String, String> inToOutPortNames = HashBiMap.create();
	
	public DeviceModel(){
		super();
		kind = ComponentKind.PSEUDODEVICE;
	}
	
	public void addOutPortName(String inPortName, String outPortName){
		inToOutPortNames.put(inPortName, outPortName);
	}
	
	public HashBiMap<String, String> getOutPortNames(){
		return inToOutPortNames;
	}
	
	public BiMap<String, String> getInPortNames(){
		return inToOutPortNames.inverse();
	}
}
