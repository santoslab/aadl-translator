.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _tsafe-constructs:

#################################
Component-Level T-SAFE Constructs
#################################

After a system's :ref:`fundamental properties<hazard-analysis-fundamentals>` have been documented, analysis can move on to individual elements. Analysis of a particular element in |SAFE| is performed in two large activities. This page documents the constructs used while performing those activities, :ref:`the next<tsafe-properties>` documents the properties used. Like the descriptions of the other activities, the documentation of these properties assume a general knowledge of the |Systematic Analysis of Faults and Errors|.

.. note:: The constructs on this page enable an analyst to perform |SAFE|'s Activity 1 and 2. Activity 0 was :ref:`described previously<hazard-analysis-fundamentals>`.

**********
Constructs
**********

Component Type Constructs
=========================

Error Propagations
------------------

Individual components propagate :construct:`error types<errortype>` in or out as sources, sinks, or transformations. These propagations are specified directly in the annexes of either :construct:`devices<device>` or :construct:`processes<process>`: the direction and error type is specified in the component's type specification, and then a more detailed flow specification (documenting whether the component is a source, sink, or path for the error type) is given in the implementation.

.. construct:: error propagation

   Specifies how error types relevant to this component enter or leave the component.
      
   :context Error-Propagations: The error propagations section of a component type's EMV2 annex
   :context-type Error-Propagations: EMV2 annex in :construct:`device` or :construct:`process` type
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 5-20
	:dedent: 1
	:emphasize-lines: 12-13
	:linenos:

Component Implementation Constructs
===================================

Error Flows
-----------

:construct:`Error propagations<errorpropagation>` allow an analyst to declare the errors that a component might receive or produce, but not what would happen when they arrive / the circumstances which would lead to their production. For that, analysts should use :construct:`error flows<errorflow>`.

.. construct:: error flow

   An error flow explains how error types get consumed, transformed, and created. There are three types:
   
   - ``error path`` declarations map one or more incoming error types on a particular port to one or more outgoing error types on a different port
   - ``error sink`` declarations declare that incoming errors on a specific port have no externally observable effect
   - ``error source`` declarations declare internal faults can trigger the propagation of one or more error types on a particular port

   :context Error-Propagations: The flows subsection of an error propagations section in a component implementation's EMV2 annex
   :context-type Error-Propagations: EMV2 annex in :construct:`device` or :construct:`process` implementation
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 32-33, 35-39, 45
	:dedent: 2
	:linenos:

Error Behavior Events
---------------------

Error behavior events are used (together with :construct:`transitions<errorbehaviortransition>`) any time an error or fault can be detected and possibly compensated for at either design- or run-time. They are used to guard the :construct:`transitions<errorbehaviortransition>` between :construct:`error behavior states<errorbehaviorstate>` The specific meaning of the event, however, depends on if its associated with an externally- or internally-caused problem.

- Externally-Caused (|SAFE| Activity 1) -- Events associated with externally-caused problems signify detection via some inbuilt mechanism.
- Internally-Caused (|SAFE| Activity 2) -- Events associated with internally-caused problems signify the occurence of some internal fault.

.. construct:: error behavior event

   An event signifies the detection of external error or the occurrence of an internal fault.

   :context Component-Error-Behavior: The events subsection of an component error behavior section in a component implementation's EMV2 annex
   :context-type Component-Error-Behavior: EMV2 annex in :construct:`device` or :construct:`process` implementation
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 46-53,63
	:dedent: 2
	:linenos:

Error Behavior Transitions
--------------------------

Error behavior transitions are used (together with :construct:`events<errorbehaviorevent>`) any time an error or fault can be detected and possibly compensated for at either design- or run-time. They provide directional links between two :construct:`states<errorbehaviorstate>` and must be guarded by at least one :construct:`event<errorbehaviorevent>` (though more events can be used if desired)

.. note:: The only supported relation between multiple guarding events is ``or``.

.. construct:: error behavior transition

   Transitions document the behavior of the component in the presence of detectable external problems or problematic internal faults.

   :context Component-Error-Behavior: The transitions subsection of an component error behavior section in a component implementation's EMV2 annex
   :context-type Component-Error-Behavior: EMV2 annex in :construct:`device` or :construct:`process` implementation
   :example:
.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 46,54-63
	:dedent: 2
	:linenos:

*******
Example
*******

.. literalinclude:: snippets/process.aadl
	:language: aadl
	:lines: 1-63,142-
	:linenos:

***********
Translation
***********

.. note:: Only the first part of this report is generatable from the constructs on this page. The second half of the report is based on T-SAFE's :ref:`property<tsafe-properties>` specifications.

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
