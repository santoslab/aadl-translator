.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-occurrence:

#######################
The Occurrence Property
#######################

The *occurrence* property is designed to do associate three main things with one particular way a control action could be provided in an unsafe way:

1. Relevant STPA :ref:`fundamentals<hazard-analysis-fundamentals>`
2. A human-readable description of the fault's cause and compensation
3. A specific fault and control action

.. construct:: EMV2 Annex

   Silly words

   :contained-element connection-error: Lolololz
   :contained-element use-types: Lolololz
   :kind connection-error: :construct:`Port Connection<portconnection>`
   :kind use-types: Set of :construct:`Error Types<error-type>`

.. property:: Occurrence

   An assumption about the environment / clinical process where the app will be used.

   :type: Record
   :subproperty Kind: Derp
   :subproperty Hazard: Derp
   :subproperty ViolatedConstraint: Derp
   :subproperty Title: Derp
   :subproperty Cause: Derp
   :subproperty Compensation: Derp
   :subproperty Impact: Derp
   :type Kind: One of ``NotProviding``, ``Providing``, ``Early``, ``Late``, ``AppliedTooLong``, ``StoppedTooSoon``, ``ValueLow``, ``ValueHigh``, ``ParamsMissing``, ``ParamsWrong``, or ``ParamsOutOfOrder``
   :type Hazard: :property:`Hazard<hazard>`
   :type ViolatedConstraint: :property:`Constraint<constraint>`
   :type Title: AADLString
   :type Cause: AADLString
   :type Compensation: AADLString
   :type Impact: :construct:`Error Type<error-type>`
   :context: :construct:`System<system>`
   :example: 

.. code-block:: aadl
   :linenos:
   
   package PCA_Shutoff
   public
   
   system PCA_Shutoff_System
   end PCA_Shutoff_System;

   system implementation PCA_Shutoff_System.imp
   subcomponents
      pulseOx : device PulseOx_Interface::ICEpoInterface.imp;
      appLogic : process PCA_Shutoff_Logic::ICEpcaShutoffProcess.imp;
   connections
      spo2_data : port pulseOx.SpO2 -> appLogic.SpO2;
   annex EMV2 {**
      use types PCA_Shutoff_Errors;
      properties
      MAP_Error_Properties::Occurrence => [
         Kind => AppliedTooLong;
         Hazard => PCA_Shutoff_Error_Properties::InadvertentPumpNormally;
         ViolatedConstraint => PCA_Shutoff_Error_Properties::PumpWhenSafe;
         Title => "Network Drop";
         Cause => "Network drops out, leaving the SpO2 value potentially too high";
         Compensation => "Physiological readings have a maximum time, after which they are no longer valid";
         Impact => reference(SpO2ValueHigh);
      ] applies to spo2_data;
   **};
   
   end PCA_Shutoff_System.imp;
   end PCA_Shutoff;
   
.. note:: The context must be set explicitly via the ``applies to`` operator, rather than by embedding this property in its context (see line 11 above).