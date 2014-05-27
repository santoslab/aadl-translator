.. include:: ../util/substitution.rst

######################
The *Device* Component
######################

AADL device components describe the app's view of a device.  Note that this is distinct from the device itself, since a) apps use a subset of a given device's capabilities, and b) apps will not know what device they'll be connected to, only the features they will get when launched.

Device components translate to the "pseudo devices" which are java classes that represent all the functionality requested from a certain device. These pseudo-devices are full logic components (equivalent to MDCF Architect :ref:`processes <aadl-process-component>`) complete with component configuration (*DeviceName*.compsig.xml) specifications.

****************************
Properties and Subcomponents
****************************

Device Component
================
Device components are simply a listing of the ports exposed by the device. There are no device-level properties.

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

Note that a port's trigger specification (eg, ``event``, ``data``, or ``event data``) only has an effect if its direction is ``in`` -- declaring an ``out``'s port's trigger specification is meaningless.
	  
Device Implementation Component
===============================
Device components are always empty in the MDCF Architect.

*******
Example
*******

.. literalinclude:: snippets/device.aadl
   :language: aadl