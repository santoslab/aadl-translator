.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-components:

##############################
Hazard Analysis for Components
##############################

Some explanatory text about the the annotations for components, and credit / links to EMV2 go here.

.. code-block:: aadl
   :linenos:

   annex EMV2
   {**
      error types
      -- These errors aren't associated with unsafe states, but they're here for completeness
      SpO2ValueLow : type extends MAP_Errors::WrongPhysioDataError;
      RespiratoryRateLow : type extends MAP_Errors::WrongPhysioDataError;
      ETCO2ValueHigh : type extends MAP_Errors::WrongPhysioDataError;
   
      -- These errors will cause the app to logic to think the patient is healthy when she isn't
      SpO2ValueHigh : type extends MAP_Errors::WrongPhysioDataError;
      RespiratoryRateHigh : type extends MAP_Errors::WrongPhysioDataError;
      ETCO2ValueLow : type extends MAP_Errors::WrongPhysioDataError;
   
      -- These are errors with devices
      DeviceAlarmFailsOn : type extends MAP_Errors::PhysioDeviceErrorCommission;
      DeviceAlarmFailsOff : type extends MAP_Errors::PhysioDeviceErrorOmission;
      BadInfoDisplayedToClinician : type extends MAP_Errors::WrongInfoDisplayedError;
      InadvertentPumpNormally : type extends MAP_Errors::AppCommission;
      InadvertentPumpMinimally : type extends MAP_Errors::AppOmission;
      
      end types;
   **};


.. code-block:: aadl
   :linenos:
   
   annex EMV2 {** 
      use types PCA_Shutoff_Errors;
      error propagations
         SpO2 : out propagation {SpO2ValueHigh};
         SpO2 : not out propagation {SpO2ValueLow};
         DeviceError : out propagation {DeviceAlarmFailsOn, DeviceAlarmFailsOff};
         flows
            SpO2UnDetectableHighValueFlowSource : error source SpO2 {SpO2ValueHigh};
            DeviceAlarmNotSent : error source DeviceError {DeviceAlarmFailsOn};
            DeviceAlarmErroneouslySent : error source DeviceError {DeviceAlarmFailsOff};
      end propagations;
   **};

.. code-block:: aadl
   :linenos:

   annex EMV2 {** 
      use types PCA_Shutoff_Errors;
      error propagations
         SpO2 : in propagation {SpO2ValueHigh};
         ETCO2 : in propagation {ETCO2ValueHigh};
         RespiratoryRate : in propagation {RespiratoryRateLow, RespiratoryRateHigh};
         CommandPumpNormal : out propagation {InadvertentPumpNormally};
         flows
            HighSpO2LeadsToOD : error path SpO2{SpO2ValueHigh} -> CommandPumpNormal{InadvertentPumpNormally};
            HighETCO2LeadsToOD : error path ETCO2{ETCO2ValueHigh} -> CommandPumpNormal{InadvertentPumpNormally};
            LowRRLeadsToOD : error path RespiratoryRate{RespiratoryRateLow, RespiratoryRateHigh} -> CommandPumpNormal{InadvertentPumpNormally};
      end propagations;
   **};

.. code-block:: aadl
   :linenos:

   annex EMV2 {**
      use types PCA_Shutoff_Errors;
      error propagations
         -- Incorrect tickets could arrive via the PumpNormally port
         PumpNormally : in propagation {InadvertentPumpNormally};
         flows
            -- Once the inadvertent ticket arrives, it doesn't cause any
            --  more failures (within the app boundary)
            ODCommand : error sink PumpNormally {InadvertentPumpNormally};   
      end propagations;
   **};