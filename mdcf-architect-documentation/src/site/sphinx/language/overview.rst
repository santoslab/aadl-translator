.. include:: ../util/substitution.rst
.. _aadl-subset-overview:

####################
AADL Subset Overview
####################

The |MDCF Architect| operates on a restricted subset of AADL's full syntax, with slightly modified semantics.  The syntax and semantics changes are necessary primarily because AADL was designed to target systems composed of known hardware and software, while MAP apps run on managed platforms.  A deeper, less developer-targeted overview of the MDCF Architect (as well as the vision and background that contextualize the effort) can be found in this |SEHC14 publication|.

***************
AADL Constructs
***************

At a high level, MAP apps are defined by systems, which contain device and process components.  Process components are further decomposed into threads.  The various components are connected via port connections. The table below gives an overview of the mapping from AADL constructs to MAP App concepts.

.. list-table:: 
	:widths: 20 10 70
	:header-rows: 1

	* - AADL Construct
	  - App Concept
	  - Mapping Explanation
	* - System
	  - Layout
	  - This equivalence is not a large stretch, as (according to the |AADL book|) systems "[represent] a composite that can include... components"
	* - Device
	  - Medical device
	  - This is essentially a direct equivalence.
	* - Process
	  - Software component
	  - AADL processes (again according to the |AADL book|) "[represent] a protected address space that [prevents] other components from accessing anything inside." Tasks are local to the class they're created in, allowing them to share state, but stopping them from directly manipulating tasks or state outside their own class.
	* - Thread
	  - Task
	  - Tasks (which extend ``java.lang.runnable``) represent some unit of work to be done either periodically or upon arrival of a message on a designated port.
	* - System port connection
	  - Channel
	  - Channels enable communication between components, using a messaging service.
	* - Process port connection
	  - Task trigger
	  - Process-level ports can be ``data``, ``event data``, or simply ``event`` ports. Message arrival on an ``event data`` or ``event`` port triggers an associated task (with or without a message payload, respectively), while ``data`` ports silently update a predictably-named field.
	* - Process implementation port connection
	  - Task-Port Communication
	  - A port connection from a process to a thread translates to a task's use of data arriving via that port.

***************
AADL Properties
***************

Real-time and quality of service parameters are defined using AADL's property mechanisms.

.. list-table:: 
	:widths: 15 15 8 8 54
	:header-rows: 1

	* - Default Name
	  - Override Name
	  - Type
	  - Example
	  - Explanation
	* - Default_Thread_Period
	  - Timing_Properties::Period
	  - Time
	  - 50 ms
	  - Periodic tasks will be dispatched to run once per period.
	* - Default_Thread_Deadline
	  - Timing_Properties::Deadline
	  - Time
	  - 50 ms
	  - A task will be scheduled such that it has time to complete before its deadline.
	* - Default_Thread_WCET
	  - Timing_Properties::Compute_Execution_Time
	  - Time
	  - 5 ms
	  - A task's worst case execution time is the most time it will take to complete after dispatch.
	* - Default_Thread_Dispatch
	  - Thread_Properties::Dispatch_Protocol
	  - Sporadic or Periodic
	  - Periodic
	  - Periodic tasks are dispatched once per period, while sporadic tasks are dispatched when a message arrives on their associated port.

***********
Example App
***********

.. image:: images/app-overview.png
	:alt: A high level view of a very simple app
	:align: center

In this language walkthrough, we'll be building a very simple "Pulse Oximetry Display" app. A high-level, information-flow centric view of this app is shown above.  This app's job is to take information from a pulse oximeter device and display it on the MAP display. It does not have many advanced features (eg, alarms, device control, complex logic, etc.) but it allows us to illustrate the various features of the MDCF architect.

********
Contents
********

.. toctree::
   system
   device
   process
   thread