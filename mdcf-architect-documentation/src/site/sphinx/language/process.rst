.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _aadl-process-component:

#####################################
The *Process* and *Thread* Components
#####################################

AADL process components describe the app's view of a software component. Process components are themselves composed of threads.

*********************************
Properties and Contained Elements
*********************************

.. construct:: process
	
	Process components contain a listing of the ports exposed by the device, and a property specifying whether this process is a display or logic component.
	
	:contained-element features: The ports this process exposes to other components (either other processes or :construct:`devices<device>`).
	:kind features: :construct:`port`
	:property ComponentType: |prop component-type|
	:type ComponentType: :property:`MAP_Properties::Component_Type<component-type>`
	
.. construct:: process implementation

	:contained-element subcomponents: The tasks that compose this process.
	:contained-element connections: A list of links between the various threads.  Note that thread-thread connections are not allowed.
	:kind subcomponents: :construct:`thread`
	:kind connections: :construct:`port connections<portconnection>`

	Process implementations contain a listing of the tasks that compose the software component and connections from those tasks to the process's external ports.  There are no process implementation level properties.

.. construct:: thread

	Threads correspond to individual units of work such as handling an incoming message or checking for an alarm condition.
	
	:contained-element features: The process-level ports this thread either reads from or writes to.
	:kind features: :construct:`port`
	:property Period: |prop period|
	:property Deadline: |prop deadline|
	:property WCET: |prop wcet|
	:property Dispatch: |prop dispatch|
	:type Period: :property:`Timing_Properties::Period<period>`
	:type Deadline: :property:`Timing_Properties::Deadline<deadline>`
	:type WCET: :property:`Timing_Properties::Compute_Execution_Time<wcet>`
	:type Dispatch: :property:`Thread_Properties::Dispatch_Protocol<dispatch-protocol>`
	
.. note::
	There must be one-to-one correspondence (bijective) mapping between sporadic threads and the combined set of event and event data ports.

.. construct:: thread implementation

	Thread implementations are always empty -- all behavior is realized in the generated code.

*******
Example
*******

.. literalinclude:: snippets/logic.aadl
	:language: aadl
	:linenos:
   
***********
Translation
***********

Process components translate to two java classes (a user-modifiable class and a supertype that hides MDCF-specific implementation details) that contain one or more :construct:`tasks<thread>`, which communicate with one another via shared state. That is, communication within one process is done via shared state, communication between two processes (or a process and a device) is done via :construct:`port connections<portconnection>`, which are realized as messages sent across the underlying middleware.
   
.. literalinclude:: snippets/process.java
	:language: java
	:linenos:

.. literalinclude:: snippets/process_supertype.java
	:language: java
	:linenos:

.. literalinclude:: snippets/process.compsig.xml
	:language: xml
	:linenos: