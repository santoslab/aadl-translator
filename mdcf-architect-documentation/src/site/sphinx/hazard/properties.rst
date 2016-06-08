.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _tsafe-properties:

#################################
Component-Level T-SAFE Properties
#################################

Once an analyst has :ref:`extended<modeling-outside-elements>` her system model to include all the :ref:`components<abstract-component>`, :ref:`connections<feature-connection>`, and :ref:`errors<error-library>` needed for analysis and has specified at least some of the :ref:`constructs<tsafe-constructs>` used in T-SAFE, she can begin annotating the model with the properties on this page. These properties are used to document the analyst's thinking and justify any decisions or claims that have been made.

.. note:: The properties on this page enable an analyst to perform |SAFE|'s Activity 1 and 2. Activity 0 was :ref:`described previously<hazard-analysis-fundamentals>`.

***************************************
Properties for Externally-Caused Errors
***************************************

.. note:: These properties are used in |SAFE|'s Activity 1.

.. property:: ExternallyCausedDanger

   Used to describe how one or more incoming errors manifest as one or more successor dangers.
   
   :context: :construct:`'error path' flows<errorflow>`
   :subproperty ProcessVariableValue: The process variable that would have an incorrect value if this danger occurs
   :subproperty ProcessVariableConstraint: A description of how the process variable's value would be incorrect
   :subproperty Explanation: A human-readable description of how the incoming error(s) would manifest as the successor danger(s)
   :type ProcessVariableValue: :construct:`data` subcomponent reference
   :type ProcessVariableConstraint: AADLString
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33,35,45,64-69
	:dedent: 1
	:linenos:

.. property:: RuntimeErrorDetection

   Used to describe how one or more incoming errors can be detected while the system is in operation.
   
   :context: externally detectable :construct:`error event<errorbehaviorevent>`
   :subproperty ErrorDetectionApproach: [Optional] The approach that would allow enable the detection of the error
   :subproperty Explanation: A human-readable description of how the incoming error(s) would be detected
   :type ErrorDetectionApproach: ``Concurrent`` or ``Preemptive``
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33, 38, 45-47, 49, 63-64, 80-83
	:dedent: 1
	:linenos:

.. property:: RuntimeErrorHandling

   Used to describe how one or more incoming errors can be compensated for while the system is in operation.
   
   :context: :construct:`error behavior transition<errorbehaviortransition>` involving externally-caused :construct:`events<errorbehaviorevent>`
   :subproperty ErrorHandlingApproach: [Optional] The approach that would allow enable compensation for the error
   :subproperty Explanation: A human-readable description of how the incoming error(s) would be handled
   :type ErrorHandlingApproach: ``Rollback``, ``Rollforward``, or ``Compensation``
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33, 37-38, 45-47, 49-50, 54-58, 63-64, 93-96
	:dedent: 1
	:linenos:

***************************************
Properties for Internally-Caused Faults
***************************************

These properties are used in |SAFE|'s Activity 2.

.. property:: InternallyCausedDanger

   Used to describe how an internal fault can cause one or more successor dangers.
   
   :context: :construct:`'error source' flows<errorflow>`
   :subproperty Explanation: A human-readable description of how the fault would cause the successor danger(s)
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33,39,45,64,98-100
	:dedent: 1
	:linenos:
	
.. warning:: The :construct:`'error source' flow<errorflow>` that this property is applied to should specify the triggering fault class using a ``when`` clause and one of the base fault classes specified in the ``StandardFaultClasses`` error type set in ``MAP_Errors``.

.. property:: DesignTimeErrorDetection

   Used to describe how faults in one or more fault classes can be detected while the system is being designed.
   
   :context: :construct:`error event<errorbehaviorevent>` that will cause a successor danger
   :subproperty FaultDetectionApproach: [Optional] The approach that would allow enable the detection of faults in this class
   :subproperty Explanation: A human-readable description of how faults in this class would be detected
   :type FaultDetectionApproach: ``StaticAnalysis``, ``TheoremProving``, ``ModelChecking``, ``SymbolicExecution``, or ``Testing``
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33, 39, 45-47, 53, 63-64, 102-105
	:dedent: 1
	:linenos:

