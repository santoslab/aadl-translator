.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _modeling-outside-elements:

###############################################
Modeling Elements Outside a System's Boundaries
###############################################

Before a hazard analysis can begin, components and connections that are not used for code generation may need to be modeled. These include human actors, controlled processes, and non-digital connections. Including these elements allows an analyst to "close the loop" and model more complete control structures.

For example, consider our pulse oximetry forwarding application. We might decide to model the patient and the clinician, as well as the treatment the clinician performs on the patient. Modeling these components is done with :construct:`abstract` constructs, while the connections are modeled as :construct:`feature` constructs.

********
Contents
********

.. toctree::
	:maxdepth: 1
	
	abstract
	feature