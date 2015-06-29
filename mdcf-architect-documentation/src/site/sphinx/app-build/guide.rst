.. include:: ../util/substitution.rst

Modeling Medical Apps with AADL
###############################

:author:       `Sam Procter <http://people.cis.ksu.edu/~samprocter>`_
:contact:      samprocter@k-state.edu
:organization: `SAnToS Laboratory <http://santoslab.org>`_, `Kansas State University <http://k-state.edu>`_
:last updated: |today| (`changes <https://github.com/santoslab/aadl-translator/commits/master>`_)

MDCF Architect Support
**********************

The MDCF Architect contains an
`open source <https://github.com/santoslab/aadl-translator>`_ plugin for the
|OSATE2| distribution of Eclipse that supports the development of
`Medical Application Platform <http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=6197383&tag=1>`_
Apps. It operates on a :ref:`subset of AADL<aadl-subset-overview>`, and provides
automatic translation to |MDCF| compatible, executable code. It also
supports :ref:`hazard analysis annotations<hazard-analysis-overview>`, from
which a hazard analysis report can be automatically generated.

Contents
********

.. toctree::
   :titlesonly:
   :includehidden:
   :maxdepth: 2

   tools/translator
   language/overview
   hazard/overview
