package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ProcessType;

public class DeviceModel extends DevOrProcModel {
	
	private HashBiMap<String, String> inToOutPortNames = HashBiMap.create();
	
	public DeviceModel(){
		super();
		processType = ProcessType.PSEUDODEVICE;
	}
	
	public void setComponentType(String componentType){
		this.componentType = ComponentType.valueOf(componentType.toUpperCase());
	}
	
	private void addOutPortName(String inPortName, String outPortName){
		inToOutPortNames.put(inPortName, outPortName);
	}
	
	public BiMap<String, String> getOutPortNames(){
		return inToOutPortNames;
	}
	
	public BiMap<String, String> getInPortNames(){
		return inToOutPortNames.inverse();
	}
	
	@Override
	public void addFeature(FeatureModel fm) {
		if(!(fm instanceof PortModel)){
			ports.put(fm.getName(), fm);
			return;
		}
		PortModel pm = (PortModel) fm;
		PortModel mirror = new PortModel();
		String pm_suffix = pm.isSubscribe() ? "In" : "Out";
		String mirror_suffix = pm.isSubscribe() ? "Out" : "In";
		
		
		mirror.setCategory(pm.getCategory());
		mirror.setExchangeName(pm.getExchangeName());
		mirror.setMaxPeriod(pm.getMaxPeriod());
		mirror.setMinPeriod(pm.getMinPeriod());
		mirror.setName(pm.getName() + mirror_suffix);
		mirror.setSubscribe(!pm.isSubscribe());
		mirror.setType(pm.getType());
		
		pm.setName(pm.getName() + pm_suffix);
		
		ports.put(mirror.getName(), mirror);
		ports.put(pm.getName(), pm);
		
		if(pm.isSubscribe())
			addOutPortName(pm.getName(), mirror.getName());
		else
			addOutPortName(mirror.getName(), pm.getName());
	}
	
//	@Override
//	public Map<String, PortModel> getPorts(){
//		return ports;
//	}
}
