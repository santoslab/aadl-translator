package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentKind;

public class DeviceModel extends ComponentModel {
	public DeviceModel(){
		super();
		kind = ComponentKind.PSEUDODEVICE;
	}
}
