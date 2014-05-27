.. include:: ../util/substitution.rst

######################
The *System* Component
######################

AADL System components describe the overall view of the app -- the devices that generate physiological signals, the software components that process or format the signals, etc.

System components translate to the app layout (the *AppName*.cfg.xml in the |MDCF|) and the port connections become the channels created by the underlying messaging service.

****************************
Properties and Subcomponents
****************************

System Component
================

System components are always empty in the MDCF Architect.

System Implementation Component
===============================

System implementations can have devices and processes as subcomponents, and can have connections between subcomponents' ports. There are no system-level properties.

.. list-table:: 
	:widths: 5 15 15 65
	:header-rows: 1
	:stub-columns: 1

	* - Type
	  - Override Properties
	  - Default Properties
	  - Explanation
	* - Device Subcomponent
	  - None
	  - None
	  - Devices represent the medical devices that the app requires.
	* - Process Subcomponent
	  - None
	  - None
	  - Processes represent software components that the app developer will be specifying.
	* - Port Connection
	  - Default\_ Channel\_ Delay
	  - MAP\_ Properties:: Channel\_ Delay
	  - System-level port connections represent communication between components.
	  
*******
Example
*******

.. literalinclude:: snippets/system.aadl
   :language: aadl