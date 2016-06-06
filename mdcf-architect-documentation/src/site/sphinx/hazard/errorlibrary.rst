.. include:: ../util/substitution.rst
.. default-domain:: aadl

####################
Error Type Libraries
####################

.. note:: This documentation assumes that the reader is familiar with the |Systematic Analysis of Faults and Errors| process.

***********
Error Types
***********

Error types are used to represent errors that can be propagated or transformed by system elements. They are specific to the app being constructed, but should be identified as manifestations of one of the failure domains specified by |Avizienis| or the violation of a system-level safety constraint. Error types must be declared within an EMv2 annex block, and are typically placed in their own file.

Here's an example of how the failure domains can be extended to be app-specific:

+----------------------------+----------------+
| |Avizienis| Failure Domain | App Error Type |
+============================+================+
| High                       | SpO2ValueHigh  |
+----------------------------+----------------+
| Low                        | SpO2ValueLow   |
+----------------------------+----------------+
| Early                      | EarlySpO2      |
+----------------------------+----------------+
| Late                       | LateSpO2       |
+----------------------------+----------------+
| Halted                     | NoSpO2         |
+----------------------------+----------------+
| Erratic                    | ErraticSpO2    |
+----------------------------+----------------+
| ViolatedConstraint         | ErraticSpO2    |
+----------------------------+----------------+

.. construct:: error type

   An application specific error and the failure domain (or constraint violation) it is a manifestation of.
   
   :context EMV2Block: Error types are must be in an EMV2 block
   :context-type EMV2Block: Standalone EMV2 Block
   :example:
.. literalinclude:: snippets/error-types.aadl
	:language: aadl
	:lines: 1-20, 27-
	:linenos:

***************
Error Behaviors
***************

Error behaviors are used to model the behavior of a component in the presence of errors. Though AADL supports fairly rich behavior specifications, T-SAFE only uses the behavior specifications in libraries to enumerate potential states. Transitions between states are specified in the components themselves, and are used in the definition of both :ref:`externally-<external-interactions>` and :ref:`internally-caused <internal-faults>` problems.

.. construct:: error behavior

   An list of the states that one of the system's components might be in in the presence of errors.
   
   :context EMV2Block: Error types are must be in an EMV2 block
   :context-type EMV2Block: Standalone EMV2 Block
   :example:
.. literalinclude:: snippets/error-types.aadl
	:language: aadl
	:lines: 1-5, 21-
	:linenos:

*******
Example
*******

.. literalinclude:: snippets/error-types.aadl
	:language: aadl
	:linenos: