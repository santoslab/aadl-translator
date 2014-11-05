.. include:: ../util/substitution.rst
.. default-domain:: aadl

######################
The *Device* Component
######################

AADL device components describe the app's view of a device.  Note that this is distinct from the device itself, since:
		* apps use a subset of a given device's capabilities, and
		* apps will not know what device they'll be connected to, only the features they will get when launched.

*********************************
Properties and Contained Elements
*********************************

.. construct:: device 
	
	The external features the app will use of this device.
	
	:contained-element features: The ports this device exposes.
	:contained-element EMv2Annex: [Optional] A list of :construct:`errors<errortype>` that will :construct:`propagate<errorpropagation>` in, out, or through this device.
	:kind features: :construct:`port`
	:kind EMv2Annex: :construct:`errorpropagation`
	  
.. construct:: device implementation
	
	Device Implementation constructs are empty; the device is only exposed to the app as an interface which is fully described in the :construct:`device` type.

*******
Example
*******

.. literalinclude:: snippets/device.aadl
	:language: aadl
	:linenos:
   
***********
Translation
***********

Device components translate to the "pseudo devices" which are java classes that represent all the functionality requested from a certain device. These pseudo-devices are full logic components (equivalent to MDCF Architect :construct:`processes<process>`) complete with component configuration (*DeviceName*.compsig.xml) specifications.
   
.. literalinclude:: snippets/device.java
	:language: java
	:linenos:

.. literalinclude:: snippets/device.compsig.xml
	:language: xml
	:linenos: