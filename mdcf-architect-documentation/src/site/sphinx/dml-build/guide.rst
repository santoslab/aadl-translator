.. include:: ../util/substitution.rst


Modeling Apps and Devices with DML
##################################

.. `Steve Barrett <http://people.cis.ksu.edu/~scbarrett>`_

:author:       Steve Barrett
:contact:      scbarrett@k-state.edu
:organization: `SAnToS Laboratory <http://santoslab.org>`_, `Kansas State University <http://k-state.edu>`_
:last updated: |today| (`changes <https://github.com/santoslab/aadl-translator/commits/master>`_)


The Device Modeling Language
****************************

The MDCF Architect is centered around |AADL| with the intent of leveraging the
analysis capabilities of its associated tooling. Unfortunately, AADL is a
complex language, and its descriptive powers can easily get an MAP component
designer into trouble. So, rather than modeling components in AADL, with MDCF
Architect, designers may choose to model in a more constrained, medical device
modeling language.

By being domain specific, |DML| directly supports the
building of components that conform to the |ICE| standard for
`Medical Application Platforms <http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=6197383&tag=1>`_.
Despite its name, DML is perfectly suited for modeling medical app requirements
as well as devices.

The MDCF Architect's *xdml translator* converts these DML models into AADL for
the stated purposes of analysis. Afterwards, other tools in the chain convert
the AADL models into executable |MDCF| code suitable for deployment on the MDCF.

The translator can also produce HTML depictions of the DML models to assist
with their visualization and publication.


Contents
********

.. toctree::
   :titlesonly:
   :includehidden:
   :maxdepth: 2

   language/dml
   tools/xdml