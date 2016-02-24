.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _aadl-port-component:

##############################
*Ports* and *Port Connections*
##############################

Ports specify how various components can communicate with other components. Port connections specify how each port is connected to other ports.

***********************************
Properties, Contexts and Directions
***********************************

.. construct:: port

	Ports are typed, directed constructs over which a component can communicate with other components.
	
	:property OutputRate: |prop output-rate|
	:property ExchangeName: |prop exchange-name|
	:type OutputRate: :property:`MAP_Properties::Output_Rate<output-rate>`
	:type ExchangeName: :property:`MAP_Properties::Exchange_Name<exchange-name>`
	:context Device: A device port can communicate (via the underlying messaging service) with :construct:`process` elements in the app.
	:context Process: A process port can communicate (via the underlying messaging service) with :construct:`process` and :construct:`device` elements in the app.
	:context Thread: A thread port can communicate with :construct:`process` ports. Message arrival on an ``in`` process port that a thread port is bound to will cause the enclosing thread to be dispatched with the arriving message's payload as a parameter. ``out`` thread ports are a commitment to send messages on the bound process port.
	:context-type Device: :construct:`device`
	:context-type Process: :construct:`process`
	:context-type Thread: :construct:`thread`
	:direction: Either ``in`` or ``out``
	:trigger-type event: Message arrival must be handled by a sporadic thread, messages have no payload.
	:trigger-type event data: Message arrival must be handled by a sporadic thread, messages have a payload of the port's type.
	:trigger-type data: Message arrival must not be handled but will rather cause a predictably-named field to be updated. Messages have a payload of the port's type. Thread ports cannot have a ``data`` trigger. Devices must not use ``data`` ports.
	
.. note::
	1. The ExchangeName property is ignored on ports not attached to devices.
	2. Triggers on ``out`` ports, while required by AADL, are ignored for MDCF Architect translation.
	
.. construct:: port connection

	Port connections are typed, directed links between ports. They are defined in the construct that contains the communicating components, eg, the parent defines the port connections for the children.
	
	:property ChannelDelay: |prop channel-delay|
	:type ChannelDelay: :property:`MAP_Properties::Channel_Delay<channel-delay>`
	:context SystemImplementation: A system implementation port connection will be realized as a channel in the underlying messaging service.
	:context ProcessImplementation: A process implementation port connection links a :construct:`thread` subcomponent to messages from other :construct:`process` or :construct:`device` components.
	:context-type SystemImplementation: :construct:`System Implementation<systemimplementation>`
	:context-type ProcessImplementation: :construct:`Process Implementation<processimplementation>`
	:Direction: Either ``<-`` or ``->``

.. note::
	The ``ChannelDelay`` property is only required for the ``SystemImplementation`` context -- it will be ignored in ``ProcessImplementations``.
	
*******
Example
*******

.. literalinclude:: snippets/display.aadl
   :language: aadl
   :emphasize-lines: 7-8, 18-19, 24, 32
   :linenos:

***********
Translation
***********

In the example user-modifiable code below, the highlighted lines correspond to the ``event`` and ``event data`` handlers -- the event handler has no typed parameter, since event ports have no type / do not support message payloads. Had the incoming SpO2 port been a ``data`` port, the most recent SpO2 value would be accessed by calling ``getSpO2Data()``, which would be defined in the supertype.

.. literalinclude:: snippets/display.java
	:language: java
	:emphasize-lines: 18, 23
	:linenos:

.. literalinclude:: snippets/display_supertype.java
	:language: java
	:linenos:

.. literalinclude:: snippets/display.compsig.xml
	:language: xml
	:linenos: