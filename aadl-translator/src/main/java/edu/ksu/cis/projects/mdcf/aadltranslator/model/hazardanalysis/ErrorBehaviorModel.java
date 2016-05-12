package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.LinkedHashSet;
import java.util.Set;

public class ErrorBehaviorModel {
	Set<String> states = new LinkedHashSet<>();
	
	public void addState(String stateName){
		states.add(stateName);
	}

}