.. property:: RuntimeFaultHandling

   Used to describe how faults and / or the error(s) resulting from their occurrence can be compensated for while the system is running.
   
   :context: :construct:`error behavior transition<errorbehaviortransition>` involving internally-caused :construct:`events<errorbehaviorevent>`
   :subproperty ErrorHandlingApproach: [Optional] The approach that would allow enable compensation for the error resulting from this fault's occurrence
   :subproperty FaultHandlingApproach: [Optional] The approach that would allow enable correction of the fault itself
   :subproperty Explanation: A human-readable description of how the fault and / or error would be compensated for
   :type ErrorHandlingApproach: ``Rollback``, ``Rollforward``, or ``Compensation``
   :type FaultHandlingApproach: ``Diagnosis``, ``Isolation``, ``Reconfiguration``, or ``Reinitialization``
   :type Explanation: AADLString
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 22, 29, 32-33, 39, 45-47, 52-54, 60-64, 107-111
	:dedent: 1
	:linenos:

.. property:: EliminatedFaults

   Allows an analyst to explain why one or more base fault classes were eliminated from consideration. The base fault classes should be elements of the  ``StandardFaultClasses`` error type set in ``MAP_Errors``.
   
   :context: :construct:`errorbehaviorstate`
   :subproperty FaultTypes: List of eliminated fault classes
   :subproperty Explanation: Justification for the fault classes' elimination
   :type FaultTypes: List of :construct:`errortype` references
   :type Explanation: AADLString
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 64, 126-132
	:dedent: 2
	:linenos:

*******
Example
*******

.. note:: The constructs in this example are described on the :ref:`previous page<tsafe-constructs>`.

.. literalinclude:: snippets/process.aadl
	:language: aadl
	:linenos:

***********
Translation
***********

.. note:: Only the second part of this report (ie, sections below 4.1: 'Successor Dangers') is generatable from the properties on this page. The first half of the report is based on T-SAFE's :ref:`constructs<tsafe-constructs>`.

You can :download:`view a full report<PulseOx_Forwarding_System.html>`, or view the report elements here:

T-SAFE: Analysis of appLogic
============================

This report was generated by the `MDCF
Architect <http://santoslab.org/pub/mdcf-architect/>`__ on June 7, 2016
at 5:58 PM

Table of Contents
-----------------

1. `Fundamentals <#fundamentals>`__
2. `Decomposition <#decomposition>`__

   1. `Control Structure <#control-structure>`__
   2. `Components <#components>`__
   3. `Connections <#connections>`__

3. `Process Model <#process-model>`__
4. `External Interactions <#external-interactions>`__

   1. `Successor Dangers <#successor-dangers>`__
   2. `Manifestions <#manifestations>`__
   3. `Externally Caused Dangers <#externally-caused-dangers>`__
   4. `Sunk Dangers <#sunk-dangers>`__

5. `Internal Faults <#internal-faults>`__

   1. `Faults Not Considered <#faults-not-considered>`__
   2. `Internally Caused Dangers <#internally-caused-dangers>`__

Fundamentals
------------

-  Element Name: appLogic
-  Element Type: PulseOx\_Logic\_Process
-  Successor Link Names:

   -  DerivedAlarm (Object)

-  Predecessor Link Names:

   -  SpO2 (Double)

-  Classification: Controller

Decomposition
-------------

Components
~~~~~~~~~~

#. `SpO2Task <spo2task.html>`__: SpO2Task
#. `CheckSpO2Thread <checkspo2thread.html>`__: CheckSpO2Thread

Connections
~~~~~~~~~~~

#. outgoing\_alarm: CheckSpO2Thread.Alarm ->
   PulseOx\_Logic\_Process.DerivedAlarm [Object]

Process Model
-------------

-  SpO2Val (FLOAT): 0.0 -- 100.0 Percent

External Interactions
---------------------

Successor Dangers
~~~~~~~~~~~~~~~~~

-  DerivedAlarm: {MissedAlarm, BogusAlarm}

Manifestations
~~~~~~~~~~~~~~

-  SpO2: {SpO2ValueHigh (HIGH), [STRIKEOUT:SpO2ValueLow (LOW)],
   [STRIKEOUT:EarlySpO2 (EARLY)], [STRIKEOUT:LateSpO2 (LATE)], NoSpO2
   (HALTED), ErraticSpO2 (ERRATIC)}

