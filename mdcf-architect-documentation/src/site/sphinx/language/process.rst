.. include:: ../util/substitution.rst
.. _aadl-process-component:

#######################
The *Process* Component
#######################

AADL process components describe the app's view of a software component. The software components are themselves composed of tasks (encoded in MDCF Architect's AADL as :ref:`threads<aadl-thread-component>`).

Process components translate to java classes that contain one or more :ref:`tasks<aadl-thread-component>`, which communicate with one another via shared state.  That is, communication within one process is done via shared state, communication between two processes (or a process and a device) is done via ``port connections``, which are realized as messages sent across the underlying middleware.

****************************
Properties and Subcomponents
****************************

Process Component
=================
Process components contain a listing of the ports exposed by the device, and a property specifying whether this process is a display or logic component.

.. list-table:: 
	:widths: 5 10 10 10 10 55
	:header-rows: 1
	:stub-columns: 1

	* - Type
	  - Override Properties
	  - Default Properties
	  - Direction
	  - Trigger
	  - Explanation
	* - Port
	  - MAP\_ Properties:: Output\_ Rate
	  - Default\_ Output\_ Rate
	  - ``in`` or ``out`` (``in out`` ports are not allowed)
	  - ``event``, ``data``, or ``event data``
	  - The ports that will be provided to the app.  These ports will be a subset of all the ports provided by a given device.
	* - Process
	  - MAP\_ Properties:: Component\_ Type
	  - N/A
	  - N/A
	  - N/A
	  - Either ``logic`` or ``display``.

Note that a port's trigger specification (eg, ``event``, ``data``, or ``event data``) only has an effect if its direction is ``in`` -- declaring an ``out``'s port's trigger specification is meaningless.

	  
Process Implementation Component
================================

Process implementations contain a listing of the tasks that compose the software component and connections from those tasks to the process's external ports.  There are no process implementation level properties.

.. list-table:: 
	:widths: 5 15 15 65
	:header-rows: 1
	:stub-columns: 1

	* - Type
	  - Override Properties
	  - Default Properties
	  - Explanation
	* - Thread Subcomponent
	  - None
	  - None
	  - Threads represent the tasks that compose this software component.
	* - Port Connection
	  - None
	  - None
	  - ``Process implementation``-level port connections represent the connection of external ports to internal tasks.

There must be a port connection from each ``event`` or ``event data`` port to a handler thread, and there must not be a port connection from ``data`` ports to threads.

*******
Example
*******

.. literalinclude:: snippets/logic.aadl
   :language: aadl
   :lines: 1-18, 29-30