.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-occurrence:

#######################
The Occurrence Property
#######################

The *occurrence* property is designed to do associate three main things with one particular way a control action could be provided in an unsafe way:

1. Relevant STPA :ref:`fundamentals<hazard-analysis-fundamentals>`
2. A human-readable description of the fault's cause and compensation
3. A specific fault and control action

The occurrence property (or, more likely, list of properties) should be placed in an EMv2 annex in the app's :construct:`System Implementation<systemimplementation>`.

.. property:: occurrence

   An assumption about the environment / clinical process where the app will be used.

   :type: Record
   :subproperty Kind: The STPA keyword associated with this hazardous occurrence
   :subproperty Hazard: The hazard (as documented in the preliminaries file) associated with this hazardous occurrence
   :subproperty ViolatedConstraint: The safety constraint (as documented in the preliminaries file) associated with this hazardous occurrence
   :subproperty Title: A short title for the occurrence
   :subproperty Cause: A human-readable cause for for the occurrence
   :subproperty Compensation: Any compensatory steps that could be taken, or an explicit recognition that there is no way to compensate for this hazardous occurrence
   :subproperty Impact: The EMv2 fault type representing the impact if this hazardous occurrence comes to pass.  This fault type should be propagated out of the component that is the source of the :construct:`connection<portconnection>` this property is applied to, and propagated into the component that is the sink of the connection.
   :type Kind: One of ``NotProviding``, ``Providing``, ``Early``, ``Late``, ``AppliedTooLong``, ``StoppedTooSoon``, ``ValueLow``, ``ValueHigh``, ``ParamsMissing``, ``ParamsWrong``, or ``ParamsOutOfOrder``
   :type Hazard: :property:`Hazard<hazard>`
   :type ViolatedConstraint: :property:`Constraint<constraint>`
   :type Title: AADLString
   :type Cause: AADLString
   :type Compensation: AADLString
   :type Impact: :construct:`Error Type<errortype>`
   :context: :construct:`System Implementation<systemimplementation>`
   :example: 

.. literalinclude:: snippets/system.aadl
	:language: aadl
	:linenos:
   
.. note:: The context must be set explicitly via the ``applies to`` operator, rather than by embedding this property in its context (see line 24 in the example above).