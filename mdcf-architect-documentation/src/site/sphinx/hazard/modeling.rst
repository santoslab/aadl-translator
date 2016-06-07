.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _modeling-outside-elements:

#####################################
Extending a Model for Hazard Analysis
#####################################

Before a hazard analysis can begin, a model should be extended to include components, connections and other modeling constructs that will not be used for code generation. Components to model include human actors and controlled processes, while a system may involve various non-digital connections. Including these elements allows an analyst to "close the loop" and model more complete control structures.

Additionally, AADL supports the defintion of system error behavior. T-SAFE uses :construct:`error types<errortype>`, which can be :construct:`propagated<errorpropagation>` and :construct:`transformed<errorflow>` by the various elements in a system, and error behavior declarations, which let an analyst specify the error-related :construct:`states<errorbehaviorstate>` and :construct:`guarded<errorbehaviorevent>` :construct:`transitions<errorbehaviortransition>` of their components as guarded transition systems. Defining these constructs is necessary to ground a hazard analysis in a system's architecture.

For example, consider our pulse oximetry forwarding application. We might decide to model the patient, the clinician, and the treatment the clinician performs on the patient. Modeling the components is done with :construct:`abstract` constructs, while the connections are modeled as :construct:`feature` constructs. 

Likewise, various problems might occur in the system, but one we care particularly about is the patient receiving improper care; we can model this using an :construct:`error type <errortype>`. Other error types might be discovered as analysis proceeds, and can be added at any time. Finally, the pulse oximeter's error behavior may be quite simple, if it (for example) either operates normally or disables its output.


.. note:: This documentation assumes that the reader is familiar with the |Systematic Analysis of Faults and Errors| process.

********
Contents
********

.. toctree::
	:maxdepth: 1
	
	abstract
	feature
	errorlibrary