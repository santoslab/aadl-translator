.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-overview:

########################
Hazard Analysis Overview
########################

Once your application has been modeled in the :ref:`supported subset of AADL<aadl-subset-overview>`, you can annotate it to include hazard analysis information.  This information will then be used our translator to produce a hazard analysis report. This work was first discussed in this |MEMOCODE14 publication|.

These annotations include both custom properties and a subset of AADL's error-modeling (EMV2) annex, and the report format is derivative of / inspired by `System Theoretic Process Analysis <http://ocw.mit.edu/courses/aeronautics-and-astronautics/16-63j-system-safety-fall-2012/lecture-notes/MIT16_63JF12_Class10STPA.pdf>`_ (STPA).

***********
Example App
***********

.. image:: images/app-overview.png
	:alt: Errors in our very simple app
	:align: center

In this language walkthrough, we'll be re-using the app developed in the :ref:`aadl-subset-overview`. The above app diagram has been annotated with two errors (in red). You can :download:`view the generated report<PulseOx_Forwarding_System.html>`.

.. You can also download :download:`annotated AADL<snippets/pulse-ox-forwarding.zip>`, or :download:`view the generated report<PulseOx_Forwarding_System.html>`.

Since our process and format are based on STPA, we focus first on hazard analysis :ref:`fundamentals<hazard-analysis-fundamentals>` (eg, accidents to be avoided, safety constraints that prevent them, etc.) then look at how faults can :ref:`occur<hazard-analysis-occurrence>` on potentially unsafe control actions (which are equivalent to system level :construct:`port connections<portconnection>` in our system) and finally document their architectural causes and impacts in individual :ref:`components<hazard-analysis-components>`.

***********************
Hazard Analysis Process
***********************

.. image:: images/hazard-analysis-process.gif
	:alt: The hazard analysis process used with our tool
	:align: center

One of the main goals of this work was to integrate the top-down nature of STPA with the bottom-up, component-focused EMv2.  To that end, the tooling enables developers / analysts to work in either direction (top-down or bottom-up) or use the two methodologies in tandem to drive analysis forward.

.. toctree::
	:hidden:
	
	fundamentals
	occurrence
	component