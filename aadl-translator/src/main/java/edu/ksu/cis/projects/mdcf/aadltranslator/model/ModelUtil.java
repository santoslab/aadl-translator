package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;

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
		CONTENTHIGH
	};

//	public final static PropagationModel FLOW_SOURCE = new PropagationModel(
//			false, null, null);
//	public final static PropagationModel FLOW_SINK = new PropagationModel(true,
//			null, null);
}
