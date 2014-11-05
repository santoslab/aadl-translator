.. include:: ../util/substitution.rst
.. default-domain:: aadl

######################
The *System* Component
######################

As the top-level (and often first-written) construct, AADL Systems define components that comprise the app and the connections between those components.

*********************************
Properties and Contained Elements
*********************************

.. construct:: system 
	  
	AADL System constructs are empty; the app layout is described in the :construct:`System Implementation<systemimplementation>`
	  
.. construct:: system implementation

	AADL System Implementations describe the elements that compose an app -- the devices that generate physiological signals, the software components that process or format the signals, and the connections between them.

	:contained-element subcomponents: Either :construct:`device` or :construct:`process` elements.
	:contained-element connections: A list of :construct:`port connections<portconnection>` between the various elements.  Note that device-device connections are not allowed.
	:contained-element EMv2Annex: [Optional] A list of :property:`occurrence` properties detailing what faults can be propagated along the system's connections.
	:kind subcomponents: :construct:`device` or :construct:`process`
	:kind connections: :construct:`port connection<portconnection>`
	:kind EMv2Annex: :property:`occurrence`
   
*******
Example
*******

.. literalinclude:: snippets/system.aadl
	:language: aadl
	:linenos:
	
***********
Translation
***********

System components translate to the app layout (the *AppName*.cfg.xml in the |MDCF|) and the port connections become the channels created by the underlying messaging service.

.. literalinclude:: snippets/system.cfg.xml
	:language: xml
	:linenos: