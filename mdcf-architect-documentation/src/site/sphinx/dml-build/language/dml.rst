.. include:: ../../util/substitution.rst
.. default-domain:: aadl

DML Overview
############

Input to the MDCF Architect toolchain is in the form of models described in DML,
the device modeling language. DML is not yet a full-fledged language, instead,
it is a Scala coded prototype of DML, called DMS, which compiles to Java byte
code.

There are two broad steps in the DML to AADL conversion: 1.) extraction of
each model's DML abstract syntax tree from its *compiled* DMS model, and 2.)
translation of each AST into an AADL model. The *xdml*  program uses the
`MDCF ModelExtractor <http://mdcf.github.io/doc/dms/language/model.html#extraction>`_
to do the first, and its own interpreter to accomplish the second.


A Device Example
****************

Below is an abridged model of an ICE compliant pulse oximeter medical device
(*line 1*). This device consists of only one *virtual medical device* named
``pulseox`` (*line 4*). The VMD has an ``spo2`` *channel* over which a
pysiological metric of type ``ICE_SpO2_Numeric`` is published through a *periodic
exchange* (*lines 10-15*).

.. literalinclude:: PulseOx.scala
   :language: scala
   :linenos:

DML translation produces one device description AADL (and/or HTML) file for each
device DML file input to it.


An App Requirement Example
**************************

Unlike devices, at this level, medical applications are only described in terms
of I/O requirements they place of devices, *i.e.*, payload type and port
characteristics. The app itself is modeled in a
:ref:`subset of AADL<aadl-subset-overview>`.

The model here specifies that the PCA shut-off app needs to connect to a device
capable of periodic publication of ``ICE_SpO2_Numeric`` data (*lines 3 & 4*). At
MDCF runtime, a matching algorithm will seek out such a device by insuring that
it statisfies the given invariant (*lines 10-24*).

.. literalinclude:: PCAShutoffApp_spo2.scala
   :language: scala
   :linenos:
  
DML translation generates one requirement interface AADL (and/or HTML) file for
each requirement DML file, and one payload type declaration AADL file for all
requirment files in a DML package. In addition, one project description and one
structure XML file are produced for each DML package.