Externally Caused Dangers
~~~~~~~~~~~~~~~~~~~~~~~~~

-  MtoN: Placeholder explanation

   -  *Successor Danger(s)* DerivedAlarm: BogusAlarm
      (VIOLATEDCONSTRAINT), MissedAlarm (VIOLATEDCONSTRAINT)
   -  *Manifestation(s)* SpO2: SpO2ValueLow (LOW), SpO2ValueHigh (HIGH)
   -  *Process Variable* :

-  MultipleOutputs: Placeholder explanation

   -  *Successor Danger(s)* DerivedAlarm: MissedAlarm
      (VIOLATEDCONSTRAINT), BogusAlarm (VIOLATEDCONSTRAINT)
   -  *Manifestation(s)* SpO2: SpO2ValueHigh (HIGH)
   -  *Process Variable* :

-  MultipleInputs: Placeholder explanation

   -  *Successor Danger(s)* DerivedAlarm: MissedAlarm
      (VIOLATEDCONSTRAINT)
   -  *Manifestation(s)* SpO2: SpO2ValueHigh (HIGH), SpO2ValueLow (LOW)
   -  *Process Variable* :

-  HighSpO2LeadsToMissedAlarm: The SpO2 value is too high, leading the
   app to fail to issue an alarm when it should

   -  *Successor Danger(s)* DerivedAlarm: MissedAlarm
      (VIOLATEDCONSTRAINT)
   -  *Manifestation(s)* SpO2: SpO2ValueHigh (HIGH)
   -  *Process Variable* SpO2Val: Higher than true value

Sunk Dangers
~~~~~~~~~~~~

-  LateSpO2DoesNothing: Not dangerous

   -  *Successor Danger(s)* SpO2: LateSpO2 (LATE)
   -  *Detections*

      -  TimestampViolation: Messages should be timestamped so latency
         violations can be detected (CONCURRENT)

   -  *Handlings*

      -  SwitchToNoOutput: The pump switches into a fail-safe mode, ie,
         it runs at a minimal (KVO) rate (ROLLFORWARD)

-  LowSpO2DoesNothing: Not dangerous

   -  *Successor Danger(s)* SpO2: SpO2ValueLow (LOW)
   -  *Detections*

      -  

   -  *Handlings*

      -  

-  EarlySpO2DoesNothing: Not dangerous

   -  *Successor Danger(s)* SpO2: EarlySpO2 (EARLY)
   -  *Detections*

      -  TimeoutViolation: Minimum separation between messages are added
         to detect early arrivals (CONCURRENT)

   -  *Handlings*

      -  SwitchToNoOutput: The pump switches into a fail-safe mode, ie,
         it runs at a minimal (KVO) rate (ROLLFORWARD)

Internal Faults
---------------

Faults Not Considered
~~~~~~~~~~~~~~~~~~~~~

-  We're using a 'proven in use' app

   -  SoftwareBug
   -  BadSoftwareDesign
   -  CompromisedSoftware
   -  CompromisedHardware
   -  HardwareBug
   -  BadHardwareDesign
   -  ProductionDefect

-  The hospital has physical security measures in place

   -  AdversaryAccessesHardware
   -  AdversaryAccessesSoftware

-  The app logic isn't a connection between two components

   -  SyntaxMismatch
   -  RateMismatch
   -  SemanticMismatch

Internally Caused Dangers
~~~~~~~~~~~~~~~~~~~~~~~~~

-  BogusAlarmsArePossible: This is a placeholder explanation to test the
   InternallyCausedDanger property.

   -  *Successor Danger(s)* DerivedAlarm: BogusAlarm
      (VIOLATEDCONSTRAINT)
   -  *Fault Class(es)* Deterioration
   -  *Run-time Detections*

      -  

   -  *Handlings*

      -  ClinicianTurnsOffPulseOx: The nurse sees that the pulse
         oximeter has deteriorated and turns it off. (ROLLFORWARD)

Missed Fault Classes
~~~~~~~~~~~~~~~~~~~~

-  OperatorSWWrongChoice
-  CosmicRay
-  OperatorSWMistake
-  OperatorHWMistake
-  OperatorHWWrongChoice