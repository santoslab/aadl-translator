.. include:: ../../util/substitution.rst
.. default-domain:: aadl
.. _dml-translator:


XDML: The DML Translator
########################

MDCF Architect uses the command-line tool *xdml* to translate |DML| models
of |ICE| compliant medical apps and devices into |AADL| models. These models
can in turn, depending on exactly what they model, be translated into MDCF
:ref:`App<aadl-app-translator>` or :ref:`Device<aadl-dev-translator>`
executables.


Installing
**********

1. Get the *xdml* translator by :download:`downloading its JAR <xdml.jar>` file.

2. Create a ``models`` (all lowercase) directory in the workspace where
   ``xdml.jar`` is to reside.


Running
*******

1. `Compile the DMS model source <http://mdcf.github.io/doc/dms/language/model.html#extractor-note>`_
   files with the Scala compiler.

2. Put the *compiled* DMS (*i.e.*, Java class files) of the artifacts to be
   translated into the ``models`` directory created above. Be sure to preserve
   the original directory structures. That is, the fully quailified name of each
   model must be reproduced by its path under ``models``.

3. Invoke the translator: ``java -jar xdml.jar -xa <model-pkg>``

   This will translate every device and requirement found in package
   ``model-pkg`` into AADL. See the next section for more details.


Usage Details
*************

::

   Usage: java xdml [OPTIONS] PACKAGE  or  java -jar xdml.jar [OPTIONS] PACKAGE
   Translate compiled DML in PACKAGE into AADL or HTML as specified by OPTIONS.

   Translation options:
    -x [a|h]   translation target a = AADL, h = HTML
    -f MATCH   filter DML package features
    -l         output long type names
    -s         output short type names - default
    -c         tag DML with sample comments - disabled
    -d         display debug messages

   General options:
    -h        display this help and exit
    -v        print version information and exit

*  ``-x`` specifies either AADL (``a``), HTML (``h``), or both, as translation
   targets.

*  ``-f`` translates only those models with names *ending with* ``MATCH``.

*  ``-c`` exercises the *Dmldoc* feature, which is not supported at this time.
