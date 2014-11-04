.. include:: ../util/substitution.rst
.. default-domain:: aadl
.. _hazard-analysis-fundamentals:

###########################
Background and Fundamentals
###########################

Before a hazard analysis can begin, there are a number of app background elements and *fundamentals* that should be established. These fundamentals are documented as AADL property constants.

*********************
Background Properties
*********************

.. property:: context

   The clinical context of the app. That is, the problem that the app will address. There should only be one context per app.

   :type: AADLString
   :example: "Clinicians want to view physiological parameters on a display not physically connected to a physiological monitor."

.. property:: assumption

   An assumption about the environment / clinical process where the app will be used.

   :type: AADLString
   :example: "There are no alarms that need forwarding."

.. note::
   The names of the context and assumption properties are ignored by the report generator.

.. property:: abbreviation

   An assumption about the environment / clinical process where the app will be used.

   :type: Record
   :subproperty Full: The full version of the abbreviated term
   :subproperty Definition: The meaning of the abbreviated term
   :type Full: AADLString
   :type Definition: AADLString
   :example: 
.. code-block:: aadl
   :linenos:
   
   property set ExampleAbbreviationSet is
	   SpO2 : constant MAP_Error_Properties::Abbreviation => [
		  Full => "Blood-oxygen saturation";
		  Definition => "The amount of oxygen in the patient's blood";
	   ];
   end ExampleAbbreviationSet;

****************************
Hazard Analysis Fundamentals
****************************

.. property:: accident_level

   The priority of an accident. Used for prioritization when mitigating one hazard would increase exposure to another.

   :type: Record
   :subproperty Level: The accident priority
   :subproperty Description: A short description of the accident level
   :type Level: AADLInteger
   :type Description: AADLString
   :example:
.. code-block:: aadl
   :linenos:
   
   property set ExampleAccident_LevelSet is
	   DeathOrInjury : constant MAP_Error_Properties::Accident_Level => [
		   Level => 1;
		   Description => "A human is killed or seriously injured.";
	   ];
   end ExampleAccident_LevelSet;
   
.. property:: accident

   A particular accident that could occur as a result of the app.

   :type: Record
   :subproperty Number: An identifier
   :subproperty Description: A short description of the accident
   :subproperty Level: The accident level associated with this accident
   :type Number: AADLInteger
   :type Description: AADLString
   :type Level: :property:`MAP_Error_Properties::Accident_Level<Accident_Level>`
   :example:
.. code-block:: aadl
   :linenos:
   
   property set ExampleAccidentSet is
	   PatientHarmed : constant MAP_Error_Properties::Accident => [
		   Number => 1;
		   Description => "Patient is killed or seriously injured.";
		   Level => PulseOx_Forwarding_Error_Properties::DeathOrInjury;
	   ];
   end ExampleAccidentSet;
   
.. property:: hazard

   A particular hazard that the app could encounter.

   :type: Record
   :subproperty Number: An identifier
   :subproperty Description: A short description of the hazard
   :subproperty Accident: The accident that would occur if this hazard comes to pass
   :type Number: AADLInteger
   :type Description: AADLString
   :type Accident: :property:`MAP_Error_Properties::Accident<Accident>`
   :example:
.. code-block:: aadl
   :linenos:
   
   property set ExampleHazardSet is
	   BadInfoDisplayed : constant MAP_Error_Properties::Hazard => [
		   Number => 1;
		   Description => "Incorrect information is sent to the display.";
		   Accident => PulseOx_Forwarding_Error_Properties::PatientHarmed;
	   ];
   end ExampleHazardSet;

.. property:: constraint

   A safety constraint that, if enforced, will prevent a particular hazard from occurring.

   :type: Record
   :subproperty Number: An identifier
   :subproperty Description: A short description of the constraint
   :subproperty Hazard: The hazard that would be present if this constraint is not enforced
   :type Number: AADLInteger
   :type Description: AADLString
   :type Hazard: :property:`MAP_Error_Properties::Hazard<Hazard>`
   :example:
.. code-block:: aadl
   :linenos:
   
   property set ExampleConstraintSet is
	   ShowGoodInfo : constant MAP_Error_Properties::Constraint => [
		   Number => 1;
		   Description => "The app must accurately inform the display of the status of the patient's vital signs.";
		   Hazard => PulseOx_Forwarding_Error_Properties::BadInfoDisplayed;
	   ];
   end ExampleConstraintSet;


*******
Example
*******

.. literalinclude:: snippets/fundamentals.aadl
	:language: aadl
	:linenos: