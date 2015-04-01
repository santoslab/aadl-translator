package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PortDataTypeMap {
	private final static Map<String, String> Default_Value_Dictionary = 
			Collections.unmodifiableMap(new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;

			{
				put("Integer", "new Integer(0)");
				put("String", "\"\"");
				put("Boolean", "new Boolean(false)");
			}});
	
	public static String getJavaTypeDefaultValueString(String type){
		String defaultValue = Default_Value_Dictionary.get(type);
		if(defaultValue == null) return "0";
		else return defaultValue;
	}
	
	
	private final static Map<String, String> PortDataTypeMapping = 
			Collections.unmodifiableMap(new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;

				{
					put("MDCF_Types::Status", "String");
					put("MDCF_Types::ICE_VMD_Status", "String");

					put("MDCF_Types::SpO2", "Integer");
					put("ICE_Types::ICE_SpO2_Numeric", "Integer");

					put("MDCF_Types::EtCO2", "Integer");
					put("MDCF_Types::RespiratoryRate", "Integer");
					put("MDCF_Types::PulseRate", "Integer");
					put("ICE_Types::ICE_Pulserate_Numeric", "Integer");

					put("MDCF_Types::BolusLoading", "Integer");
					put("MDCF_Types::BolusDose", "Integer");
					put("MDCF_Types::LockOutInterval", "Integer");
					put("MDCF_Types::LockOutTimerStatus", "String");
					put("MDCF_Types::DrugConcentration", "Integer");
					put("MDCF_Types::DrugName", "String");
					put("MDCF_Types::DrugInitialVolume", "Integer");
					put("MDCF_Types::InfusedVolumeStatus", "String");
					put("MDCF_Types::OperationalStatus", "String");
					put("MDCF_Types::WarningLowReservoir", "Boolean");
					put("MDCF_Types::AlarmEmptyReservoir", "Boolean");
				}
			});
	
	public static String getJavaTypeString(String type){
		String javaType = PortDataTypeMapping.get(type);
		if(javaType == null) return "int";
		else return javaType;
	}
	
}
