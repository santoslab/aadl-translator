.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-components:

##########################################
Architecturally Integrated Hazard Analysis
##########################################

As part of specifying an :property:`occurrence<occurrence>` property, developers are asked to specify the *impact* of the hazardous occurrence.  That impact is an EMv2 :construct:`Error Type<errortype>` that is propagated between the components joined by the :construct:`connection<portconnection>` that the occurrence is applied to.

***********
Error Types
***********

Error types are faults that are specific to the app being constructed.  They can extend, rename, or be totally independent of existing error types.  Extension of MAP Errors is the most common way to create new error types, though completely new types can be created if necessary.  Similarly, existing types can be renamed.  Error types must be declared within an EMv2 annex block, and are typically placed in their own file.

Here's an example of the EMv2 error hierarchy used in our example app:

+--------------------+----------------------+------------------+
| Error Library Type | MAP Error Type       | App Error Type   |
+====================+======================+==================+
| LateDelivery       | LatePhysioDataError  | SpO2ValueLate    |
+--------------------+----------------------+------------------+
| IncorrectValue     | WrongPhysioDataError | SpO2ValueHigh    |
+--------------------+----------------------+------------------+
| N/A                | PhysioDeviceFailure  | NoSpO2Data       |
+--------------------+----------------------+------------------+
| N/A                | N/A                  | MissedAlarm      |
+--------------------+----------------------+------------------+

.. construct:: error type

   An application specific fault and its relation (extension, renaming, or none) to existing error libraries.
   
   :context EMv2Block: Error types are must be in an EMv2 block
   :context-type EMv2Block: Standalone EMv2 Block
   
Example
=======

.. literalinclude:: snippets/error-types.aadl
	:language: aadl
	:linenos:
	
******************
Error Propagations
******************

Individual components propagate :construct:`error types<errortype>` in or out as sources, sinks, or transformations. These propagations are specified directly in the annexes of either :construct:`devices<device>` or :construct:`processes<process>`: the direction and error type is specified first, and then a more detailed flow specification (documenting whether the component is a source, sink, or path for the error type).

.. construct:: error propagations

   Specifies how error types relevant to this component enter or leave the component.
      
   :context device: Device interfaces, as endpoints, are typically sources or sinks for error propagations.
   :context process: Processes, as transformers, can be -- in addition to sources and sinks -- (potentially transformative) paths for errors to propagate through
   :context-type device: :construct:`device`
   :context-type process: :construct:`process`

Examples
========

Here's a pulse oximeter interface that has been extended to specify that its value may be too high.

.. literalinclude:: snippets/device.aadl
	:language: aadl
	:linenos:
	
Here's an excerpt of the app's logic extended to include an error path.  Note that this error path transforms the error -- the incoming incorrectly-high SpO\ :sub:`2` leads to a missed alarm.

.. literalinclude:: snippets/logic.aadl
	:language: aadl
	:linenos:
	
Finally, here's an error sink -- the missed alarm error is forwarded to our display.

.. literalinclude:: snippets/display.aadl
	:language: aadl
	:linenos: