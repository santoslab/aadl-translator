.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _feature-connection:

####################################
*Features* and *Feature Connections*
####################################

Ports specify how various components can communicate with other components. Port connections specify how each port is connected to other ports.

***********************************
Properties, Contexts and Directions
***********************************

.. construct:: feature

	Features are untyped, directed constructs over which a component can communicate with other components. They do not trigger code generation, and thus can be used in connections that do not exist entirely within the system boundary.
	
	:context Device: A :construct:`device` can use a feature to model communication with non-system elements (like users).
	:context Process: A :construct:`process` can use a feature to model communication with non-system elements (like users).
	:context Abstract: An :construct:`abstract` element can use a feature to model communication with non-system elements (like users).
	
.. construct:: feature connection

	Feature connections are typed, directed links between ports. They are defined in the construct that contains the communicating components, which is typically the :construct:`system`.

*******
Example
*******


.. literalinclude:: snippets/abstract.aadl
   :language: aadl
   :emphasize-lines: 6-7
   :linenos:

.. literalinclude:: snippets/system.aadl
   :language: aadl
   :lines: 1, 8-34, 77-78
   :emphasize-lines: 24-26
   :linenos: