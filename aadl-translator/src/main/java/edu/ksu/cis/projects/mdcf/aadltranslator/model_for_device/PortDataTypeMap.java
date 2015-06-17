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
				put("Float", "new Float(0.0f)");
				put("String", "\"\"");
				put("Boolean", "new Boolean(false)");
				put("RangeFloat", "new RangeFloat(0.0f,0.0f)");
				put("RangeInteger", "new RangeFloat(0,0)");
				put("RangeAlertFloat", "new RangeAlertFloat(0.0f,0.0f)");
				put("RangeAlertInteger", "new RangeAlertFloat(0,0)");

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
					put("ICE_Types::ICE_VMD_Status", "String");
					put("ICE_Types::ICE_Status", "String");
					put("ICE_Types::ICE_Setting", "Float");

					put("ICE_Types::ICE_SpO2_Numeric", "Float");
					put("ICE_Types::ICE_PulseRate_Numeric", "Float");
					put("ICE_Types::ICE_EtCo2_Numeric", "Float");
					put("ICE_Types::ICE_RespiratoryRate_Numeric", "Float");
					
					put("ICE_Types::ICE_BloodPressure_Mean_Numeric", "Float");
					put("ICE_Types::ICE_BloodPressure_Diastolic_Numeric", "Float");
					put("ICE_Types::ICE_BloodPressure_Systolic_Numeric", "Float");

					
					put("ICE_Types::PCAF_Warning_Below_VTBI_Soft_Limit", "Integer");
					put("ICE_Types::PCAF_Warning_Above_VTBI_Soft_Limit", "Integer");
					put("ICE_Types::PCAF_Warning_Below_Basal_Rate_Soft_Limit", "Integer");
					put("ICE_Types::PCAF_Warning_Above_Basal_Rate_Soft_Limit", "Integer");
					put("ICE_Types::PCAF_Warning_Maximum_Safe_Dose", "Integer");
					put("ICE_Types::PCAF_Warning_Basal_Underinfusion", "Integer");
					put("ICE_Types::PCAF_Warning_Bolus_Underinfusion", "Integer");
					put("ICE_Types::PCAF_Warning_Square_Bolus_Underinfusion", "Integer");
					put("ICE_Types::PCAF_Warning_Battery_Backup", "Integer");
					put("ICE_Types::PCAF_Warning_Low_Battery", "Integer");
					put("ICE_Types::PCAF_Warning_Low_Reservoir", "Integer");
					put("ICE_Types::PCAF_Warning_Long_Pause", "Integer");
					put("ICE_Types::PCAF_Warning_Input_Needed", "Integer");
					put("ICE_Types::PCAF_Alarm_Clinician_Authentication_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Patient_Authentication_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Prescription_Authentication_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Below_VTBI_Hard_Limit", "Integer");

					put("ICE_Types::PCAF_Alarm_Above_VTBI_Hard_Limit", "Integer");
					put("ICE_Types::PCAF_Alarm_Below_Basal_Rate_Hard_Limit", "Integer");
					put("ICE_Types::PCAF_Alarm_Above_Basal_Rate_Hard_Limit", "Integer");
					put("ICE_Types::PCAF_Alarm_Drug_Not_In_Library", "Integer");
					put("ICE_Types::PCAF_Alarm_Open_Door_Alarm", "Integer");
					put("ICE_Types::PCAF_Alarm_Basal_Overinfusion", "Integer");
					put("ICE_Types::PCAF_Alarm_Bolus_Overinfusion", "Integer");
					put("ICE_Types::PCAF_Alarm_Square_Bolus_Overinfusion", "Integer");
					put("ICE_Types::PCAF_Alarm_Alert_Stop_Start", "Integer");

					put("ICE_Types::PCAF_Alarm_Air_In_Line", "Integer");
					put("ICE_Types::PCAF_Alarm_Empty_Reservoir", "Integer");
					put("ICE_Types::PCAF_Alarm_Pump_Overheated", "Integer");
					put("ICE_Types::PCAF_Alarm_Downstream_Occlusion", "Integer");
					put("ICE_Types::PCAF_Alarm_Upstream_Occlusion", "Integer");
					put("ICE_Types::PCAF_Alarm_POST_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Sound_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_RAM_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_ROM_Failure", "Integer");


					put("ICE_Types::PCAF_Alarm_CPU_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Thread_Monitor_Alarm", "Integer");
					put("ICE_Types::PCAF_Alarm_Battery_Failure", "Integer");
					put("ICE_Types::PCAF_Alarm_Voltage_Out_of_Range", "Integer");
					put("ICE_Types::PCAF_Alarm_PowerSupplyFailure", "Integer");
					put("ICE_Types::ICE_PCAF_DrugDeliveryRate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_VolumeToBeInfused_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_LockOutInterval_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_MaxDrugPerHour_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_DrugConcentration_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_MassLoadingDose_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_DrugDiluent_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_DrugName_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_SyringeType_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_PatientLineType_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_DiluentVolume_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_SyringeVolume_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_DrugCode_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_InitialVolumeDrug_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_KVORate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_BolusVolume_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_BolusDeliveryDuration_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_FluidBolusRate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_BolusDrugDeliveryRate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_BolusDose_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_MaxFluidDeliveryRate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_MinFluidDeliveryRate_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_FluidDeliveryRateResolution_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_VolumeResolution_Setting", "Integer");
					put("ICE_Types::ICE_PCAF_FluidDeliveryTime_Status", "Integer");
					put("ICE_Types::ICE_PCAF_StandbyTime_Status", "Integer");
					put("ICE_Types::ICE_PCAF_FluidDeliveryRate_Status", "Integer");
					put("ICE_Types::ICE_PCAF_DeliveredDrugMass_Status", "Integer");
					put("ICE_Types::ICE_PCAF_InfusedVolume_Status", "Integer");
					put("ICE_Types::ICE_PCAF_VolumeRemainingToBeInfused_Status", "Integer");
					put("ICE_Types::ICE_PCAF_TotalDeliveredFluidVolume_Status", "Integer");
					put("ICE_Types::ICE_PCAF_GoodDemandNumber_Status", "Integer");
					put("ICE_Types::ICE_PCAF_TotalDemandNumber_Status", "Integer");
					put("ICE_Types::ICE_PCAF_DoseGrantsPerHour_Status", "Integer");
					put("ICE_Types::ICE_PCAF_DoseRequestsPerHour_Status", "Integer");
					put("ICE_Types::ICE_PCAF_CurrentPumpReservoirAmount_Status", "Integer");
					put("ICE_Types::ICE_PCAF_OperationalMode_Status", "Integer");
					put("ICE_Types::ICE_PCAF_CalculatedFluidPressure_Status", "Integer");
					put("ICE_Types::ICE_PCAF_MeasuredFluidPressure_Status", "Integer");
					put("ICE_Types::ICE_PCAF_LockoutTimer_Status", "Integer");

					put("ICE_Types::ICE_PCAF_OperationalStatus_Status", "Integer");
					put("ICE_Types::ICE_PCAF_VolumeInfusedActualTotal_Status", "Integer");
					put("ICE_Types::ICE_PCAF_RemainingBatteryTime_Status", "Integer");
					put("ICE_Types::ICE_PCAF_UsingBatteryPower_Status", "Integer");
					put("ICE_Types::ICE_PCAF_UpstreamFlow_Status", "Integer");
					put("ICE_Types::ICE_PCAF_DownstreamFlow_Status", "Integer");
					put("ICE_Types::ICE_PCAF_Start_Action", "Integer");
					put("ICE_Types::ICE_PCAF_Pause_Action", "Integer");
					put("ICE_Types::ICE_PCAF_Stop_Action", "Integer");
					put("ICE_Types::ICE_PCAF_Prime_Action", "Integer");
					put("ICE_Types::ICE_PCAF_ResetAdministrationSession_Action", "Integer");
					put("ICE_Types::ICE_PCAF_ResetVolumeInfusedActualTotal_Action", "Integer");
					put("ICE_Types::ICE_PCAF_PatientBolus_Action", "Integer");
					put("ICE_Types::ICE_PCAF_SquareBolus_Action", "Integer");
					put("ICE_Types::ICE_SimplePCA_LockOutTimer_Status", "Integer");
					put("ICE_Types::ICE_SimplePCA_PumpState_Status", "Integer");
					put("ICE_Types::ICE_SimplePCA_LoadingBolus_Setting", "Integer");

					put("ICE_Types::ICE_FloatRangeValueAlert", "RangeAlertFloat");
					put("ICE_Types::ICE_FloatRangeSetting", "RangeFloat");


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
