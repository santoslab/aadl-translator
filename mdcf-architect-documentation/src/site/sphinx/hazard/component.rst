.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-components:

##############################
Hazard Analysis for Components
##############################

Some explanatory text about the the annotations for components, and credit / links to EMV2 go here.

.. code-block:: aadl
   :linenos:

   package PCA_Shutoff_Errors
   public
   with MAP_Errors, PCA_Shutoff_Error_Properties, MAP_Error_Properties,
      PCA_Shutoff;

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

   end PCA_Shutoff_Errors; 


.. code-block:: aadl
   :linenos:
   
   package PulseOx_Interface
   public
   with PCA_Shutoff_Types, PCA_Shutoff_Errors, EMV2, MAP_Error_Properties, PCA_Shutoff;
      device ICEpoInterface
      features
         SpO2 : out event data port PCA_Shutoff_Types::SpO2;
      annex EMV2 {** 
         use types PCA_Shutoff_Errors;
         error propagations
            SpO2 : out propagation {SpO2ValueHigh};
            flows
               SpO2UnDetectableHighValueFlowSource : error source SpO2 {SpO2ValueHigh};
         end propagations;
      **};
      end ICEpoInterface;

      device implementation ICEpoInterface.imp
      end ICEpoInterface.imp;

   end PulseOx_Interface;

.. code-block:: aadl
   :linenos:

   package PCA_Shutoff_Logic
   public
   with PCA_Shutoff_Types, PCA_Shutoff_Properties, MAP_Properties;

      process ICEpcaShutoffProcess
      features
         SpO2 : in event data port PCA_Shutoff_Types::SpO2;      
         CommandPumpNormal : out event data port PCA_Shutoff_Types::PumpNormalCommand;
      properties
         MAP_Properties::Component_Type => logic;
      annex EMV2 {** 
         use types PCA_Shutoff_Errors;
         error propagations
            SpO2 : in propagation {SpO2ValueHigh};
            CommandPumpNormal : out propagation {InadvertentPumpNormally};
            flows
               HighSpO2LeadsToOD : error path SpO2{SpO2ValueHigh} -> CommandPumpNormal{InadvertentPumpNormally};
         end propagations;
      **};
      end ICEpcaShutoffProcess;

      -- Process implementation redacted
   end PCA_Shutoff_Logic;

.. code-block:: aadl
   :linenos:

   package PCAPump_Interface
   public
   with PCA_Shutoff_Types;
      device ICEpcaInterface
      features
         PumpNormally : in event data port PCA_Shutoff_Types::PumpNormalCommand;
      annex EMV2 {**
         use types PCA_Shutoff_Errors;
         error propagations
            PumpNormally : in propagation {InadvertentPumpNormally};
            flows
               ODCommand : error sink PumpNormally {InadvertentPumpNormally};   
         end propagations;
      **};
      end ICEpcaInterface;

      device implementation ICEpcaInterface.imp
      end ICEpcaInterface.imp;

   end PCAPump_Interface;