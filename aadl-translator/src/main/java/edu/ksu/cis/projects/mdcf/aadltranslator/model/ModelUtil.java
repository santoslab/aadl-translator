package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class ModelUtil {
	public static enum ProcessType {
		PSEUDODEVICE, DISPLAY, LOGIC
	};

	public static enum ComponentType {
		SENSOR, ACTUATOR, CONTROLLER, CONTROLLEDPROCESS, AGGREGATION, TOP
	};

	public static enum Keyword {
		NOTPROVIDING, PROVIDING, EARLY, LATE, APPLIEDTOOLONG, STOPPEDTOOSOON, VALUELOW, VALUEHIGH, PARAMSMISSING, PARAMSWRONG, PARAMSOUTOFORDER
	};
	
	public static enum ManifestationType {
		CONTENT, HIGH, LOW, TIMING, EARLY, LATE, HALTED, ERRATIC, VIOLATEDCONSTRAINT 
	};
}
