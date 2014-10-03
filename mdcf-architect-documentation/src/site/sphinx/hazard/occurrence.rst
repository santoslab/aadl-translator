.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-occurrence:

#######################
The Occurrence Property
#######################

Some explanatory text about the occurrence property goes here.

.. code-block:: aadl
   :linenos:
   
   annex EMV2 {**
      properties
         MAP_Error_Properties::Occurrence => [
         Kind => AppliedTooLong;
         Hazard => PCA_Shutoff_Error_Properties::H1;
         ViolatedConstraint => PCA_Shutoff_Error_Properties::C1;
         Title => "Network Drop";
         Cause => "Network drops out, leaving the SpO2 value potentially too high";
         Compensation => "Physiological readings have a maximum time, after which they are no longer valid";
         Impact => reference(SpO2ValueHigh);
      ] applies to spo2_logic;
   **};
   
.. literalinclude:: snippets/system.aadl
	:language: aadl
	:linenos: