.. include:: ../util/substitution.rst
.. default-domain:: aadl

########################
The *Abstract* Component
########################

Abstract components are used to model any system component for which code generation is not required.

*********************************
Properties and Contained Elements
*********************************

.. construct:: abstract 

	An abstract component defines the interface of the component.

	:contained-element features: The connection-ends this component exposes.
	:property ComponentType: |prop component-type|
	:kind features: :construct:`feature`
	:type ComponentType: :property:`MAP_Properties::Component_Type<component-type>`

.. construct:: abstract implementation
	
	Abstract implementations are empty, since the internals of these components are not used for hazard analysis (and may not be known).

*******
Example
*******

.. literalinclude:: snippets/abstract.aadl
	:language: aadl
	:linenos: