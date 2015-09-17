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

.. 1. Get the *xdml* translator by :download:`downloading its JAR <xdml.jar>` file.

1. Get the *xdml* translator by `downloading its JAR
   <http://people.cis.ksu.edu/~scbarrett/mdcf-architect/_downloads/xdml.jar>`_ file.

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


Building the JAR
****************

Two approaches to packaging *xdml* into a JAR file are described below. The
first builds a JAR that can be run independent of any libraries,
while the second builds a much smaller JAR that expects to find its dependencies
in the local file system.

Case 1: Stand-alone JAR File
============================

This solution is the most convenient from a deployment point of view, but 
requires more packaging effort, and produces a large JAR that likely contains 
redundant files: besides the *xdml* classes, it also holds all the class files 
of the libraries on which *xdml* depends.

Starting with an empty working directory:

1. Copy the `edu/*` subtree of compiled *xdml* code found under the project's 
   `bin/` directory into the directory. 

2. Gather together all libraries and MDCF (sub)projects that *xdml* is dependent
   on. The former should be in the form of JAR files, while the latter will be 
   available as a mix of JARs and raw class files.
     
   Libraries: apfloat, commons-cli-1.2, commons-lang3, guava, jettison, 
   scala-compiler, scala-library, scala-reflect, string-template, 
   xmlpull, and xstream.
   
   Projects: mdcf-dml-ast, mdcf-dms-core, mdcf-dms-examplev2.clas, 
   mdcf-dms-examplev2.schema, sireum-util,  

3. Because JAR files cannot be nested, the above libraries must be unarchived,
   and their trees merged (e.g., all packages in the `org` domain must be part
   of the same directory tree). The resulting structure should resemble this.

   ::

      <work dir>/
           |
           |-- com/
           |
           |-- edu/
           |
           |-- org/
           |
           |-- scala/
           |
           |-- st4hidden/


4. Create a manifest file named `MANIFEST.MF` with the following content, where
   `<model-dir>` is the name of the directory, relative to the eventual
   `xdml.jar`, that will house the DML models to be translated.

   ::

      Main-Class: edu.ksu.cis.santos.mdcf.dml.translator.xdml
      Class-Path: <model-dir>/

5. Pack *xdml* along with its dependencies into a JAR by running this command 
   from the working directory:

   ::

      jar cfm xdml.jar MANIFEST.MF edu/* org/* com/* scala/* st4hidden/*

6. Once `xdml.jar` has been created, the above directories may be removed. Put 
   the JAR alongside the `<model-dir>` directory mentioned in the manifest 
   file.
   
7. Add the compiled Scala of any DML model packages to be translated 
   to `<model-dir>`.
   
8. Invoke *xdml* with this command, where `<model-pkg>` is the fully qualified 
   name of a package that contains DML models.
 
   ::

      java -jar xdml.jar -xa <model-pkg>

::

    >>> CASE 2: xdml in a JAR, library dependencies in original JARs <<<
    Include this information in the Manifest: 
      Main-Class: edu.ksu.cis.santos.mdcf.dml.translator.xdml
      Class-Path:
    lib/apfloat.jar 
    lib/commons-cli-1.2.jar 
    lib/commons-lang3.jar 
    lib/guava.jar 
    lib/jettison.jar 
    lib/mdcf-dml-ast.jar 
    lib/mdcf-dms-core.jar 
    lib/mdcf-dms-example.jar
    lib/scala-compiler.jar 
    lib/scala-library.jar 
    lib/scala-reflect.jar 
    lib/sireum-util.jar
    lib/string-template.jar 
    lib/xmlpull.jar 
    lib/xstream.jar 
    models/
    
    Copy the edu/* subtree of compiled xdml code found under the project's bin/ 
    directory into the current working directory. Copy the JARs listed in the
    manifest above from their respective places in the MDCF repository into a lib/ 
    directory under the current working one. DML models to be translated will 
    eventually go into a models/ sub-directory. The structure should look like this. 

  <work dir>/
      |
      |-- edu/     (The xdml class files.)
      |
      |-- lib/     (Dependencies as JARs.)
      |
      |-- models/  (DML models to be translated.)
      |
      |-- xdml.jar (Doesn't exist yet!)  

Package up the JAR with: 
  jar cfm xdml.jar MANIFEST.MF edu/*

Run the new xdml.jar from the same location with: 
  java -jar xdml.jar -xa <model-pkg>  (Add -xh for HTML, -d for debug print.)


>>> CASE 3: xdml as byte code, library dependencies in original JARs <<<
Put complied xdml code in a tree rooted at bin. In the same location place DML 
models in the models/ directory & and library JARs in lib/. Invoke xdml: 
  java -cp bin/:lib/*:models/ edu.ksu.cis.santos.mdcf.dml.translator.xdml
      -xa <model-pkg>  (Add -xh for HTML, -d for debug print.)
