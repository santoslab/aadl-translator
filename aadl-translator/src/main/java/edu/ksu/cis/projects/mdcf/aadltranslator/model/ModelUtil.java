package edu.ksu.cis.projects.mdcf.aadltranslator.model;

public class ModelUtil {
	public static enum ProcessType {
		PSEUDODEVICE, DISPLAY, LOGIC
	};

	public static enum ComponentType {
		SENSOR, ACTUATOR, CONTROLLER, CONTROLLEDPROCESS, AGGREGATION, TOP
	};

	public static enum ManifestationType {
		CONTENT, HIGH, LOW, TIMING, EARLY, LATE, HALTED, ERRATIC, VIOLATEDCONSTRAINT 
	};
	
	public static interface HandlingApproach { };
	
	public static enum RuntimeErrorHandlingApproach implements HandlingApproach{
		NOTSPECIFIED, ROLLBACK, ROLLFORWARD, COMPENSATION,
	};
	
	public static enum RuntimeFaultHandlingApproach implements HandlingApproach{
		NOTSPECIFIED, DIAGNOSIS, ISOLATION, RECONFIGURATION, REINITIALIZATION,
	};
	
	public static interface DetectionApproach {	};
	
	public static enum RuntimeErrorDetectionApproach implements DetectionApproach{
		NOTSPECIFIED, CONCURRENT, PREEMPTIVE,
	};
	
	public static enum DesignTimeFaultDetectionApproach implements DetectionApproach{
		NOTSPECIFIED, STATICANALYSIS, THEOREMPROVING, MODELCHECKING, SYMBOLICEXECUTION, TESTING,
	};
}
