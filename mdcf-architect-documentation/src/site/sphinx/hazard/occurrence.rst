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

*********************
Relevant Fundamentals
*********************

.. code-block:: aadl
   :linenos:
   :emphasize-lines: 3-5
   
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
      ] applies to spo2_data;
   **};