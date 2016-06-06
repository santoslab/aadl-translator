.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _modeling-outside-elements:

###############################################
Modeling Elements Outside a System's Boundaries
###############################################

Before a hazard analysis can begin, a model should be extended to include components, connections and other modeling constructs that will not be used for code generation. Components to model include human actors and controlled processes, while a system may involve various non-digital connections. Including these elements allows an analyst to "close the loop" and model more complete control structures.

Additionally, AADL supports the defintion of system error behavior. T-SAFE uses error types, which can be propagated and transformed by the various elements in a system, and error behavior declarations, which let an analyst specify the behavior of their components as guarded transition systems. Defining these constructs is necessary to ground a hazard analysis in a system's architecture.

For example, consider our pulse oximetry forwarding application. We might decide to model the patient, the clinician, and the treatment the clinician performs on the patient. Modeling the components is done with :construct:`abstract` constructs, while the connections are modeled as :construct:`feature` constructs. 

Likewise, various problems might occur in the system, but one we care particularly about is the patient receiving improper care; we can model this using an :construct:`errortype`. Other error types might be discovered as analysis proceeds, and can be added at any time. Finally, the pulse oximeter's  :construct:`errorbehavior` may be quite simple, if it (for example) either operates normally or disables its output.

********
Contents
********

.. toctree::
	:maxdepth: 1
	
	abstract
	feature
	errorlibrary