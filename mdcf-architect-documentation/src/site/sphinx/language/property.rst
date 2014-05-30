.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _aadl-properties-data:

#############################
*Properties* and *Data* Types
#############################


**********
Properties
**********

Thread Properties
=================

.. property:: period 

	|prop period|
	
   :default name: Default_Thread_Period
   :override: Timing_Properties::Period
   :type: Time
   :context: :construct:`thread`
   :example: ``50 ms``

.. property:: deadline

	|prop deadline|
	
   :default name: Default_Thread_Deadline
   :override: Timing_Properties::Deadline
   :type: Time
   :context: :construct:`thread`
   :example: ``50 ms``
   
.. property:: wcet

	|prop wcet|
	
   :default name: Default_Thread_WCET
   :override: Timing_Properties::Compute_Execution_Time
   :type: Time
   :context: :construct:`thread`
   :example: ``5 ms``  

.. property:: dispatch

	|prop dispatch|
	
   :default name: Default_Thread_Dispatch
   :override: Thread_Properties::Dispatch_Protocol
   :type: ``sporadic`` or ``periodic``
   :context: :construct:`thread`
   :example: ``periodic``


Port Properties
===============

.. property:: output-rate

	|prop output-rate| Note that this is ignored on :construct:`thread` ports  and incoming :construct:`process implementation<processimplementation>` ports.
	
   :default name: Default_Output_Rate
   :override: MAP_Properties::Output_Rate
   :type: Time range
   :context: :construct:`port`
   :example: ``100 ms .. 300 ms``

Port Connection Properties
==========================

.. property:: channel-delay

	|prop channel-delay| Note that this is ignored on port connections which are attached to :construct:`process implementations<processimplementation>`.
	
   :default name: Default_Channel_Delay
   :override: MAP_Properties::Channel_Delay
   :type: Time
   :context: :construct:`port connection<portconnection>`
   :example: ``100 ms``

Process Properties
==================

.. property:: component-type

	|prop component-type|
	
   :default name: N/A
   :override: MAP_Properties::Component_Type
   :type: Either ``Logic`` or ``Display``
   :context: :construct:`process`
   :example: ``Display``

Data Properties
==================

.. property:: data-representation

	|prop data-representation|
	
   :default name: N/A
   :override: Data_Model::Data_Representation
   :type: ``Boolean``, ``Integer``, or ``Double``
   :context: :construct:`data`
   :example: ``Integer``

Example Property Set
====================

.. literalinclude:: snippets/default_properties.aadl
   :language: aadl
   :linenos:
   
**********
Data Types
**********

.. construct:: data

	:property Data_Representation: |prop data-representation|
	:type Data_Representation: :property:`Data_Model::Data_Representation<data-representation>`
	
Example Data Type
=================

.. literalinclude:: snippets/data_type.aadl
   :language: aadl
   :linenos:
