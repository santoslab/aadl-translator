.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _external-interactions:

#####################
External Interactions
#####################

After a system's :ref:`fundamental properties<hazard-analysis-fundamentals>` have been documented, analysis can move on to individual elements. Analysis of a particular element in |SAFE| is performed in two large activities. The first, described here, analyzes external interactions of the element while the second considers :ref:`internal faults<internal-faults>`. Like the descriptions of the other activities, the documentation of these properties assume a general knowledge of the |Systematic Analysis of Faults and Errors|.

.. note:: The properties on this page correspond to |SAFE|'s Activity 1.

**********
Constructs
**********

Error Propagations
==================

Individual components propagate :construct:`error types<errortype>` in or out as sources, sinks, or transformations. These propagations are specified directly in the annexes of either :construct:`devices<device>` or :construct:`processes<process>`: the direction and error type is specified in the component's type specification, and then a more detailed flow specification (documenting whether the component is a source, sink, or path for the error type) is given in the implementation.

.. construct:: error propagation

   Specifies how error types relevant to this component enter or leave the component.
      
   :context device: Device interfaces, as endpoints, are typically sources or sinks for error propagations.
   :context process: Processes, as transformers, can be -- in addition to sources and sinks -- (potentially transformative) paths for errors to propagate through
   :context-type device: :construct:`device`
   :context-type process: :construct:`process`
   :example:
.. literalinclude:: snippets/type.aadl
	:language: aadl
	:emphasize-lines: 13-14
	:linenos:

.. flows
   * Sinks
   * Sources
   * Paths

.. Events

.. Transitions

**********
Properties
**********

.. ExternallyCausedDanger

.. RuntimeErrorDetection

.. RuntimeErrorHandling

*******
Example
*******

***********
Translation
***********