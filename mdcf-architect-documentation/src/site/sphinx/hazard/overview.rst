.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-overview:

###############
T-SAFE Overview
###############

Once your application has been modeled in the :ref:`supported subset of AADL<aadl-subset-overview>`, you can annotate it to include hazard analysis information. This can be done as part of the tool-supported version of the |Systematic Analysis of Faults and Errors|, or T-SAFE. These annotations will then be used our translator to produce a hazard analysis report and they include both custom properties and a subset of AADL's error-modeling (EMV2) annex.

The resulting report's format can be considered a highly modified form of |STPA|. This work was first discussed in this |MEMOCODE14 publication|, but has evolved considerably since then. The most up-to-date resource for users is the |SAFE| website, while academics and researchers may be more interested in the |dissertation| this work was based on.

********
Contents
********

.. toctree::
	:maxdepth: 1
	
	modeling
	fundamentals
	constructs
	properties

***********
Example App
***********

.. image:: images/app-overview.png
	:alt: Errors in our very simple app
	:align: center

In this language walkthrough, we'll be re-using the app developed in the :ref:`aadl-subset-overview`, though we'll extend it to include components and connections that are not used for code generation. The above app diagram has been annotated with two errors (in red). You can :download:`download the annotated model<snippets/pulse-ox-forwarding.zip>` or :download:`view the generated report<PulseOx_Forwarding_System.html>`.

We first :ref:`extend<modeling-outside-elements>` our model to include the patient and clinician, and then -- since T-SAFE is derived from STPA -- start the analysis with system-level :ref:`fundamentals<hazard-analysis-fundamentals>` (eg, accidents to be avoided, safety constraints that prevent them, etc.). Then, the main, component-level analysis documents how safety constraint violations can be caused using both :ref:`constructs<tsafe-constructs>` and :ref:`properties<tsafe-properties>`.

***********************
Hazard Analysis Process
***********************

.. image:: images/hazard-analysis-process.gif
	:alt: The hazard analysis process used with our tool
	:align: center

One of the main goals of this work was to integrate the top-down nature of STPA with the bottom-up, component-focused EMv2.  To that end, the tooling enables developers / analysts to work in either direction (top-down or bottom-up) or use the two methodologies in tandem to drive analysis forward.